package academy.wakanda.wakanda_ai.gameficacao.xp.historicoclasse.infra;

import academy.wakanda.wakanda_ai.gameficacao.xp.historicoclasse.domain.HistoricoClasseWakander;
import academy.wakanda.wakanda_ai.gameficacao.xp.historicoclasse.domain.HistoricoClasseWakanderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface HistoricoClasseWakanderSpringDataRepository extends JpaRepository<HistoricoClasseWakander, UUID> {

    Optional<HistoricoClasseWakander> findAllByIdXpWakanderAndStatus(
            UUID idXpWakander,
            HistoricoClasseWakanderStatus historicoClasseWakanderStatus);

    Optional<HistoricoClasseWakander> findByIdWakanderAndStatus(
            UUID idWakander,
            HistoricoClasseWakanderStatus historicoClasseWakanderStatus);

}
