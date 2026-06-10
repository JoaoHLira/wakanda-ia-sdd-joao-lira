package academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.dataHelper;

import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.domain.ProgressoWakander;

import java.util.UUID;

public class ProgressoWakanderDataHelper {

    public static UUID getIdWakander() {
        return UUID.fromString("11111111-1111-1111-1111-111111111111");
    }

    public static ProgressoWakander getProgressoWakanderIniciante() {
        ProgressoWakander progresso = new ProgressoWakander(getIdWakander());
        return progresso;
    }
}
