package academy.wakanda.wakanda_ai.wakander.application.event;

import academy.wakanda.wakanda_ai.wakander.domain.StatusContatoClint;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CadastroCompletoEvent {
    
    private UUID idWakander;
    private String nome;
    private String cpf;
    private String whatsapp;
    private String email;
    private LocalDate dataNascimento;
    private String nomeFiador;
    private StatusContatoClint statusContatoClint;

    public CadastroCompletoEvent(Wakander wakanderCadastro) {
        this.idWakander = wakanderCadastro.getIdWakander();
        this.nome = wakanderCadastro.getNome();
        this.cpf = wakanderCadastro.getCpf();
        this.whatsapp = wakanderCadastro.getContato().getWhatsapp();
        this.email = wakanderCadastro.getContato().getEmail();
        this.dataNascimento = wakanderCadastro.getDataNascimento();
        this.nomeFiador = wakanderCadastro.getFiador().getNome();
        this.statusContatoClint = StatusContatoClint.FORMULARIO_PREENCHIDO;
    }
}
