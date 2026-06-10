package academy.wakanda.wakanda_ai.gameficacao.catalogo.trilhawakanda.application.api;

import lombok.Builder;

@Builder
public record TrilhaDetalhadaResponse(
        String nome,
        String descricao,
        Integer xpTotal,
        int totalDeJornadas
) {

    public TrilhaDetalhadaResponse(TrilhaDetalhadaProjection trilhaDetalhadaProjection) {
        this(
                trilhaDetalhadaProjection.getNome(),
                trilhaDetalhadaProjection.getDescricao(),
                trilhaDetalhadaProjection.getXpTotal(),
                trilhaDetalhadaProjection.getTotalDeJornadas()
        );
    }
}