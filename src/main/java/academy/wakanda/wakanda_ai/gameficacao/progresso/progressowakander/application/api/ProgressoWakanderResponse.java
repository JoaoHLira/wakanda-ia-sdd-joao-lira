package academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.api;

import java.time.LocalDateTime;
import java.util.UUID;

import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.domain.ProgressoWakander;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ProgressoWakanderResponse {

    @Schema(description = "Id do progresso", example = "a330c529-c5d9-408c-b07a-e882365f23f2")
    private UUID idProgressoWakander;

    @Schema(description = "Id do Wakander", example = "a330c529-c5d9-408c-b07a-e882365f23f2")
    private UUID idWakander;

    @Schema(example = "2025-10-08")
    private LocalDateTime dataCriacao;

    public ProgressoWakanderResponse(ProgressoWakander progressoWakander) {
        this.idProgressoWakander = progressoWakander.getIdProgressoWakander();
        this.idWakander = progressoWakander.getIdWakander();
        this.dataCriacao = progressoWakander.getDataCriacao();
    }
}
