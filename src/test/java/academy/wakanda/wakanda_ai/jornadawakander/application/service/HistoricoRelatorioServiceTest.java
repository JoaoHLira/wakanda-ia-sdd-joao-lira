package academy.wakanda.wakanda_ai.jornadawakander.application.service;

import academy.wakanda.wakanda_ai.jornadawakander.domain.StatusRelatorio;
import academy.wakanda.wakanda_ai.jornadawakander.infra.HistoricoRelatorioInfraRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HistoricoRelatorioServiceTest {

    @InjectMocks
    HistoricoRelatorioService historicoRelatorioService;

    @Mock
    HistoricoRelatorioInfraRepository historicoRelatorioInfraRepository;

    @Test
    @DisplayName("Deve Registar relatório com sucesso")
    void registraRelatorio_Sucesso() {
        StatusRelatorio status = StatusRelatorio.SUCESSO;
        String mensagem = "Relatório enviado com sucesso.";

        historicoRelatorioService.registraRelatorio(status, mensagem);

        verify(historicoRelatorioInfraRepository, times(1)).save(argThat(historicoRelatorio ->
                historicoRelatorio.getStatus() == status &&
                        historicoRelatorio.getMensagem().equals(mensagem) &&
                        historicoRelatorio.getDataEnvio() != null
        ));
    }
}