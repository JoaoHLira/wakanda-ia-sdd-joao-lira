package academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.service;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.MemberkitConfig;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.dto.MemberkitCourseDTO;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.dto.MemberkitLessonDTO;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.dto.MissaoOrigemDTO;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.datahelper.MemberkitDataHelper;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.api.MissaoExternaDTO;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.service.MissaoWakandaService;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.MissaoWakanda;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberkitIntegrationImporterTest {

	@InjectMocks
	private MemberkitIntegrationImporter memberkitIntegrationImporter;

	@Mock
	private MemberkitConfig memberkitConfig;

	@Mock
	private MemberkitClientService memberkitClientService;

	@Mock
	private MissaoWakandaService missaoWakandaService;

	@BeforeEach
	void setup() {
		lenient().when(memberkitConfig.getUrlApi()).thenReturn("http://memberkit.local");
		lenient().when(memberkitConfig.getApiKey()).thenReturn("api-key");
	}

	@Test
	@DisplayName("Cria missão do curso e das aulas")
	void processarCurso_criaMissaoCursoEAulas() {
		UUID idTipoMissao = UUID.randomUUID();
		UUID idJornada = UUID.randomUUID();

		MemberkitCourseDTO curso = MemberkitDataHelper.criarCurso("course-1", "Curso Teste", "Desc curso");
		List<MemberkitLessonDTO> aulas = MemberkitDataHelper.criarDuasAulas("course-1");

		when(memberkitClientService.getLessonsByCourse("course-1", "api-key")).thenReturn(aulas);
		MissaoWakanda missaoMock = mock(MissaoWakanda.class);
		when(missaoMock.getIdMissao()).thenReturn(UUID.randomUUID());
		when(missaoWakandaService.criaMissaoExterna(any(MissaoExternaDTO.class), eq(true))).thenReturn(missaoMock);

		// act
		memberkitIntegrationImporter.validarConfiguracoes();
		memberkitIntegrationImporter.processarCurso(curso, new HashMap<>(), idTipoMissao, idJornada);

		// assert
		verify(memberkitClientService, times(1)).getLessonsByCourse("course-1", "api-key");
		verify(missaoWakandaService, times(3)).criaMissaoExterna(any(MissaoExternaDTO.class), eq(true));
	}

	@Test
	@DisplayName("Sem aulas cria somente a missão do curso")
	void processarCurso_semAulas_criaSomenteCurso() {
		UUID idTipoMissao = UUID.randomUUID();
		UUID idJornada = UUID.randomUUID();
		MemberkitCourseDTO curso = MemberkitDataHelper.criarCurso("course-2", "Curso Sem Aulas", "Desc");

		when(memberkitClientService.getLessonsByCourse("course-2", "api-key")).thenReturn(Collections.emptyList());
		when(missaoWakandaService.criaMissaoExterna(any(MissaoExternaDTO.class), eq(true))).thenAnswer(inv -> {
			MissaoWakanda missao = mock(MissaoWakanda.class);
			when(missao.getIdMissao()).thenReturn(UUID.randomUUID());
			return missao;
		});

		memberkitIntegrationImporter.validarConfiguracoes();
		memberkitIntegrationImporter.processarCurso(curso, new HashMap<>(), idTipoMissao, idJornada);

		verify(missaoWakandaService, times(1)).criaMissaoExterna(any(MissaoExternaDTO.class), eq(true));
	}

	@Test
	@DisplayName("Processa todos os cursos e chama processarCurso para cada curso")
	void buscarCursos_processarCadaCurso() {
		UUID idJornada = UUID.randomUUID();
		UUID idTipoMissao = UUID.randomUUID();

		MemberkitCourseDTO c1 = MemberkitDataHelper.criarCurso("c1", "Curso 1", "D1");
		MemberkitCourseDTO c2 = MemberkitDataHelper.criarCurso("c2", "Curso 2", "D2");

		when(memberkitClientService.getCourses("api-key")).thenReturn(List.of(c1, c2));
		when(memberkitClientService.getLessonsByCourse(anyString(), anyString())).thenReturn(Collections.emptyList());
		when(missaoWakandaService.criaMissaoExterna(any(MissaoExternaDTO.class), eq(true))).thenAnswer(inv -> {
			MissaoWakanda missao = mock(MissaoWakanda.class);
			when(missao.getIdMissao()).thenReturn(UUID.randomUUID());
			return missao;
		});

		memberkitIntegrationImporter.validarConfiguracoes();
		List<MemberkitCourseDTO> cursos = memberkitIntegrationImporter.buscarCursosDoMemberkit();
		Assertions.assertEquals(2, cursos.size());
		memberkitIntegrationImporter.processarCurso(c1, new HashMap<>(), idTipoMissao, idJornada);
		memberkitIntegrationImporter.processarCurso(c2, new HashMap<>(), idTipoMissao, idJornada);

		verify(memberkitClientService, times(1)).getCourses("api-key");
		verify(missaoWakandaService, times(2)).criaMissaoExterna(any(MissaoExternaDTO.class), eq(true));
	}

	@Test
	@DisplayName("Deve associar link do video na missao e registrar origem da aula ao importar")
	void processarAula_deveAssociarLinkDoVideoNaMissaoERegistrarOrigem() {
		UUID idTipoMissao = UUID.randomUUID();
		UUID idCursoPai = UUID.randomUUID();
		UUID idJornada = UUID.randomUUID();
		Map<String, List<MissaoOrigemDTO>> titulosDuplicados = new HashMap<>();

		MemberkitLessonDTO aula = MemberkitLessonDTO.builder()
				.id("lesson-video")
				.title("Aula com video")
				.content("Conteudo da aula")
				.video(MemberkitLessonDTO.Video.builder().uid(" video-123 ").build())
				.build();

		when(missaoWakandaService.criaMissaoExterna(any(MissaoExternaDTO.class), eq(true)))
				.thenReturn(mock(MissaoWakanda.class));

		memberkitIntegrationImporter.processarAula(aula, idCursoPai, titulosDuplicados, idTipoMissao, idJornada);

		ArgumentCaptor<MissaoExternaDTO> missaoCaptor = ArgumentCaptor.forClass(MissaoExternaDTO.class);
		verify(missaoWakandaService).criaMissaoExterna(missaoCaptor.capture(), eq(true));

		MissaoExternaDTO missaoExternaDTO = missaoCaptor.getValue();
		Assertions.assertEquals("lesson-video", missaoExternaDTO.getIdExterno());
		Assertions.assertEquals("Aula com video", missaoExternaDTO.getTitulo());
		Assertions.assertEquals("https://www.youtube.com/watch?v=video-123", missaoExternaDTO.getConteudoUrl());
		Assertions.assertEquals(idTipoMissao, missaoExternaDTO.getIdTipoMissao());
		Assertions.assertEquals(idJornada, missaoExternaDTO.getIdJornada());
		Assertions.assertEquals(idCursoPai, missaoExternaDTO.getIdExternoPai().orElse(null));

		Assertions.assertEquals(1, titulosDuplicados.get("Aula com video").size());
		MissaoOrigemDTO origem = titulosDuplicados.get("Aula com video").get(0);
		Assertions.assertEquals("lesson-video", origem.id());
		Assertions.assertEquals("Aula", origem.tipo());
		Assertions.assertEquals(idCursoPai.toString(), origem.cursoPai());
	}
}
