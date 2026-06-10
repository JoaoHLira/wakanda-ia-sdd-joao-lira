package academy.wakanda.wakanda_ai.gameficacao.progresso.jornadaprogresso.infra;

import academy.wakanda.wakanda_ai.gameficacao.progresso.jornadaprogresso.domain.JornadaProgresso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JornadaProgressoSpringDataRepository extends JpaRepository<JornadaProgresso, UUID> {
    Optional<JornadaProgresso> findByidJornadaWakandaAndIdProgressoWakander(UUID idJornada, UUID idProgressoWakander);
    List<JornadaProgresso> findByidProgressoWakander(UUID idProgressoWakander);
}