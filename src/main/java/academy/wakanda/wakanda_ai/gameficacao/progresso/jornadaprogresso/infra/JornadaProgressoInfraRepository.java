package academy.wakanda.wakanda_ai.gameficacao.progresso.jornadaprogresso.infra;

import academy.wakanda.wakanda_ai.gameficacao.progresso.jornadaprogresso.application.service.JornadaProgressoRepository;
import academy.wakanda.wakanda_ai.gameficacao.progresso.jornadaprogresso.domain.JornadaProgresso;
import academy.wakanda.wakanda_ai.handler.APIException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Log4j2
public class JornadaProgressoInfraRepository implements JornadaProgressoRepository {
    private final JornadaProgressoSpringDataRepository jornadaProgressoSpringDataRepository;

    @Override
    public JornadaProgresso buscaJornadaProgresso(UUID idJornada, UUID idProgressoWakander) {
        log.info("[start] JornadaProgressoInfraRepository - buscaJornadaProgresso");
        JornadaProgresso jornadaProgresso = jornadaProgressoSpringDataRepository.findByidJornadaWakandaAndIdProgressoWakander(idJornada, idProgressoWakander)
                .orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND,
                        "Progresso da jornada " + idJornada + " com idProgressoWakander " + idProgressoWakander + " não foi encontrado!"));
        log.debug("[finish] JornadaProgressoInfraRepository - buscaJornadaProgresso");
        return jornadaProgresso;
    }

    @Override
    public JornadaProgresso buscaJornadaProgressoPorId(UUID idJornadaProgresso) {
        log.info("[start] JornadaProgressoInfraRepository - buscaJornadaProgressoPorId");
        JornadaProgresso jornadaProgresso = jornadaProgressoSpringDataRepository.findById(idJornadaProgresso)
                .orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Progresso da jornada " + idJornadaProgresso + " não foi encontrado!"));
        log.debug("[finish] JornadaProgressoInfraRepository - buscaJornadaProgressoPorId");
        return jornadaProgresso;
    }

    @Override
    public void salvaJornadaProgresso(JornadaProgresso jornadaProgresso) {
        log.info("[start] JornadaProgressoInfraRepository - salvaJornadaProgresso");
        jornadaProgressoSpringDataRepository.save(jornadaProgresso);
        log.debug("[finish] JornadaProgressoInfraRepository - salvaJornadaProgresso");
    }

}