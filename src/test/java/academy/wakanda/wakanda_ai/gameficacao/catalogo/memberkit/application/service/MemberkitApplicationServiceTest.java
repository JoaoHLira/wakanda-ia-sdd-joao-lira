package academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.service;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.application.service.JornadaWakandaRepository;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.domain.JornadaWakanda;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.dto.MemberkitCourseDTO;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.datahelper.MemberkitDataHelper;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.tipomissao.application.service.TipoMissaoRepository;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.tipomissao.domain.TipoMissao;
import academy.wakanda.wakanda_ai.handler.APIException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberkitApplicationServiceTest {

	@InjectMocks
	private MemberkitApplicationService service;

	@Mock
	private MemberkitIntegrationImporter importer;

	@Mock
	private TipoMissaoRepository tipoMissaoRepository;

	@Mock
	private JornadaWakandaRepository jornadaWakandaRepository;

	@Test
	@DisplayName("Valida, busca jornada, tipo e processa cursos")
	void importaMemberkit_fluxoCompleto() {
		UUID idTipoMissao = UUID.randomUUID();
		UUID idJornada = UUID.randomUUID();

		// stubs basicos
		TipoMissao tipo = Mockito.mock(TipoMissao.class);
		when(tipo.getIdTipoMissao()).thenReturn(idTipoMissao);
		when(tipoMissaoRepository.buscaTipoMissaoId(idTipoMissao)).thenReturn(tipo);

		JornadaWakanda jornadaResponse = Mockito.mock(JornadaWakanda.class);
		when(jornadaResponse.getIdJornada()).thenReturn(idJornada);
		when(jornadaWakandaRepository.buscaJornadaId(idJornada)).thenReturn(jornadaResponse);

		MemberkitCourseDTO c1 = MemberkitDataHelper.criarCurso("c1", "Curso 1", "D1");
		MemberkitCourseDTO c2 = MemberkitDataHelper.criarCurso("c2", "Curso 2", "D2");
		when(importer.buscarCursosDoMemberkit()).thenReturn(List.of(c1, c2));

		// act
		service.importaMemberkit(idTipoMissao, idJornada);

		// assert
		verify(importer, times(1)).validarConfiguracoes();
		verify(jornadaWakandaRepository, times(1)).buscaJornadaId(idJornada);
		verify(tipoMissaoRepository, times(1)).buscaTipoMissaoId(idTipoMissao);
		verify(importer, times(1)).buscarCursosDoMemberkit();
		verify(importer, times(2)).processarCurso(any(MemberkitCourseDTO.class), anyMap(), eq(idTipoMissao),
				eq(idJornada));
	}

	@Test
	@DisplayName("Lança exceção quando não há cursos")
	void importaMemberkit_semCursos_lancaExcecao() {
		UUID idTipoMissao = UUID.randomUUID();
		UUID idJornada = UUID.randomUUID();

		TipoMissao tipo = Mockito.mock(TipoMissao.class);
		when(tipo.getIdTipoMissao()).thenReturn(idTipoMissao);
		when(tipoMissaoRepository.buscaTipoMissaoId(idTipoMissao)).thenReturn(tipo);

		JornadaWakanda jornadaResponse = Mockito.mock(JornadaWakanda.class);
		when(jornadaResponse.getIdJornada()).thenReturn(idJornada);
		when(jornadaWakandaRepository.buscaJornadaId(idJornada)).thenReturn(jornadaResponse);

		when(importer.buscarCursosDoMemberkit()).thenReturn(List.of());

		Assertions.assertThrows(APIException.class,
				() -> service.importaMemberkit(idTipoMissao, idJornada));

		verify(importer, times(1)).validarConfiguracoes();
		verify(importer, times(1)).buscarCursosDoMemberkit();
		verify(importer, never()).processarCurso(any(), anyMap(), eq(idTipoMissao), eq(idJornada));
	}
}