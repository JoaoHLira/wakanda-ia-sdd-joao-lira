package academy.wakanda.wakanda_ai.wakander.application.api;

import academy.wakanda.wakanda_ai.wakander.domain.StatusCadastro;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import academy.wakanda.wakanda_ai.wakander.domain.WakanderStatusFinanceiro;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.UUID;

@Getter
@Schema(description = "Dados resumidos do Wakander para listagem paginada")
public class WakanderPaginadoResponse {

    @Schema(description = "ID único do Wakander", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID idWakander;

    @Schema(description = "Nome do Wakander", example = "João Silva")
    private String nome;

    @Schema(description = "CPF do Wakander", example = "63920752082")
    private String cpf;

    @Schema(description = "E-mail do Wakander", example = "joao@email.com")
    private String email;

    @Schema(description = "Telefone do Wakander", example = "71999999999")
    private String telefone;

    @Schema(description = "Status do cadastro do Wakander")
    private StatusCadastro statusCadastro;

    @Schema(description = "Status financeiro do Wakander")
    private WakanderStatusFinanceiro statusFinanceiro;

    public WakanderPaginadoResponse(Wakander wakander) {
        this.idWakander = wakander.getIdWakander();
        this.nome = wakander.getNome();
        this.cpf = wakander.getCpf();
        this.email = wakander.getContato() != null ? wakander.getContato().getEmail() : null;
        this.telefone = wakander.getContato() != null ? wakander.getContato().getWhatsapp() : null;
        this.statusCadastro = wakander.getStatusCadastro();
        this.statusFinanceiro = wakander.getFinanceiro() != null
                ? wakander.getFinanceiro().getStatus()
                : null;
    }
}
