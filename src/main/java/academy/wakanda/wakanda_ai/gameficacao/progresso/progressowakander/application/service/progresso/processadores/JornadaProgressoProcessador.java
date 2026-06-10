package academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.service.progresso.processadores;

import academy.wakanda.wakanda_ai.gameficacao.progresso.jornadaprogresso.application.service.JornadaProgressoService;
import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.service.ProgressoWakanderEventDto;
import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.service.ProgressoWakanderEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class JornadaProgressoProcessador implements ProgressoWakanderProcessor {

    private final JornadaProgressoService jornadaProgressoService;

    @Override
    public boolean validaProcessaProgresso(ProgressoWakanderEventType typeProgressoWakander) {
        return typeProgressoWakander == ProgressoWakanderEventType.JORNADA_PROGRESSO;
    }

    @Override
    public void processaProgressoWakander(ProgressoWakanderEventDto progressoWakander) {
        log.info("[start] JornadaProgressoProcessador - processaProgressoWakander");
        jornadaProgressoService.concluiJornadaProgresso(progressoWakander);
        log.debug("[finish] JornadaProgressoProcessador - processaProgressoWakander");
    }
}
