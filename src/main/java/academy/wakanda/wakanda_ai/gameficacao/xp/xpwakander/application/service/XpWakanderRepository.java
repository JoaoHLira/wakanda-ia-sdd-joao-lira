package academy.wakanda.wakanda_ai.gameficacao.xp.xpwakander.application.service;

import academy.wakanda.wakanda_ai.gameficacao.xp.xpwakander.domain.XpWakander;

import java.util.UUID;

public interface XpWakanderRepository {

    void salva(XpWakander wakander);

    XpWakander buscaPorIdProgressoWakander(UUID idProgressoWakander);

}
