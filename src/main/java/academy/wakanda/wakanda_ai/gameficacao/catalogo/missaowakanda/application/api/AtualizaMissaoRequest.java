package academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.api;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.Sabedorias;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.util.UUID;

@Value
@Schema(description = "Resultado do processamento da missao pelo Agente de IA")
public class AtualizaMissaoRequest {

    @NotNull
    @Schema(description = "XP definido pela IA", example = "120")
    Integer xpBase;

    @NotNull
    @Schema(description = "Sabedorias identificadas pela IA")
    Sabedorias sabedorias;

    @Schema(description = "Observacoes adicionais do processamento")
    String descricao;

    @Schema(description = "ID da classe mínima da missão")
    UUID idClasseMinima;
}
