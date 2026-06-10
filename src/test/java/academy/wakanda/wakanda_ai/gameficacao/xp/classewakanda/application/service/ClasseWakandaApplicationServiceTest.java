package academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.application.api.ClasseWakandaRequest;
import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.application.api.ClasseWakandaResponse;
import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.application.repository.ClasseWakandaRepository;
import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.datahelper.ClasseWakandaDataHelper;
import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.domain.ClasseWakanda;
import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.domain.ClasseWakandaStatus;
import academy.wakanda.wakanda_ai.handler.APIException;

@ExtendWith(MockitoExtension.class)
class ClasseWakandaApplicationServiceTest {

	@InjectMocks
	private ClasseWakandaApplicationService classeWakandaApplicationService;

	@Mock
	private ClasseWakandaRepository classeWakandaRepository;

	@Mock
	private ValidadorMissoesClasse validadorMissoesClasse;

	@Test
	@DisplayName("Deve criar classe com sucesso quando não há classes existentes")
	void criaNovaClasseComSucessoSemOrdemPrevia() {
		ClasseWakandaRequest request = ClasseWakandaDataHelper.criarClasseWakandaRequestValida();

		when(classeWakandaRepository.verificaSeExistePorNome(request.getNome())).thenReturn(false);
		when(classeWakandaRepository.verificaSeExistePorDescricao(request.getDescricao())).thenReturn(false);
		when(classeWakandaRepository.buscaMaiorOrdemClasse()).thenReturn(Optional.empty());
		when(classeWakandaRepository.salva(any(ClasseWakanda.class)))
				.thenReturn(ClasseWakandaDataHelper.criarClasseWakanda(request, 1));

		ClasseWakandaResponse response = classeWakandaApplicationService.criaNovaClasse(request);

		verify(classeWakandaRepository).salva(any(ClasseWakanda.class));
		assertNotNull(response);
	}

	@Test
	@DisplayName("Deve criar classe com sucesso quando existe ordem anterior")
	void criaNovaClasseComSucessoComOrdemPrevia() {
		ClasseWakandaRequest request = ClasseWakandaDataHelper.criarClasseWakandaRequestValida();

		when(classeWakandaRepository.verificaSeExistePorNome(request.getNome())).thenReturn(false);
		when(classeWakandaRepository.verificaSeExistePorDescricao(request.getDescricao())).thenReturn(false);
		when(classeWakandaRepository.buscaMaiorOrdemClasse()).thenReturn(Optional.of(3));

		classeWakandaApplicationService.criaNovaClasse(request);

		ArgumentCaptor<ClasseWakanda> captor = ArgumentCaptor.forClass(ClasseWakanda.class);
		verify(classeWakandaRepository).salva(captor.capture());
		assertEquals(4, captor.getValue().getOrdemClasse());
	}

	@Test
	@DisplayName("Deve lançar exceção quando nome já existe")
	void criaNovaClasseComNomeDuplicado() {
		ClasseWakandaRequest request = ClasseWakandaDataHelper.criarClasseWakandaRequestValida();

		when(classeWakandaRepository.verificaSeExistePorNome(request.getNome())).thenReturn(true);
		when(classeWakandaRepository.verificaSeExistePorDescricao(request.getDescricao())).thenReturn(false);

		APIException exception = assertThrows(APIException.class,
				() -> classeWakandaApplicationService.criaNovaClasse(request));

		assertEquals("Já existe uma classe com esse nome!", exception.getMessage());
		verify(classeWakandaRepository, never()).buscaMaiorOrdemClasse();
		verify(classeWakandaRepository, never()).salva(any());
	}

	@Test
	@DisplayName("Deve lançar exceção quando descrição já existe")
	void criaNovaClasseComDescricaoDuplicada() {
		ClasseWakandaRequest request = ClasseWakandaDataHelper.criarClasseWakandaRequestValida();

		when(classeWakandaRepository.verificaSeExistePorNome(request.getNome())).thenReturn(false);
		when(classeWakandaRepository.verificaSeExistePorDescricao(request.getDescricao())).thenReturn(true);

		APIException exception = assertThrows(APIException.class,
				() -> classeWakandaApplicationService.criaNovaClasse(request));

		assertEquals("Já existe uma classe com essa descrição!", exception.getMessage());
		verify(classeWakandaRepository, never()).buscaMaiorOrdemClasse();
		verify(classeWakandaRepository, never()).salva(any());
	}

