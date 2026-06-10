package academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.api;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

@Value
@Schema(description = "Response da missão criada")
public class MissaoWakandaResponse {

    @Schema(description = "ID único da missão criada", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID idMissao;
}
