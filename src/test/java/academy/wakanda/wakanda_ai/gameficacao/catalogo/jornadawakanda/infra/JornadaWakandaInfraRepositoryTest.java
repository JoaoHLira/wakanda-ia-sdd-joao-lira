package academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.infra;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.datahelper.JornadaWakandaDataHelper;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.domain.JornadaWakanda;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class JornadaWakandaInfraRepositoryTest {

    @Mock
    private JornadaWakandaSpringDataJpaRepository springDataRepository;

    @InjectMocks
    private JornadaWakandaInfraRepository jornadaInfraRepository;

    private JornadaWakanda jornadaMockada;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jornadaMockada = JornadaWakandaDataHelper.criarJornadaJavaBasico();
    }

    @Test
    void deveSalvarJornadaComSucesso() {
        when(springDataRepository.save(jornadaMockada)).thenReturn(jornadaMockada);

        JornadaWakanda resultado = jornadaInfraRepository.save(jornadaMockada);

        assertEquals(jornadaMockada, resultado);
        verify(springDataRepository, times(1)).save(jornadaMockada);
    }
}

