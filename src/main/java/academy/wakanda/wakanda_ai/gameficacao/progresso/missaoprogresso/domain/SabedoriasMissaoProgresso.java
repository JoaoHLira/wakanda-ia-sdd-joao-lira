package academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.domain;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.Sabedorias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SabedoriasMissaoProgresso {

    @Column(name = "sab_teorico", nullable = false)
    private Integer teorico;

    @Column(name = "sab_processo", nullable = false)
    private Integer processo;

    @Column(name = "sab_know_how", nullable = false)
    private Integer knowHow;

    @Column(name = "sab_comportamental", nullable = false)
    private Integer comportamental;

    @Column(name = "sab_criativo", nullable = false)
    private Integer criativo;

    public SabedoriasMissaoProgresso(int valorTeorico, int valorProcesso, int valorKnowHow,
                                     int valorComportamental, int valorCriativo) {
        this.teorico = valorTeorico;
        this.processo = valorProcesso;
        this.knowHow = valorKnowHow;
        this.comportamental = valorComportamental;
        this.criativo = valorCriativo;
    }

    public static SabedoriasMissaoProgresso ofNullable(SabedoriasMissaoProgresso sabedorias) {
        if (sabedorias == null) {
            return new SabedoriasMissaoProgresso(0, 0, 0, 0, 0);
        }

        return new SabedoriasMissaoProgresso(
                Objects.requireNonNullElse(sabedorias.getTeorico(), 0),
                Objects.requireNonNullElse(sabedorias.getProcesso(), 0),
                Objects.requireNonNullElse(sabedorias.getKnowHow(), 0),
                Objects.requireNonNullElse(sabedorias.getComportamental(), 0),
                Objects.requireNonNullElse(sabedorias.getCriativo(), 0)
        );
    }

    public static SabedoriasMissaoProgresso fromCatalogo(Sabedorias sabedorias) {
        if (sabedorias == null) {
            return new SabedoriasMissaoProgresso(0, 0, 0, 0, 0);
        }

        return new SabedoriasMissaoProgresso(
                Objects.requireNonNullElse(sabedorias.getTeorico(), 0),
                Objects.requireNonNullElse(sabedorias.getProcesso(), 0),
                Objects.requireNonNullElse(sabedorias.getKnowHow(), 0),
                Objects.requireNonNullElse(sabedorias.getComportamental(), 0),
                Objects.requireNonNullElse(sabedorias.getCriativo(), 0)
        );
    }

    public Sabedorias toCatalogo() {
        return new Sabedorias(
                Objects.requireNonNullElse(teorico, 0),
                Objects.requireNonNullElse(processo, 0),
                Objects.requireNonNullElse(knowHow, 0),
                Objects.requireNonNullElse(comportamental, 0),
                Objects.requireNonNullElse(criativo, 0)
        );
    }

    @JsonIgnore
    public boolean isEmpty() {
        return (teorico == null || teorico == 0)
                && (processo == null || processo == 0)
                && (knowHow == null || knowHow == 0)
                && (comportamental == null || comportamental == 0)
                && (criativo == null || criativo == 0);
    }
}
