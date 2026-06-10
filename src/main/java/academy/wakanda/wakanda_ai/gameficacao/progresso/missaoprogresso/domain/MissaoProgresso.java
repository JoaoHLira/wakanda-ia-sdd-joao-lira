package academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.domain;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.Sabedorias;
import academy.wakanda.wakanda_ai.handler.APIException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "missao_progresso", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id_missao_wakanda", "id_progresso_wakander"})})
public class MissaoProgresso {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid", name = "id_missao_progresso")
    private UUID idMissaoProgresso;
    @Column(name = "id_missao_wakanda")
    private UUID idMissaoWakanda;
    @Column(name = "id_progresso_wakander")
    private UUID idProgressoWakander;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private MissaoProgressoStatus statusProgresso;
    @Column(name = "xp_obtido")
    private int xpObtido;
    @Column(name = "tentativas")
    private int tentativas;
    @Column(name = "ultima_atualizacao")
    private LocalDateTime ultimaAtualizacao;
    @Column(name = "data_conclusao")
    private LocalDateTime dataConclusao;
    @Embedded
    private SabedoriasMissaoProgresso sabedorias;

    public void concluiMissao(Integer xpBase, Sabedorias sabedorias) {
        validaConclusaoMissao();
        this.statusProgresso = MissaoProgressoStatus.CONCLUIDA;
        this.dataConclusao = LocalDateTime.now();
        this.ultimaAtualizacao = LocalDateTime.now();
        this.xpObtido = xpBase;
        this.sabedorias = SabedoriasMissaoProgresso.fromCatalogo(sabedorias);
    }

    private void validaConclusaoMissao() {
        if (this.statusProgresso.equals(MissaoProgressoStatus.CONCLUIDA)) {
            throw APIException.build(HttpStatus.BAD_REQUEST,
                    "A missão já está concluída.");
        }
    }

    public static MissaoProgresso criarEmAndamento(UUID idMissao, UUID idProgresso) {
        MissaoProgresso missaoProgresso = new MissaoProgresso();
        missaoProgresso.idMissaoWakanda = idMissao;
        missaoProgresso.idProgressoWakander = idProgresso;
        missaoProgresso.statusProgresso = MissaoProgressoStatus.EM_ANDAMENTO;
        missaoProgresso.xpObtido = 0;
        missaoProgresso.tentativas = 0;
        missaoProgresso.ultimaAtualizacao = LocalDateTime.now();
        missaoProgresso.dataConclusao = null;
        missaoProgresso.sabedorias = new SabedoriasMissaoProgresso(0, 0, 0, 0, 0);
        return missaoProgresso;
    }
}
