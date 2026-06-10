package academy.wakanda.wakanda_ai.wakander.application.api;

import academy.wakanda.wakanda_ai.wakander.domain.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Schema(description = "Dados pessoais de um Wakander")
public class WakanderComDadosPessoaisOcultoResponse {
    @Schema(description = "ID único do Wakander", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID idWakander;
    @Schema(description = "Nome do Wakander", example = "João Silva dos Santos")
    private String nome;
    @Schema(description = "Data de nascimento", example = "yyyy/MM/dd")
    private LocalDate dataNascimento;
    @Schema(description = "CPF do Wakander", example = "63920752082")
    private String cpf;
    @Schema(description = "Informações de contato do Wakander", implementation = WakanderContato.class)
    private WakanderContato contato;

    public WakanderComDadosPessoaisOcultoResponse(Wakander wakander) {
        this.idWakander = wakander.getIdWakander();
        this.nome = wakander.getNome();
        this.dataNascimento = wakander.getDataNascimento();

        if(wakander.getCpf() != null)
            this.cpf = wakander.retornaCpfOculto();


        if(wakander.getContato() != null && wakander.getContato().getEmail() != null) {
            this.contato = wakander.getContato();
            this.contato.ocultaEmail();
            this.contato.ocultarWhatsapp();
        }
    }

}