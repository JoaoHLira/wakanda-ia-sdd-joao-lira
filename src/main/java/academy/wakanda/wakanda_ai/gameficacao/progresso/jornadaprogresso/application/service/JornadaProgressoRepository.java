package academy.wakanda.wakanda_ai.gameficacao.progresso.jornadaprogresso.application.service;

import academy.wakanda.wakanda_ai.gameficacao.progresso.jornadaprogresso.domain.JornadaProgresso;

import java.util.UUID;

public interface JornadaProgressoRepository {
    JornadaProgresso buscaJornadaProgresso(UUID idJornada, UUID idProgressoWakander);
    JornadaProgresso buscaJornadaProgressoPorId(UUID idJornadaProgresso);
    void salvaJornadaProgresso(JornadaProgresso jornadaProgresso);
}
