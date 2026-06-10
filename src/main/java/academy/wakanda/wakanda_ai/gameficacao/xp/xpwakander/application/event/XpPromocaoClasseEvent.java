package academy.wakanda.wakanda_ai.gameficacao.xp.xpwakander.application.event;

import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.domain.ProgressoWakander;
import academy.wakanda.wakanda_ai.gameficacao.xp.xpwakander.domain.XpWakander;
import lombok.*;

import java.util.UUID;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class XpPromocaoClasseEvent {
    private UUID idXpWakander;
    private UUID idProgressoWakander;
    private UUID idWakander;
    private int xpTotal;
    private int nivelAtual;

    public XpPromocaoClasseEvent(XpWakander xpWakander, ProgressoWakander progressoWakander) {
        this.idXpWakander = xpWakander.getIdXpWakander();
        this.idProgressoWakander = xpWakander.getIdProgressoWakander();
        this.idWakander = progressoWakander.getIdWakander();
        this.xpTotal = xpWakander.getXpTotal();
        this.nivelAtual = xpWakander.getNivelAtual();
    }

}
