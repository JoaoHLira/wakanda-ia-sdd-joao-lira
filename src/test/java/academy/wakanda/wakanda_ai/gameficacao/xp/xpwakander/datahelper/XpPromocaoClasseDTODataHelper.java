package academy.wakanda.wakanda_ai.gameficacao.xp.xpwakander.datahelper;

import academy.wakanda.wakanda_ai.gameficacao.common.dto.XpPromocaoClasseDTO;

import java.util.UUID;

public class XpPromocaoClasseDTODataHelper {

    public static XpPromocaoClasseDTO criarXpPromocaoClasseDTO(UUID idXpWakander,
                                                                UUID idProgressoWakander,
                                                                UUID idWakander,
                                                                Integer nivel) {
        return new XpPromocaoClasseDTO(
                idXpWakander,
                idProgressoWakander,
                idWakander,
                100,
                nivel
        );
    }
}
