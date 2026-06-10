package academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.application.api;

import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.domain.MissaoProgresso;
import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.domain.MissaoProgressoStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class MissaoProgressoResponse {

    private UUID idMissaoProgresso;
    private UUID idMissaoWakanda;
    private UUID idProgressoWakander;
    private MissaoProgressoStatus statusProgresso;
    private int xpObtido;
    private int tentativas;
    private LocalDateTime ultimaAtualizacao;

    public MissaoProgressoResponse(MissaoProgresso missaoProgresso) {
        this.idMissaoProgresso = missaoProgresso.getIdMissaoProgresso();
        this.idMissaoWakanda = missaoProgresso.getIdMissaoWakanda();
        this.idProgressoWakander = missaoProgresso.getIdProgressoWakander();
        this.statusProgresso = missaoProgresso.getStatusProgresso();
        this.xpObtido = missaoProgresso.getXpObtido();
        this.tentativas = missaoProgresso.getTentativas();
        this.ultimaAtualizacao = missaoProgresso.getUltimaAtualizacao();
    }
}
