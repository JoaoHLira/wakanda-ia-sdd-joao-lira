package academy.wakanda.wakanda_ai.jornadawakander.application.service;

import java.util.UUID;

import academy.wakanda.wakanda_ai.jornadawakander.domain.OnboardingWakander;

public interface OnboardingWakanderRepository {
	OnboardingWakander save(OnboardingWakander onboardingWakander);
	OnboardingWakander buscaPorIdWakander(UUID idWakander);
}