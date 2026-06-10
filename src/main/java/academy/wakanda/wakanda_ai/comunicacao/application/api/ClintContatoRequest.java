package academy.wakanda.wakanda_ai.comunicacao.application.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@AllArgsConstructor
public class ClintContatoRequest {
    private String idWakander;
    private String nome;
    private String cpf;
    private String whatsapp;
    private String email;
    private String dataNascimento;
    private String nomeFiador;
    private String statusContatoClint;

    public void formataDataNascimento(String dataNascimento) {
        this.dataNascimento = LocalDate.parse(dataNascimento).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}
