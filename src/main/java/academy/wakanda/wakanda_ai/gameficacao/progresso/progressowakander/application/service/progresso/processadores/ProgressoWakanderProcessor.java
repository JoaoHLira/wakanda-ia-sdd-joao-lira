package academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.service.progresso.processadores;

import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.service.ProgressoWakanderEventDto;
import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.service.ProgressoWakanderEventType;

public interface ProgressoWakanderProcessor {
    boolean validaProcessaProgresso(ProgressoWakanderEventType typeProgressoWakander);
    void processaProgressoWakander(ProgressoWakanderEventDto progressoWakander);
}
