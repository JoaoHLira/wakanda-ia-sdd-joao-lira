package academy.wakanda.wakanda_ai.jornadawakander.application.service;

import academy.wakanda.wakanda_ai.jornadawakander.domain.HistoricoRelatorio;
import academy.wakanda.wakanda_ai.jornadawakander.domain.StatusRelatorio;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Log4j2
@RequiredArgsConstructor
public class HistoricoRelatorioService {

    private final HistoricoRelatorioRepository historicoRelatorioRepository;

    public void registraRelatorio(StatusRelatorio status, String mensagem) {
        log.info("[start] HistoricoRelatorioService - registraRelatorio");
        HistoricoRelatorio historicoRelatorio = HistoricoRelatorio.builder()
                .dataEnvio(LocalDateTime.now())
                .status(status)
                .mensagem(mensagem)
                .build();
        historicoRelatorioRepository.save(historicoRelatorio);
        log.debug("[finish] HistoricoRelatorioService - registraRelatorio");
    }
}
