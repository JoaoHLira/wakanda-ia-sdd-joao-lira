package academy.wakanda.wakanda_ai.gameficacao.catalogo.tipomissao.application.service;

import java.util.UUID;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.tipomissao.domain.TipoMissao;

public interface TipoMissaoRepository {
    TipoMissao save(TipoMissao tipoMissao);

    TipoMissao buscaTipoMissaoId(UUID idTipoMissao);

    TipoMissao buscaTipoMissaoPorDescricao(String tipoMissaoWakanda);
}
