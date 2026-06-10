package academy.wakanda.wakanda_ai.gameficacao.progresso.jornadaprogresso.application.service;

import academy.wakanda.wakanda_ai.comunicacao.infra.PublicadorNotificacaoSns;
import academy.wakanda.wakanda_ai.constants.TopicNames;
import academy.wakanda.wakanda_ai.gameficacao.common.dto.XpWakanderEventDTO;
import academy.wakanda.wakanda_ai.gameficacao.progresso.jornadaprogresso.domain.JornadaProgresso;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.application.service.JornadaWakandaRepository;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.domain.JornadaWakanda;
import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.application.service.MissaoProgressoRepository;
import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.domain.MissaoProgresso;
import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.domain.MissaoProgressoStatus;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.repository.MissaoWakandaRepository;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.MissaoStatus;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.MissaoWakanda;
import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.service.ProgressoWakanderEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class JornadaProgressoApplicationService implements JornadaProgressoService {
    private final MissaoWakandaRepository missaoWakandaRepository;
    private final MissaoProgressoRepository missaoProgressoRepository;
    private final PublicadorNotificacaoSns publicadorNotificacaoSns;
    private final JornadaProgressoRepository jornadaProgressoRepository;
    private final JornadaWakandaRepository jornadaWakandaRepository;
    private final TopicNames topicNames;

    @Override
    public void concluiJornadaSeMissoesConcluidas(UUID idJornada, UUID idProgressoWakander) {
        log.info("[start] JornadaProgressoApplicationService - concluiJornadaSeMissoesConcluidas");
        List<UUID> idsMissoes = MissaoWakanda.retornaIdsMissoes(missaoWakandaRepository.buscaMissoesAtivasPorIdJornada(idJornada, MissaoStatus.ATIVA));
        List<MissaoProgresso> missoesProgresso = missaoProgressoRepository.buscaMissoesProgressoPorIdsMissoes(idsMissoes);
        validaEConcluiJornada(idJornada, idProgressoWakander, idsMissoes, missoesProgresso);
        log.debug("[finish] JornadaProgressoApplicationService - concluiJornadaSeMissoesConcluidas");
    }

    private void validaEConcluiJornada(UUID idJornada, UUID idProgressoWakander, List<UUID> idsMissoes, List<MissaoProgresso> missoesProgresso) {
        if (validaSeTodasMissoesConcluidas(idsMissoes, missoesProgresso)) {
            JornadaProgresso jornadaAtual = jornadaProgressoRepository.buscaJornadaProgresso(idJornada, idProgressoWakander);
            JornadaWakanda jornadaWakandaAtual = jornadaWakandaRepository.buscaJornadaId(idJornada);
            publicaJornadaConcluida(ProgressoWakanderEventDto.onJornadaProgresso(jornadaAtual));
            jornadaAtual.concluiJornada(jornadaWakandaAtual);
            jornadaProgressoRepository.salvaJornadaProgresso(jornadaAtual);
            ativaProximaJornada(idProgressoWakander, jornadaWakandaAtual.getIdTrilhaWakanda(), jornadaWakandaAtual );
        } else {
            log.info("Ainda existem missões pendentes de conclusão para a jornada {}!", idJornada);
        }
    }

    void ativaProximaJornada(UUID idProgressoWakander, UUID idTrilhaWakanda, JornadaWakanda jornadaWakandaAtual) {
        List<JornadaWakanda> jornadasPelaTrilha = jornadaWakandaRepository.buscaJornadasPorIdTrilha(idTrilhaWakanda);
        Optional<JornadaWakanda> proximaJornada = encontraProximaJornada(jornadasPelaTrilha, jornadaWakandaAtual);
        criaProximaJornada(idProgressoWakander, proximaJornada);
    }

    private void criaProximaJornada(UUID idProgressoWakander, Optional<JornadaWakanda> proximaJornada) {
        if (proximaJornada.isPresent()) {
            JornadaProgresso jornadaProgresso = new JornadaProgresso(proximaJornada.get().getIdJornada(), idProgressoWakander);
            jornadaProgressoRepository.salvaJornadaProgresso(jornadaProgresso);
        } else {
            log.info("Não há próxima jornada para ativar. Esta é a última jornada da trilha.");
        }
    }

    private Optional<JornadaWakanda> encontraProximaJornada(List<JornadaWakanda> jornadasDaTrilha,  JornadaWakanda jornadaWakandaAtual) {
        return jornadasDaTrilha.stream()
                .filter(jornadaWakanda -> jornadaWakanda.getOrdemJornada() == jornadaWakandaAtual.getOrdemJornada() + 1)
                .findFirst();
    }

    private static boolean validaSeTodasMissoesConcluidas(List<UUID> idsMissoes, List<MissaoProgresso> missoesProgresso) {
        return idsMissoes.stream()
                .allMatch(idMissao -> missoesProgresso.stream()
                        .anyMatch(p -> p.getIdMissaoWakanda().equals(idMissao)
                                && p.getStatusProgresso() == MissaoProgressoStatus.CONCLUIDA));
    }

    private void publicaJornadaConcluida(ProgressoWakanderEventDto progressoWakanderEventDto) {
        log.info("[start] JornadaProgressoApplicationService - publicaJornadaConcluida");
        publicadorNotificacaoSns.enviaNotificacaoSns(
                UUID.randomUUID().toString(),
                progressoWakanderEventDto,
                topicNames.getProgressoWakanderRequests()
        );
        log.debug("[finish] JornadaProgressoApplicationService - publicaJornadaConcluida");
    }

    @Override
    public void concluiJornadaProgresso(ProgressoWakanderEventDto progressoWakander) {
        log.info("[start] JornadaProgressoApplicationService - concluiJornadaProgresso");
        JornadaProgresso jornadaProgresso = jornadaProgressoRepository.buscaJornadaProgressoPorId(progressoWakander.getIdJornadaProgresso());
        jornadaProgresso.concluiJornada(jornadaWakandaRepository.buscaJornadaId(jornadaProgresso.getIdJornadaWakanda()));
        jornadaProgressoRepository.salvaJornadaProgresso(jornadaProgresso);
        XpWakanderEventDTO xpWakanderEventDTO = XpWakanderEventDTO.onJornadaProgresso(jornadaProgresso);
        publicaXpJornadaProgresso(xpWakanderEventDTO);
        log.debug("[finish] JornadaProgressoApplicationService - concluiJornadaProgresso");
    }

    private void publicaXpJornadaProgresso(XpWakanderEventDTO xpWakanderEventDTO) {
        log.info("[start] JornadaProgressoApplicationService - publicaXpJornadaProgresso");
        publicadorNotificacaoSns.enviaNotificacaoSns(
                UUID.randomUUID().toString(),
                xpWakanderEventDTO,
                topicNames.getXpWakanderRequests()
        );
        log.debug("[finish] JornadaProgressoApplicationService - publicaXpJornadaProgresso");
    }
}