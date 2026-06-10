package academy.wakanda.wakanda_ai.jornadawakander.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.dto.AulaMemberKitDTO;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class AulaAssistida {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid")
    private UUID idAulaAssistida;
    @Column(name = "id_aula", nullable = false)
    private Long idAula;
    @Column(name = "id_curso", nullable = false)
    private Long idCurso;
    @Column(columnDefinition = "uuid", name = "id_wakander", nullable = false)
    private UUID idWakander;
    @Column(name = "data_conclusao", nullable = false)
    private LocalDateTime dataConclusao;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusAula status;
    
    public AulaAssistida(UUID idWakander, AulaMemberKitDTO aulaAssistida) {
		this.idAula = aulaAssistida.getData().getLesson().getId();
		this.idCurso = aulaAssistida.getData().getCourse().getId();
		this.idWakander = idWakander;
		this.dataConclusao = aulaAssistida.getData().getCompletedAt().toLocalDateTime();
		this.status = StatusAula.CONCLUIDO;
	}
}