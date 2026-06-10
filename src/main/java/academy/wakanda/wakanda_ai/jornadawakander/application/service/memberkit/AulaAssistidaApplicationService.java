package academy.wakanda.wakanda_ai.jornadawakander.application.service.memberkit;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.dto.AulaMemberKitDTO;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.repository.MissaoWakandaRepository;
import academy.wakanda.wakanda_ai.comunicacao.infra.PublicadorNotificacaoSns;
import academy.wakanda.wakanda_ai.constants.TopicNames;
import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.service.ProgressoWakanderEventDto;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.JornadaWakanderRepository;
import academy.wakanda.wakanda_ai.jornadawakander.domain.AulaAssistida;
import academy.wakanda.wakanda_ai.wakander.application.service.WakanderRepository;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class AulaAssistidaApplicationService implements AulaAssistidaService {

    private final WakanderRepository wakanderRepository;
    private final JornadaWakanderRepository jornadaWakanderRepository;
    private final List<AulaAssistidaProcessador> processadoresAulaAssistida;
    private final MissaoWakandaRepository missaoWakandaRepository;
    private final PublicadorNotificacaoSns publicadorNotificacaoSns;
    private final TopicNames topicNames;

    @Override
    public void processaEventoAulaAssistida(AulaMemberKitDTO aulaMemberKit) {
        log.info("[start] AulaAssistidaApplicationService - processaEventoAulaAssistida");
        Wakander wakander = wakanderRepository.buscaWakanderPorIdMemberKit(aulaMemberKit.getData().getUser().getId());
        AulaAssistida aulaAssistida = registraAulaAssistida(aulaMemberKit, wakander);
        strategyAulaAssistidaProcessador(aulaAssistida, wakander);
        publicaEventoProgressoMissaoSeExistir(aulaAssistida, wakander);
        log.debug("[finish] AulaAssistidaApplicationService - processaEventoAulaAssistida");
    }

    private void strategyAulaAssistidaProcessador(AulaAssistida aulaAssistida, Wakander wakander) {
        processadoresAulaAssistida.stream()
                .filter(a -> a.validaSeEventoProcessa(aulaAssistida, wakander))
                .findFirst()
                .ifPresent(a -> a.processaEvento(aulaAssistida, wakander));
    }

    private AulaAssistida registraAulaAssistida(AulaMemberKitDTO aulaMemberKit, Wakander wakander) {
        log.info("[start] AulaAssistidaApplicationService - registraAulaAssistida");
        aulaMemberKit.validaSeAulaAssistida();
        AulaAssistida aulaAssistida = jornadaWakanderRepository.save(new AulaAssistida(wakander.getIdWakander(), aulaMemberKit));
        wakander.atualizaUltimaAulaAssistida(aulaAssistida.getIdAulaAssistida(), aulaAssistida.getDataConclusao());
        wakanderRepository.save(wakander);
        log.debug("[finish] AulaAssistidaApplicationService - registraAulaAssistida");
        return aulaAssistida;
    }

    private void publicaEventoProgressoMissaoSeExistir(AulaAssistida aula, Wakander wakander) {
        ProgressoWakanderEventDto evento = ProgressoWakanderEventDto.onMissaoProgresso(
                wakander.getIdWakander(), aula.getIdAula().toString());
        publicadorNotificacaoSns.enviaNotificacaoSns(
                wakander.getIdWakander().toString(),
                evento,
                topicNames.getProgressoWakanderRequests()
        );
    }
}
