package academy.wakanda.wakanda_ai.gameficacao.xp.xpwakander.infra;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import academy.wakanda.wakanda_ai.gameficacao.xp.xpwakander.domain.XpWakander;

public interface XpWakanderSpringDataRepository extends JpaRepository<XpWakander, UUID> {

    Optional<XpWakander> findByIdProgressoWakander(UUID idProgressoWakander);

}
