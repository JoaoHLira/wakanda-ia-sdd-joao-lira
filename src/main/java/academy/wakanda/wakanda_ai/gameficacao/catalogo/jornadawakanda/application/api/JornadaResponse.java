package academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.application.api;


import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.domain.JornadaWakanda;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.domain.StatusJornada;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.trilhawakanda.domain.TrilhaWakanda;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;


@Getter
@Builder
@AllArgsConstructor
public class JornadaResponse {
    @Schema(description = "Id da Jornada", example = "a330c529-c5d9-408c-b07a-e882365f23f2")
    private UUID idJornada;

    @Schema(description = "XP total da Jornada", example = "500")
    private Integer xpTotal;

    @Schema(description = "Titulo da Jornada", example = "Nova Jornada")
    private String titulo;

    @Schema(description = "Descrição da Jornada", example = "Descrição da nova Jornada")
    private String descricao;

    @Schema(description = "Trilha a qual a Jornada está associada", example = "{\"idTrilha\":\"a330c529-c5d9-408c-b07a-e882365f23f2\",\"nome\":\"Trilha Backend Java\",\"descricao\":\"Trilha focada em desenvolvimento backend com Java e Spring Boot.\",\"xpTotal\":1200}")
    private TrilhaWakanda idTrilhaWakanda;

    @Schema(description = "Status da Jornada", example = "ATIVA")
    private StatusJornada statusJornada;

    @Schema(description = "XP Bônus da Jornada", example = "100")
    private int xpBonus;

    @Schema(description = "Posição da Jornada", example = "1")
    private int ordemJornada;

    public JornadaResponse(JornadaWakanda jornadaWakanda, TrilhaWakanda trilhaWakanda, int posicaoJornada) {
        this.idJornada = jornadaWakanda.getIdJornada();
        this.titulo = jornadaWakanda.getTitulo();
        this.descricao = jornadaWakanda.getDescricao();
        this.xpTotal = jornadaWakanda.getXpTotal();
        this.idTrilhaWakanda = trilhaWakanda;
        this.statusJornada = jornadaWakanda.getStatusJornada();
        this.xpBonus = jornadaWakanda.getXpBonus();
        this.ordemJornada = posicaoJornada;
    }

    public JornadaResponse(JornadaWakanda jornadaWakanda) {
        this.idJornada = jornadaWakanda.getIdJornada();
        this.statusJornada = jornadaWakanda.getStatusJornada();
    }
}


