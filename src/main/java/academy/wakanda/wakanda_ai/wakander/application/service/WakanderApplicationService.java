package academy.wakanda.wakanda_ai.wakander.application.service;

import academy.wakanda.wakanda_ai.autenticacao.application.service.AutenticacaoService;
import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventDto;
import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventype;
import academy.wakanda.wakanda_ai.comunicacao.infra.ComunicacaoSendSqs;
import academy.wakanda.wakanda_ai.comunicacao.infra.PublicadorNotificacaoSns;
import academy.wakanda.wakanda_ai.config.security.TokenService;
import academy.wakanda.wakanda_ai.constants.MensagensWhatsapp;
import academy.wakanda.wakanda_ai.constants.TopicNames;
import academy.wakanda.wakanda_ai.financeiro.application.api.FiadorDTO;
import academy.wakanda.wakanda_ai.financeiro.domain.assinatura.AssinaturaAsaasDto;
import academy.wakanda.wakanda_ai.financeiro.infra.AsaasClient;
import academy.wakanda.wakanda_ai.handler.APIException;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.OnboardingWakanderService;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.ProgressoWakanderRepository;
import academy.wakanda.wakanda_ai.jornadawakander.domain.JornadaWakanda;
import academy.wakanda.wakanda_ai.jornadawakander.domain.OnboardingWakander;
import academy.wakanda.wakanda_ai.wakander.application.api.*;
import academy.wakanda.wakanda_ai.wakander.application.event.AssinaturaCanceladaEvent;
import academy.wakanda.wakanda_ai.wakander.application.event.CadastroCompletoEvent;
import academy.wakanda.wakanda_ai.wakander.application.event.DiscordEventRequest;
import academy.wakanda.wakanda_ai.wakander.domain.*;
import academy.wakanda.wakanda_ai.wakander.infra.WakanderEstudo;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.*;

import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Service
@RequiredArgsConstructor
@Log4j2
public class WakanderApplicationService implements WakanderService {
    private final WakanderRepository wakanderRepository;
    private final ProgressoWakanderRepository progressoWakanderRepository;
    private final PublicadorNotificacaoSns publicadorNotificacaoSns;
    private final TopicNames topicNames;
    private final ApplicationEventPublisher eventPublisher;
    private final AsaasClient asaasClient;
    private final OnboardingWakanderService onboardingWakanderService;
    private final ComunicacaoSendSqs comunicacaoSendSqs;
    private final AutenticacaoService autenticacaoService;
    private final TokenService tokenService;

    private static final Set<String> CAMPOS_ORDENACAO_PERMITIDOS = Set.of("nome");

    @Value("${aws.url}")
    private String urlInstancia;
    @Value("${z-api.lideres-group-id}")
    private String lideresPhoneGroupId;
    @Value("${autenticacao.tempo-expiracao-cadastro}")
    private Integer tempoExpiracaoTokenCadastro;
    @Value("${autenticacao.tempo-expiracao-dados-complementares}")
    private Integer tempoExpiracaoTokenDadosComplementares;

    @Deprecated
    @Override
    public WakanderCriadoResponse matriculaWakander(WakanderNovoRequest wakanderNovo) {
        log.info("[start] WakanderApplicationService - matriculaWakander");
        Wakander wakanderCriado = new Wakander(wakanderNovo);
        wakanderRepository.save(wakanderCriado);
        log.debug("[finish] WakanderApplicationService - matriculaWakander");
        return new WakanderCriadoResponse(wakanderCriado);
    }

    @Override
    public String geraLinkAtualizacaoFiador(UUID idWakander) {
        log.info("[start] WakanderApplicationService - geraLinkAtualizacaoFiador");
        log.debug("[idWakander]: {}", idWakander);
        wakanderRepository.buscaWakanderPorId(idWakander);
        String token = tokenService.geraTokenDeAutenticacao(idWakander, tempoExpiracaoTokenDadosComplementares);
        String url = UriComponentsBuilder.fromHttpUrl(urlInstancia)
                .path("/wakanda-ai/api/formulario/atualiza-fiador")
                .path("/")
                .path(token)
                .toUriString();
        log.debug("[formulario] Link do Formulário de atualização de fiador {}", url);
        log.debug("[finish] WakanderApplicationService - geraLinkAtualizacaoFiador");
        return url;
    }

