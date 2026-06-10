package academy.wakanda.wakanda_ai.wakander.infra;

import academy.wakanda.wakanda_ai.wakander.application.api.WakanderRelatorioDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class WakanderJDBCInfraRepositoryTest {

    @Mock
    private JdbcClient jdbcClient;

    @Mock
    private JdbcClient.StatementSpec statementSpec;

    @Mock
    private JdbcClient.MappedQuerySpec<WakanderRelatorioDTO> mappedQuerySpec;

    @InjectMocks
    WakanderJDBCInfraRepository wakanderJDBCInfraRepository;

    private LocalDateTime dataAtual;
    private LocalDateTime dataLimite;
    private LocalDateTime dataLimite30;

    @BeforeEach
    void setUP() {
        dataAtual = LocalDateTime.now();
        dataLimite = dataAtual.minusDays(7);
        dataLimite30 = dataAtual.minusDays(30);
    }

    @DisplayName("Deve retornar métricas quando a consulta for bem-sucedida ")
    @Test
    void buscaMetricasWakander_QuandoConsultaBemSucedida_RetornaMetricas() {

        var params = Map.of(
                "dataInicio", Timestamp.valueOf(dataLimite),
                "dataLimite", Timestamp.valueOf(dataAtual),
                "dataInicio30", Timestamp.valueOf(dataLimite30),
                "dataLimiteNovo30", Timestamp.valueOf(dataAtual)
        );

        var resultadoEsperado = new WakanderRelatorioDTO(
                100,
                80,
                70,
                200,
                90);

        when(jdbcClient.sql(anyString())).thenReturn(statementSpec);
        when(statementSpec.params(anyMap())).thenReturn(statementSpec);
        when(statementSpec.query(any(RowMapper.class))).thenReturn(mappedQuerySpec);
        when(mappedQuerySpec.stream()).thenReturn(Stream.of(resultadoEsperado));

        var resultado = wakanderJDBCInfraRepository.buscaMetricasWakander();

        assertNotNull(resultado);
        assertEquals(resultadoEsperado, resultado);
        verify(jdbcClient, times(1)).sql(anyString());
        verify(statementSpec, times(1)).params(anyMap());
        verify(statementSpec, times(1)).query(any(RowMapper.class));
        verify(mappedQuerySpec, times(1)).stream();
    }

    @DisplayName("Deve lançar exceção quando a consulta falhar")
    @Test
    void buscaMetricasWakander_QuandoConsultaFalhar_EntaoLancaExcecao() {

        var params = Map.of(
                "dataInicio", Timestamp.valueOf(dataLimite),
                "dataLimite", Timestamp.valueOf(dataAtual),
                "dataInicio30", Timestamp.valueOf(dataLimite30),
                "dataLimiteNovo30", Timestamp.valueOf(dataAtual)
        );

        when(jdbcClient.sql(anyString())).thenReturn(statementSpec);
        when(statementSpec.params(anyMap())).thenReturn(statementSpec);
        when(statementSpec.query(any(RowMapper.class))).thenThrow(new DataAccessException("Erro de banco de dados") {
        });

        var exeption = assertThrows(RuntimeException.class, () -> wakanderJDBCInfraRepository.buscaMetricasWakander());

        assertEquals("Falha ao acessar as métricas do banco de dados. Tente novamente mais tarde.", exeption.getMessage());
        verify(jdbcClient, times(1)).sql(anyString());
        verify(statementSpec, times(1)).params(anyMap());
        verify(statementSpec, times(1)).query(any(RowMapper.class));
    }

    @DisplayName("Deve Lançar exceção quando nenhuma métrica for encontrada")
    @Test
    void buscaMetricasWakander_QuandoNenhumaMetricaEncontrada_EntaoLancaExcecao() {

        var params = Map.of(
                "dataInicio", Timestamp.valueOf(dataLimite),
                "dataLimite", Timestamp.valueOf(dataAtual),
                "dataInicio30", Timestamp.valueOf(dataLimite30),
                "dataLimiteNovo30", Timestamp.valueOf(dataAtual)
        );

        when(jdbcClient.sql(anyString())).thenReturn(statementSpec);
        when(statementSpec.params(anyMap())).thenReturn(statementSpec);
        when(statementSpec.query(any(RowMapper.class))).thenReturn(mappedQuerySpec);
        when(mappedQuerySpec.stream()).thenReturn(Stream.empty());

        var exception = assertThrows(RuntimeException.class, () -> wakanderJDBCInfraRepository.buscaMetricasWakander());

        assertEquals("Nenhuma métrica encontrada.", exception.getMessage());
        verify(jdbcClient, times(1)).sql(anyString());
        verify(statementSpec, times(1)).params(anyMap());
        verify(statementSpec, times(1)).query(any(RowMapper.class));
        verify(mappedQuerySpec, times(1)).stream();
    }
}