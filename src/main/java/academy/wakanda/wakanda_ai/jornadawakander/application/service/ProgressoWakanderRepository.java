package academy.wakanda.wakanda_ai.jornadawakander.application.service;

import academy.wakanda.wakanda_ai.jornadawakander.domain.JornadaWakanda;

import java.util.UUID;

public interface ProgressoWakanderRepository {
    void salvaProgresso(UUID idWakander, JornadaWakanda jornadaConcluida, JornadaWakanda jornadaWakanda);
}