    @Override
    public WakanderDetalhadoResponse buscaWakanderPorId(UUID idWakander) {
        log.info("[start] WakanderApplicationService - buscaWakanderPorId");
        log.debug("[idWakender]: {}", idWakander);
        Wakander wakander = wakanderRepository.buscaWakanderPorId(idWakander);
        log.debug("[finish] WakanderApplicationService - buscaWakanderPorId");
        return new WakanderDetalhadoResponse(wakander);
    }

    @Override
    public void regularizaWakander(UUID idWakander) {
        log.info("[start] WakanderApplicationService - regularizaWakander");
        log.debug("[idWakander]: {}", idWakander);
        Wakander wakander = wakanderRepository.buscaWakanderPorId(idWakander);
        wakander.mudaStatusFinanceiro(WakanderStatusFinanceiro.REGULAR, LocalDateTime.now());
        wakanderRepository.save(wakander);
        log.debug("[finish] WakanderApplicationService - regularizaWakander");
    }

    @Override
    public Wakander buscaWakanderPorIdMemberKit(String idMemberKit) {
        log.info("[start] WakanderApplicationService - buscaWakanderPorIdMemberKit");
        log.info("[idMemberKit]: {}", idMemberKit);
        Wakander wakander = wakanderRepository.buscaWakanderPorIdMemberKit(idMemberKit);
        log.debug("[finish] WakanderApplicationService - buscaWakanderPorIdMemberKit");
        return wakander;
    }

    @Override
    public void salvaWakander(Wakander wakander) {
        log.info("[start] WakanderApplicationService - salvaWakander");
        wakanderRepository.save(wakander);
        log.debug("[finish] WakanderApplicationService - salvaWakander");
    }

    @Override
    public List<WakanderEstudo> buscaWakandersSemEstudar(LocalDateTime dataLimite) {
        log.info("[start] WakanderApplicationService - buscaWakandersSemEstudar");
        List<Wakander> wakanders = wakanderRepository.buscaWakandersSemEstudar(dataLimite);
        log.debug("[finish] WakanderApplicationService - buscaWakandersSemEstudar");
        return WakanderEstudo.converteParaResponse(wakanders);
    }

    @Override
    public List<WakanderInativoResponse> buscaWakandersInativos(LocalDateTime dataInatividade) {
        log.info("[start] WakanderApplicationService - buscaWakandersInativos");
        List<Wakander> wakanders = wakanderRepository.buscaWakandersSemEstudar(dataInatividade);
        log.debug("[finish] WakanderApplicationService - buscaWakandersInativos");
        return WakanderInativoResponse.converteParaResponse(wakanders);
    }

    @Override
    public void atualizaProgressoParaJornada(UUID idWakander, JornadaWakanda jornada) {
        log.info("[start] WakanderApplicationService - Atualiza Progresso Para {}", jornada);
        Wakander wakander = wakanderRepository.buscaWakanderPorId(idWakander);
        JornadaWakanda jornadaJaConcluida = wakander.getJornadaAtual();
        wakander.atualizaProgresso(jornada);
        salvaWakander(wakander);
        progressoWakanderRepository.salvaProgresso(idWakander, jornadaJaConcluida, jornada);
        log.debug("[finish] WakanderApplicationService - Atualiza Progresso Para {}", jornada);
    }

    @Override
    public void solicitaCancelamentoWakander(Wakander wakander) {
        log.info("[start] WakanderApplicationService - solicitaCancelamentoWakander");
        String mensagem = atualizaStatusFinanceiro(wakander);
        publicaSnsMensagemWhatsapp(wakander.getIdWakander(), lideresPhoneGroupId, mensagem);
        log.debug("[finish] WakanderApplicationService - solicitaCancelamentoWakander");
    }

