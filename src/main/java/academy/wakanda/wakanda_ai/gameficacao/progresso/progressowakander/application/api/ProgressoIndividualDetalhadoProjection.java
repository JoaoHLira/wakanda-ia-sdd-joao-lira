package academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.api;

import java.time.LocalDateTime;

public interface ProgressoIndividualDetalhadoProjection {
    String getNome();
    int getNivelAtual();
    int getXpTotal();
    int getXpAtual();
    String getTitulo();
    LocalDateTime getUltimaAtualizacao();
    int getXpProximoNivel();
    Integer getSabTeorico();
    Integer getSabProcesso();
    Integer getSabKnowHow();
    Integer getSabComportamental();
    Integer getSabCriativo();
}