	@Test
	@DisplayName("Deve lançar exceção quando nome e descrição já existem")
	void criaNovaClasseComNomeEDescricaoDuplicados() {
		ClasseWakandaRequest request = ClasseWakandaDataHelper.criarClasseWakandaRequestValida();

		when(classeWakandaRepository.verificaSeExistePorNome(request.getNome())).thenReturn(true);
		when(classeWakandaRepository.verificaSeExistePorDescricao(request.getDescricao())).thenReturn(true);

		APIException exception = assertThrows(APIException.class,
				() -> classeWakandaApplicationService.criaNovaClasse(request));

		assertEquals("Já existe uma classe com esse nome e essa descrição!", exception.getMessage());
		verify(classeWakandaRepository, never()).buscaMaiorOrdemClasse();
		verify(classeWakandaRepository, never()).salva(any());
	}

	@Test
	@DisplayName("Deve lançar exceção quando nível necessário é inválido")
	void criaNovaClasseComNivelNecessarioInvalido() {
		ClasseWakandaRequest request = ClasseWakandaDataHelper.criarClasseWakandaRequestComNivel(0);

		when(classeWakandaRepository.verificaSeExistePorNome(request.getNome())).thenReturn(false);
		when(classeWakandaRepository.verificaSeExistePorDescricao(request.getDescricao())).thenReturn(false);
		when(classeWakandaRepository.buscaMaiorOrdemClasse()).thenReturn(Optional.empty());

		APIException exception = assertThrows(APIException.class,
				() -> classeWakandaApplicationService.criaNovaClasse(request));

		assertEquals("Nível Necessário deve ser maior que zero!", exception.getMessage());
		verify(classeWakandaRepository, never()).salva(any());
	}

	@Test
	@DisplayName("Deve lançar exceção quando sabedorias estão vazias")
	void criaNovaClasseSemSabedorias() {
		ClasseWakandaRequest request = ClasseWakandaDataHelper.criarClasseWakandaRequestSemSabedorias();

		when(classeWakandaRepository.verificaSeExistePorNome(request.getNome())).thenReturn(false);
		when(classeWakandaRepository.verificaSeExistePorDescricao(request.getDescricao())).thenReturn(false);
		when(classeWakandaRepository.buscaMaiorOrdemClasse()).thenReturn(Optional.empty());

		APIException exception = assertThrows(APIException.class,
				() -> classeWakandaApplicationService.criaNovaClasse(request));

		assertEquals("Informe ao menos um tipo de sabedoria!", exception.getMessage());
		verify(classeWakandaRepository, never()).salva(any());
	}

	@Test
	@DisplayName("Deve lançar exceção quando missões necessárias estão vazias")
	void criaNovaClasseSemMissoesNecessarias() {
		ClasseWakandaRequest request = ClasseWakandaDataHelper.criarClasseWakandaRequestSemMissoes();

		when(classeWakandaRepository.verificaSeExistePorNome(request.getNome())).thenReturn(false);
		when(classeWakandaRepository.verificaSeExistePorDescricao(request.getDescricao())).thenReturn(false);
		when(classeWakandaRepository.buscaMaiorOrdemClasse()).thenReturn(Optional.empty());

		APIException exception = assertThrows(APIException.class,
				() -> classeWakandaApplicationService.criaNovaClasse(request));

		assertEquals("Informe ao menos uma missão necessária!", exception.getMessage());
		verify(classeWakandaRepository, never()).salva(any());
	}

	@Test
	@DisplayName("Deve lançar exceção quando nível necessário já existe")
	void criaNovaClasseComNivelNecessarioDuplicado() {
		ClasseWakandaRequest request = ClasseWakandaDataHelper.criarClasseWakandaRequestValida();

		when(classeWakandaRepository.verificaSeExistePorNome(request.getNome())).thenReturn(false);
		when(classeWakandaRepository.verificaSeExistePorDescricao(request.getDescricao())).thenReturn(false);
		when(classeWakandaRepository.verificaSeExistePorNivelNecessario(request.getNivelNecessario())).thenReturn(true);

		APIException exception = assertThrows(APIException.class,
				() -> classeWakandaApplicationService.criaNovaClasse(request));

		assertEquals("Já existe uma classe com esse nível necessário!", exception.getMessage());
		verify(validadorMissoesClasse, never()).valida(any());
		verify(classeWakandaRepository, never()).buscaMaiorOrdemClasse();
		verify(classeWakandaRepository, never()).salva(any());
	}