    private void publicaSnsMensagemWhatsapp(UUID idWakander, String whatsapp, String mensagem) {
        log.info("[start] WakanderApplicationService - publicaSnsMensagemWhatsapp");
        ZApiEventDto ZApiEventDto = new ZApiEventDto(ZApiEventype.NORMAL_MESSAGE, whatsapp, mensagem);
        publicadorNotificacaoSns.enviaNotificacaoSns(
                idWakander.toString(),
                ZApiEventDto,
                topicNames.getZapiRequests()
        );
        log.debug("[finish] WakanderApplicationService - publicaSnsMensagemWhatsapp");
    }

    private String atualizaStatusFinanceiro(Wakander wakander) {
        try {
            atualizaParaCancelamentoSolicitado(wakander);
            String nomeWakander = obtemNomeValido(wakander);
            return MensagensWhatsapp.FORMULARIO_CANCELAMENTO_WAKANDER.getMensagem(nomeWakander)
                    + urlFormularioCancelamento(wakander.getIdWakander());
        } catch (APIException e) {
            return mensagemErroSolicitacaoCancelamento(wakander);
        }
    }

    private void atualizaParaCancelamentoSolicitado(Wakander wakander) {
        wakander.validaStatusCancelado();
        wakander.mudaStatusFinanceiro(WakanderStatusFinanceiro.CANCELAMENTO_SOLICITADO, LocalDateTime.now());
        wakanderRepository.save(wakander);
    }

    private String mensagemErroSolicitacaoCancelamento(Wakander wakander) {
        String statusMensagem = wakander.getFinanceiro().getStatus().getDescricao();
        String nomeWakander = obtemNomeValido(wakander);
        String mensagem = MensagensWhatsapp.NOTIFICA_ERRO_WAKANDER_STATUS_CANCELAMENTO
                .mensagemErroAguardandoCancelamento(nomeWakander, statusMensagem);
        return mensagem;
    }

    private String urlFormularioCancelamento(UUID idWakander) {
        String url = UriComponentsBuilder.fromHttpUrl(urlInstancia)
                .path("/wakanda-ai/api/formulario/").path("/cancelamento-assinatura/")
                .path(idWakander.toString()).toUriString();
        log.debug("[formulario] Link do Formulário de cancelamento {}", url);
        return url;
    }

    private String obtemNomeValido(Wakander wakander) {
        return (wakander.getNome() != null && !wakander.getNome().isBlank())
                ? wakander.getNome()
                : wakander.getFiador().getNome();
    }

    @Override
    public void editaWakander(UUID idWakander, WakanderAlteracaoRequest wakanderAlteracao) {
        log.info("[start] WakanderApplicationService - editaWakander");
        Wakander wakander = wakanderRepository.buscaWakanderPorId(idWakander);
        wakander.altera(wakanderAlteracao);
        wakanderRepository.save(wakander);
        log.debug("[finish] WakanderApplicationService - editaWakander");
    }

    @Override
    public Wakander buscaWakanderPorIdAssinatura(String idAssinatura) {
        log.info("[start] WakanderApplicationService - buscaWakanderPorIdAssinatura");
        Wakander wakander = wakanderRepository.buscaWakanderPorIdAssinatura(idAssinatura)
                .orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Wakander não encontrado para assinatura!"));
        log.debug("[finish] WakanderApplicationService - buscaWakanderPorIdAssinatura");
        return wakander;
    }

    @Override
    public void completaCadastroWakander(String token, WakanderCadastroCompleto request, Wakander wakander) {
        log.info("[start] WakanderApplicationService - completaCadastroWakander");
        wakander.completaCadastro(request);
        wakanderRepository.save(wakander);
        autenticacaoService.alteraStatusTokenParaUtilizado(token);
        onboardingWakanderService.save(new OnboardingWakander(wakander));
        publicaEventoCadastroCompleto(wakander);
        log.debug("[finish] WakanderApplicationService - completaCadastroWakander");
    }

