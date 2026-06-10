package academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.api;

import java.util.UUID;

public interface RankingWakanderProjection {
    UUID getIdWakander();

    String getNome();

    Long getMissoesConcluidas();

    Integer getXpTotal();
}
