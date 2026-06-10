package academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.domain;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.application.api.JornadaCriacaoRequest;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.TipoRecalculo;
import academy.wakanda.wakanda_ai.handler.APIException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Objects;
import java.util.UUID;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "jornada_wakanda")
public class JornadaWakanda {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid", name = "id_jornada")
    private UUID idJornada;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false)
    private Integer xpTotal;

    @Column(nullable = false)
    private UUID idTrilhaWakanda;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusJornada statusJornada;

    @Column(nullable = false)
    private int xpBonus;

    @Column(nullable = false)
    private int ordemJornada;

    public JornadaWakanda(JornadaCriacaoRequest jornadaCriacaoRequest, int posicaoJornada ) {
        this.titulo = jornadaCriacaoRequest.getTitulo();
        this.descricao = jornadaCriacaoRequest.getDescricao();
        this.idTrilhaWakanda = jornadaCriacaoRequest.getIdTrilhaWakanda();
        this.xpTotal = 0;
        this.statusJornada = StatusJornada.ATIVA;
        this.ordemJornada = posicaoJornada;
    }

    public void subtraiXpMissao(Integer xpMissao) {
        this.xpTotal -= xpMissao;
    }

    public void recalculaXpJornada(TipoRecalculo tipo, Integer xpAntigoMissao, Integer novoXpBaseMissao) {
        validaJornadaAtiva();
        if (tipo == TipoRecalculo.XP_ALTERADO && !Objects.equals(xpAntigoMissao, novoXpBaseMissao)) {
            this.xpTotal = (this.xpTotal - xpAntigoMissao) + novoXpBaseMissao;
        } else if (tipo == TipoRecalculo.MISSAO_ADICIONADA) {
            this.xpTotal += novoXpBaseMissao;
        }
    }

    private void validaJornadaAtiva() {
        if (this.statusJornada != StatusJornada.ATIVA) {
            throw APIException.build(HttpStatus.BAD_REQUEST, "A Jornada não está ativa!");
        }
    }

}