    private void publicaEventoCadastroCompleto(Wakander wakanderCadastro) {
        log.info("[start] WakanderApplicationService - publicaEventoCadastroCompleto");
        CadastroCompletoEvent evento = new CadastroCompletoEvent(wakanderCadastro);
        log.debug("[sending] Evento com valor {}", evento);
        eventPublisher.publishEvent(evento);
        log.debug("[finish] WakanderApplicationService - publicaEventoCadastroCompleto");
    }

    @Override
    public void cancelaAssinatura(UUID idWakander, WakanderCancelaAssinaturaDTO wakanderCancelaAssinatura) {
        log.info("[start] WakanderApplicationService - cancelaAssinatura");
        Wakander wakander = wakanderRepository.buscaWakanderPorId(idWakander);
        wakander.cancelaAssinatura(wakanderCancelaAssinatura);
        wakanderRepository.save(wakander);
        publicaEventoAssinaturaCancelada(wakander);
        log.debug("[finish] WakanderApplicationService - cancelaAssinatura");
    }

    private void publicaEventoAssinaturaCancelada(Wakander wakander) {
        log.info("[start] WakanderApplicationService - publicaEventoAssinaturaCancelada");
        AssinaturaCanceladaEvent evento = new AssinaturaCanceladaEvent(wakander);
        log.debug("[sending] Evento com valor {}", evento);
        eventPublisher.publishEvent(evento);
        log.debug("[finish] WakanderApplicationService - publicaEventoAssinaturaCancelada");
    }

    @Override
    public void reverteCancelamento(UUID idWaknder, WakanderCancelaAssinaturaDTO desistiDoCancelamento) {
        log.info("[start] WakanderApplicationService - reverteCancelamento");
        Wakander wakander = wakanderRepository.buscaWakanderPorId(idWaknder);
        wakander.reverteCancelamentoParaRegular(desistiDoCancelamento);
        wakanderRepository.save(wakander);
        log.debug("[finish] WakanderApplicationService - reverteCancelamento");
    }

    @Override
    public WakanderComDadosPessoaisOcultoResponse buscaWakanderPorIdRetornaDadosOcultos(String token) {
        log.info("[start] WakanderApplicationService - buscaWakanderPorIdRetornaDadosOcultos");
        Wakander wakander = autenticacaoService.buscaWakanderPeloToken(token);
        wakander.validaDadosPessoaisJaEstaoCompletos();
        WakanderComDadosPessoaisOcultoResponse wakanderResponse = new WakanderComDadosPessoaisOcultoResponse(wakander);
        log.debug("[finish] WakanderApplicationService - buscaWakanderPorIdRetornaDadosOcultos");
        return wakanderResponse;
    }

    @Override
    public void atualizaDadosWakander(WakanderCadastroCompleto wakander) {
        log.info("[start] WakanderApplicationService - atualizaDadosWakander");
        Wakander wakanderBanco = wakanderRepository.buscaWakanderPorId(wakander.getIdWakander());
        wakanderBanco.verificaStatusDoCadastro();
        wakanderBanco.preencheDadosPessoaisIncompletos(wakander);
        wakanderBanco.atualizaStatusCadastro();
        wakanderRepository.save(wakanderBanco);
        publicadorNotificacaoDadosCompletos(wakanderBanco);
        log.debug("[finish] WakanderApplicationService - atualizaDadosWakander");
    }

    private void publicadorNotificacaoDadosCompletos(Wakander wakander) {
        DiscordEventRequest canalComunicacaoEnvelope = new DiscordEventRequest(
                DiscordEventype.SEND_DISCORD_URL, wakander);
        publicadorNotificacaoSns.enviaNotificacaoSns(wakander.getIdWakander().toString(), canalComunicacaoEnvelope,
                topicNames.getDiscordRequest());
    }

