package academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.application.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;


@Getter
@Builder
public class JornadaCriacaoRequest {
    @NotEmpty(message = "O titulo não pode estar vazio.")
    @Schema(description = "Titulo da Jornada", example = "Nova Jornada")
    private String titulo;

    @NotEmpty(message = "A descrição não pode estar vazio.")
    @Schema(description = "Descrição da Jornada", example = "Descrição da nova Jornada")
    private String descricao;

    @NotNull(message = "A jornada precisa de estar associada a uma trilha")
    @Schema(description = "Id da Trilha a ser associado à Jornada", example = "a330c529-c5d9-408c-b07a-e882365f23f2")
    private UUID idTrilhaWakanda;
}
