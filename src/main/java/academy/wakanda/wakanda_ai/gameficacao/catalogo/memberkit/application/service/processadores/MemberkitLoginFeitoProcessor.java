package academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.service.processadores;

import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventDto;
import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventype;
import academy.wakanda.wakanda_ai.comunicacao.infra.PublicadorNotificacaoSns;
import academy.wakanda.wakanda_ai.constants.TopicNames;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.MemberkitEventRequest;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.dto.LoginMemberkitDto;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.service.MemberkitEventType;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.OnboardingWakanderService;
import academy.wakanda.wakanda_ai.jornadawakander.domain.JornadaWakanda;
import academy.wakanda.wakanda_ai.jornadawakander.domain.OnboardingWakander;
import academy.wakanda.wakanda_ai.wakander.application.service.WakanderRepository;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class MemberkitLoginFeitoProcessor implements MemberkitProcessor {
	private final TopicNames topicNames;
	private final WakanderRepository wakanderRepository;
	private final PublicadorNotificacaoSns publicadorNotificacaoSns;
	private final OnboardingWakanderService onboardingWakanderService;
	private final ObjectMapper objectMapper;

	@Override
	public boolean validaSeEventoProcessa(String type) {
		return type.equals(MemberkitEventType.LOGIN_FEITO.getDescricao());
	}

	@Override
	public void processaEvento(MemberkitEventRequest request) {
		log.info("[start] LoginFeitoMemberkitProcessor - processaEvento");
		try {
			LoginMemberkitDto loginMemberkitDto = deserializeMessageContent(request, LoginMemberkitDto.class);
			log.debug("[LoginMemberKitDTO] {}", loginMemberkitDto);
			atualizaDadosWakander(loginMemberkitDto);
		} catch (JsonProcessingException e) {
			log.error("Erro ao desserializar o conteúdo da mensagem: ", e);
		}
		log.debug("[finish] LoginFeitoMemberkitProcessor - processaEvento");
	}

	private void atualizaDadosWakander(LoginMemberkitDto loginMemberkitDto) {
		Wakander wakander = wakanderRepository.buscaWakanderPorIdMemberKit(loginMemberkitDto.getData().getIdMemberKit());
		if (validaPrimeiroLogin(wakander)) {
			atualizaAcessoPlataformaEstudo(wakander);
			wakander.atualizaStatusJornada(JornadaWakanda.LOGOU_NO_MEMBERKIT);
			wakanderRepository.save(wakander);
		}
	}

	private static boolean validaPrimeiroLogin(Wakander wakander) {
		return wakander.getJornadaAtual() == JornadaWakanda.ONBOARD;
	}

	private void atualizaAcessoPlataformaEstudo(Wakander wakander) {
		log.info("[start] LoginFeitoMemberkitProcessor - atualizaAcessoPlataformaEstudo");
		OnboardingWakander onboardingWakander = onboardingWakanderService
				.buscaOnboardingPorIdWakander(wakander.getIdWakander());
		onboardingWakander.atualizaAcessoPlataformaEstudo();
		onboardingWakanderService.save(onboardingWakander);
		publicaChecklist(wakander, onboardingWakander);
		log.debug("[finish] LoginFeitoMemberkitProcessor - atualizaAcessoPlataformaEstudo");
	}

	private void publicaChecklist(Wakander wakander, OnboardingWakander onboardingWakander) {
		log.info("[start] LoginFeitoMemberkitProcessor - publicaChecklist");
		String checklist = onboardingWakanderService.retornaChecklist(onboardingWakander);
		ZApiEventDto ZApiEventDto = new ZApiEventDto(ZApiEventype.NORMAL_MESSAGE, wakander.getContato().getWhatsapp(),
				checklist);
		publicadorNotificacaoSns.enviaNotificacaoSns("checklist", ZApiEventDto, topicNames.getZapiRequests());
		log.debug("[finish] LoginFeitoMemberkitProcessor - publicaChecklist");
	}

	public <T> T deserializeMessageContent(MemberkitEventRequest request, Class<T> classeModelo )
			throws JsonProcessingException {
		String payload = objectMapper.writeValueAsString(request);
		return objectMapper.readValue(payload, classeModelo );
	}
}
