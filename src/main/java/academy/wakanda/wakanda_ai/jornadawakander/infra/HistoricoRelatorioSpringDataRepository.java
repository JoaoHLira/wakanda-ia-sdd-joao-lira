package academy.wakanda.wakanda_ai.jornadawakander.infra;

import academy.wakanda.wakanda_ai.jornadawakander.domain.HistoricoRelatorio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HistoricoRelatorioSpringDataRepository extends JpaRepository<HistoricoRelatorio, UUID> {
}
