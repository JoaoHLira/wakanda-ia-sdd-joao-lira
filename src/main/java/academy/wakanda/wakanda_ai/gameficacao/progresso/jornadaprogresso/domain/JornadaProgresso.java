package academy.wakanda.wakanda_ai.gameficacao.progresso.jornadaprogresso.domain;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.domain.JornadaWakanda;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "jornada_progresso")
public class JornadaProgresso {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid", name = "id_jornada_progresso")
    private UUID idJornadaProgresso;
    @Column(name = "id_jornada_wakanda")
    private UUID idJornadaWakanda;
    @Column(name = "id_progresso_wakander")
    private UUID idProgressoWakander;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private JornadaProgressoStatus status;
    @Column(name = "xp_obtido")
    private int xpObtido;
    @Column(name = "data_inicio")
    private LocalDateTime dataInicio;
    @Column(name = "ultima_atualizacao")
    private LocalDateTime ultimaAtualizacao;

    public JornadaProgresso(UUID idJornadaWakanda, UUID idProgresso) {
        this.idJornadaWakanda = idJornadaWakanda;
        this.idProgressoWakander = idProgresso;
        this.status = JornadaProgressoStatus.EM_ANDAMENTO;
        this.dataInicio = LocalDateTime.now();
        this.ultimaAtualizacao = LocalDateTime.now();
    }

    public void concluiJornada(JornadaWakanda jornadaWakanda) {
        this.status = JornadaProgressoStatus.CONCLUIDA;
        this.xpObtido = jornadaWakanda.getXpBonus();
        this.ultimaAtualizacao = LocalDateTime.now();
    }

    public static JornadaProgresso criarEmAndamento(UUID idJornada, UUID idProgresso) {
        JornadaProgresso jp = new JornadaProgresso();
        jp.idJornadaWakanda = idJornada;
        jp.idProgressoWakander = idProgresso;
        jp.status = JornadaProgressoStatus.EM_ANDAMENTO;
        jp.xpObtido = 0;
        jp.dataInicio = LocalDateTime.now();
        jp.ultimaAtualizacao = LocalDateTime.now();
        return jp;
    }

}
