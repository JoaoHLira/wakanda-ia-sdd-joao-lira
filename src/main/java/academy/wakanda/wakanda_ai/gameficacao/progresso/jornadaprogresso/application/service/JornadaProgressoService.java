package academy.wakanda.wakanda_ai.gameficacao.progresso.jornadaprogresso.application.service;

import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.service.ProgressoWakanderEventDto;

import java.util.UUID;

public interface JornadaProgressoService {
    void concluiJornadaSeMissoesConcluidas(UUID idJornada, UUID idProgressoWakander);
    void concluiJornadaProgresso(ProgressoWakanderEventDto progressoWakander);
}
