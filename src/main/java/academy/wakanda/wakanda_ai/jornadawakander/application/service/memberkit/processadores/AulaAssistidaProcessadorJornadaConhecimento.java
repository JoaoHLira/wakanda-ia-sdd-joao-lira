package academy.wakanda.wakanda_ai.jornadawakander.application.service.memberkit.processadores;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventDto;
import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventype;
import academy.wakanda.wakanda_ai.comunicacao.infra.PublicadorNotificacaoSns;
import academy.wakanda.wakanda_ai.constants.TopicNames;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.OnboardingWakanderService;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.memberkit.AulaAssistidaProcessador;
import academy.wakanda.wakanda_ai.jornadawakander.domain.AulaAssistida;
import academy.wakanda.wakanda_ai.jornadawakander.domain.JornadaWakanda;
import academy.wakanda.wakanda_ai.jornadawakander.domain.OnboardingWakander;
import academy.wakanda.wakanda_ai.wakander.application.service.WakanderRepository;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@RequiredArgsConstructor
public class AulaAssistidaProcessadorJornadaConhecimento implements AulaAssistidaProcessador {

    private final WakanderRepository wakanderRepository;
    private final PublicadorNotificacaoSns publicadorNotificacaoSns;
    private final TopicNames topicNames;
    private final OnboardingWakanderService onboardingWakanderService;

    @Value("${memberkit.lesson.id-primeira-aula-conhecimento}")
    private String idPrimeiraAulaJornadaConhecimento;

    @Override
    public boolean validaSeEventoProcessa(AulaAssistida aulaAssistida, Wakander wakander) {
        log.info("[start] AulaAssistidaProcessadorJornadaConhecimento - validaSeEventoProcessa");
        log.debug("[finish] AulaAssistidaProcessadorJornadaConhecimento - validaSeEventoProcessa");
        return ehPrimeiraAulaDoConhecimento(aulaAssistida) && wakanderEstaNoOnboard(wakander);
    }

    @Override
    public void processaEvento(AulaAssistida aulaAssistida, Wakander wakander) {
        log.info("[start] AulaAssistidaProcessadorJornadaConhecimento - processaEvento");
        wakander.atualizaProgresso(JornadaWakanda.JORNADA_CONHECIMENTO);
        wakanderRepository.save(wakander);
        atualizaStatusPrimeiraAulaJornadaOnboarding(wakander);
        log.debug("[finish] AulaAssistidaProcessadorJornadaConhecimento - processaEvento");
    }

    private void enviaMensagemParaWakander(Wakander wakander, OnboardingWakander onboardingWakander) {
        log.info("[start] AulaAssistidaProcessadorJornadaConhecimento - enviaMensagemParaWakander");
		String mensagem = onboardingWakanderService.retornaChecklist(onboardingWakander);
        publicadorNotificacaoSns.enviaNotificacaoSns(
                wakander.getIdWakander().toString(),
                new ZApiEventDto(ZApiEventype.NORMAL_MESSAGE, wakander.getContato().getWhatsapp(), mensagem),
                topicNames.getZapiRequests()
        );
        log.debug("[finish] AulaAssistidaProcessadorJornadaConhecimento - enviaMensagemParaWakander");
    }

    private boolean ehPrimeiraAulaDoConhecimento(AulaAssistida aulaAssistida) {
        return aulaAssistida.getIdAula().toString().equals(idPrimeiraAulaJornadaConhecimento);
    }

    private boolean wakanderEstaNoOnboard(Wakander wakander) {
        return wakander.getJornadaAtual().equals(JornadaWakanda.FINALIZOU_COMECE_AQUI);
    }
    
    private void atualizaStatusPrimeiraAulaJornadaOnboarding(Wakander wakander) {
        log.info("[start] AulaAssistidaProcessadorJornadaConhecimento - atualizaStatusJornadaConhecimentoOnboarding");
        OnboardingWakander onboardingWakander = onboardingWakanderService.buscaOnboardingPorIdWakander(wakander.getIdWakander());
        onboardingWakander.atualizaPrimeiraAulaJornada ();
        onboardingWakanderService.save(onboardingWakander);
        enviaMensagemParaWakander(wakander, onboardingWakander);
        log.debug("[finish] AulaAssistidaProcessadorJornadaConhecimento - atualizaStatusJornadaConhecimentoOnboarding");
	}

}
