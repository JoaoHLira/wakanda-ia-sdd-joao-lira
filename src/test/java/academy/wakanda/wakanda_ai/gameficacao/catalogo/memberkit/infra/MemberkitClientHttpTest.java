package academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.infra;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.dto.MemberkitCourseDTO;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.dto.MemberkitLessonDTO;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.dto.MemberkitSectionDTO;
import academy.wakanda.wakanda_ai.handler.APIException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MemberkitClientHttpTest {

	@InjectMocks
	private MemberkitClientHttp client;

	@Mock
	private WebClient webClientMock;

	@Mock
	private WebClient.Builder webClientBuilder;

	@Mock
	@SuppressWarnings("rawtypes")
	private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

	@Mock
	@SuppressWarnings("rawtypes")
	private WebClient.RequestHeadersSpec requestHeadersSpec;

	@Mock
	private WebClient.ResponseSpec responseSpec;

	private static final String API_URL = "http://memberkit.local";
	private static final String API_KEY = "test-api-key";

	@BeforeEach
	@SuppressWarnings("unchecked")
	void setup() throws Exception {
		when(webClientBuilder.build()).thenReturn(webClientMock);
		when(webClientMock.get()).thenReturn(requestHeadersUriSpec);
		when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersSpec);
		when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
		when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

		client = new MemberkitClientHttp(webClientBuilder);

		Field urlField = MemberkitClientHttp.class.getDeclaredField("memberkitApiUrl");
		urlField.setAccessible(true);
		urlField.set(client, API_URL);
	}

	@Test
	@DisplayName("Deve buscar curso por ID com sucesso")
	void buscaCursoPorId() {
		String idCurso = "curso-1";
		MemberkitCourseDTO expectedCourse = MemberkitCourseDTO.builder().id(idCurso).name("Curso 1")
				.description("Descrição do curso").build();

		when(responseSpec.bodyToMono(MemberkitCourseDTO.class)).thenReturn(Mono.just(expectedCourse));

		MemberkitCourseDTO result = client.getCourseById(idCurso, API_KEY);

		assertEquals(idCurso, result.getId());
		assertEquals("Curso 1", result.getName());
	}

	@Test
	@DisplayName("Deve retornar lista de cursos")
	void buscaCursos() {
		MemberkitCourseDTO curso1 = MemberkitCourseDTO.builder().id("c1").name("Curso 1").description("Desc 1").build();
		MemberkitCourseDTO curso2 = MemberkitCourseDTO.builder().id("c2").name("Curso 2").description("Desc 2").build();
		MemberkitCourseDTO[] cursosArray = { curso1, curso2 };

		when(responseSpec.bodyToMono(MemberkitCourseDTO[].class)).thenReturn(Mono.just(cursosArray));

		List<MemberkitCourseDTO> result = client.getCourses(API_KEY);

		assertEquals(2, result.size());
		assertEquals("c1", result.get(0).getId());
		assertEquals("c2", result.get(1).getId());
	}

	@Test
	@DisplayName("Deve buscar aulas por curso com sucesso")
	void buscaAulasPorCurso() {
		String courseId = "course-1";
		MemberkitLessonDTO aula1 = MemberkitLessonDTO.builder().id("aula-1").title("Aula 1")
				.content("Conteúdo da aula 1").build();
		MemberkitLessonDTO aula2 = MemberkitLessonDTO.builder().id("aula-2").title("Aula 2")
				.content("Conteúdo da aula 2").build();
		MemberkitSectionDTO section = MemberkitSectionDTO.builder().lessons(List.of(aula1, aula2)).build();

		MemberkitCourseDTO course = MemberkitCourseDTO.builder().id(courseId).name("Curso de Teste")
				.sections(List.of(section)).build();

		when(responseSpec.bodyToMono(MemberkitCourseDTO.class)).thenReturn(Mono.just(course));

		List<MemberkitLessonDTO> result = client.getLessonsByCourse(courseId, API_KEY);

		assertEquals(2, result.size());
		assertEquals("aula-1", result.get(0).getId());
		assertEquals("aula-2", result.get(1).getId());
	}

	@Test
	@DisplayName("Deve lançar exceção quando API retornar erro")
	void deveLancarExcecaoQuandoApiRetornarErro() {
		when(responseSpec.bodyToMono(MemberkitCourseDTO[].class)).thenReturn(
				Mono.error(new WebClientResponseException(400, "Bad Request", HttpHeaders.EMPTY, null, null)));

		assertThrows(APIException.class, () -> client.getCourses(API_KEY));
	}
}
