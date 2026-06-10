package academy.wakanda.wakanda_ai.jornadawakander.infra;

import academy.wakanda.wakanda_ai.jornadawakander.application.service.ProgressoWakanderRepository;
import academy.wakanda.wakanda_ai.jornadawakander.domain.JornadaWakanda;
import academy.wakanda.wakanda_ai.jornadawakander.domain.JornadaWakander;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Log4j2
@RequiredArgsConstructor
public class ProgressoWakanderInfraRepository implements ProgressoWakanderRepository {
    private final ProgressoWakanderSpringDataRepository progressoWakanderSpringDataRepository;

    @Override
    public void salvaProgresso(UUID idWakander, JornadaWakanda jornadaConcluida, JornadaWakanda jornadaWakanda) {
        log.info("[start] ProgressoWakanderInfraRepository - salvaProgresso");
        JornadaWakander progresso = new JornadaWakander(idWakander, jornadaConcluida, jornadaWakanda);
        progressoWakanderSpringDataRepository.save(progresso);
        log.debug("[finish] ProgressoWakanderInfraRepository - salvaProgresso");
    }
}
