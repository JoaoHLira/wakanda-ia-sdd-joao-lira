package academy.wakanda.wakanda_ai.gameficacao.catalogo.trilhawakanda.application.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TrilhaWakandaRequest {

    @NotEmpty(message = "O nome não pode estar vazio.")
    @Schema(description = "Nome da Trilha", example = "Profissão Programador")
    private String nome;
    @NotEmpty(message = "A descrição não pode estar vazio.")
    @Schema(description = "Descrição da Trilha", example = "Trilha de Formação em Desenvolvimento Back-End")
    private String descricao;
    private Integer xpTotal;
}