	@Test
	@DisplayName("Deve lançar exceção quando validação de missões falha")
	void criaNovaClasseComMissoesInvalidas() {
		ClasseWakandaRequest request = ClasseWakandaDataHelper.criarClasseWakandaRequestValida();
		APIException erro = APIException.build(HttpStatus.BAD_REQUEST,
				"Erro na validação das missões. Não encontradas: [id]. Inativas: []");

		when(classeWakandaRepository.verificaSeExistePorNome(request.getNome())).thenReturn(false);
		when(classeWakandaRepository.verificaSeExistePorDescricao(request.getDescricao())).thenReturn(false);
		doThrow(erro).when(validadorMissoesClasse).valida(request.getMissoesNecessarias());

		APIException exception = assertThrows(APIException.class,
				() -> classeWakandaApplicationService.criaNovaClasse(request));

		assertEquals("Erro na validação das missões. Não encontradas: [id]. Inativas: []", exception.getMessage());
		verify(classeWakandaRepository, never()).salva(any());
	}

	@Test
	@DisplayName("Deve buscar classe por ID com sucesso")
	void buscaClassePorIdComSucesso() {
		ClasseWakandaRequest request = ClasseWakandaDataHelper.criarClasseWakandaRequestValida();
		ClasseWakanda classeWakanda = ClasseWakandaDataHelper.criarClasseWakanda(request, 2);
		UUID idClasse = UUID.randomUUID();

		when(classeWakandaRepository.buscaClassePorId(idClasse)).thenReturn(classeWakanda);

		ClasseWakandaResponse response = classeWakandaApplicationService.buscaClassePorId(idClasse);

		assertNotNull(response);
		assertEquals(classeWakanda.getStatus(), response.getStatus());
	}

	@Test
	@DisplayName("Deve lançar exceção quando classe não é encontrada")
	void buscaClassePorIdNaoEncontrada() {
		UUID idClasse = UUID.randomUUID();
		APIException notFound = APIException.build(HttpStatus.NOT_FOUND, "Classe Wakanda não encontrada");

		when(classeWakandaRepository.buscaClassePorId(idClasse)).thenThrow(notFound);

		APIException exception = assertThrows(APIException.class,
				() -> classeWakandaApplicationService.buscaClassePorId(idClasse));

		assertEquals("Classe Wakanda não encontrada", exception.getMessage());
	}

	@Test
	@DisplayName("Deve buscar classes ativas com sucesso, retornando nome e requisitos")
	void buscaClassesAtivas_quandoExistemClassesAtivas_deveRetornarComNomeERequisitos() {
		ClasseWakandaRequest request = ClasseWakandaDataHelper.criarClasseWakandaRequestValida();
		ClasseWakanda classe1 = ClasseWakandaDataHelper.criarClasseWakanda(request, 1);
		ClasseWakanda classe2 = ClasseWakandaDataHelper.criarClasseWakanda(request, 2);

		when(classeWakandaRepository.buscaPorStatus(ClasseWakandaStatus.ATIVA)).thenReturn(List.of(classe1, classe2));

		var response = classeWakandaApplicationService.buscaClassesAtivas();

		assertNotNull(response);
		assertEquals(2, response.size());

		verify(classeWakandaRepository, times(1)).buscaPorStatus(ClasseWakandaStatus.ATIVA);
	}

	@Test
	@DisplayName("Não deve buscar classes com status diferente de ATIVA")
	void buscaClassesAtivas_deveBuscarSomentePorStatusAtiva() {
		when(classeWakandaRepository.buscaPorStatus(ClasseWakandaStatus.ATIVA)).thenReturn(List.of());

		classeWakandaApplicationService.buscaClassesAtivas();

		verify(classeWakandaRepository, times(1)).buscaPorStatus(ClasseWakandaStatus.ATIVA);
	}

	@Test
	@DisplayName("Quando não existir nenhuma classe ativa, deve retornar lista vazia")
	void buscaClassesAtivas_quandoNaoExistemClassesAtivas_deveRetornarListaVazia() {
		when(classeWakandaRepository.buscaPorStatus(ClasseWakandaStatus.ATIVA)).thenReturn(List.of());

		var response = classeWakandaApplicationService.buscaClassesAtivas();

		assertNotNull(response);
		assertTrue(response.isEmpty());

		verify(classeWakandaRepository, times(1)).buscaPorStatus(ClasseWakandaStatus.ATIVA);
	}

