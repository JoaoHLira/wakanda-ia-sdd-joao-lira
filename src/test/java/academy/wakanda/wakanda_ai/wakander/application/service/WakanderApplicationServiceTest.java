package academy.wakanda.wakanda_ai.wakander.application.service;

import academy.wakanda.wakanda_ai.autenticacao.application.service.AutenticacaoService;
import academy.wakanda.wakanda_ai.autenticacao.datahelper.AutenticacaoDataHelper;
import academy.wakanda.wakanda_ai.autenticacao.domain.Autenticacao;
import academy.wakanda.wakanda_ai.comunicacao.application.service.CancelaClintService;
import academy.wakanda.wakanda_ai.comunicacao.application.service.ComunicacaoService;
import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventDto;
import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventype;
import academy.wakanda.wakanda_ai.comunicacao.infra.ComunicacaoSendSqs;
import academy.wakanda.wakanda_ai.comunicacao.infra.PublicadorNotificacaoSns;
import academy.wakanda.wakanda_ai.config.security.TokenService;
import academy.wakanda.wakanda_ai.constants.MensagensWhatsapp;
import academy.wakanda.wakanda_ai.constants.TopicNames;
import academy.wakanda.wakanda_ai.financeiro.application.api.FiadorDTO;
import academy.wakanda.wakanda_ai.financeiro.datahelper.FinanceiroDataHelper;
import academy.wakanda.wakanda_ai.financeiro.domain.assinatura.AssinaturaAsaasDto;
import academy.wakanda.wakanda_ai.financeiro.domain.assinatura.AssinaturaListaAsaasDto;
import academy.wakanda.wakanda_ai.financeiro.domain.assinatura.ClienteAsaasDto;
import academy.wakanda.wakanda_ai.financeiro.infra.AsaasClient;
import academy.wakanda.wakanda_ai.handler.APIException;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.OnboardingWakanderService;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.ProgressoWakanderRepository;
import academy.wakanda.wakanda_ai.jornadawakander.domain.JornadaWakanda;
import academy.wakanda.wakanda_ai.jornadawakander.domain.OnboardingWakander;
import academy.wakanda.wakanda_ai.utils.DataHelper;
import academy.wakanda.wakanda_ai.utils.templates.Templates;
import academy.wakanda.wakanda_ai.wakander.application.api.*;
import academy.wakanda.wakanda_ai.wakander.datahelper.WakanderDataHelper;
import academy.wakanda.wakanda_ai.wakander.domain.StatusCadastro;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import academy.wakanda.wakanda_ai.wakander.domain.WakanderStatusFinanceiro;
import academy.wakanda.wakanda_ai.wakander.infra.WakanderEstudo;
import br.com.six2six.fixturefactory.Fixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static academy.wakanda.wakanda_ai.utils.templates.WakanderTemplate.WAKANDER;
import static br.com.six2six.fixturefactory.loader.FixtureFactoryLoader.loadTemplates;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WakanderApplicationServiceTest {
    @InjectMocks
    WakanderApplicationService wakanderApplicationService;
    @Mock
    WakanderRepository wakanderRepository;
    @Mock
    ProgressoWakanderRepository progressoWakanderRepository;
    @Mock
    PublicadorNotificacaoSns publicadorNotificacaoSns;
    @Mock
    ApplicationEventPublisher eventPublisher;
    @Mock
    private AsaasClient asaasClient;
    @Mock
    AutenticacaoService autenticacaoService;
    @Mock
    private OnboardingWakanderService onboardingService;
    @Mock
    CancelaClintService cancelaClintService;
    @Mock
    private TokenService tokenService;
    @Mock
    private ComunicacaoSendSqs comunicacaoSendSqs;

    private static final String URL_INSTANCIA = "http://localhost:8080";
    @Mock
    TopicNames topicNames;
    @Mock
    private ComunicacaoService comunicacaoService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(wakanderApplicationService, "urlInstancia", URL_INSTANCIA);
        lenient().when(topicNames.getZapiRequests()).thenReturn("zapi-requests-queue.fifo");
        loadTemplates(Templates.BASE_PACKAGE);
    }

    @Test
    @DisplayName("Deve gerar link de atualização de fiador com token válido")
    void geraLinkAtualizacaoFiador_ComSucesso() {

        Wakander wakander = DataHelper.criaWakander();
        String TOKEN_FAKE = "token-estatico";

        when(wakanderRepository.buscaWakanderPorId(wakander.getIdWakander())).thenReturn(wakander);
        when(tokenService.geraTokenDeAutenticacao(eq(wakander.getIdWakander()), any())).thenReturn(TOKEN_FAKE);
        ReflectionTestUtils.setField(wakanderApplicationService, "tempoExpiracaoTokenDadosComplementares", 15);

        String link = wakanderApplicationService.geraLinkAtualizacaoFiador(wakander.getIdWakander());

        assertNotNull(link);
        assertEquals(URL_INSTANCIA + "/wakanda-ai/api/formulario/atualiza-fiador/" + TOKEN_FAKE, link);
        verify(wakanderRepository, times(1)).buscaWakanderPorId(wakander.getIdWakander());
        verify(tokenService, times(1)).geraTokenDeAutenticacao(eq(wakander.getIdWakander()), any());
    }

    @Test
    @DisplayName("Deve lançar 404 quando gerar link e wakander não existir")
    void geraLinkAtualizacaoFiador_WakanderNaoEncontrado() {
        UUID idInexistente = UUID.randomUUID();
        when(wakanderRepository.buscaWakanderPorId(idInexistente))
                .thenThrow(APIException.build(HttpStatus.NOT_FOUND, "Wakander não encontrado!"));

        APIException exception = assertThrows(APIException.class, () ->
                wakanderApplicationService.geraLinkAtualizacaoFiador(idInexistente));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusException());
        verify(wakanderRepository, times(1)).buscaWakanderPorId(idInexistente);
        verify(tokenService, never()).geraTokenDeAutenticacao(any(), any());
    }

    @Test
    void deveMudarStatusFinanceiroParaRegular() {
        Wakander wakander = DataHelper.criaWakanderCancelado();
        when(wakanderRepository.buscaWakanderPorId(any())).thenReturn(wakander);
        when(wakanderRepository.save(wakander)).thenReturn(wakander);

        wakanderApplicationService.regularizaWakander(any());

        assertEquals(WakanderStatusFinanceiro.REGULAR, wakander.getFinanceiro().getStatus());

        verify(wakanderRepository, times(1)).buscaWakanderPorId(wakander.getIdWakander());
        verify(wakanderRepository, times(1)).save(wakander);
    }

    @Test
    @DisplayName("Devo lançar exceção quando wakander não for encontrado pelo ID.")
    void mudaStatusParaRegular_QuandoIdWakanderNaoForEncontrado_DeveLancarExcecao() {
        when(wakanderRepository.buscaWakanderPorId(any()))
                .thenThrow(APIException.build(HttpStatus.NOT_FOUND, "Wakander não encontrado!"));

        APIException exception = assertThrows(APIException.class, () -> {
            wakanderApplicationService.regularizaWakander(UUID.randomUUID());
        });

        assertEquals("Wakander não encontrado!", exception.getMessage());
    }

    @Test
    void deveBuscarWakanderPorId() {
        Wakander wakander = DataHelper.criaWakander();
        UUID idWakander = wakander.getIdWakander();

        when(wakanderRepository.buscaWakanderPorId(idWakander)).thenReturn(wakander);
        WakanderDetalhadoResponse wakanderDetalhadoResponse = wakanderApplicationService.buscaWakanderPorId(idWakander);

        verify(wakanderRepository, times(1)).buscaWakanderPorId(idWakander);
        assertEquals(WakanderDetalhadoResponse.class, wakanderDetalhadoResponse.getClass());
        assertNotNull(wakanderDetalhadoResponse);
    }

    @Test
    void deveRetornarExcecaoAoBuscarWakanderPorIdQuandoNaoEncontrar() {
        UUID idInexistente = UUID.randomUUID();

        when(wakanderRepository.buscaWakanderPorId(idInexistente))
                .thenThrow(APIException.build(HttpStatus.NOT_FOUND, "Wakander não encontrado!"));

        APIException exception = assertThrows(APIException.class,
                () -> wakanderApplicationService.buscaWakanderPorId(idInexistente));
        assertEquals("Wakander não encontrado!", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusException());
        verify(wakanderRepository, times(1)).buscaWakanderPorId(idInexistente);
    }

    @Test
    @DisplayName("Salva novo Wakander")
    void deveSalvarWakanderQuandoDadosForemValidos() {
        WakanderNovoRequest request = DataHelper.criaWakanderNovoRequest();
        Wakander wakander = DataHelper.criaWakander();

        when(wakanderRepository.save(any())).thenReturn(new Wakander(request));

        WakanderCriadoResponse response = wakanderApplicationService.matriculaWakander(request);
        verify(wakanderRepository, times(1)).save(any());

        assertEquals(wakander.getNome(), request.getNome());
        assertEquals(WakanderCriadoResponse.class, response.getClass());
    }

    @Test
    @DisplayName("Retorna dados duplicados ao salvar novo Wakander com mesmo CPF")
    void deveRetornaExceptionAoSalvarWakanderQuandoCpfForDuplicado() {
        WakanderNovoRequest request = DataHelper.criaWakanderNovoRequest();

        doThrow(APIException.build(HttpStatus.CONFLICT, "Existem dados duplicados"))
                .when(wakanderRepository).save(any(Wakander.class));

        APIException exception = assertThrows(APIException.class,
                () -> wakanderApplicationService.matriculaWakander(request));

        verify(wakanderRepository, times(1)).save(any());

        assertEquals("Existem dados duplicados", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatusException());
    }

    @Test
    void deveSalvarWakander() {
        Wakander wakander = DataHelper.criaWakander();

        wakanderApplicationService.salvaWakander(wakander);

        verify(wakanderRepository, times(1)).save(any());
    }

    @Test
    void deveCompletarCadastroWakander() {
        Wakander wakander = DataHelper.criaWakanderIncompleto();
        Autenticacao autenticacao = AutenticacaoDataHelper.criaAutenticacao();
        WakanderCadastroCompleto wakanderAtualizacao = WakanderDataHelper.criaWakanderCadastroCompleto();

        doNothing().when(autenticacaoService).alteraStatusTokenParaUtilizado(autenticacao.getToken());
        doNothing().when(onboardingService).save(any(OnboardingWakander.class));

        wakanderApplicationService.completaCadastroWakander(autenticacao.getToken(), wakanderAtualizacao, wakander);

        verify(wakanderRepository, times(1)).save(any());
    }

    @Test
    void deveLancarExceptionEmSalvaWakanderQuandoDadosForemDuplicados() {
        Wakander wakander = DataHelper.criaWakander();

        doThrow(APIException.build(HttpStatus.CONFLICT, "Existem dados duplicados"))
                .when(wakanderRepository).save(wakander);

        APIException ex = assertThrows(APIException.class, () -> wakanderApplicationService.salvaWakander(wakander));
        verify(wakanderRepository, times(1)).save(wakander);
        assertEquals(HttpStatus.CONFLICT, ex.getStatusException());
        assertNotNull(ex);
    }

    @Test
    void deveBuscarWakanderPorIdMemberKit() {
        Wakander wakander = DataHelper.criaWakander();
        String idMemberKit = wakander.getIdMemberKit();

        when(wakanderRepository.buscaWakanderPorIdMemberKit(idMemberKit)).thenReturn(wakander);
        Wakander wakanderBuscado = wakanderApplicationService.buscaWakanderPorIdMemberKit(idMemberKit);

        verify(wakanderRepository, times(1)).buscaWakanderPorIdMemberKit(idMemberKit);
        assertEquals(Wakander.class, wakanderBuscado.getClass());
        assertNotNull(wakanderBuscado);
    }

    @Test
    void deveRetornarExcecaoAoBuscarWakanderPorIdMemberkitQuandoNaoEncontrar() {
        String idInexistente = UUID.randomUUID().toString();

        when(wakanderRepository.buscaWakanderPorIdMemberKit(idInexistente))
                .thenThrow(
                        APIException.build(HttpStatus.NOT_FOUND, "Não há Wakander cadastrado com esse ID MemberKit!"));

        APIException exception = assertThrows(APIException.class,
                () -> wakanderApplicationService.buscaWakanderPorIdMemberKit(idInexistente));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusException());
        verify(wakanderRepository, times(1)).buscaWakanderPorIdMemberKit(idInexistente);
    }

    @Test
    void deveBuscarWakandersRegularesSemEstudar() {
        LocalDateTime dataLimite = LocalDateTime.now();
        Wakander wakander1 = Fixture.from(Wakander.class).gimme(WAKANDER);
        Wakander wakander2 = Fixture.from(Wakander.class).gimme(WAKANDER);

        when(wakanderRepository.buscaWakandersSemEstudar(dataLimite)).thenReturn(List.of(wakander1, wakander2));
        List<WakanderEstudo> wakandersSemEstudar = wakanderApplicationService.buscaWakandersSemEstudar(dataLimite);

        verify(wakanderRepository, times(1)).buscaWakandersSemEstudar(dataLimite);
        assertNotNull(wakandersSemEstudar);
        assertNotEquals(0, wakandersSemEstudar.size());
    }

    @Test
    void deveRetornarListaVaziaAoBuscarWakandersRegularesSemEstudarQuandoNaoEncontrar() {
        LocalDateTime dataLimite = LocalDateTime.now();

        when(wakanderRepository.buscaWakandersSemEstudar(dataLimite)).thenReturn(List.of());
        List<WakanderEstudo> wakandersSemEstudar = wakanderApplicationService.buscaWakandersSemEstudar(dataLimite);

        verify(wakanderRepository, times(1)).buscaWakandersSemEstudar(dataLimite);
        assertNotNull(wakandersSemEstudar);
        assertEquals(0, wakandersSemEstudar.size());
    }

    @Test
    @DisplayName("Deve atualizar o progresso para a jornada habilidade com sucesso")
    void deveAtualizarProgressoParaJornadaHabilidade() {
        Wakander wakander = DataHelper.criaWakander();
        UUID wakanderID = wakander.getIdWakander();
        JornadaWakanda jornadaJaConcluida = wakander.getJornadaAtual();

        when(wakanderRepository.buscaWakanderPorId(any())).thenReturn(wakander);
        when(wakanderRepository.save(wakander)).thenReturn(wakander);
        doNothing().when(progressoWakanderRepository).salvaProgresso(wakanderID, jornadaJaConcluida,
                JornadaWakanda.JORNADA_HABILIDADE);

        wakanderApplicationService.atualizaProgressoParaJornada(wakanderID, JornadaWakanda.JORNADA_HABILIDADE);

        verify(wakanderRepository, times(1)).save(wakander);
        verify(wakanderRepository, times(1)).buscaWakanderPorId(wakanderID);
        verify(progressoWakanderRepository, times(1)).salvaProgresso(wakanderID, jornadaJaConcluida,
                JornadaWakanda.JORNADA_HABILIDADE);
        assertEquals(JornadaWakanda.JORNADA_HABILIDADE, wakander.getJornadaAtual());
    }

    @Test
    @DisplayName("Deve atualizar o progresso para a jornada conquista com sucesso")
    void deveAtualizarProgressoParaJornadaConquista() {
        Wakander wakander = DataHelper.criaWakander();
        UUID wakanderID = wakander.getIdWakander();
        JornadaWakanda jornadaJaConcluida = wakander.getJornadaAtual();

        when(wakanderRepository.buscaWakanderPorId(any())).thenReturn(wakander);
        when(wakanderRepository.save(wakander)).thenReturn(wakander);
        doNothing().when(progressoWakanderRepository).salvaProgresso(wakanderID, jornadaJaConcluida,
                JornadaWakanda.JORNADA_CONQUISTA);

        wakanderApplicationService.atualizaProgressoParaJornada(wakanderID, JornadaWakanda.JORNADA_CONQUISTA);

        verify(wakanderRepository, times(1)).save(wakander);
        verify(wakanderRepository, times(1)).buscaWakanderPorId(wakanderID);
        verify(progressoWakanderRepository, times(1)).salvaProgresso(wakanderID, jornadaJaConcluida,
                JornadaWakanda.JORNADA_CONQUISTA);
        assertEquals(JornadaWakanda.JORNADA_CONQUISTA, wakander.getJornadaAtual());
    }

    @Test
    @DisplayName("Deve atualizar o progresso para vibraniun com sucesso")
    void deveAtualizarProgressoParaVibraniun() {
        Wakander wakander = DataHelper.criaWakander();
        UUID wakanderID = wakander.getIdWakander();
        JornadaWakanda jornadaJaConcluida = wakander.getJornadaAtual();

        when(wakanderRepository.buscaWakanderPorId(any())).thenReturn(wakander);
        when(wakanderRepository.save(wakander)).thenReturn(wakander);
        doNothing().when(progressoWakanderRepository).salvaProgresso(wakanderID, jornadaJaConcluida,
                JornadaWakanda.VIBRANIUN);

        wakanderApplicationService.atualizaProgressoParaJornada(wakanderID, JornadaWakanda.VIBRANIUN);

        verify(wakanderRepository, times(1)).save(wakander);
        verify(wakanderRepository, times(1)).buscaWakanderPorId(wakanderID);
        verify(progressoWakanderRepository, times(1)).salvaProgresso(wakanderID, jornadaJaConcluida,
                JornadaWakanda.VIBRANIUN);
        assertEquals(JornadaWakanda.VIBRANIUN, wakander.getJornadaAtual());
    }

    @Test
    @DisplayName("Deve lançar exceção quando atualizar progresso e Wakander não está regularizado")
    void deveLancarExcecaoQuandoAtualizarProgressoEWakanderNaoEstaRegularizado() {
        Wakander wakander = DataHelper.criaWakanderCancelado();
        JornadaWakanda jornadaInicial = wakander.getJornadaAtual();
        when(wakanderRepository.buscaWakanderPorId(wakander.getIdWakander())).thenReturn(wakander);

        APIException exception = assertThrows(APIException.class, () -> wakanderApplicationService
                .atualizaProgressoParaJornada(wakander.getIdWakander(), JornadaWakanda.JORNADA_HABILIDADE));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusException());
        assertEquals(jornadaInicial, wakander.getJornadaAtual());

        verify(wakanderRepository, times(1)).buscaWakanderPorId(wakander.getIdWakander());
        verify(progressoWakanderRepository, never()).salvaProgresso(wakander.getIdWakander(), jornadaInicial,
                JornadaWakanda.JORNADA_CONQUISTA);
    }

    @Test
    void deveBuscarWakandersInativosComSucesso() {
        LocalDateTime dataInatividade = LocalDateTime.now().minusDays(15);
        List<Wakander> wakanders = DataHelper.criaListaWakanders();
        when(wakanderRepository.buscaWakandersSemEstudar(dataInatividade)).thenReturn(wakanders);

        List<WakanderInativoResponse> resultado = wakanderApplicationService.buscaWakandersInativos(dataInatividade);

        verify(wakanderRepository, times(1)).buscaWakandersSemEstudar(dataInatividade);
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremWakandersInativos() {
        LocalDateTime dataInatividade = LocalDateTime.now().minusDays(15);
        List<Wakander> wakanders = List.of();

        when(wakanderRepository.buscaWakandersSemEstudar(dataInatividade)).thenReturn(wakanders);

        List<WakanderInativoResponse> resultado = wakanderApplicationService.buscaWakandersInativos(dataInatividade);

        verify(wakanderRepository, times(1)).buscaWakandersSemEstudar(dataInatividade);
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Deve solicitar o cancelamento de um Wakander com sucesso")
    void deveSolicitarCancelamentoWakander() {
        Wakander wakander = Fixture.from(Wakander.class).gimme(WAKANDER);

        doNothing().when(publicadorNotificacaoSns)
                .enviaNotificacaoSns(anyString(), any(ZApiEventDto.class), anyString());

        wakanderApplicationService.solicitaCancelamentoWakander(wakander);
    }

    @Test
    @DisplayName("Deve solicitar notificar lideranca quando o wakander já estiver cancelado")
    void deveNotificarQuandoWakanderJaEstiverCancelado() {
        Wakander wakanderReal = Fixture.from(Wakander.class).gimme(WAKANDER);
        Wakander wakander = spy(wakanderReal);

        wakander.mudaStatusFinanceiro(WakanderStatusFinanceiro.CANCELAMENTO_SOLICITADO, LocalDateTime.now());

        wakanderApplicationService.solicitaCancelamentoWakander(wakander);
    }

    @DisplayName("Deve editar um Wakander com sucesso")
    @Test
    void deveEditaWakander_Sucesso() {

        UUID idWakander = UUID.randomUUID();

        WakanderAlteracaoRequest alteracaoRequest = DataHelper.createWakanderAlteracaoRequest();
        Wakander wakander = DataHelper.criaWakander();

        when(wakanderRepository.buscaWakanderPorId(idWakander)).thenReturn(wakander);

        wakanderApplicationService.editaWakander(idWakander, alteracaoRequest);

        verify(wakanderRepository, times(1)).save(wakander);
        assertEquals("João Silva", wakander.getNome());
        assertEquals("49383316080", wakander.getCpf());
    }

    @DisplayName("Deve tratar falha ao salvar Wakander")
    @Test
    void editaWakander_Falha_AoSalvar() {

        UUID idWakander = UUID.randomUUID();

        WakanderAlteracaoRequest alteracaoRequest = DataHelper.createWakanderAlteracaoRequest();

        Wakander wakander = DataHelper.criaWakander();
        when(wakanderRepository.buscaWakanderPorId(idWakander)).thenReturn(wakander);
        doThrow(new RuntimeException("Erro ao salvar")).when(wakanderRepository).save(any(Wakander.class));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            wakanderApplicationService.editaWakander(idWakander, alteracaoRequest);
        });

        assertEquals("Erro ao salvar", exception.getMessage());
    }

    @DisplayName("DEVE cancelar assinatura COM SUCESSO quando dados válidos são fornecidos")
    @Test
    void cancelaAssinatura_Sucesso() {

        WakanderCancelaAssinaturaDTO cancelaAssinatura = WakanderDataHelper.criaWakanderCancelaAssinaturaDTO();
        Wakander wakanderReal = Fixture.from(Wakander.class).gimme(WAKANDER);
        Wakander wakander = spy(wakanderReal);
        UUID idWakander = wakander.getIdWakander();

        when(wakanderRepository.buscaWakanderPorId(idWakander)).thenReturn(wakander);
        doNothing().when(wakander).cancelaAssinatura(cancelaAssinatura);
        when(wakanderRepository.save(wakander)).thenReturn(wakander);

        wakanderApplicationService.cancelaAssinatura(idWakander, cancelaAssinatura);

        verify(wakanderRepository, times(1)).buscaWakanderPorId(idWakander);
        verify(wakanderRepository, times(1)).save(wakander);
        verify(wakander, times(1)).cancelaAssinatura(cancelaAssinatura);
    }

    @DisplayName("DEVE lançar exceção QUANDO a data de cancelamento é nula")
    @Test
    void cancelaAssinatura_DataCancelamentoNula() {

        UUID idWakander = UUID.randomUUID();
        WakanderCancelaAssinaturaDTO cancelaAssinatura = WakanderDataHelper
                .criaWakanderCancelaAssinaturaDTOSemDataCancelamento();
        Wakander wakander = mock(Wakander.class);

        when(wakanderRepository.buscaWakanderPorId(idWakander)).thenReturn(wakander);
        doThrow(new IllegalArgumentException("A data do cancelamento é obrigatória."))
                .when(wakander).cancelaAssinatura(cancelaAssinatura);

        assertThrows(IllegalArgumentException.class,
                () -> wakanderApplicationService.cancelaAssinatura(idWakander, cancelaAssinatura));
    }

    @Test
    @DisplayName("Deve retornar formulário com link para suporte quando o status de cadastro do wakander estiver completo")
    void deveRetornarFormularioSuporteQuandoStatusForCompleto() {
        Wakander wakander = DataHelper.criaStatusWakander(StatusCadastro.COMPLETO);

        Optional<ModelAndView> redirectModel = wakanderApplicationService
                .retornaFormularioCadastroCompleto(wakander);

        assertFalse(redirectModel.isEmpty());
        assertEquals("formulario-status-completo", redirectModel.get().getViewName());
    }

    @Test
    @DisplayName("Não deve retornar formulário com link para suporte quando o status de cadastro do wakander estiver incompleto")
    void naoDeveRetornarFormularioSuporteQuandoStatusForIncompleto() {
        Wakander wakander = DataHelper.criaStatusWakander(StatusCadastro.INCOMPLETO);

        Optional<ModelAndView> redirectModel = wakanderApplicationService
                .retornaFormularioCadastroCompleto(wakander);
        ModelAndView model = new ModelAndView("formulario-cadastro");

        assertTrue(redirectModel.isEmpty());
        assertNotEquals(redirectModel.orElse(null), model);
    }

    @Test
    @DisplayName("Deve buscar Wakander pelo token e retornar dados ocultos com sucesso")
    void buscaWakanderPorIdRetornaDadosOcultos_ComSucesso() {

        String mockToken = "sFr4cH-3LvoYDvPD4MVAlA";
        Wakander wakander = mock(Wakander.class);

        when(autenticacaoService.buscaWakanderPeloToken(mockToken)).thenReturn(wakander);

        WakanderComDadosPessoaisOcultoResponse response = wakanderApplicationService.buscaWakanderPorIdRetornaDadosOcultos(mockToken);

        assertNotNull(response);

        verify(autenticacaoService, times(1)).buscaWakanderPeloToken(mockToken);
        verify(wakander, times(1)).validaDadosPessoaisJaEstaoCompletos();
    }

    @Test
    @DisplayName("Deve atualizar os dados do Wakander com sucesso")
    void atualizaDadosWakander_ComSucesso() {
        UUID idWakander = UUID.randomUUID();
        WakanderCadastroCompleto wakanderDTO = DataHelper.atualizaWakanderCadastroCompleto(idWakander);
        Wakander wakanderBanco = Fixture.from(Wakander.class).gimme(WAKANDER);

        when(wakanderRepository.buscaWakanderPorId(any())).thenReturn(wakanderBanco);
        doNothing().when(publicadorNotificacaoSns).enviaNotificacaoSns(any(), any(), any());

        wakanderApplicationService.atualizaDadosWakander(wakanderDTO);

        verify(wakanderRepository, times(1)).buscaWakanderPorId(any());
        verify(wakanderRepository, times(1)).save(wakanderBanco);
    }

    @Test
    @DisplayName("Deve publicar notificação SNS para cada Wakander com dados Asaas incompletos")
    void postaWakanderComDadosAsaasIncompletoNaFila_DevePublicarNotificacaoParaCadaWakander() {

        Wakander wakander1 = DataHelper.criaWakander();
        Wakander wakander2 = DataHelper.criaWakander();

        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        ReflectionTestUtils.setField(wakander1, "idWakander", id1);
        ReflectionTestUtils.setField(wakander2, "idWakander", id2);

        List<Wakander> wakandersIncompletos = List.of(wakander1, wakander2);

        when(wakanderRepository.buscaWakandersSemDadosAsaasCompleto()).thenReturn(wakandersIncompletos);
        when(topicNames.getAsaasRequests()).thenReturn("asaas-requests-topic");

        wakanderApplicationService.postaWakanderComDadosAsaasIncompletoNaFila();

        verify(wakanderRepository, times(1)).buscaWakandersSemDadosAsaasCompleto();
        verify(publicadorNotificacaoSns, times(1)).enviaNotificacaoSns(id1.toString(), wakander1, "asaas-requests-topic");
        verify(publicadorNotificacaoSns, times(1)).enviaNotificacaoSns(id2.toString(), wakander2, "asaas-requests-topic");
    }

    @Test
    @DisplayName("Deve buscar dados do cliente e assinatura quando idAsaas estiver preenchido")
    void buscaDadosAsaas_ComIdAsaas_DeveBuscarClienteEAssinatura() {
        Wakander wakander = DataHelper.criaWakander();
        String idAsaas = "cus_G7Dvo4iphUNk";
        ReflectionTestUtils.setField(wakander.getFiador(), "idAsaas", idAsaas);

        ClienteAsaasDto clienteMock = mock(ClienteAsaasDto.class);
        AssinaturaListaAsaasDto assinaturaMock = mock(AssinaturaListaAsaasDto.class);

        when(asaasClient.buscaCliente(idAsaas)).thenReturn(clienteMock);
        when(asaasClient.buscaAssinatura(idAsaas)).thenReturn(assinaturaMock);
        when(wakanderRepository.save(wakander)).thenReturn(wakander);

        wakanderApplicationService.buscaDadosAsaas(wakander);

        verify(asaasClient).buscaCliente(idAsaas);
        verify(asaasClient).buscaAssinatura(idAsaas);
        verify(wakanderRepository).save(wakander);
    }

    @Test
    @DisplayName("Deve buscar cliente por assinatura quando idAsaas estiver vazio e idAssinatura preenchido")
    void buscaDadosAsaas_ComIdAssinatura_DeveBuscarClientePorAssinatura() {
        // Arrange
        Wakander wakander = DataHelper.criaWakander();
        String idAssinatura = "assinatura456";
        String customerId = "customerXYZ";

        ReflectionTestUtils.setField(wakander.getFiador(), "idAsaas", "");
        ReflectionTestUtils.setField(wakander.getFiador(), "idAssinatura", idAssinatura);

        AssinaturaAsaasDto assinaturaDto = mock(AssinaturaAsaasDto.class);
        ClienteAsaasDto clienteDto = mock(ClienteAsaasDto.class);

        when(assinaturaDto.getCustomer()).thenReturn(customerId);
        when(asaasClient.buscaClientePorAssinatura(idAssinatura)).thenReturn(assinaturaDto);
        when(asaasClient.buscaCliente(customerId)).thenReturn(clienteDto);
        when(wakanderRepository.save(wakander)).thenReturn(wakander);

        // Act
        wakanderApplicationService.buscaDadosAsaas(wakander);

        // Assert
        verify(asaasClient).buscaClientePorAssinatura(idAssinatura);
        verify(asaasClient).buscaCliente(customerId);
        verify(wakanderRepository).save(wakander);
    }

    @Test
    @DisplayName("Deve buscar wakanders filtrando por busca")
    void deveBuscarWakandersComBusca() {

        Pageable pageable = PageRequest.of(0, 10);
        String busca = "Lucas";
        Boolean incluiCancelados = false;

        Wakander wakander = DataHelper.criaWakander();
        Page<Wakander> page = new PageImpl<>(List.of(wakander));

        when(wakanderRepository.buscaPorQueryCpfTelefoneOuNome(pageable, busca, incluiCancelados))
                .thenReturn(page);


        Page<WakanderResponseDashboardDTO> result = wakanderApplicationService
                .buscaTodosOsWakanderComPaginacao(pageable, busca, incluiCancelados);


        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(wakander.getIdWakander(), result.getContent().get(0).getIdWakander());

        verify(wakanderRepository, times(1))
                .buscaPorQueryCpfTelefoneOuNome(pageable, busca, incluiCancelados);
    }

    @Test
    @DisplayName("Deve buscar todos os wakanders incluindo cancelados quando busca for vazia")
    void deveBuscarTodosIncluindoCancelados() {
        Pageable pageable = PageRequest.of(0, 10);
        String busca = "";
        Boolean incluiCancelados = true;

        Wakander wakander = DataHelper.criaWakander();
        Page<Wakander> page = new PageImpl<>(List.of(wakander));

        when(wakanderRepository.buscaTodosWakandersPaginado(pageable))
                .thenReturn(page);

        Page<WakanderResponseDashboardDTO> result = wakanderApplicationService
                .buscaTodosOsWakanderComPaginacao(pageable, busca, incluiCancelados);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(wakander.getIdWakander(), result.getContent().get(0).getIdWakander());

        verify(wakanderRepository, times(1))
                .buscaTodosWakandersPaginado(pageable);
    }

    @Test
    @DisplayName("Deve buscar wakanders regulares quando busca for vazia")
    void deveBuscarWakandersRegularesQuandoBuscaForVazia() {
        Pageable pageable = PageRequest.of(0, 10);
        String busca = null;
        Boolean incluiCancelados = false;

        Wakander wakander = DataHelper.criaWakander();
        Page<Wakander> page = new PageImpl<>(List.of(wakander));

        when(wakanderRepository.buscaWakandersRegularesPaginado(pageable))
                .thenReturn(page);

        Page<WakanderResponseDashboardDTO> result = wakanderApplicationService
                .buscaTodosOsWakanderComPaginacao(pageable, busca, incluiCancelados);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(wakander.getIdWakander(), result.getContent().get(0).getIdWakander());

        verify(wakanderRepository, times(1))
                .buscaWakandersRegularesPaginado(pageable);
    }

    @Test
    @DisplayName("Deve buscar estatísticas incluindo wakanders cancelados")
    void deveBuscarEstatisticasIncluindoCancelados() {
        var wakander1 = DataHelper.criaWakander();
        var wakander2 = DataHelper.criaWakanderCancelado();
        List<Wakander> wakanders = List.of(wakander1, wakander2);

        when(wakanderRepository.buscaTodosWakanders()).thenReturn(wakanders);

        var resultado = wakanderApplicationService.buscaEstatisticasWakanders(true);

        assertNotNull(resultado);
        assertEquals(2, resultado.getTotalWakanders());

        verify(wakanderRepository, times(1)).buscaTodosWakanders();
        verify(wakanderRepository, never()).buscaWakanderPorStatusFinanceiro(any());
    }

    @Test
    @DisplayName("Deve buscar estatísticas apenas dos wakanders regulares")
    void deveBuscarEstatisticasApenasRegulares() {
        var wakander1 = DataHelper.criaWakander();
        List<Wakander> wakanders = List.of(wakander1);

        when(wakanderRepository.buscaWakanderPorStatusFinanceiro(WakanderStatusFinanceiro.REGULAR))
                .thenReturn(wakanders);

        var resultado = wakanderApplicationService.buscaEstatisticasWakanders(false);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalWakanders());

        verify(wakanderRepository, times(1))
                .buscaWakanderPorStatusFinanceiro(WakanderStatusFinanceiro.REGULAR);
        verify(wakanderRepository, never()).buscaTodosWakanders();
    }

    @Test
    @DisplayName("Deve buscar wakanders por status com paginação")
    void deveBuscarWakandersPorStatusComPaginacao() {
        Pageable pageable = PageRequest.of(0, 10);
        StatusCadastro statusCadastro = StatusCadastro.COMPLETO;
        boolean incluiCancelados = false;

        Wakander wakander = DataHelper.criaWakander();
        Page<Wakander> page = new PageImpl<>(List.of(wakander));

        when(wakanderRepository.buscaWakandersPorStatus(pageable, statusCadastro, incluiCancelados))
                .thenReturn(page);

        Page<WakanderResponseDashboardDTO> resultado = wakanderApplicationService
                .buscaPorStatus(pageable, statusCadastro, incluiCancelados);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals(wakander.getIdWakander(), resultado.getContent().get(0).getIdWakander());

        verify(wakanderRepository, times(1))
                .buscaWakandersPorStatus(pageable, statusCadastro, incluiCancelados);
    }

    @Test
    @DisplayName("Deve atualizar status cadastro de wakanders regulares e salvar")
    void deveAtualizarStatusCadastro() {
        Wakander wakander1 = mock(Wakander.class);
        Wakander wakander2 = mock(Wakander.class);
        List<Wakander> wakanderList = List.of(wakander1, wakander2);

        when(wakanderRepository.buscaWakanderPorStatusFinanceiro(WakanderStatusFinanceiro.REGULAR))
                .thenReturn(wakanderList);

        when(wakanderRepository.saveAll(wakanderList)).thenReturn(wakanderList);

        wakanderApplicationService.atualizaStatusCadastro();

        verify(wakander1, times(1)).atualizaStatusCadastro();
        verify(wakander2, times(1)).atualizaStatusCadastro();

        verify(wakanderRepository, times(1)).saveAll(wakanderList);

        verify(wakanderRepository, times(1))
                .buscaWakanderPorStatusFinanceiro(WakanderStatusFinanceiro.REGULAR);
    }


    @Test
    @DisplayName("Deve enviar formulário para wakander com telefone válido")
    void solicitaEnvioFormularioParaWakanderComTelefone() {
        String TOKEN_FAKE = "token-fake";

        Wakander wakander = DataHelper.criaWakander();

        when(wakanderRepository.buscaWakanderComDadosPessoaisIncompletos())
                .thenReturn(List.of(wakander));

        when(tokenService.geraTokenDeAutenticacao(eq(wakander.getIdWakander()), any()))
                .thenReturn(TOKEN_FAKE);

        ReflectionTestUtils.setField(wakanderApplicationService, "lideresPhoneGroupId", "123456");
        ReflectionTestUtils.setField(wakanderApplicationService, "tempoExpiracaoTokenDadosComplementares", 15);

        wakanderApplicationService.solicitaEnvioDeFormularioDadosComplementares();

        ArgumentCaptor<ZApiEventDto> captor = ArgumentCaptor.forClass(ZApiEventDto.class);
        verify(comunicacaoSendSqs, times(1)).enviaMensagemZAPIComDelay(captor.capture(), eq(1));

        ZApiEventDto eventoEnviado = captor.getValue();
        assertNotNull(eventoEnviado);
        assertEquals(wakander.getContato().getWhatsapp(), eventoEnviado.getWhatsapp());
        assertTrue(eventoEnviado.getMensagem().contains(wakander.getNome()));
        assertEquals(ZApiEventype.NORMAL_MESSAGE, eventoEnviado.getType());
    }

    @Test
    @DisplayName("Deve notificar grupo de líderes quando wakander não possui contato")
    void solicitaEnvioFormularioParaWakanderSemTelefone_notificaLideres() {
        Wakander wakander = DataHelper.criaWakanderSemContato();

        when(wakanderRepository.buscaWakanderComDadosPessoaisIncompletos())
                .thenReturn(List.of(wakander));

        ReflectionTestUtils.setField(wakanderApplicationService, "lideresPhoneGroupId", "123456");

        wakanderApplicationService.solicitaEnvioDeFormularioDadosComplementares();

        ArgumentCaptor<ZApiEventDto> captor = ArgumentCaptor.forClass(ZApiEventDto.class);
        verify(comunicacaoSendSqs).enviaMensagemZAPIComDelay(captor.capture(), eq(1));

        ZApiEventDto eventoEnviado = captor.getValue();
        assertEquals("123456", eventoEnviado.getWhatsapp()); // deve ir para o grupo dos líderes
        assertTrue(eventoEnviado.getMensagem().contains("👤 *Nome:* " + wakander.getNome()));
        assertTrue(eventoEnviado.getMensagem().contains("📞 *WhatsApp:* Não cadastrado!"));
        assertEquals(ZApiEventype.NORMAL_MESSAGE, eventoEnviado.getType());
    }

    @Test
    @DisplayName("Deve atualizar os dados do fiador do wakander")
    void atualizaFiador_ComSucesso() {
        String token = "YhngL1DqQXNNuwPnp5LrWy";
        Wakander wakander = DataHelper.criaWakander();
        FiadorDTO fiadorDTO = FinanceiroDataHelper.criaFiadorDTO();

        when(autenticacaoService.buscaWakanderPeloToken(token)).thenReturn(wakander);
        when(wakanderRepository.save(wakander)).thenReturn(wakander);

        wakanderApplicationService.atualizaFiador(token, fiadorDTO);

        verify(autenticacaoService, times(1)).buscaWakanderPeloToken(token);
        verify(wakanderRepository, times(1)).save(wakander);
    }

    @Test
    @DisplayName("Suporte inicia onboarding manual com sucesso")
    void iniciaOnboardingManual_DeveEnviarBoasVindasAoFiador() {
        UUID idWakander = UUID.randomUUID();
        String TOKEN_FAKE = "token-fake";

        Wakander wakander = DataHelper.criaWakanderIncompleto();
        ReflectionTestUtils.setField(wakander, "idWakander", idWakander);

        when(wakanderRepository.buscaWakanderPorId(idWakander)).thenReturn(wakander);
        when(tokenService.geraTokenDeAutenticacao(eq(idWakander), eq(5))).thenReturn(TOKEN_FAKE);

        ReflectionTestUtils.setField(wakanderApplicationService, "tempoExpiracaoTokenCadastro", 5);

        wakanderApplicationService.iniciaOnboardingManual(idWakander);

        ArgumentCaptor<ZApiEventDto> captor = ArgumentCaptor.forClass(ZApiEventDto.class);
        verify(publicadorNotificacaoSns, times(2)).enviaNotificacaoSns(
                eq(idWakander.toString()),
                captor.capture(),
                eq("zapi-requests-queue.fifo")
        );

        String expectedUrl = URL_INSTANCIA + "/wakanda-ai/api/formulario/cadastro/" + TOKEN_FAKE;
        String expectedBoasVindas = MensagensWhatsapp.MENSAGEM_BOAS_VINDAS.getMensagem(expectedUrl);
        String expectedChecklist = MensagensWhatsapp.mensagemChecklistPadrao();

        List<ZApiEventDto> eventosEnviados = captor.getAllValues();
        assertEquals(2, eventosEnviados.size());

        assertEquals(ZApiEventype.NORMAL_MESSAGE, eventosEnviados.get(0).getType());
        assertEquals(wakander.getFiador().getTelefone(), eventosEnviados.get(0).getWhatsapp());
        assertEquals(expectedBoasVindas, eventosEnviados.get(0).getMensagem());

        assertEquals(ZApiEventype.NORMAL_MESSAGE, eventosEnviados.get(1).getType());
        assertEquals(wakander.getFiador().getTelefone(), eventosEnviados.get(1).getWhatsapp());
        assertEquals(expectedChecklist, eventosEnviados.get(1).getMensagem());
        verify(tokenService).geraTokenDeAutenticacao(idWakander, 5);
    }

    @Test
    @DisplayName("Deve bloquear onboarding manual quando cadastro do Wakander já estiver COMPLETO")
    void iniciaOnboardingManual_DeveBloquearQuandoCadastroJaEstiverCompleto() {
        UUID idWakander = UUID.randomUUID();

        Wakander wakander = DataHelper.criaStatusWakander(StatusCadastro.COMPLETO);
        ReflectionTestUtils.setField(wakander, "idWakander", idWakander);

        when(wakanderRepository.buscaWakanderPorId(idWakander)).thenReturn(wakander);

        APIException exception = assertThrows(
                APIException.class,
                () -> wakanderApplicationService.iniciaOnboardingManual(idWakander)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusException());
        assertEquals("O Wakander ja está com cadastro completo!", exception.getMessage());

        verify(publicadorNotificacaoSns, never()).enviaNotificacaoSns(
                anyString(),
                any(ZApiEventDto.class),
                anyString()
        );
    }

    @Test
    @DisplayName("Deve bloquear onboarding manual quando Wakander não estiver regular financeiramente")
    void iniciaOnboardingManual_DeveBloquearQuandoWakanderNaoEstiverRegular() {
        UUID idWakander = UUID.randomUUID();

        Wakander wakander = DataHelper.criaWakanderIncompleto();
        ReflectionTestUtils.setField(wakander, "idWakander", idWakander);
        wakander.mudaStatusFinanceiro(WakanderStatusFinanceiro.CANCELADO, LocalDateTime.now());

        when(wakanderRepository.buscaWakanderPorId(idWakander)).thenReturn(wakander);

        APIException exception = assertThrows(
                APIException.class,
                () -> wakanderApplicationService.iniciaOnboardingManual(idWakander)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusException());
        assertEquals("Wakander não está regularizado!", exception.getMessage());

        verify(publicadorNotificacaoSns, never()).enviaNotificacaoSns(
                anyString(),
                any(ZApiEventDto.class),
                anyString()
        );
    }

    @DisplayName("Deve buscar wakanders com sucesso quando o pageable não possuir ordenação")
    @Test
    void deveBuscarWakandersQuandoPageableSemOrdenacao() {

        WakanderPaginadoRequest filtros = mock(WakanderPaginadoRequest.class);
        Pageable pageable = PageRequest.of(0, 10);

        Wakander wakander = mock(Wakander.class);
        Page<Wakander> retornoRepository = new PageImpl<>(List.of(wakander));

        when(wakanderRepository.buscarWakanders(filtros, pageable))
                .thenReturn(retornoRepository);

        Page<WakanderPaginadoResponse> resultado =
                wakanderApplicationService.buscarWakanders(filtros, pageable);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getTotalElements()).isEqualTo(1);
        verify(wakanderRepository, times(1)).buscarWakanders(filtros, pageable);
        verifyNoMoreInteractions(wakanderRepository);
    }

    @DisplayName("Deve buscar wakanders com sucesso quando a ordenação for pelo campo permitido nome")
    @Test
    void deveBuscarWakandersQuandoOrdenacaoForValida() {

        WakanderPaginadoRequest filtros = mock(WakanderPaginadoRequest.class);
        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by(Sort.Order.asc("nome"))
        );

        Wakander wakander = mock(Wakander.class);
        Page<Wakander> retornoRepository = new PageImpl<>(List.of(wakander));

        when(wakanderRepository.buscarWakanders(filtros, pageable))
                .thenReturn(retornoRepository);

        Page<WakanderPaginadoResponse> resultado =
                wakanderApplicationService.buscarWakanders(filtros, pageable);

        assertThat(resultado).isNotNull();

        verify(wakanderRepository, times(1))
                .buscarWakanders(filtros, pageable);

        verifyNoMoreInteractions(wakanderRepository);
    }

    @DisplayName("Deve lançar BAD_REQUEST quando o campo de ordenação informado for inválido")
    @Test
    void deveLancarExcecaoQuandoCampoOrdenacaoForInvalido() {

        WakanderPaginadoRequest filtros = mock(WakanderPaginadoRequest.class);
        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by(Sort.Order.asc("cpf"))
        );

        APIException exception = assertThrows(
                APIException.class,
                () -> wakanderApplicationService.buscarWakanders(filtros, pageable)
        );

        assertThat(exception.getStatusException()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getMessage())
                .isEqualTo("Campo de ordenação inválido: cpf");

        verify(wakanderRepository, never())
                .buscarWakanders(any(), any());

        verifyNoInteractions(wakanderRepository);
    }

    @DisplayName("Deve lançar BAD_REQUEST quando houver múltiplos campos de ordenação e um deles for inválido")
    @Test
    void deveLancarExcecaoQuandoHouverMultiplosCamposEUmForInvalido() {

        WakanderPaginadoRequest filtros = mock(WakanderPaginadoRequest.class);
        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by(
                        Sort.Order.asc("nome"),
                        Sort.Order.asc("email")
                )
        );

        APIException exception = assertThrows(
                APIException.class,
                () -> wakanderApplicationService.buscarWakanders(filtros, pageable)
        );

        assertThat(exception.getStatusException()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getMessage())
                .isEqualTo("Campo de ordenação inválido: email");

        verify(wakanderRepository, never())
                .buscarWakanders(any(), any());

        verifyNoInteractions(wakanderRepository);
    }
}
