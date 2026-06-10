package academy.wakanda.wakanda_ai.wakander.application.api;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.Sabedorias;
import academy.wakanda.wakanda_ai.jornadawakander.domain.JornadaWakanda;
import academy.wakanda.wakanda_ai.wakander.domain.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Schema(description = "Informações detalhadas de um Wakander")
@AllArgsConstructor
public class WakanderDetalhadoResponse {
    @Schema(description = "ID único do Wakander", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID idWakander;
    @Schema(description = "Nome do Wakander", example = "João Silva dos Santos")
    private String nome;
    @Schema(description = "Data de nascimento", example = "yyyy/MM/dd")
    private LocalDate dataNascimento;
    @Schema(description = "CPF do Wakander", example = "63920752082")
    private String cpf;
    @Schema(description = "ID do kit de membro do Wakander", example = "12345678")
    private String idMemberKit;
    @Schema(description = "Status do cadastro do Wakander", implementation = StatusCadastro.class)
    private StatusCadastro statusCadastro;
    @Schema(description = "Informações da jornada atual do Wakander", implementation = JornadaWakanda.class)
    private JornadaWakanda jornadaAtual;
    @Schema(description = "Informações de contato do Wakander", implementation = WakanderContato.class)
    private WakanderContato contato;
    @Schema(description = "Informações financeiras do Wakander", implementation = WakanderFinanceiro.class)
    private WakanderFinanceiro financeiro;
    @Schema(description = "Informações do fiador do Wakander", implementation = WakanderFiador.class)
    private WakanderFiador fiador;

    public WakanderDetalhadoResponse(Wakander wakander) {
        this.idWakander = wakander.getIdWakander();
        this.nome = wakander.getNome();
        this.dataNascimento = wakander.getDataNascimento();
        this.cpf = wakander.getCpf();
        this.idMemberKit = wakander.getIdMemberKit();
        this.statusCadastro = wakander.getStatusCadastro();
        this.jornadaAtual = wakander.getJornadaAtual();
        this.contato = wakander.getContato();
        this.financeiro = wakander.getFinanceiro();
        this.fiador = wakander.getFiador();
    }
}