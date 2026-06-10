package academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.infra;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.api.MissaoWebhookDTO;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class MissaoWebhookInfraTest {

	@Mock
	private WebClient.Builder webClientBuilder;

	@Mock
	private WebClient webClient;

	@Mock
	private WebClient.RequestBodyUriSpec requestBodyUriSpec;

	@Mock
	private WebClient.RequestBodySpec requestBodySpec;

	@Mock
	@SuppressWarnings("rawtypes")
	private WebClient.RequestHeadersSpec requestHeadersSpec;

	@Mock
	private WebClient.ResponseSpec responseSpec;

	private MissaoWebhookInfra missaoWebhookInfra;

	@BeforeEach
	void setup() {
		when(webClientBuilder.build()).thenReturn(webClient);
		when(webClient.post()).thenReturn(requestBodyUriSpec);
		when(requestBodyUriSpec.uri(nullable(String.class))).thenReturn(requestBodySpec);
		when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
		when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
		missaoWebhookInfra = new MissaoWebhookInfra(webClientBuilder);
	}

	@Test
	@DisplayName("Deve enviar webhook com sucesso")
	void deveEnviarWebhookComSucesso() {
		when(responseSpec.toBodilessEntity()).thenReturn(Mono.just(ResponseEntity.ok().build()));

		MissaoWebhookDTO dto = new MissaoWebhookDTO(UUID.randomUUID(), "Teste WireMock");
		missaoWebhookInfra.enviaWebhookProcessaMissao (dto);

		verify(webClient, times(1)).post();
		verify(requestBodyUriSpec, times(1)).uri(nullable(String.class));
		verify(requestBodySpec, times(1)).bodyValue(dto);
		verify(requestHeadersSpec, times(1)).retrieve();
		verify(responseSpec, times(1)).toBodilessEntity();
	}

	@Test
	@DisplayName("Deve tratar erro ao enviar webhook")
	void deveTratarErroAoEnviarWebhook() {
		when(responseSpec.toBodilessEntity()).thenReturn(Mono.error(new RuntimeException("erro n8n")));

		MissaoWebhookDTO dto = new MissaoWebhookDTO(UUID.randomUUID(), "Teste WireMock");
		missaoWebhookInfra.enviaWebhookProcessaMissao (dto);

		verify(webClient, times(1)).post();
		verify(requestBodyUriSpec, times(1)).uri(nullable(String.class));
		verify(requestBodySpec, times(1)).bodyValue(dto);
		verify(requestHeadersSpec, times(1)).retrieve();
		verify(responseSpec, times(1)).toBodilessEntity();
	}
}
