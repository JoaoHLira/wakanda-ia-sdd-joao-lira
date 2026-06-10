package academy.wakanda.wakanda_ai.autenticacao.application.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioAdmCadastroRequest {

    @NotBlank(message = "Informe o nome.")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres.")
    private String nome;

    @NotBlank(message = "Informe o nome de usuário.")
    @Size(max = 50, message = "Nome de usuário deve ter no máximo 50 caracteres.")
    private String username;

    @NotBlank(message = "Informe a senha.")
    @Size(min = 6, max = 255, message = "A senha deve ter entre 6 e 255 caracteres.")
    private String senha;
}
