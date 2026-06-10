package academy.wakanda.wakanda_ai.jornadawakander.infra;

import academy.wakanda.wakanda_ai.jornadawakander.application.service.HistoricoRelatorioRepository;
import academy.wakanda.wakanda_ai.jornadawakander.domain.HistoricoRelatorio;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

@Repository
@Log4j2
@RequiredArgsConstructor
public class HistoricoRelatorioInfraRepository implements HistoricoRelatorioRepository {
    private final HistoricoRelatorioSpringDataRepository historicoRelatorioSpringDataRepository;

    @Override
    public void save(HistoricoRelatorio historicoRelatorio) {
        log.info("[start] HistoricoRelatorioInfraRepository - save");
        historicoRelatorioSpringDataRepository.save(historicoRelatorio);
        log.debug("[finish] HistoricoRelatorioInfraRepository - save");
    }
}
