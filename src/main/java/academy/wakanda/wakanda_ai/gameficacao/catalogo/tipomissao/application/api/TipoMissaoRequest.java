package academy.wakanda.wakanda_ai.gameficacao.catalogo.tipomissao.application.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoMissaoRequest {

    @Schema(description = "Descrição do Tipo de Missão", example = "Tipo de Missão XPTO")
    @NotBlank(message = "A descrição do Tipo de Missão não pode ser nula ou vazia.")
    private String descricao;
}
