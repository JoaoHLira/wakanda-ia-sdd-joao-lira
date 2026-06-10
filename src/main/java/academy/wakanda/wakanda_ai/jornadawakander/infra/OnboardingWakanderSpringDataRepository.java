package academy.wakanda.wakanda_ai.jornadawakander.infra;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import academy.wakanda.wakanda_ai.jornadawakander.domain.OnboardingWakander;

public interface OnboardingWakanderSpringDataRepository extends JpaRepository<OnboardingWakander, UUID> {
	Optional<OnboardingWakander> findByIdWakander(UUID idWakander);
}