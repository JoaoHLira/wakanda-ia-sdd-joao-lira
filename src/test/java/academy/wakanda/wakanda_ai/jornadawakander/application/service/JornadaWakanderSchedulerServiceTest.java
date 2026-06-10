package academy.wakanda.wakanda_ai.jornadawakander.application.service;

import academy.wakanda.wakanda_ai.comunicacao.application.service.ComunicacaoService;
import academy.wakanda.wakanda_ai.comunicacao.infra.PublicadorNotificacaoSns;
import academy.wakanda.wakanda_ai.constants.MensagensWhatsapp;
import academy.wakanda.wakanda_ai.constants.TopicNames;
import academy.wakanda.wakanda_ai.handler.APIException;
import academy.wakanda.wakanda_ai.jornadawakander.datahelper.JornadaWakanderDataHelper;
import academy.wakanda.wakanda_ai.jornadawakander.domain.StatusRelatorio;
import academy.wakanda.wakanda_ai.utils.DataHelper;
import academy.wakanda.wakanda_ai.utils.templates.Templates;
import academy.wakanda.wakanda_ai.wakander.application.api.WakanderRelatorioDTO;
import academy.wakanda.wakanda_ai.wakander.application.service.WakanderInativoResponse;
import academy.wakanda.wakanda_ai.wakander.application.service.WakanderService;
import academy.wakanda.wakanda_ai.wakander.infra.WakanderEstudo;
import academy.wakanda.wakanda_ai.wakander.infra.WakanderJDBCRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static br.com.six2six.fixturefactory.loader.FixtureFactoryLoader.loadTemplates;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JornadaWakanderSchedulerServiceTest {
    @InjectMocks
    JornadaWakanderSchedulerService jornadaWakanderSchedulerService;
    @Mock
    WakanderService wakanderService;
    @Mock
    PublicadorNotificacaoSns publicadorNotificacaoSNS;
    @Mock
    TopicNames topicNames;
    @Mock
    private ComunicacaoService comunicacaoService;
    @Mock
    private WakanderJDBCRepository jdbcRepository;
    @Mock
    private HistoricoRelatorioService historicoRelatorioService;

    @BeforeAll
    static void setUp() {
        loadTemplates(Templates.BASE_PACKAGE);
    }

    @Test
    void deveNotificarWakandersNaoEstudaram() {
        WakanderEstudo wakanderEstudo1 = DataHelper.criaWakanderEstudo("Joao", "5573900000000");
        WakanderEstudo wakanderEstudo2 = DataHelper.criaWakanderEstudo("Maria", "5573911111111");

        List<WakanderEstudo> wakandersSemEstudar = List.of(wakanderEstudo1, wakanderEstudo2);

        when(wakanderService.buscaWakandersSemEstudar(any())).thenReturn(wakandersSemEstudar);
        doNothing().when(publicadorNotificacaoSNS).enviaNotificacaoSns(any(), any(), any());
        jornadaWakanderSchedulerService.notificaWakandersNaoEstudaram();

        verify(wakanderService, times(1)).buscaWakandersSemEstudar(any());
        verify(publicadorNotificacaoSNS, times(wakandersSemEstudar.size())).enviaNotificacaoSns(any(), any(), any());
    }

    @Test
    void naoDeveLancarNotificacaoQuandoListaDeWakadersSemEstudarForVazia() {
        List<WakanderEstudo> wakandersSemEstudar = List.of();

        when(wakanderService.buscaWakandersSemEstudar(any())).thenReturn(wakandersSemEstudar);
        jornadaWakanderSchedulerService.notificaWakandersNaoEstudaram();

        verify(wakanderService, times(1)).buscaWakandersSemEstudar(any());
        verify(publicadorNotificacaoSNS, never()).enviaNotificacaoSns(any(), any(), any());
    }

    @Test
    void deveGerarRelatorioQuandoExistemWakandersInativos() {
        LocalDateTime dataInatividade = LocalDate.now().minusDays(15).atTime(8, 0, 0);
        List<WakanderInativoResponse> response = WakanderInativoResponse.converteParaResponse(DataHelper.criaListaWakanders());
        String relatorio = MensagensWhatsapp.formataRelatorioWakandersInativos(response);

        when(wakanderService.buscaWakandersInativos(dataInatividade)).thenReturn(response);

        jornadaWakanderSchedulerService.geraRelatorioWakandersInativos();

        verify(wakanderService, times(1)).buscaWakandersInativos(dataInatividade);
    }

    @Test
    void deveEnviarMensagemParaLiderancaQuandoNaoExistemWakandersInativos() {
        LocalDateTime dataInatividade = LocalDate.now().minusDays(15).atTime(8, 0, 0);
        List<WakanderInativoResponse> wakandersInativos = List.of();

        when(wakanderService.buscaWakandersInativos(dataInatividade)).thenReturn(wakandersInativos);

        jornadaWakanderSchedulerService.geraRelatorioWakandersInativos();

        verify(wakanderService, times(1)).buscaWakandersInativos(dataInatividade);
    }

    @Test
    @DisplayName("Deve executar o agendamento com sucesso")
    void agendaEnvioRelatorio_EnvioRelatorioComSUcesso() {

        WakanderRelatorioDTO wakanderRelatorioDTO = JornadaWakanderDataHelper.criaWakanderRelatorioDTO();
        when(jdbcRepository.buscaMetricasWakander()).thenReturn(wakanderRelatorioDTO);

        doNothing().when(historicoRelatorioService).registraRelatorio(StatusRelatorio.SUCESSO, MensagensWhatsapp.RELATORIO_ATIVIDADE_WAKANDERS.formataRelatorioWakanderAtivos(wakanderRelatorioDTO));

        jornadaWakanderSchedulerService.agendaEnvioRelatorio();

        verify(jdbcRepository, times(1)).buscaMetricasWakander();
        verify(historicoRelatorioService, times(1)).registraRelatorio(StatusRelatorio.SUCESSO, MensagensWhatsapp.RELATORIO_ATIVIDADE_WAKANDERS.formataRelatorioWakanderAtivos(wakanderRelatorioDTO));
    }

    @Test
    @DisplayName("Deve lançar exceção quando não houver wakanders ativos na jornada de conhecimento")
    void agendaEnvioRelatorio_DeveLancarExcecaoQuandoNaoHouverWakandersAtivos() {

        WakanderRelatorioDTO wakanderRelatorioDTO = JornadaWakanderDataHelper.criaWakanderRelatorioDTOZeroMetricas();

        when(jdbcRepository.buscaMetricasWakander()).thenReturn(wakanderRelatorioDTO);

        APIException exception = assertThrows(APIException.class, () -> jornadaWakanderSchedulerService.agendaEnvioRelatorio());

        assertEquals(HttpStatus.NO_CONTENT, exception.getStatusException());
        assertEquals("Nenhum wakander ativo na jornada de conhecimento.", exception.getMessage());

        verify(historicoRelatorioService, times(0)).registraRelatorio(any(), any());
    }
}
