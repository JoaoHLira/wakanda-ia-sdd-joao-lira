package academy.wakanda.wakanda_ai.autenticacao.application.service;

import academy.wakanda.wakanda_ai.autenticacao.repository.AutenticacaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Log4j2
public class AutenticacaoSchedulerService {
    private final AutenticacaoRepository autenticacaoRepository;

    @Scheduled(cron = "0 0 5 * * 3")
    public void deletaTokensExpirados() {
        log.info("[start] AutenticacaoSchedulerService - deletaTokensExpirados");
        autenticacaoRepository.deletaTokensExpirados(LocalDateTime.now().minusDays(3));
        log.debug("[finish] AutenticacaoSchedulerService - deletaTokensExpirados");
    }

}
