package academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.application.api;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.UUID;

@Getter
public class MissaoProgressoRequest {

    @NotNull
    private UUID idWakander;
    @NotNull
    private UUID idMissao;
}
