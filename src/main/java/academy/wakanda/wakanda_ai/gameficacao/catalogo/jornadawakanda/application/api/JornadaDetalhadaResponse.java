package academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.application.api;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.domain.StatusJornada;
import lombok.Builder;

@Builder
public record JornadaDetalhadaResponse(
        String titulo,
        String descricao,
        Integer xpTotal,
        StatusJornada statusJornada,
        int xpBonus,
        int totalDeMissoes
) {

    public JornadaDetalhadaResponse(JornadaDetalhadaProjection jornadaDetalhadaProjection) {
        this(
                jornadaDetalhadaProjection.getTitulo(),
                jornadaDetalhadaProjection.getDescricao(),
                jornadaDetalhadaProjection.getXpTotal(),
                jornadaDetalhadaProjection.getStatusJornada(),
                jornadaDetalhadaProjection.getXpBonus(),
                jornadaDetalhadaProjection.getTotalDeMissoes()
        );
    }
}
