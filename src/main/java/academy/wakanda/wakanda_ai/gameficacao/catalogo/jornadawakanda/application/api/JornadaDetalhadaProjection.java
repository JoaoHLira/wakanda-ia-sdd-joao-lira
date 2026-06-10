package academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.application.api;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.domain.StatusJornada;

public interface JornadaDetalhadaProjection {
    String getTitulo();
    String getDescricao();
    Integer getXpTotal();
    StatusJornada getStatusJornada();
    int getXpBonus();
    int getTotalDeMissoes();
}
