package academy.wakanda.wakanda_ai.jornadawakander.application.service.memberkit;

import academy.wakanda.wakanda_ai.jornadawakander.domain.AulaAssistida;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;

public interface AulaAssistidaProcessador {
    boolean validaSeEventoProcessa(AulaAssistida aulaAssistida, Wakander wakander);
    void processaEvento(AulaAssistida aulaAssistida, Wakander wakander);
}
