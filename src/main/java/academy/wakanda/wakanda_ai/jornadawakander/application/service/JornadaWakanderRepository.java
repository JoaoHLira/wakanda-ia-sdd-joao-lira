package academy.wakanda.wakanda_ai.jornadawakander.application.service;

import academy.wakanda.wakanda_ai.jornadawakander.domain.AulaAssistida;

import java.util.List;

public interface JornadaWakanderRepository {
    AulaAssistida save(AulaAssistida aulaAssistida);

    List<AulaAssistida> buscaAulasPorIdCurso(Long idCurso);
}