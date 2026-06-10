package academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.api;

import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class IgnoradoProgressoDetalhe {
    private UUID idWakander;
    private String nome;
    private String motivo;

    public static IgnoradoProgressoDetalhe irregular(Wakander w) {
        return new IgnoradoProgressoDetalhe(w.getIdWakander(), w.getNome(),
                "Wakander não é elegível por estar irregular financeiramente.");
    }

    public static IgnoradoProgressoDetalhe jaPossuiProgresso(Wakander w) {
        return new IgnoradoProgressoDetalhe(w.getIdWakander(), w.getNome(),
                "Wakander já possui progresso registrado.");
    }
}
