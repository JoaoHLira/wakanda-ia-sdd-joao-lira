package academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.infra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.MissaoWakanda;
import academy.wakanda.wakanda_ai.handler.APIException;

class MissaoWakandaInfraRepositoryTest {

    @InjectMocks
    private MissaoWakandaInfraRepository missaoInfraRepository;

    @Mock
    private MissaoWakandaSpringDataJPARepository springDataRepository;

    private MissaoWakanda missaoWakanda;
    private UUID missaoId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        missaoId = UUID.randomUUID();
        missaoWakanda = mock(MissaoWakanda.class);
    }

    @Test
    void deveSalvarMissaoComSucesso() {
        when(springDataRepository.save(missaoWakanda)).thenReturn(missaoWakanda);
        MissaoWakanda resultado = missaoInfraRepository.salvaMissao(missaoWakanda);
        assertEquals(missaoWakanda, resultado);
        verify(springDataRepository, times(1)).save(missaoWakanda);
    }

    @Test
    void deveLancarExcecaoAoSalvarMissaoDuplicada() {
        when(springDataRepository.save(missaoWakanda))
                .thenThrow(new DataIntegrityViolationException("Missão já existe!"));
        APIException ex = assertThrows(APIException.class, () -> missaoInfraRepository.salvaMissao(missaoWakanda));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusException());
        assertEquals("Missão já existe!", ex.getMessage());
        verify(springDataRepository, times(1)).save(missaoWakanda);
    }

    @Test
    void deveBuscarMissaoPorIdComSucesso() {
        when(springDataRepository.findById(missaoId)).thenReturn(Optional.of(missaoWakanda));
        MissaoWakanda resultado = missaoInfraRepository.buscaMissaoPorId(missaoId);
        assertEquals(missaoWakanda, resultado);
        verify(springDataRepository, times(1)).findById(missaoId);
    }

    @Test
    void deveLancarExcecaoQuandoMissaoNaoEncontrada() {
        when(springDataRepository.findById(missaoId)).thenReturn(Optional.empty());
        APIException ex = assertThrows(APIException.class, () -> missaoInfraRepository.buscaMissaoPorId(missaoId));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusException());
        assertEquals("Missão não encontrada!", ex.getMessage());
        verify(springDataRepository, times(1)).findById(missaoId);
    }

    @Test
    void deveValidarTituloExistente() {
        String titulo = "Missao Teste";
        when(springDataRepository.existsByTitulo(titulo)).thenReturn(true);
        boolean existe = missaoInfraRepository.validaTitulo(titulo);
        assertTrue(existe);
        verify(springDataRepository, times(1)).existsByTitulo(titulo);
    }

    @Test
    void deveValidarTituloInexistente() {
        String titulo = "Missao Inexistente";
        when(springDataRepository.existsByTitulo(titulo)).thenReturn(false);
        boolean existe = missaoInfraRepository.validaTitulo(titulo);
        assertFalse(existe);
        verify(springDataRepository, times(1)).existsByTitulo(titulo);
    }
}
