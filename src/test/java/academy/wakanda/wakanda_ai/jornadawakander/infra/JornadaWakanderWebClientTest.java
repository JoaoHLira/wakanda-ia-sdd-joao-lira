package academy.wakanda.wakanda_ai.jornadawakander.infra;

import academy.wakanda.wakanda_ai.handler.APIException;
import academy.wakanda.wakanda_ai.jornadawakander.application.api.CadastraMembroRequest;
import academy.wakanda.wakanda_ai.jornadawakander.application.api.MemberkitUserDto;
import academy.wakanda.wakanda_ai.utils.DataHelper;
import academy.wakanda.wakanda_ai.utils.templates.Templates;
import academy.wakanda.wakanda_ai.wakander.application.event.CadastroCompletoEvent;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import br.com.six2six.fixturefactory.Fixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

import static academy.wakanda.wakanda_ai.utils.templates.WakanderTemplate.WAKANDER;
import static br.com.six2six.fixturefactory.loader.FixtureFactoryLoader.loadTemplates;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JornadaWakanderWebClientTest {
    @InjectMocks
    private JornadaWakanderWebClient jornadaWakanderWebClient;
    @Mock
    private WebClient.Builder webClientBuilder;

    @BeforeEach
    void setUp() {
        WebClient webClient = mock(WebClient.class);
        when(webClientBuilder.build()).thenReturn(webClient);
        jornadaWakanderWebClient = new JornadaWakanderWebClient(webClientBuilder);
        loadTemplates(Templates.BASE_PACKAGE);
    }

    @Test
    void deveCadastrarUsuarioNoMemberkit() {
        WebClient.RequestBodyUriSpec requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        Wakander wakander = Fixture.from(Wakander.class).gimme(WAKANDER);
        CadastroCompletoEvent cadastroCompletoEvent = new CadastroCompletoEvent(wakander);
        CadastraMembroRequest request = new CadastraMembroRequest(cadastroCompletoEvent, 1, List.of(1));
        MemberkitUserDto memberkitUserDto = DataHelper.criaMemberkitUserDto();

        when(webClientBuilder.build().post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(MemberkitUserDto.class))
                .thenReturn(reactor.core.publisher.Mono.just(memberkitUserDto));

        MemberkitUserDto resultado = jornadaWakanderWebClient.requisicaoPostParaOMemberKit(request, MemberkitUserDto.class);

        assertThat(resultado).isNotNull().isEqualTo(memberkitUserDto);
    }

    @Test
    void deveLancarWebClientExceptionAoTentarCadastrarUsuarioNoMemberkit() {
        WebClient.RequestBodyUriSpec requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        Wakander wakander = Fixture.from(Wakander.class).gimme(WAKANDER);
        CadastroCompletoEvent cadastroCompletoEvent = new CadastroCompletoEvent(wakander);
        CadastraMembroRequest request = new CadastraMembroRequest(cadastroCompletoEvent, 1, List.of(1));

        when(webClientBuilder.build().post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(MemberkitUserDto.class))
                .thenReturn(reactor.core.publisher.Mono.error(
                        new WebClientResponseException(400, "Bad Request", null, null, null)
                ));

        assertThatThrownBy(() -> jornadaWakanderWebClient.requisicaoPostParaOMemberKit(request, MemberkitUserDto.class))
                .isInstanceOf(APIException.class)
                .hasMessageContaining("Bad Request");

        verify(webClientBuilder.build()).post();
        verify(requestBodyUriSpec).uri(anyString());
        verify(requestBodyUriSpec).bodyValue(request);
    }

    @Test
    void deveLancarExceptionAoTentarCadastrarUsuarioNoMemberkit() {
        WebClient.RequestBodyUriSpec requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        Wakander wakander = Fixture.from(Wakander.class).gimme(WAKANDER);
        CadastroCompletoEvent cadastroCompletoEvent = new CadastroCompletoEvent(wakander);
        CadastraMembroRequest request = new CadastraMembroRequest(cadastroCompletoEvent, 1, List.of(1));

        when(webClientBuilder.build().post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenThrow(new RuntimeException("Erro inesperado"));

        assertThatThrownBy(() -> jornadaWakanderWebClient.requisicaoPostParaOMemberKit(request, MemberkitUserDto.class))
                .isInstanceOf(APIException.class)
                .hasMessageContaining("Erro inesperado");

        verify(webClientBuilder.build()).post();
        verify(requestBodyUriSpec).uri(anyString());
        verify(requestBodyUriSpec).bodyValue(request);
        verify(requestHeadersSpec).retrieve();
    }
}