    public Optional<ModelAndView> retornaFormularioCadastroCompleto(Wakander wakander) {
        log.info("[start] WakanderApplicationService - retornaFormularioCadastroCompleto");
        if (wakander.validaStatusCadastroWakander(StatusCadastro.COMPLETO)) {
            ModelAndView modelAndView = new ModelAndView("formulario-status-completo");
            modelAndView.addObject("wakander", wakander);
            return Optional.of(modelAndView);
        }
        log.debug("[finish] WakanderApplicationService - retornaFormularioCadastroCompleto");
        return Optional.empty();
    }

    @Override
    public void postaWakanderComDadosAsaasIncompletoNaFila() {
        log.info("[start] WakanderApplicationService - atualizadadosAsaasWakander");
        List<Wakander> wakandersSemDadosAsaas = wakanderRepository.buscaWakandersSemDadosAsaasCompleto();
        wakandersSemDadosAsaas.forEach(wakander -> {
            publicadorNotificacaoSns.enviaNotificacaoSns(wakander.getIdWakander().toString(), wakander, topicNames.getAsaasRequests());
        });
        log.debug("[finish] WakanderApplicationService - atualizadadosAsaasWakander");
    }

    @Override
    public void buscaDadosAsaas(Wakander wakander) {
        log.info("[start] WakanderApplicationService - buscaDadosAsaas");
        if (wakander.getFiador() != null && isNotBlank(wakander.getFiador().getIdAsaas())) {
            wakander.atualizaDadosFiadorAsaas(asaasClient.buscaCliente(wakander.getFiador().getIdAsaas()));
            wakander.atualizaDadosFiadorAsaas(asaasClient.buscaAssinatura(wakander.getFiador().getIdAsaas()));
        } else if (wakander.getFiador() != null && isNotBlank(wakander.getFiador().getIdAssinatura())) {
            AssinaturaAsaasDto assinaturaAsaasDto = asaasClient.buscaClientePorAssinatura(wakander.getFiador().getIdAssinatura());
            wakander.atualizaDadosFiadorAsaas(asaasClient.buscaCliente(assinaturaAsaasDto.getCustomer()));
        }
        wakander.atualizaStatusCadastro();
        tentaSalvarWakander(wakander);
        log.debug("[finish] WakanderApplicationService - buscaDadosAsaas");
    }

    private void tentaSalvarWakander(Wakander wakander) {
        log.info("[start] WakanderApplicationService - tentaSalvarWakander");
        try {
            wakanderRepository.save(wakander);
            log.debug("[finish] WakanderApplicationService - tentaSalvarWakander");
        } catch (TransactionSystemException e) {
            log.error("Erro ao atualizar dados do wakander: " + wakander.getNome());
            log.error(e.getMessage());
        }
    }

    @Override
    public Page<WakanderResponseDashboardDTO> buscaTodosOsWakanderComPaginacao(Pageable pageable, String busca, Boolean incluiCancelados) {
        log.info("[start] WakanderApplicationService - buscaTodosOsWakanderComPaginacao");
        if (isNotBlank(busca))
            return wakanderRepository.buscaPorQueryCpfTelefoneOuNome(pageable, busca, incluiCancelados).map(WakanderResponseDashboardDTO::new);
        var page = incluiCancelados ? wakanderRepository.buscaTodosWakandersPaginado(pageable) : wakanderRepository.buscaWakandersRegularesPaginado(pageable);
        log.debug("[finish] WakanderApplicationService - buscaTodosOsWakanderComPaginacao");
        return page.map(WakanderResponseDashboardDTO::new);
    }

    @Override
    public EstatisticaWakanderDTO buscaEstatisticasWakanders(Boolean incluiCancelados) {
        log.info("[start] WakanderApplicationService - buscaEstatisticasWakanders");
        EstatisticaWakanderDTO estatisticaWakanderDTO = incluiCancelados ? new EstatisticaWakanderDTO(wakanderRepository.buscaTodosWakanders())
                : new EstatisticaWakanderDTO(wakanderRepository.buscaWakanderPorStatusFinanceiro(WakanderStatusFinanceiro.REGULAR));
        log.debug("[finish] WakanderApplicationService - buscaEstatisticasWakanders");
        return estatisticaWakanderDTO;
    }

