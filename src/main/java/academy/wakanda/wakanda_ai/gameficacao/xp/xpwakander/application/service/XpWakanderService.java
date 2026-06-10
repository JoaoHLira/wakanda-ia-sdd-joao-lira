package academy.wakanda.wakanda_ai.gameficacao.xp.xpwakander.application.service;

import academy.wakanda.wakanda_ai.gameficacao.common.dto.XpPromocaoClasseDTO;
import academy.wakanda.wakanda_ai.gameficacao.common.dto.XpWakanderEventDTO;

public interface XpWakanderService {

    void processaXP(XpWakanderEventDTO xpWakander);
    void processaPromocaoClasse(XpPromocaoClasseDTO xpPromocaoClasseDTO);
}
