package academy.wakanda.wakanda_ai.gameficacao.xp.xpwakander.datahelper;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.Sabedorias;
import academy.wakanda.wakanda_ai.gameficacao.xp.xpwakander.domain.XpWakander;

import java.time.LocalDateTime;
import java.util.UUID;

public class XpWakanderDataHelper {

    public static XpWakander criarXpWakanderComXp(UUID idProgresso, int xp, int xpProximoNivel) {
        return new XpWakander(
                UUID.randomUUID(),
                idProgresso,
                xp,
                1,
                xpProximoNivel,
                new Sabedorias(10, 10, 10, 10, 10),
                LocalDateTime.now()
        );
    }
}
