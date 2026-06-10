package academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SincronizacaoProgressoResponse {
    private int registrosCriados;
    private int registrosIgnorados;
}
