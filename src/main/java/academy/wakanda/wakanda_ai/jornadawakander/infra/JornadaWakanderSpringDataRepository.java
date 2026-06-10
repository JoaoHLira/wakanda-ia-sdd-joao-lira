package academy.wakanda.wakanda_ai.jornadawakander.infra;

import academy.wakanda.wakanda_ai.jornadawakander.domain.AulaAssistida;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JornadaWakanderSpringDataRepository extends JpaRepository<AulaAssistida, UUID> {
    List<AulaAssistida> findByIdCurso(Long idCurso);
}