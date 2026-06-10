package academy.wakanda.wakanda_ai.gameficacao.catalogo.trilhawakanda.application.service;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.trilhawakanda.application.api.TrilhaDetalhadaProjection;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.trilhawakanda.domain.TrilhaWakanda;

import java.util.List;
import java.util.UUID;

public interface TrilhaRepository {
    TrilhaWakanda save(TrilhaWakanda trilhaCriada);
    TrilhaWakanda buscaTrilhaPorId(UUID idTrilhaWakanda);
    TrilhaDetalhadaProjection buscaTrilhaDetalhada(UUID idTrilha);
    List<TrilhaWakanda> listaTrilhas();
}
