package academy.wakanda.wakanda_ai.jornadawakander.datahelper;

import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventDto;
import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventype;
import academy.wakanda.wakanda_ai.constants.MensagensWhatsapp;
import academy.wakanda.wakanda_ai.jornadawakander.domain.AulaAssistida;
import academy.wakanda.wakanda_ai.jornadawakander.domain.OnboardingWakander;
import academy.wakanda.wakanda_ai.jornadawakander.domain.StatusAula;
import academy.wakanda.wakanda_ai.wakander.application.api.WakanderRelatorioDTO;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;

import java.time.LocalDateTime;
import java.util.UUID;

public class JornadaWakanderDataHelper {

    public static AulaAssistida criaAulaAssistida() {
        return new AulaAssistida(UUID.randomUUID(),
                12345L,
                12345L,
                UUID.randomUUID(),
                LocalDateTime.now(),
                StatusAula.CONCLUIDO
        );
    }

    public static ZApiEventDto criaZApiEventDto() {
        return new ZApiEventDto(
                ZApiEventype.NORMAL_MESSAGE,
                "5511987654321",
                MensagensWhatsapp.PROGRESSO_CHECKLIST.getMensagem()
        );
    }

    public static WakanderRelatorioDTO criaWakanderRelatorioDTO() {
        return new WakanderRelatorioDTO(
                10,
                9,
                8,
                7,
                6
        );
    }

    public static WakanderRelatorioDTO criaWakanderRelatorioDTOZeroMetricas() {
        return new WakanderRelatorioDTO(
                0,
                0,
                0,
                0,
                0
        );
    }
    
    public static OnboardingWakander criaOnboardingWakander(Wakander wakander) {
    	OnboardingWakander onboardingWakander = new OnboardingWakander(wakander);
    	onboardingWakander.atualizaEntrouNoDiscord();
        wakander.associarDiscord("123456", "teste");
    	return onboardingWakander;
    }
}
