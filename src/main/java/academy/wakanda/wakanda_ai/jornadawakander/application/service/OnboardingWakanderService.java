package academy.wakanda.wakanda_ai.jornadawakander.application.service;

import java.util.UUID;

import academy.wakanda.wakanda_ai.jornadawakander.application.api.DiscordRequest;
import academy.wakanda.wakanda_ai.jornadawakander.domain.OnboardingWakander;

public interface OnboardingWakanderService {
	void save(OnboardingWakander onboardingWakander);
	OnboardingWakander buscaOnboardingPorIdWakander(UUID idWakander);
	String retornaChecklist(OnboardingWakander onboardingWakander);
	void associarUsuarioDiscord(DiscordRequest request);
}