package academy.wakanda.wakanda_ai.gameficacao.catalogo.tipomissao.application.api;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.tipomissao.domain.TipoMissao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class TipoMissaoResponse {
    private UUID idTipoMissao;
    private String descricao;

    public TipoMissaoResponse(TipoMissao tipoMissaoCriada) {
        this.idTipoMissao = tipoMissaoCriada.getIdTipoMissao();
        this.descricao = tipoMissaoCriada.getDescricao();
    }
}
