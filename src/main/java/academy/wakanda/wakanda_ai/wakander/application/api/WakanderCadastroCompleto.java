package academy.wakanda.wakanda_ai.wakander.application.api;

import java.time.LocalDate;
import java.util.UUID;

import org.hibernate.validator.constraints.br.CPF;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class WakanderCadastroCompleto {
    private String nomeFiador;
    private UUID idWakander;
    private String nome;
    @CPF(message = "O CPF informado é inválido.")
    private String cpf;
    private LocalDate dataNascimento;
    private String whatsapp;
    @Email(message = "O email deve ser válido.")
    private String email;
}