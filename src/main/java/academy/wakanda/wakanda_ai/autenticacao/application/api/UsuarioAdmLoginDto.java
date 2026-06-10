package academy.wakanda.wakanda_ai.autenticacao.application.api;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class UsuarioAdmLoginDto {
    @NotBlank(message = "Insira o nome de usuario!")
    private String username;
    @NotBlank(message = "Insira a senha!")
    private String senha;
}


