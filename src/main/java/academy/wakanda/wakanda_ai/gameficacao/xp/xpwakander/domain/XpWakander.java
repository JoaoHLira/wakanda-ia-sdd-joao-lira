package academy.wakanda.wakanda_ai.gameficacao.xp.xpwakander.domain;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.Sabedorias;
import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.domain.ClasseWakanda;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor( access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Entity
@Table(name = "xp_wakander")
public class XpWakander {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid", name = "id_xp_wakander", nullable = false)
    private UUID idXpWakander;
    @Column(name = "id_progresso_wakander", nullable = false)
    private UUID idProgressoWakander;
    @Column(nullable = false)
    private int xpTotal;
    @Column(nullable = false)
    private int nivelAtual;
    private int xpProximoNivel;
    @Embedded
    private Sabedorias sabedorias;
    private LocalDateTime ultimaAtualizacao;

    public static XpWakander novoComDefaults(UUID idProgressoWakander) {
        XpWakander xpWakander = new XpWakander();
        xpWakander.idProgressoWakander = idProgressoWakander;
        xpWakander.xpTotal = 0;
        xpWakander.nivelAtual = 1;
        xpWakander.xpProximoNivel = xpWakander.fibonacciCalculaXpParaNivel(2);
        xpWakander.sabedorias = new Sabedorias(0, 0, 0, 0, 0);
        xpWakander.ultimaAtualizacao = LocalDateTime.now();
        return xpWakander;
    }

    public void adicionarXpEAtualizarNivel(int xpObtido, Sabedorias sabedorias) {
        adicionaXp(xpObtido);
        atualizaNivel();
        atualizaSabedoriasDoWakander(sabedorias);
    }

    private void atualizaSabedoriasDoWakander(Sabedorias sabedorias) {
        if (sabedorias != null) {
            if (this.sabedorias == null) {
                this.sabedorias = new Sabedorias(0, 0, 0, 0, 0);
            }
            this.sabedorias.atualizaSabedorias(sabedorias);
        }
    }

    public void adicionaXp(int xpGanho) {
        if (xpGanho <= 0)
            return;
        this.xpTotal += xpGanho;
        this.ultimaAtualizacao = LocalDateTime.now();
    }

    public int atualizaNivel() {
        int niveisSubidos = 0;
        while (podeSubirDeNivel()) {
            subirNivel();
            niveisSubidos++;
        }
        return niveisSubidos;
    }

    private boolean podeSubirDeNivel() {
        return xpTotal >= xpProximoNivel;
    }

    private void subirNivel() {
        xpTotal -= xpProximoNivel;
        nivelAtual++;
        xpProximoNivel = fibonacciCalculaXpParaNivel(nivelAtual + 1);
        ultimaAtualizacao = LocalDateTime.now();
    }

    private static final int FIBONACCI_MULTIPLICADOR = 35;

    private int fibonacciCalculaXpParaNivel(int nivel) {
        if (nivel == 2)
            return FIBONACCI_MULTIPLICADOR;
        int fibAnterior = 1, fibAtual = 1, fibProximo = 1;
        for (int i = 2; i < nivel; i++) {
            fibProximo = fibAnterior + fibAtual;
            fibAnterior = fibAtual;
            fibAtual = fibProximo;
        }
        return fibProximo * FIBONACCI_MULTIPLICADOR;
    }

    public boolean validaXpSabedoriasProximaClasseAtingido(ClasseWakanda proximaClasse) {
        return this.sabedorias.getTeorico() >= proximaClasse.getSabedorias().getTeorico() &&
                this.sabedorias.getProcesso() >= proximaClasse.getSabedorias().getProcesso() &&
                this.sabedorias.getKnowHow() >= proximaClasse.getSabedorias().getKnowHow() &&
                this.sabedorias.getComportamental() >= proximaClasse.getSabedorias().getComportamental() &&
                this.sabedorias.getCriativo() >= proximaClasse.getSabedorias().getCriativo();
    }
}
