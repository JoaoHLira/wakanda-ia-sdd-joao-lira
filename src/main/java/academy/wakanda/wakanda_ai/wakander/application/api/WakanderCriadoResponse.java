package academy.wakanda.wakanda_ai.wakander.application.api;

import java.util.UUID;

import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "Response do Wakander criado")
public class WakanderCriadoResponse {
    @Schema(description = "ID único do Wakander criado", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID idWakander;

    public WakanderCriadoResponse(Wakander wakanderCriado) {
        idWakander = wakanderCriado.getIdWakander();
    }
}