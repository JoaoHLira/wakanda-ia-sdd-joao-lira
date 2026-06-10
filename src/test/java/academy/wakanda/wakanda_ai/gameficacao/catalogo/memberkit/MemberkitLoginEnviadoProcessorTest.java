package academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit;

import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventDto;
import academy.wakanda.wakanda_ai.comunicacao.infra.PublicadorNotificacaoSns;
import academy.wakanda.wakanda_ai.constants.TopicNames;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.MemberkitEventRequest;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.dto.LoginMemberkitDto;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.service.processadores.MemberkitLoginEnviadoProcessor;
import academy.wakanda.wakanda_ai.utils.DataHelper;
import academy.wakanda.wakanda_ai.utils.templates.Templates;
import academy.wakanda.wakanda_ai.wakander.application.service.WakanderRepository;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import br.com.six2six.fixturefactory.Fixture;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static academy.wakanda.wakanda_ai.utils.templates.WakanderTemplate.WAKANDER;
import static br.com.six2six.fixturefactory.loader.FixtureFactoryLoader.loadTemplates;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberkitLoginEnviadoProcessorTest {
	@InjectMocks
	MemberkitLoginEnviadoProcessor memberkitLoginEnviadoProcessor;
	@Mock
	WakanderRepository wakanderRepository;
	@Mock
	PublicadorNotificacaoSns publicadorNotificacaoSns;
	@Mock
	ObjectMapper objectMapper;
	private static final String URL_MEMBERKIT = "https://wakanda-academy.memberkit.com.br";
	@Mock
	TopicNames topicNames;

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(memberkitLoginEnviadoProcessor, "urlMemberkit", URL_MEMBERKIT);
		lenient().when(topicNames.getZapiRequests()).thenReturn("zapi-requests-queue.fifo");
		loadTemplates(Templates.BASE_PACKAGE);
	}

	@Test
	@DisplayName("Deve enviar login de acesso do Memberkit ao Wakander")
	void deveEnviarMensagensLoginMemberkit() throws Exception {
		LoginMemberkitDto loginMemberkit = DataHelper.criaLoginMemberkit("login.sent");
		Wakander wakanderReal = Fixture.from(Wakander.class).gimme(WAKANDER);
		Wakander wakander = spy(wakanderReal);
		
		MemberkitEventRequest request = new MemberkitEventRequest("login.sent", loginMemberkit.getData());

		lenient().doNothing().when(publicadorNotificacaoSns).enviaNotificacaoSns(anyString(), any(ZApiEventDto.class),
				anyString());
		when(wakanderRepository.buscaWakanderPorIdMemberKit(anyString())).thenReturn(wakander);
		when(objectMapper.writeValueAsString(any())).thenReturn("{\"type\":\"login.sent\",\"data\":{}}");
		when(objectMapper.readValue(anyString(), any(Class.class))).thenReturn(loginMemberkit);

		memberkitLoginEnviadoProcessor.processaEvento(request);
	}

	@Test
	@DisplayName("Não processar se o tipo do evento é diferente de login enviado")
	void deveRetornarFalseQuandoValidarTipoEventoDiferenteDeLogin() {
		String tipoEvento = "user.last_seen";
		boolean resultado = memberkitLoginEnviadoProcessor.validaSeEventoProcessa(tipoEvento);

		assertFalse(resultado);
	}
	
    @Test
    @DisplayName("Validar se processa evento de login enviado")
    void deveRetornarTrueQuandoValidarTipoCorretoDeEvento() {
        String tipoEvento = "login.sent";
        boolean resultado = memberkitLoginEnviadoProcessor.validaSeEventoProcessa(tipoEvento);

        assertTrue(resultado);
    }
}
