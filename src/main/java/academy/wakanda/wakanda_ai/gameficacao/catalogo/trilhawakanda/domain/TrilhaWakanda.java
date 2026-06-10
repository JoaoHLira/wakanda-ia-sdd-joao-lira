package academy.wakanda.wakanda_ai.gameficacao.catalogo.trilhawakanda.domain;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.domain.StatusJornada;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.TipoRecalculo;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.trilhawakanda.application.api.TrilhaWakandaRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.UUID;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "trilha_wakanda")
public class TrilhaWakanda {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid", name = "id_trilha")
    private UUID idTrilha;
    @Column(name = "nome", unique = true)
    private String nome;
    private String descricao;
    private Integer xpTotal;

    public TrilhaWakanda(TrilhaWakandaRequest novaTrilha) {
        this.nome = novaTrilha.getNome();
        this.descricao = novaTrilha.getDescricao();
        this.xpTotal = 0;
    }

    public TrilhaWakanda(UUID idTrilha, String nome, String descricao) {
        this.idTrilha = idTrilha;
        this.nome = nome;
        this.descricao = descricao;
        this.xpTotal = 0;
    }

    public void atualizaXp(Integer xpMissao) {
        if (xpTotal > 0) {
            this.xpTotal -= xpMissao;
        }
    }

    public void recalculaXpTrilha(TipoRecalculo tipo, Integer xpAntigoJornada, Integer xpNovoJornada) {
        if (tipo == TipoRecalculo.XP_ALTERADO && !Objects.equals(xpAntigoJornada, xpNovoJornada)) {
            this.xpTotal = (this.xpTotal - xpAntigoJornada) + xpNovoJornada;
        } else if (tipo == TipoRecalculo.MISSAO_ADICIONADA) {
            this.xpTotal += (xpNovoJornada - xpAntigoJornada);
        }
    }
}