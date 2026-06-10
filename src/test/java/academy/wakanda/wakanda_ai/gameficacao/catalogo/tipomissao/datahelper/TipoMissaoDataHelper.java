package academy.wakanda.wakanda_ai.gameficacao.catalogo.tipomissao.datahelper;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.tipomissao.application.api.TipoMissaoRequest;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.tipomissao.application.api.TipoMissaoResponse;

import java.util.UUID;


public class TipoMissaoDataHelper {

    public static TipoMissaoRequest criarTipoMissaoRequestValido() {
        return TipoMissaoRequest.builder()
                .descricao("Tipo Missão XPTO")
                .build();
    }

    public static TipoMissaoRequest criarTipoMissaoRequestSemDescricao() {
        return TipoMissaoRequest.builder()
                .descricao(null)
                .build();
    }

    public static TipoMissaoResponse criarTipoMissaoResponseValido() {
        return TipoMissaoResponse.builder()
                .idTipoMissao(UUID.randomUUID())
                .descricao("Tipo Missão XPTO")
                .build();
    }
}
