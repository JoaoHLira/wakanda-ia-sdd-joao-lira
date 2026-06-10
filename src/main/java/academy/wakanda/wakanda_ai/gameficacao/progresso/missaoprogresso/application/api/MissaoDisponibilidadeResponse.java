package academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.application.api;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class MissaoDisponibilidadeResponse {
    UUID idMissao;
    String titulo;
    MissaoDisponibilidadeStatus statusDisponibilidade;
}