    @Override
    public Page<WakanderResponseDashboardDTO> buscaPorStatus(Pageable pageable, StatusCadastro statusCadastro, boolean incluiCancelados) {
        log.info("[start] WakanderApplicationService - buscaPorStatus");
        Page<WakanderResponseDashboardDTO> listWakander = wakanderRepository.buscaWakandersPorStatus(pageable, statusCadastro, incluiCancelados)
                .map(WakanderResponseDashboardDTO::new);
        log.debug("[finish] WakanderApplicationService - buscaPorStatus");
        return listWakander;
    }

    @Override
    public void atualizaStatusCadastro() {
        log.info("[start] WakanderApplicationService - atualizaStatusCadastro");
        List<Wakander> wakanderList = wakanderRepository.buscaWakanderPorStatusFinanceiro(WakanderStatusFinanceiro.REGULAR);
        wakanderList.forEach(Wakander::atualizaStatusCadastro);
        wakanderRepository.saveAll(wakanderList);
        log.debug("[finish] WakanderApplicationService - atualizaStatusCadastro");
    }

    @Override
    public void solicitaEnvioDeFormularioDadosComplementares() {
        log.info("[start] WakanderApplicationService - solicitaEnvioDeFormularioDadosComplementares");
        List<Wakander> wakanders = wakanderRepository.buscaWakanderComDadosPessoaisIncompletos();
        processaEnvioDeMensagens(wakanders);
        log.debug("[finish] WakanderApplicationService - solicitaEnvioDeFormularioDadosComplementares");
    }

    private void processaEnvioDeMensagens(List<Wakander> wakandersList) {
        log.info("[start] WakanderApplicationService - enviaSolicitacaoParaFilaComGerenciamentoDeIndex");
        int index = 1;
        List<Wakander> wakanderSemContato = new ArrayList<>();
        for (Wakander wakander : wakandersList) {
            String telefone = pegaTelefoneDeWakander(wakander);
            if (telefone != null) {
                enviarMensagemPara(wakander, telefone, index++);
            } else {
                wakanderSemContato.add(wakander);
            }
        }
        notificarLideresSobreWakandersSemContato(wakanderSemContato, index);
        log.debug("[finish] WakanderApplicationService - enviaSolicitacaoParaFilaComGerenciamentoDeIndex");
    }

    private void enviarMensagemPara(Wakander wakander, String telefone, int index) {
        ZApiEventDto evento = montarEventoParaWakander(wakander, telefone);
        comunicacaoSendSqs.enviaMensagemZAPIComDelay(evento, index);
    }

    private void notificarLideresSobreWakandersSemContato(List<Wakander> wakanderSemContato, Integer index) {
        log.info("[start] WakanderApplicationService - notificarLideresSobreWakandersSemContato");
        if (wakanderSemContato.isEmpty()) return;
        String mensagem = MensagensWhatsapp.MENSAGEM_LIDERES_WAKANDER_SEM_CONTATO.mensagemLideresWakanderSemContato(wakanderSemContato);
        ZApiEventDto evento = new ZApiEventDto(ZApiEventype.NORMAL_MESSAGE, lideresPhoneGroupId, mensagem);
        comunicacaoSendSqs.enviaMensagemZAPIComDelay(evento, index);
        log.debug("[finish] WakanderApplicationService - notificarLideresSobreWakandersSemContato");
    }

    private String pegaTelefoneDeWakander(Wakander wakander) {
        return Optional.ofNullable(wakander.getContato()).map(WakanderContato::getWhatsapp).filter(s -> !s.isBlank())
                .orElseGet(() -> Optional.ofNullable(wakander.getFiador()).map(WakanderFiador::getTelefone).filter(s -> !s.isBlank())
                        .orElse(null));
    }