	@Test
	@DisplayName("Deve atualizar requisitos de uma classe com sucesso quando dados são válidos e sem missões inativas")
	void atualizaRequisitosClasse_ComSucesso() {
		var requestOriginal = ClasseWakandaDataHelper.criarClasseWakandaRequestValida();
		var classeExistente = ClasseWakandaDataHelper.criarClasseWakanda(requestOriginal, 3);
		var patch = ClasseWakandaDataHelper.patchParcial();
		UUID idClasse = UUID.randomUUID();

		when(classeWakandaRepository.buscaClassePorId(idClasse)).thenReturn(classeExistente);
		when(classeWakandaRepository.verificaSeExistePorNome(patch.getNome())).thenReturn(false);
		when(classeWakandaRepository.verificaSeExistePorDescricao(patch.getDescricao())).thenReturn(false);

		when(classeWakandaRepository.salva(any(ClasseWakanda.class))).thenReturn(classeExistente);

		classeWakandaApplicationService.atualizaRequisitosClasse(patch, idClasse);

		verify(classeWakandaRepository).buscaClassePorId(idClasse);
		verify(classeWakandaRepository).verificaSeExistePorNome(patch.getNome());
		verify(classeWakandaRepository).verificaSeExistePorDescricao(patch.getDescricao());
		verify(classeWakandaRepository).salva(any(ClasseWakanda.class));
	}

	@Test
	@DisplayName("Deve impedir atualização quando novo nome já existe em outra classe")
	void atualizaRequisitosClasse_NomeDuplicado() {
		var requestOriginal = ClasseWakandaDataHelper.criarClasseWakandaRequestValida();
		var classeExistente = ClasseWakandaDataHelper.criarClasseWakanda(requestOriginal, 2);
		var patch = ClasseWakandaDataHelper.patchParcial();
		UUID idClasse = UUID.randomUUID();

		when(classeWakandaRepository.buscaClassePorId(idClasse)).thenReturn(classeExistente);
		when(classeWakandaRepository.verificaSeExistePorNome(patch.getNome())).thenReturn(true);

		APIException ex = assertThrows(APIException.class,
				() -> classeWakandaApplicationService.atualizaRequisitosClasse(patch, idClasse));

		assertEquals("Já existe uma classe com esse nome!", ex.getMessage());
		verify(classeWakandaRepository, never()).salva(any());
	}

	@Test
	@DisplayName("Deve impedir atualização quando nova descrição já existe em outra classe")
	void atualizaRequisitosClasse_DescricaoDuplicada() {
		var requestOriginal = ClasseWakandaDataHelper.criarClasseWakandaRequestValida();
		var classeExistente = ClasseWakandaDataHelper.criarClasseWakanda(requestOriginal, 2);
		var patch = ClasseWakandaDataHelper.patchParcial();
		UUID idClasse = UUID.randomUUID();

		when(classeWakandaRepository.buscaClassePorId(idClasse)).thenReturn(classeExistente);
		when(classeWakandaRepository.verificaSeExistePorDescricao(patch.getDescricao())).thenReturn(true);

		APIException ex = assertThrows(APIException.class,
				() -> classeWakandaApplicationService.atualizaRequisitosClasse(patch, idClasse));

		assertEquals("Já existe uma classe com essa descrição!", ex.getMessage());
		verify(classeWakandaRepository, never()).salva(any());
	}

	@Test
	@DisplayName("Deve impedir atualização quando missões necessárias são removidas (lista vazia)")
	void atualizaRequisitosClasse_MissoesNecessariasVazias() {
		var requestOriginal = ClasseWakandaDataHelper.criarClasseWakandaRequestValida();
		var classeExistente = ClasseWakandaDataHelper.criarClasseWakanda(requestOriginal, 2);
		var patch = ClasseWakandaDataHelper.patchParcialSemMissoes();
		UUID idClasse = UUID.randomUUID();

		when(classeWakandaRepository.buscaClassePorId(idClasse)).thenReturn(classeExistente);

		APIException ex = assertThrows(APIException.class,
				() -> classeWakandaApplicationService.atualizaRequisitosClasse(patch, idClasse));

		assertEquals("Informe ao menos uma missão necessária!", ex.getMessage());
		verify(classeWakandaRepository, never()).salva(any());
	}

}
