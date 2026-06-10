package academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.service;

import java.util.UUID;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.MemberkitEventRequest;

public interface MemberkitService {

    void importaMemberkit(UUID idTipoMissao, UUID idJornada);

    void processaWebhook(MemberkitEventRequest request);

}