    private ZApiEventDto montarEventoParaWakander(Wakander wakander, String telefoneDestino) {
        log.info("[start] WakanderApplicationService - montarEventoParaWakander");
        String mensagem = MensagensWhatsapp.MENSAGEM_ENVIO_FORMULARIO_DADOS_PENDENTE.mensagemEnvioFormularioDadosPendente(wakander.getNome(), urlFormularioDadosPendentes(wakander));
        log.debug("[finish] WakanderApplicationService - montarEventoParaWakander");
        return new ZApiEventDto(ZApiEventype.NORMAL_MESSAGE, telefoneDestino, mensagem);
    }

    private String urlFormularioDadosPendentes(Wakander wakander) {
        String token = tokenService.geraTokenDeAutenticacao(wakander.getIdWakander(), tempoExpiracaoTokenDadosComplementares);
        String url = UriComponentsBuilder.fromHttpUrl(urlInstancia)
                .path("/wakanda-ai/api/formulario/dados-complementares")
                .path("/" + token)
                .toUriString();
        log.debug("[formulario] Link do Formulário {}", url);
        return url;
    }

    @Override
    public void iniciaOnboardingManual(UUID idWakander) {
        log.info("[start] WakanderApplicationService - iniciaOnboardingManual");
        Wakander wakander = wakanderRepository.buscaWakanderPorId(idWakander);
        wakander.validaElegibilidadeOnboardingManual();
        String token = tokenService.geraTokenDeAutenticacao(wakander.getIdWakander(), tempoExpiracaoTokenCadastro);
        String urlFormularioCadastro = UriComponentsBuilder.fromHttpUrl(urlInstancia)
                .path("/wakanda-ai/api/formulario/cadastro")
                .path("/" + token)
                .toUriString();
        List<String> mensagens = geraMensagensIniciaisOnboarding(urlFormularioCadastro);
        mensagens.forEach(mensagem ->
                publicaSnsMensagemWhatsapp(wakander.getIdWakander(), wakander.getFiador().getTelefone(), mensagem));
        log.debug("[finish] WakanderApplicationService - iniciaOnboardingManual");
    }

    private static List<String> geraMensagensIniciaisOnboarding(String urlFormularioCadastro) {
        String mensagemBoasVindas = MensagensWhatsapp.MENSAGEM_BOAS_VINDAS.getMensagem(urlFormularioCadastro);
        String mensagemChecklist = MensagensWhatsapp.mensagemChecklistPadrao();
        return List.of(mensagemBoasVindas, mensagemChecklist);
    }

    @Override
    public void atualizaFiador(String token, FiadorDTO fiadorDTO) {
        log.info("[start] WakanderApplicationService - atualizaFiador");
        Wakander wakander = autenticacaoService.buscaWakanderPeloToken(token);
        wakander.atualizaFiador(fiadorDTO);
        wakanderRepository.save(wakander);
        autenticacaoService.alteraStatusTokenParaUtilizado(token);
        log.debug("[finish] WakanderApplicationService - atualizaFiador");
    }

    @Override
    public Page<WakanderPaginadoResponse> buscarWakanders(WakanderPaginadoRequest filtros, Pageable pageable) {
        log.info("[start] WakanderApplicationService - buscarWakanders");
        validarOrdenacao(pageable);
        Page<WakanderPaginadoResponse> wakandersPaginados = wakanderRepository.buscarWakanders(filtros, pageable)
                .map(WakanderPaginadoResponse::new);
        log.debug("[finish] WakanderApplicationService - buscarWakanders");
        return wakandersPaginados;
    }

    private void validarOrdenacao(Pageable pageable) {
        for (Sort.Order order : pageable.getSort()) {
            String campoOrdenacao = order.getProperty();

            if (!CAMPOS_ORDENACAO_PERMITIDOS.contains(order.getProperty())) {
                throw APIException.build(HttpStatus.BAD_REQUEST, "Campo de ordenação inválido: " + campoOrdenacao);
            }
        }
    }
}
