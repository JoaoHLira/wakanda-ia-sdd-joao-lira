package academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.api.MissaoAlteracaoXpBaseRequest;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.api.AtualizaMissaoRequest;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.api.MissaoWakandaRequest;
import academy.wakanda.wakanda_ai.handler.APIException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "missao_wakanda")
public class MissaoWakanda {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid", name = "id_missao", nullable = false)
    private UUID idMissao;

    @Column(nullable = false, unique = true)
    @NotBlank
    private String titulo;

    @Column(nullable = false)
    @NotBlank
    private String descricao;

    @Column(nullable = false)
    private Integer xpBase;

    @Column(nullable = false)
    @NotNull
    private UUID idTipoMissao;

    @Column(nullable = false)
    @NotNull
    private UUID idJornada;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MissaoStatus missaoStatus;

    @Embedded
    private OrdemMissao ordemMissao;

    @Embedded
    private Sabedorias sabedorias;

    @Column(nullable = false)
    private String idMissaoExterna;

    @Column(name = "id_missao_pai")
    private UUID idMissaoPai;

    @Column(name = "id_classe_minima")
    private UUID idClasseMinima;

    @Column(name = "conteudo_url")
    private String conteudoUrl;

    @Column(name = "processamento_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ProcessamentoStatus processamentoStatus = ProcessamentoStatus.EM_PROCESSO;

    public MissaoWakanda(MissaoWakandaRequest missaoRequest, int posicao, MissaoWakanda missaoPai) {
        this(missaoRequest, posicao, missaoPai, true);
    }

    public MissaoWakanda(MissaoWakandaRequest missaoRequest, int posicao, MissaoWakanda missaoPai, boolean validar) {
        this.titulo = missaoRequest.getTitulo();
        this.descricao = missaoRequest.getDescricao();
        this.xpBase = missaoRequest.getXpBase();
        this.idTipoMissao = missaoRequest.getIdTipoMissao();
        this.idJornada = missaoRequest.getIdJornada();
        this.missaoStatus = MissaoStatus.ATIVA;
        this.ordemMissao = OrdemMissao.criar(posicao);
        this.idMissaoExterna = missaoRequest.getIdMissaoExterna();
        this.sabedorias = Sabedorias.ofNullable(missaoRequest.getSabedorias());
        this.idMissaoPai = missaoRequest.getIdMissaoPai();
        this.conteudoUrl = missaoRequest.getConteudoUrl();
        this.idClasseMinima = missaoRequest.getIdClasseMinima();

        if (validar) {
            validaXPBase();
            validaSabedorias();
        }
    }

    public static List<UUID> retornaIdsMissoes(List<MissaoWakanda> missoesWakanda) {
        return missoesWakanda.stream().map(MissaoWakanda::getIdMissao).toList();
    }

    public void validaSabedorias() {
        validaSabedorias(this.sabedorias);
    }

    public void validaSabedorias(Sabedorias sabedorias) {
        if (sabedorias == null || sabedorias.isEmpty()) {
            throw APIException.build(HttpStatus.BAD_REQUEST, "Informe ao menos um tipo de sabedoria!");
        }
    }

    private void validaMissaoDesativada() {
        if (MissaoStatus.INATIVA.equals(missaoStatus)) {
            throw APIException.build(HttpStatus.CONFLICT, "Esta missão já está desativada.");
        }
    }

    public void desativaMissao() {
        validaMissaoDesativada();
        this.missaoStatus = MissaoStatus.INATIVA;
    }

    public void alteraXpBase(MissaoAlteracaoXpBaseRequest request) {
        validaMissaoAtiva();
        validaNovoXpBase(request.getXpBase());
        this.xpBase = request.getXpBase();
    }

    private void validaNovoXpBase(Integer novoXpBase) {
        if (novoXpBase.equals(this.xpBase)) {
            throw APIException.build(HttpStatus.BAD_REQUEST, "O novo XPBase deve ser diferente do valor atual.");
        }
    }

    private void validaMissaoAtiva() {
        if (MissaoStatus.INATIVA.equals(this.missaoStatus)) {
            throw APIException.build(HttpStatus.BAD_REQUEST, "Esta missão está desativada.");
        }
    }

    private void validaXPBase() {
        validaXPBase(this.xpBase);
    }

    private void validaXPBase(Integer xpBase) {
        if (xpBase == null || xpBase <= 0) {
            throw APIException.build(HttpStatus.BAD_REQUEST, "XPBase deve ser informado e maior que zero!");
        }
    }

    public void validaXpBaseAlteracao(MissaoAlteracaoXpBaseRequest missaoAlteracaoXpBase) {
        Integer novoXpBase = missaoAlteracaoXpBase.getXpBase();
        if (novoXpBase == null || novoXpBase <= 0 || novoXpBase.equals(this.xpBase)) {
            throw APIException.build(HttpStatus.BAD_REQUEST,
                    novoXpBase == null || novoXpBase <= 0 ? "O XPBase deve ser maior que zero."
                            : "O novo XPBase deve ser diferente do valor atual.");
        }
    }

    public void alterarOrdem(OrdemMissao novaOrdem) {
        this.ordemMissao = novaOrdem;
    }

    public void atualizaMissaoComDadosIA(AtualizaMissaoRequest request) {
        validaMissaoAtiva();
        validaXPBase(request.getXpBase());
        validaProcessamentoEmAndamento();
        validaSabedorias(request.getSabedorias());
        validaDescricao(request.getDescricao());
        this.xpBase = request.getXpBase();
        this.sabedorias = request.getSabedorias();
        this.descricao = request.getDescricao();
        this.idClasseMinima = request.getIdClasseMinima();
        mudaStatusProcessamentoIA(ProcessamentoStatus.COMPLETO);
    }

    public void atualizaConteudoUrl(String conteudoUrl) {
        this.conteudoUrl = conteudoUrl;
    }

    private void mudaStatusProcessamentoIA(ProcessamentoStatus processamentoStatus) {
        this.processamentoStatus = processamentoStatus;
        this.missaoStatus = MissaoStatus.ATIVA;
    }

    private void validaProcessamentoEmAndamento() {
        if (!ProcessamentoStatus.EM_PROCESSO.equals(processamentoStatus)) {
            throw APIException.build(HttpStatus.BAD_REQUEST,
                    "Missão já foi processada por IA");
        }
    }

    private void validaDescricao(String descricao) {
        if (descricao == null) {
            throw APIException.build(HttpStatus.BAD_REQUEST, "A descricao nao pode ser nula.");
        }
    }

    public boolean estaAtiva() {
        return MissaoStatus.ATIVA.equals(this.missaoStatus);
    }

    public boolean possuiClasseMinimaDefinida() {
        return this.idClasseMinima != null;
    }
}
