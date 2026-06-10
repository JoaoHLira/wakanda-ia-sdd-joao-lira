package academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.service.progresso.processadores;

import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.application.service.MissaoProgressoService;
import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.service.ProgressoWakanderEventDto;
import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.service.ProgressoWakanderEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class MissaoProgressoProcessador implements ProgressoWakanderProcessor {

    private final MissaoProgressoService missaoProgressoService;

    @Override
    public boolean validaProcessaProgresso(ProgressoWakanderEventType typeProgressoWakander) {
        return typeProgressoWakander.equals(ProgressoWakanderEventType.MISSAO_PROGRESSO);
    }

    @Override
    public void processaProgressoWakander(ProgressoWakanderEventDto progressoWakander) {
        log.info("[start] MissaoProgressoProcessador - processaProgressoWakander");
        missaoProgressoService.concluiMissao(progressoWakander);
        log.debug("[finish] MissaoProgressoProcessador - processaProgressoWakander");
    }
}
