package academy.wakanda.wakanda_ai.jornadawakander.infra;

import academy.wakanda.wakanda_ai.jornadawakander.domain.JornadaWakander;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProgressoWakanderSpringDataRepository extends JpaRepository<JornadaWakander, UUID> {
}
