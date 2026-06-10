package academy.wakanda.wakanda_ai.wakander.infra;

import academy.wakanda.wakanda_ai.wakander.application.api.WakanderRelatorioDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface WakanderJDBCRepository {
    List<WakanderEstudo> buscaWakandersQueEstudaram(LocalDateTime dataInicio, LocalDateTime dataLimite);
    WakanderRelatorioDTO buscaMetricasWakander();
}