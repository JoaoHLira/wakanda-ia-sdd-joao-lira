package academy.wakanda.wakanda_ai.jornadawakander.application.service;

import academy.wakanda.wakanda_ai.constants.MensagensWhatsapp;
import academy.wakanda.wakanda_ai.jornadawakander.application.api.DiscordRequest;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.discord.DiscordService;
import academy.wakanda.wakanda_ai.jornadawakander.domain.OnboardingWakander;
import academy.wakanda.wakanda_ai.wakander.application.service.WakanderRepository;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Log4j2
@RequiredArgsConstructor
public class OnboardingWakanderApplicationService implements OnboardingWakanderService {
	private final OnboardingWakanderRepository onboardingRepository;
	private final WakanderRepository wakanderRepository;
	private final DiscordService discordService;
	
	@Override
	public void save(OnboardingWakander onboardingWakander) {
		log.info("[start] OnboardingWakanderApplicationService - save");
		onboardingRepository.save(onboardingWakander);
		log.debug("[finish] OnboardingWakanderApplicationService - save");
	}

	@Override
	public OnboardingWakander buscaOnboardingPorIdWakander(UUID idWakander) {
		log.info("[start] OnboardingWakanderApplicationService - buscaOnboardingPorIdWakander");
		OnboardingWakander onboardingWakander = onboardingRepository.buscaPorIdWakander(idWakander);
		log.debug("[finish] OnboardingWakanderApplicationService - buscaOnboardingPorIdWakander");
		return onboardingWakander;
	}

	@Override
	public String retornaChecklist(OnboardingWakander onboardingWakander) {
		log.info("[start] OnboardingWakanderApplicationService - retornaChecklist");
		List<Integer> posicoesConcluidas = obtemIndicesDasEtapasConcluidas(onboardingWakander);
		String checklist = MensagensWhatsapp.retornaChecklistProgresso(posicoesConcluidas);
		log.debug("[finish] OnboardingWakanderApplicationService - retornaChecklist");
		return checklist;
	}

	private List<Map.Entry<String, Boolean>> listaEtapasDeOnboarding(OnboardingWakander onboardingWakander) {
	    return List.of(
	        Map.entry("cadastroConfirmado", onboardingWakander.getCadastroConfirmado()),
	        Map.entry("entrouDiscord", onboardingWakander.getEntrouDiscord()),
	        Map.entry("entrouGrupoWhatsapp", onboardingWakander.getEntrouGrupoWhatsapp()),
	        Map.entry("acessouPlataformaEstudo", onboardingWakander.getAcessouPlataformaEstudo()),
	        Map.entry("concluiuComeceAqui", onboardingWakander.getConcluiuComeceAqui()),
	        Map.entry("concluiuPrimeiraAulaJornada", onboardingWakander.getConcluiuPrimeiraAulaJornada())
	    );
	}
	
	private List<Integer> obtemIndicesDasEtapasConcluidas(OnboardingWakander onboardingWakander) {
	    List<Map.Entry<String, Boolean>> etapas = listaEtapasDeOnboarding(onboardingWakander);
	    return IntStream.range(0, etapas.size())
	        .filter(i -> Boolean.TRUE.equals(etapas.get(i).getValue()))
	        .boxed()
	        .collect(Collectors.toList());
	}
	
	public OnboardingWakander buscaPorIdWakander(UUID idWakander) {
		log.info("[start] OnboardingWakanderApplicationService - buscaPorIdWakander");
		OnboardingWakander onboardingWakander =  onboardingRepository.buscaPorIdWakander(idWakander);
		log.debug("[finish] OnboardingWakanderApplicationService - buscaPorIdWakander");
		return onboardingWakander;
	}

	@Override
	public void associarUsuarioDiscord(DiscordRequest request) {
		log.info("[start] OnboardingWakanderApplicationService - associarUsuarioDiscord");
		Wakander wakander = wakanderRepository.buscaWakanderPorEmail(request.getEmail());
		OnboardingWakander onboardingWakander = onboardingRepository.buscaPorIdWakander(wakander.getIdWakander());
		onboardingWakander.atualizaEntrouNoDiscord();
		wakander.associarDiscord(request.getIdDiscord(), request.getNome());
		wakanderRepository.save(wakander);
		onboardingRepository.save(onboardingWakander);
		discordService.atualizaCargoParaWakander(wakander.getIdDiscord());
		log.debug("[finish] OnboardingWakanderApplicationService - associarUsuarioDiscord");
	}
}