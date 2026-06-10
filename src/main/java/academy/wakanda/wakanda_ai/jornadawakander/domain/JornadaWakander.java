package academy.wakanda.wakanda_ai.jornadawakander.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JornadaWakander {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid")
    private UUID idJornadaWakander;
    @Column(columnDefinition = "uuid", name = "id_wakander", nullable = false)
    private UUID idWakander;
    @Enumerated(EnumType.STRING)
    private JornadaWakanda jornadaAtual;
    @Enumerated(EnumType.STRING)
    private JornadaWakanda jornadaConcluida;
    private LocalDateTime momentoAlteracao;

    public JornadaWakander(UUID idWakander, JornadaWakanda jornadaConcluida, JornadaWakanda jornadaWakanda) {
        this.idWakander = idWakander;
        this.jornadaAtual = jornadaWakanda;
        this.jornadaConcluida = jornadaConcluida;
        this.momentoAlteracao = LocalDateTime.now();
    }
}