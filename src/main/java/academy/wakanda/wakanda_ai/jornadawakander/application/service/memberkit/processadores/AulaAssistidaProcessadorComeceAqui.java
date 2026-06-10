package academy.wakanda.wakanda_ai.jornadawakander.application.service.memberkit.processadores;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventDto;
import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventype;
import academy.wakanda.wakanda_ai.comunicacao.infra.PublicadorNotificacaoSns;
import academy.wakanda.wakanda_ai.constants.TopicNames;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.JornadaWakanderRepository;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.OnboardingWakanderService;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.ProgressoWakanderRepository;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.memberkit.AulaAssistidaProcessador;
import academy.wakanda.wakanda_ai.jornadawakander.domain.AulaAssistida;
import academy.wakanda.wakanda_ai.jornadawakander.domain.JornadaWakanda;
import academy.wakanda.wakanda_ai.jornadawakander.domain.OnboardingWakander;
import academy.wakanda.wakanda_ai.wakander.application.service.WakanderRepository;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RequiredArgsConstructor
@Log4j2
@Component
public class AulaAssistidaProcessadorComeceAqui implements AulaAssistidaProcessador {

    private final JornadaWakanderRepository jornadaWakanderRepository;
    private final WakanderRepository wakanderRepository;
    private final ProgressoWakanderRepository progressoWakanderRepository;
    private final PublicadorNotificacaoSns publicadorNotificacaoSns;
    private final TopicNames topicNames;
    private final OnboardingWakanderService onboardingWakanderService;

    @Value("${memberkit.course.id-comece-aqui}")
    private Long IDComeceAqui;

    @Override
    public boolean validaSeEventoProcessa(AulaAssistida aulaAssistida, Wakander wakander) {
        return aulaAssistida.getIdCurso().equals(IDComeceAqui);
    }

    @Override
    public void processaEvento(AulaAssistida aulaAssistida, Wakander wakander) {
        log.info("[start] AulaAssistidaProcessadorComeceAqui - processaEvento");
        List<AulaAssistida> aulas = jornadaWakanderRepository.buscaAulasPorIdCurso(aulaAssistida.getIdCurso());
        Long totalDeAulas = retornaQuantidadeAulasComeceAqui(aulaAssistida, aulas);
        if (totalDeAulas == 3) {
            atualizaStatusWakander(aulaAssistida, wakander);
            atualizaStatusComeceAquiOnboarding(wakander);
        } else {
            log.info("Wakander completou as aulas necessárias do curso Comece Aqui. Total de Aulas: " + totalDeAulas);
        }
        log.debug("[finish] AulaAssistidaProcessadorComeceAqui - processaEvento");
    }

	private long retornaQuantidadeAulasComeceAqui(AulaAssistida aulaAssistida, List<AulaAssistida> aulas) {
		return aulas.stream()
                .filter(a -> a.getIdWakander().equals(aulaAssistida.getIdWakander()))
                .count();
	}

    private void atualizaStatusWakander(AulaAssistida aulaAssistida, Wakander wakander) {
        log.info("[start] AulaAssistidaProcessadorComeceAqui - mudaStatusJornada");
        JornadaWakanda jornadaConcluida = wakander.getJornadaAtual();
        progressoWakanderRepository.salvaProgresso(aulaAssistida.getIdWakander(), jornadaConcluida, JornadaWakanda.FINALIZOU_COMECE_AQUI);
        wakander.atualizaStatusJornada(JornadaWakanda.FINALIZOU_COMECE_AQUI);
        wakanderRepository.save(wakander);
        log.debug("[finish] AulaAssistidaProcessadorComeceAqui - mudaStatusJornada");
    }

    private void publicadorNotificacaoSns(Wakander wakander, String mensagem) {
        log.info("[start] AulaAssistidaProcessadorComeceAqui - publicadorNotificacaoSns");
        ZApiEventDto zApiEventDto = new ZApiEventDto(
                ZApiEventype.NORMAL_MESSAGE,
                wakander.getContato().getWhatsapp(),
                mensagem
        );
        publicadorNotificacaoSns.enviaNotificacaoSns(
                wakander.getIdWakander().toString(),
                zApiEventDto,
                topicNames.getZapiRequests()
        );
        log.debug("[finish] AulaAssistidaProcessadorComeceAqui - publicadorNotificacaoSns");
    }
    
    private void atualizaStatusComeceAquiOnboarding(Wakander wakander) {
        log.info("[start] AulaAssistidaProcessadorComeceAqui - atualizaStatusComeceAquiOnboarding");
        OnboardingWakander onboardingWakander = onboardingWakanderService.buscaOnboardingPorIdWakander(wakander.getIdWakander());
        onboardingWakander.atualizaComeceAqui();
        onboardingWakanderService.save(onboardingWakander);
        publicaChecklist(wakander, onboardingWakander);
        log.debug("[finish] AulaAssistidaProcessadorComeceAqui - atualizaStatusComeceAquiOnboarding");
	}

	private void publicaChecklist(Wakander wakander, OnboardingWakander onboardingWakander) {
		String checklist = onboardingWakanderService.retornaChecklist(onboardingWakander);
        publicadorNotificacaoSns(wakander, checklist);
	}
}
