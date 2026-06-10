package academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.application.api;

import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.domain.SabedoriasMissaoProgresso;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class MissaoConcluidaResponse {
    private UUID idMissaoProgresso;
    private UUID idMissao;
    private String titulo;
    private int xpObtido;
    private LocalDateTime dataConclusao;
    private SabedoriasMissaoProgresso sabedoriasGanhas;

    public MissaoConcluidaResponse(MissaoConcluidaProjection missaoConcluida) {
        this.idMissaoProgresso = missaoConcluida.getIdMissaoProgresso();
        this.idMissao = missaoConcluida.getIdMissao();
        this.titulo = missaoConcluida.getTitulo();
        this.xpObtido = missaoConcluida.getXpObtido();
        this.dataConclusao = missaoConcluida.getDataConclusao();
        this.sabedoriasGanhas = SabedoriasMissaoProgresso.ofNullable(new SabedoriasMissaoProgresso(
                missaoConcluida.getSabTeorico(),
                missaoConcluida.getSabProcesso(),
                missaoConcluida.getSabKnowHow(),
                missaoConcluida.getSabComportamental(),
                missaoConcluida.getSabCriativo()
        ));
    }
}
