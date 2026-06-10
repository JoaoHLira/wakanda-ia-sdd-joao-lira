package academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.api;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.MissaoStatus;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.MissaoWakanda;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.Sabedorias;
import lombok.Value;

import java.util.UUID;

@Value
public class MissaoWakandaDetalhadoResponse {

    private UUID idMissao;

    private String titulo;

    private String descricao;

    private Integer xpBase;

    private UUID idTipoMissao;

    private UUID idJornada;

    private MissaoStatus missaoStatus;

    private int ordemMissao;

    private Sabedorias sabedorias;

    private String idMissaoExterna;

    private UUID idMissaoPai;

    public MissaoWakandaDetalhadoResponse(MissaoWakanda missao) {
        this.idMissao = missao.getIdMissao();
        this.ordemMissao = missao.getOrdemMissao().getOrdem();
        this.sabedorias = missao.getSabedorias();
        this.titulo = missao.getTitulo();
        this.descricao = missao.getDescricao();
        this.xpBase = missao.getXpBase();
        this.missaoStatus = missao.getMissaoStatus();
        this.idTipoMissao = missao.getIdTipoMissao();
        this.idJornada = missao.getIdJornada();
        this.idMissaoExterna = missao.getIdMissaoExterna();
        this.idMissaoPai = missao.getIdMissaoPai();
    }

}
