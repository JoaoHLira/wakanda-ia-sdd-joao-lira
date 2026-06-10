package academy.wakanda.wakanda_ai.wakander.application.event;

import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventDto;
import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventype;
import academy.wakanda.wakanda_ai.comunicacao.infra.PublicadorNotificacaoSns;
import academy.wakanda.wakanda_ai.constants.TopicNames;
import academy.wakanda.wakanda_ai.financeiro.datahelper.FinanceiroDataHelper;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.memberkit.MemberKitMessageEnvelope;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.memberkit.MemberKitTipoRequisicao;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static academy.wakanda.wakanda_ai.utils.DataHelper.criaAssinaturaCanceladaEvent;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.springframework.test.util.ReflectionTestUtils;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class AssinaturaCanceladaConsumerTest {

	@InjectMocks
	private AssinaturaCanceladaConsumer consumer;
	@Mock
	private PublicadorNotificacaoSns publicadorNotificacaoSns;
	@Mock
	private TopicNames topicNames;
	@Mock
	private ObjectMapper objectMapper;
	private AssinaturaCanceladaEvent evento;
	private JsonNode fakeJson;
	private String topicArn;

	@BeforeEach
	void setUp() throws Exception {
		evento = criaAssinaturaCanceladaEvent();
		ReflectionTestUtils.setField(evento, "idWakander", UUID.randomUUID());

		fakeJson = new ObjectMapper().createObjectNode().put("idMemberKit", evento.getIdMemberKit()).put("email",
				evento.getEmail());

		topicArn = "memberkit-requests-topic.fifo";
	}

	@Test
	void devePublicarMensagemQuandoReceberEventoDeAssinaturaCancelada() {

		AssinaturaCanceladaEvent evento = FinanceiroDataHelper.criaAssinaturaCanceladaEvent();

		when(topicNames.getZapiRequests()).thenReturn("zapi-topic");

		consumer.removeWakanderDoGrupoWhatsapp(evento);

		ArgumentCaptor<ZApiEventDto> captor = ArgumentCaptor.forClass(ZApiEventDto.class);
		verify(publicadorNotificacaoSns).enviaNotificacaoSns(eq(evento.getIdWakander().toString()), captor.capture(),
				eq("zapi-topic"));

		ZApiEventDto dto = captor.getValue();
		assertEquals(ZApiEventype.REMOVE_TO_GROUP, dto.getType());
		assertEquals(evento.getTelefone(), dto.getWhatsapp());
		assertEquals(evento.getNome(), dto.getMensagem());
	}

	@Test
	void devePublicarNoSnsComEnvelopeACESSO_BLOQUEADO() {
		when(topicNames.getMemberkitRequests()).thenReturn(topicArn);
		when(objectMapper.valueToTree(evento)).thenReturn(fakeJson);
		doNothing().when(publicadorNotificacaoSns).enviaNotificacaoSns(anyString(), any(), anyString());

		consumer.bloqueiaWakanderNoMemberKit(evento);

		ArgumentCaptor<String> groupIdCap = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<MemberKitMessageEnvelope> envCap = ArgumentCaptor.forClass(MemberKitMessageEnvelope.class);
		ArgumentCaptor<String> topicCap = ArgumentCaptor.forClass(String.class);

		verify(publicadorNotificacaoSns, times(1)).enviaNotificacaoSns(groupIdCap.capture(), envCap.capture(),
				topicCap.capture());

		assertEquals(evento.getIdWakander().toString(), groupIdCap.getValue());
		assertEquals(topicArn, topicCap.getValue());

		MemberKitMessageEnvelope envelope = envCap.getValue();
		assertEquals(MemberKitTipoRequisicao.ACESSO_BLOQUEADO, envelope.getTipo());
		assertEquals(fakeJson, envelope.getPayload());
	}
}