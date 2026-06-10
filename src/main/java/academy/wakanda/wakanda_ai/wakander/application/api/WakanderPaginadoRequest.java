package academy.wakanda.wakanda_ai.wakander.application.api;

import academy.wakanda.wakanda_ai.wakander.domain.StatusCadastro;
import academy.wakanda.wakanda_ai.wakander.domain.WakanderStatusFinanceiro;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Critérios de filtro para listagem de Wakanders")
public class WakanderPaginadoRequest {

    @Schema(description = "Busca parcial por nome (case insensitive)", example = "joão")
    private String nome;

    @Schema(description = "Busca exata por CPF", example = "63920752082")
    private String cpf;

    @Schema(description = "Status financeiro do Wakander")
    private WakanderStatusFinanceiro statusFinanceiro;

    @Schema(description = "Status do cadastro do Wakander")
    private StatusCadastro statusCadastro;

    @Schema(description = "Busca exata por email", example = "joao@email.com")
    private String email;

    @Schema(description = "Busca exata por telefone", example = "71999999999")
    private String telefone;
}
