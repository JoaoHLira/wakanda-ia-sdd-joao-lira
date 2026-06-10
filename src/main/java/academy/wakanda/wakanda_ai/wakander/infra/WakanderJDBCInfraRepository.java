package academy.wakanda.wakanda_ai.wakander.infra;

import academy.wakanda.wakanda_ai.handler.APIException;
import academy.wakanda.wakanda_ai.wakander.application.api.WakanderRelatorioDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
@Log4j2
@RequiredArgsConstructor
public class WakanderJDBCInfraRepository implements WakanderJDBCRepository {
    private final JdbcClient jdbcClient;

    @Override
    public List<WakanderEstudo> buscaWakandersQueEstudaram(LocalDateTime dataInicio, LocalDateTime dataLimite) {
        String sql = """
                SELECT id_wakander, nome, whatsapp
                FROM wakander
                WHERE ultima_aula_assistida_datetime BETWEEN :dataInicio AND :dataFinal
                  AND status_financeiro = 'REGULAR'
                """;

        var params = Map.of(
                "dataInicio", Timestamp.valueOf(dataInicio),
                "dataFinal", Timestamp.valueOf(dataLimite)
        );

        return jdbcClient.sql(sql)
                .params(params)
                .query(new WakanderRowMapper())
                .list();
    }

    private static class WakanderRowMapper implements RowMapper<WakanderEstudo> {
        @Override
        public WakanderEstudo mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new WakanderEstudo(
                    rs.getString("id_wakander"),
                    rs.getString("nome"),
                    rs.getString("whatsapp"));
        }
    }

    @Override
    public WakanderRelatorioDTO buscaMetricasWakander() {
        String sqlQuery = """
                    SELECT 
                        COUNT(CASE WHEN w.jornada_atual = 'JORNADA_CONHECIMENTO' THEN 1 ELSE NULL END) AS wakanders_ativo_conhecimento,
                        SUM(CASE WHEN (w.ultima_aula_assistida_datetime BETWEEN :dataInicio AND :dataLimite) THEN 1 ELSE 0 END) AS wakanders_regular_studaram_7_dias,
                        SUM(CASE WHEN (w.ultima_aula_assistida_datetime BETWEEN :dataInicio AND :dataLimite 
                        AND w.jornada_atual = 'JORNADA_CONHECIMENTO') THEN 1 ELSE 0 END) AS wakadenders_regular_estudaram_conhecimento_7_dias,
                        (SELECT COUNT(*) FROM aula_assistida WHERE data_conclusao BETWEEN :dataInicio AND :dataLimite) AS aulas_assistidas_wakanders_7_dias,
                        SUM(CASE WHEN (w.ultima_aula_assistida_datetime BETWEEN :dataInicio30 AND :dataLimite30
                        AND w.jornada_atual = 'JORNADA_CONHECIMENTO') THEN 1 ELSE 0 END) AS wakanders_conhecimento_regular_estudaram_30_dias
                    FROM wakander w
                    WHERE w.status_financeiro = 'REGULAR'
                """;

        LocalDateTime dataAtual = LocalDateTime.now();
        LocalDateTime dataLimite = dataAtual.minusDays(7);
        LocalDateTime dataLimite30 = dataAtual.minusDays(30);

        var params = Map.of(
                "dataInicio", Timestamp.valueOf(dataLimite),
                "dataLimite", Timestamp.valueOf(dataAtual),
                "dataInicio30", Timestamp.valueOf(dataLimite30),
                "dataLimite30", Timestamp.valueOf(dataAtual)

        );

        return recuperaWakanderRelatorioDTO(sqlQuery, params);
    }

    private WakanderRelatorioDTO recuperaWakanderRelatorioDTO(String sqlQuery, Map<String, Timestamp> params) {
        try {
            return jdbcClient.sql(sqlQuery)
                    .params(params)
                    .query((rs, rowNum) -> new WakanderRelatorioDTO(
                            rs.getInt("wakanders_ativo_conhecimento"),
                            rs.getInt("wakanders_regular_studaram_7_dias"),
                            rs.getInt("wakadenders_regular_estudaram_conhecimento_7_dias"),
                            rs.getInt("aulas_assistidas_wakanders_7_dias"),
                            rs.getInt("wakanders_conhecimento_regular_estudaram_30_dias")
                    ))
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Nenhuma métrica encontrada."));
        } catch (DataAccessException e) {
            log.error("Erro ao consultar as métricas da jornada de conhecimento: {}", e.getMessage());
            throw APIException.build(HttpStatus.INTERNAL_SERVER_ERROR, "Falha ao acessar as métricas do banco de dados. Tente novamente mais tarde.");
        }
    }
}
