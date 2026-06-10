package academy.wakanda.wakanda_ai.financeiro.application.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.br.CPF;

@Getter
@Builder
public class FiadorDTO {

    @NotBlank(message = "O nome é obrigatório.")
    @JsonProperty("name")
    private String nome;

    @CPF(message = "O CPF informado é inválido.")
    @JsonProperty("cpfCnpj")
    private String cpf;

    @Email(message = "O e-mail é inválido.")
    @JsonProperty("email")
    private String email;

    @JsonProperty("phone")
    private String telefoneFixo;

    @NotBlank(message = "O telefone celular é obrigatório.")
    @JsonProperty("mobilePhone")
    private String whatsapp;

    @JsonProperty("address")
    private String endereco;

    @JsonProperty("addressNumber")
    private String numeroEndereco;

    @JsonProperty("complement")
    private String complemento;

    @JsonProperty("province")
    private String bairro;

    @JsonProperty("postalCode")
    private String cep;

    @JsonProperty("externalReference")
    private String idWakander;

    @JsonProperty("notificationDisabled")
    private Boolean notificacaoCobranca;
}