package academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.application.api;

import java.time.LocalDateTime;
import java.util.UUID;

public interface MissaoConcluidaProjection {
    UUID getIdMissaoProgresso();

    UUID getIdMissao();

    String getTitulo();

    int getXpObtido();

    LocalDateTime getDataConclusao();

    Integer getSabTeorico();

    Integer getSabProcesso();

    Integer getSabKnowHow();

    Integer getSabComportamental();

    Integer getSabCriativo();
}
