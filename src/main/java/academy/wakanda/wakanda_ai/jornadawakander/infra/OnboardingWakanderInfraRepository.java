package academy.wakanda.wakanda_ai.jornadawakander.infra;

import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import academy.wakanda.wakanda_ai.handler.APIException;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.OnboardingWakanderRepository;
import academy.wakanda.wakanda_ai.jornadawakander.domain.OnboardingWakander;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Repository
@Log4j2
@RequiredArgsConstructor
public class OnboardingWakanderInfraRepository implements OnboardingWakanderRepository {
	private final OnboardingWakanderSpringDataRepository onboardingSpringDataRepository;

	@Override
	public OnboardingWakander save(OnboardingWakander onboardingWakander) {
		log.info("[start] OnboardingWakanderInfraRepository - save");
		try {
			onboardingSpringDataRepository.save(onboardingWakander);
		} catch (DataIntegrityViolationException ex) {
			throw APIException.build(HttpStatus.CONFLICT, "Existem dados duplicados");
		}
		log.debug("[finish] OnboardingWakanderInfraRepository - save");
		return onboardingWakander;
	}

	@Override
	public OnboardingWakander buscaPorIdWakander(UUID idWakander) {
		log.info("[start] OnboardingWakanderInfraRepository - buscaPorIdWakander");
		OnboardingWakander onboardingWakander = onboardingSpringDataRepository.findByIdWakander(idWakander).orElseThrow(
				() -> APIException.build(HttpStatus.NOT_FOUND, "Onboarding não encontrado!"));
		log.debug("[finish] OnboardingWakanderInfraRepository - buscaPorIdWakander");
		return onboardingWakander;
	}

}