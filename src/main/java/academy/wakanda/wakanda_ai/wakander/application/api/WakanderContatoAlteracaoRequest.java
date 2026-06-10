package academy.wakanda.wakanda_ai.wakander.application.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WakanderContatoAlteracaoRequest {

    @NotEmpty
    private String whatsapp;

    @Email(message = "O email deve ser válido.")
    private String email;
}
