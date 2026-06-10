package academy.wakanda.wakanda_ai.gameficacao.catalogo.tipomissao.application.service;

import java.util.UUID;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.tipomissao.application.api.TipoMissaoRequest;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.tipomissao.application.api.TipoMissaoResponse;
import jakarta.validation.Valid;

public interface TipoMissaoService {
    TipoMissaoResponse insereTipoMissao(TipoMissaoRequest tipoMissaoRequest);

    TipoMissaoResponse buscaTipoMissaoId(UUID tipoMissaoId);
}
