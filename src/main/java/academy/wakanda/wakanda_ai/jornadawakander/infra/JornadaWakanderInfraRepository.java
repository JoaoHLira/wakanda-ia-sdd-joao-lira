package academy.wakanda.wakanda_ai.jornadawakander.infra;

import academy.wakanda.wakanda_ai.jornadawakander.application.service.JornadaWakanderRepository;
import academy.wakanda.wakanda_ai.jornadawakander.domain.AulaAssistida;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Log4j2
@RequiredArgsConstructor
public class JornadaWakanderInfraRepository implements JornadaWakanderRepository {

    private final JornadaWakanderSpringDataRepository jornadaWakanderSpringDataRepository;

    @Override
    public AulaAssistida save(AulaAssistida aulaAssistida) {
        log.info("[start] JornadaWakanderInfraRepository - save");
        jornadaWakanderSpringDataRepository.save(aulaAssistida);
        log.debug("[finish] JornadaWakanderInfraRepository - save");
        return aulaAssistida;
    }

    @Override
    public List<AulaAssistida> buscaAulasPorIdCurso(Long idCurso) {
        log.info("[start] JornadaWakanderInfraRepository - findyByIdCurso");
        var aulasAssistidas = jornadaWakanderSpringDataRepository.findByIdCurso(idCurso);
        log.debug("[finish] JornadaWakanderInfraRepository - findyByIdCurso");
        return aulasAssistidas;
    }
}