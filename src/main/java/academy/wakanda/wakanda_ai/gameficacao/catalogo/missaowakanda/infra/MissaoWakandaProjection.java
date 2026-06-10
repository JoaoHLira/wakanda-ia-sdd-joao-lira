package academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.infra;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.MissaoStatus;

import java.util.UUID;

public interface MissaoWakandaProjection {

    UUID getIdMissao();

    MissaoStatus getMissaoStatus();
}
