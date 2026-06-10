package academy.wakanda.wakanda_ai.gameficacao.catalogo.trilhawakanda.application.service;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.TipoRecalculo;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.trilhawakanda.application.api.TrilhaCriadaResponse;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.trilhawakanda.application.api.TrilhaDetalhadaResponse;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.trilhawakanda.application.api.TrilhaListResponse;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.trilhawakanda.application.api.TrilhaWakandaRequest;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.trilhawakanda.domain.TrilhaWakanda;

import java.util.List;
import java.util.UUID;

public interface TrilhaService {
    TrilhaCriadaResponse criaTrilha(TrilhaWakandaRequest novaTrilha);
    TrilhaWakanda buscaTrilhaPorId(UUID idTrilhaWakanda);
    TrilhaWakanda salvaTrilha(TrilhaWakanda trilha);
    void recalculaXpTrilha(UUID idTrilhaWakanda, Integer xpMissao);
    void recalculaXpTotalTrilha(UUID idTrilhaWakanda, TipoRecalculo tipoRecalculo, Integer xpAntigoJornada, Integer xpNovoJornada);
    TrilhaDetalhadaResponse buscaTrilhaDetalhada(UUID idTrilha);
    List<TrilhaListResponse> listaTrilhas();
}
