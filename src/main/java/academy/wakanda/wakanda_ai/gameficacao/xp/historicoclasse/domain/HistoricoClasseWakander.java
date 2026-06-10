package academy.wakanda.wakanda_ai.gameficacao.xp.historicoclasse.domain;

import academy.wakanda.wakanda_ai.gameficacao.common.dto.XpPromocaoClasseDTO;
import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.domain.ClasseWakanda;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "historico_classe_wakander")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class HistoricoClasseWakander {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid", nullable = false)
    private UUID idHistoricoClasse;

    @Column(nullable = false)
    private UUID idClasse;

    @Column(nullable = false)
    private UUID idXpWakander;

    @Column(nullable = false)
    private UUID idProgressoWakander;

    @Column(nullable = false)
    private UUID idWakander;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private HistoricoClasseWakanderStatus status;

    @Column(nullable = false)
    private LocalDateTime dataInicio;

    private LocalDateTime dataFim;

    public HistoricoClasseWakander(ClasseWakanda proximaClasse, XpPromocaoClasseDTO xpPromocaoClasseDTO) {
        this.idClasse = proximaClasse.getIdClasse();
        this.idXpWakander = xpPromocaoClasseDTO.getIdXpWakander();
        this.idProgressoWakander = xpPromocaoClasseDTO.getIdProgressoWakander();
        this.idWakander = xpPromocaoClasseDTO.getIdWakander();
        this.status = HistoricoClasseWakanderStatus.EM_ANDAMENTO;
        this.dataInicio = LocalDateTime.now();
    }

    public void concluiClasseAtual() {
        this.status = HistoricoClasseWakanderStatus.CONCLUIDA;
        this.dataFim = LocalDateTime.now();
    }

}
