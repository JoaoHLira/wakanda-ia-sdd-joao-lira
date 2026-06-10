package academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.application.api;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.domain.JornadaWakanda;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.domain.StatusJornada;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
public class JornadaListResponse {
    @Schema(description = "Id da Jornada", example = "a330c529-c5d9-408c-b07a-e882365f23f2")
    private UUID idJornada;

    @Schema(description = "Id da Trilha", example = "a440c529-c5d9-408c-b07a-e882365f23f2")
    private UUID idTrilhaWakanda;

    @Schema(description = "XP total da Jornada", example = "500")
    private Integer xpTotal;

    @Schema(description = "Titulo da Jornada", example = "Nova Jornada")
    private String titulo;

    @Schema(description = "Descrição da Jornada", example = "Descrição da nova Jornada")
    private String descricao;

    @Schema
    private StatusJornada statusJornada;

    private int xpBonus;

    @Schema(description = "Posicao da Jornada", example = "0")
    private int ordemJornada;

    public static List<JornadaListResponse> converte(List<JornadaWakanda> jornadas) {
        return jornadas.stream()
                .map(JornadaListResponse::new)
                .collect(Collectors.toList());
    }

    public JornadaListResponse(JornadaWakanda jornadaWakanda) {
        this.idJornada = jornadaWakanda.getIdJornada();
        this.idTrilhaWakanda = jornadaWakanda.getIdTrilhaWakanda();
        this.titulo = jornadaWakanda.getTitulo();
        this.descricao = jornadaWakanda.getDescricao();
        this.xpTotal = jornadaWakanda.getXpTotal();
        this.idTrilhaWakanda = jornadaWakanda.getIdTrilhaWakanda();
        this.statusJornada = jornadaWakanda.getStatusJornada();
        this.xpBonus = jornadaWakanda.getXpBonus();
        this.ordemJornada = jornadaWakanda.getOrdemJornada();
    }

}
