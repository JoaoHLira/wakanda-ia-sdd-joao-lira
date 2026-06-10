package academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.service.processadores;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.MemberkitEventRequest;

public interface MemberkitProcessor {
    boolean validaSeEventoProcessa(String type);
    void processaEvento(MemberkitEventRequest request);
}
