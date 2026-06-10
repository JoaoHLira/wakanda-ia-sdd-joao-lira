package academy.wakanda.wakanda_ai.wakander.application.api;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.br.CPF;

@Getter
@Builder
public class WakanderAlteracaoRequest {

    @NotEmpty(message = "O nome não pode estar vazio.")
    private String nome;

    @CPF(message = "O CPF informado é inválido.")
    private String cpf;

    private WakanderContatoAlteracaoRequest contato;
}
