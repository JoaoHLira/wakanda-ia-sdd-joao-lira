package academy.wakanda.wakanda_ai.autenticacao.application.service;

import academy.wakanda.wakanda_ai.autenticacao.repository.AutenticacaoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutenticacaoSchedulerServiceTest {

    @InjectMocks
    private AutenticacaoSchedulerService autenticacaoSchedulerService;

    @Mock
    private AutenticacaoRepository autenticacaoRepository;

    @Test
    @DisplayName("Deve deletar tokens com data de expiração menor que 3 dias")
    void deveDeletarTokensComDataBaseMenorQueTresDias() {
        doNothing().when(autenticacaoRepository).deletaTokensExpirados(any(LocalDateTime.class));

        autenticacaoSchedulerService.deletaTokensExpirados();

        ArgumentCaptor<LocalDateTime> captor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(autenticacaoRepository, times(1)).deletaTokensExpirados(captor.capture());
        LocalDateTime dataBasePassada = captor.getValue();

        LocalDateTime esperado = LocalDateTime.now().minusDays(3);
        long diferencaEmSegundos = Math.abs(ChronoUnit.SECONDS.between(dataBasePassada, esperado));
        assertTrue("A data base deve ser aproximadamente (now - 3 dias), com tolerância de 15s",
                diferencaEmSegundos < 15);
    }

    @Test
    @DisplayName("Deve lançar exceção quando ocorrer algum erro no repositório")
    void deveLancarExcecaoQuandoOcorrerErroNoRepositorio() {
        doThrow(new RuntimeException("Falha ao deletar"))
                .when(autenticacaoRepository).deletaTokensExpirados(any(LocalDateTime.class));

        assertThrows(RuntimeException.class, () -> autenticacaoSchedulerService.deletaTokensExpirados());
        verify(autenticacaoRepository, times(1)).deletaTokensExpirados(any(LocalDateTime.class));
    }
}
