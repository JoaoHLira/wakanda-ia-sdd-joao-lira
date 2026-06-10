package academy.wakanda.wakanda_ai.wakander.application.api;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.br.CPF;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Deprecated
@AllArgsConstructor
@Schema(description = "Informações para criação de um novo Wakander")
public class WakanderNovoRequest {

    @NotEmpty(message = "O nome não pode estar vazio.")
    @Schema(description = "Nome do Wakander", example = "Nome do Wakander")
    private String nome;

    @CPF(message = "O CPF informado é inválido.")
    @NotEmpty(message = "O CPF não pode estar vazio.")
    @Pattern(regexp = "^\\d{11}$", message = "O CPF deve ter 11 dígitos numéricos, sem espaços, pontos ou hífen, apenas os números.")
    @Schema(description = "CPF do Wakander", example = "12345678912")
    private String cpf;

    @NotNull(message = "Data de nascimento não deve estar vazia.")
    @Schema(description = "Data de Nascimento", example = "yyyy/MM/dd")
    private LocalDate dataNascimento;

    @NotEmpty(message = "O ID do Kit de Membro não pode estar vazio.")
    @Schema(description = "ID do kit de membro", example = "12345678")
    private String idMemberKit;

    @NotEmpty(message = "O número de WhatsApp não pode estar vazio.")
    @Pattern(regexp = "^\\d{13,15}$", message = "O número de WhatsApp deve ter entre 13 e 15 dígitos numéricos, sem espaços, hífen ou sinal de mais, apenas os números.")
    @Schema(description = "Número de WhatsApp", example = "5573900000000")
    private String whatsapp;

    @NotEmpty(message = "O email não pode estar vazio.")
    @Email(message = "O email informado é inválido.")
    @Schema(description = "Email do Wakander", example = "exemplo@wakanda.com")
    private String email;

    @NotEmpty(message = "O ID do fiador no Asaas não pode estar vazio.")
    @Schema(description = "ID do fiador no Asaas", example = "cus_G7Dvo4iphUNk")
    private String idAsaasFiador;

    @NotEmpty(message = "O ID da assinatura do fiador no Asaas não pode estar vazio.")
    @Schema(description = "ID da assinatura do fiador no Asaas", example = "sub_VXJBYgP2u0eO")
    private String idAssinaturaFiador;

    @NotEmpty(message = "O nome do fiador não pode estar vazio.")
    @Schema(description = "Nome do fiador", example = "Maria Silva dos Santos")
    private String nomeFiador;

    @CPF(message = "O CPF do fiador informado é inválido.")
    @NotEmpty(message = "O CPF do fiador não pode estar vazio.")
    @Pattern(regexp = "^\\d{11}$", message = "O CPF do fiador deve ter 11 dígitos numéricos, sem espaços, pontos ou hífen, apenas os números.")
    @Schema(description = "CPF do fiador", example = "012345678901")
    private String cpfFiador;

    @NotEmpty(message = "O número de telefone do fiador não pode estar vazio.")
    @Pattern(regexp = "^\\d{13,15}$", message = "O número de telefone do fiador deve ter entre 13 e 15 dígitos numéricos, sem espaços, hífen ou sinal de mais, apenas os números.")
    @Schema(description = "Número de telefone do fiador", example = "5573900000000")
    private String telefoneFiador;
}
