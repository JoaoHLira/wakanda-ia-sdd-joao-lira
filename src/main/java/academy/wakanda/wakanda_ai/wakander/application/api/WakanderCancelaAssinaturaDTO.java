package academy.wakanda.wakanda_ai.wakander.application.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class WakanderCancelaAssinaturaDTO {

    @NotBlank(message = "O motivo do cancelamento é obrigatório.")
    private String motivoCancelamento;

    @NotNull(message = "A data do cancelamento é obrigatória.")
    private LocalDate dataCancelamento;
}
