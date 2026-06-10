package academy.wakanda.wakanda_ai.gameficacao.common.dto;

import academy.wakanda.wakanda_ai.gameficacao.xp.xpwakander.application.event.XpPromocaoClasseEvent;
import lombok.*;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class XpPromocaoClasseDTO {
    private UUID idXpWakander;
    private UUID idProgressoWakander;
    private UUID idWakander;
    private int xpTotal;
    private int nivelAtual;

    public XpPromocaoClasseDTO(XpPromocaoClasseEvent evento) {
        this.idXpWakander = evento.getIdXpWakander();
        this.idProgressoWakander = evento.getIdProgressoWakander();
        this.idWakander = evento.getIdWakander();
        this.xpTotal = evento.getXpTotal();
        this.nivelAtual = evento.getNivelAtual();
    }
}
