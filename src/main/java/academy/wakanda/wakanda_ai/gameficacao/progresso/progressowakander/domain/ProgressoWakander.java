package academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "progresso_wakander", uniqueConstraints = { @UniqueConstraint(columnNames = "id_wakander") })
public class ProgressoWakander {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid", name = "id_progresso_wakander")
    private UUID idProgressoWakander;
    @Column(columnDefinition = "uuid", name = "id_wakander", nullable = false)
    private UUID idWakander;
    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    public ProgressoWakander(UUID idWakander) {
        this.idWakander = idWakander;
        this.dataCriacao = LocalDateTime.now();
    }
}