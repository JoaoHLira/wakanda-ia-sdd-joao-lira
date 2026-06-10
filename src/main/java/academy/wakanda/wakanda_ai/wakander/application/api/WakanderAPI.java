package academy.wakanda.wakanda_ai.wakander.application.api;

import academy.wakanda.wakanda_ai.autenticacao.application.service.AutenticacaoService;
import academy.wakanda.wakanda_ai.docs.swagger.WakanderAPIDocs;
import academy.wakanda.wakanda_ai.jornadawakander.domain.JornadaWakanda;
import academy.wakanda.wakanda_ai.wakander.application.service.WakanderService;
import academy.wakanda.wakanda_ai.wakander.domain.StatusCadastro;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Log4j2
@RestController
@RequestMapping("/wakander")
@RequiredArgsConstructor
@Tag(name = "WakanderAPI", description = "Controle responsavel pelas operações do wakander.")
public class WakanderAPI {
    private final WakanderService wakanderService;
    private final AutenticacaoService autenticacaoService;

    @Deprecated
    @WakanderAPIDocs.CriaNovoWakander
    @PostMapping("/novo-wakander")
    @ResponseStatus(code = HttpStatus.CREATED)
    public WakanderCriadoResponse postNovoWakander(@RequestBody @Valid WakanderNovoRequest wakanderNovo) {
        log.info("[start] WakanderAPI - postNovoWakander");
        WakanderCriadoResponse wakanderCreated = wakanderService.matriculaWakander(wakanderNovo);
        log.debug("[finish] WakanderAPI - postNovoWakander");
        return wakanderCreated;
    }

    @WakanderAPIDocs.RegularizaWakander
    @PatchMapping("/{idWakander}/regulariza")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void patchRegularizaWakander(@PathVariable UUID idWakander) {
        log.info("[start] WakanderAPI - patchRegularizaWakander");
        wakanderService.regularizaWakander(idWakander);
        log.debug("[finish] WakanderAPI - patchRegularizaWakander");
    }

    @WakanderAPIDocs.AtualizaJornadaWakander
    @PatchMapping(value = "/{idWakander}/progresso/jornada-conhecimento")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void patchAtualizaProgressoParaJornadaConhecimento(@PathVariable UUID idWakander) {
        log.info("[start] WakanderAPI - patchAtualizaProgressoParaJornadaConhecimento");
        wakanderService.atualizaProgressoParaJornada(idWakander, JornadaWakanda.JORNADA_CONHECIMENTO);
        log.debug("[finish] WakanderAPI - patchAtualizaProgressoParaJornadaConhecimento");
    }

    @WakanderAPIDocs.AtualizaJornadaWakander
    @PatchMapping(value = "/{idWakander}/progresso/jornada-habilidade")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void patchAtualizaProgressoParaJornadaHabilidade(@PathVariable UUID idWakander) {
        log.info("[start] WakanderAPI - patchAtualizaProgressoParaJornadaHabilidade");
        wakanderService.atualizaProgressoParaJornada(idWakander, JornadaWakanda.JORNADA_HABILIDADE);
        log.debug("[finish] WakanderAPI - patchAtualizaProgressoParaJornadaHabilidade");
    }

    @WakanderAPIDocs.AtualizaJornadaWakander
    @PatchMapping(value = "/{idWakander}/progresso/jornada-conquista")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void patchAtualizaProgressoParaJornadaConquista(@PathVariable UUID idWakander) {
        log.info("[start] WakanderAPI - patchAtualizaProgressoParaJornadaConquista");
        wakanderService.atualizaProgressoParaJornada(idWakander, JornadaWakanda.JORNADA_CONQUISTA);
        log.debug("[finish] WakanderAPI - patchAtualizaProgressoParaJornadaConquista");
    }

    @WakanderAPIDocs.AtualizaJornadaWakander
    @PatchMapping(value = "/{idWakander}/progresso/vibraniun")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void patchAtualizaProgressoParaVibraniun(@PathVariable UUID idWakander) {
        log.info("[start] WakanderAPI - patchAtualizaProgressoParaVibraniun");
        wakanderService.atualizaProgressoParaJornada(idWakander, JornadaWakanda.VIBRANIUN);
        log.debug("[finish] WakanderAPI - patchAtualizaProgressoParaVibraniun");
    }

    @WakanderAPIDocs.EditaWakander
    @PatchMapping(value = "/{idWakander}/edita-wakander")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void patchEditaWakander(@PathVariable UUID idWakander,
                                   @Valid @RequestBody WakanderAlteracaoRequest wakanderAlteracao) {
        log.info("[start] WakanderAPI - patchEditaWakander");
        wakanderService.editaWakander(idWakander, wakanderAlteracao);
        log.debug("[finish] WakanderAPI - patchEditaWakander");
    }

    @WakanderAPIDocs.BuscaPorIdWakander
    @GetMapping(value = "/{idWakander}")
    @ResponseStatus(code = HttpStatus.OK)
    public WakanderDetalhadoResponse getWakanderPorId(@PathVariable UUID idWakander) {
        log.info("[start] WakanderAPI - getWakanderPorId");
        WakanderDetalhadoResponse wakanderDetalhado = wakanderService.buscaWakanderPorId(idWakander);
        log.debug("[finish] WakanderAPI - getWakanderPorId");
        return wakanderDetalhado;
    }

    @WakanderAPIDocs.BuscaPorIdWakanderRetornaDadosOcultos
    @GetMapping(value = "/dado-oculto/{token}")
    @ResponseStatus(code = HttpStatus.OK)
    public WakanderComDadosPessoaisOcultoResponse getWakanderComDadosOcultoPorId(@PathVariable String token) {
        log.info("[start] WakanderAPI - getWakanderComDadosOcultoPorId");
        WakanderComDadosPessoaisOcultoResponse wakanderResponse = wakanderService.buscaWakanderPorIdRetornaDadosOcultos(token);
        log.debug("[finish] WakanderAPI - getWakanderComDadosOcultoPorId");
        return wakanderResponse;
    }

    @WakanderAPIDocs.RetornaTodosOsWakandersDeFormaPaginada
    @GetMapping(value = "/busca-wakanders")
    @ResponseStatus(code = HttpStatus.OK)
    public Page<WakanderResponseDashboardDTO> buscaTodosWakanders(Pageable pageable, @RequestParam(required = false) String busca,
                                                                  @RequestParam(required = true) Boolean incluiCancelados) {
        log.info("[start] WakanderAPI - buscaTodosWakanders");
        Page<WakanderResponseDashboardDTO> wakandersResponse = wakanderService.buscaTodosOsWakanderComPaginacao(pageable, busca, incluiCancelados);
        log.debug("[finish] WakanderAPI - buscaTodosWakanders");
        return wakandersResponse;
    }

    @WakanderAPIDocs.RetornaTodosOsWakandersDeFormaPaginadaPorStatus
    @GetMapping(value = "/busca-wakanders/{statusCadastro}")
    @ResponseStatus(code = HttpStatus.OK)
    public Page<WakanderResponseDashboardDTO> buscaPorStatus(Pageable pageable, @PathVariable StatusCadastro statusCadastro, @RequestParam(required = true) Boolean incluiCancelados) {
        log.info("[start] WakanderAPI - buscaPorStatus");
        Page<WakanderResponseDashboardDTO> wakandersResponse = wakanderService.buscaPorStatus(pageable, statusCadastro, incluiCancelados);
        log.debug("[finish] WakanderAPI - buscaPorStatus");
        return wakandersResponse;
    }

    @WakanderAPIDocs.AdicionaDadosFaltantesDoWakander
    @PatchMapping("/dados-wakander")
    @ResponseStatus(code = HttpStatus.OK)
    public void recebeDadosPessoaisWakander(@RequestBody WakanderCadastroCompleto wakander) {
        log.info("[start] WakanderAPI - recebeDadosPessoaisWakander");
        wakanderService.atualizaDadosWakander(wakander);
        log.debug("[finish] WakanderAPI - recebeDadosPessoaisWakander");
    }

    @WakanderAPIDocs.SolicitaEnvioDeFormularioDadosComplementares
    @PatchMapping("/envia-formularios-wakanders")
    @ResponseStatus(code = HttpStatus.OK)
    public void solicitaEnvioDeFormularioDadosComplementares() {
        log.info("[start] WakanderAPI - solicitaEnvioDeFormularioDadosComplementares");
        wakanderService.solicitaEnvioDeFormularioDadosComplementares();
        log.debug("[finish] WakanderAPI - solicitaEnvioDeFormularioDadosComplementares");
    }

    @WakanderAPIDocs.RegularizaStatusCadastroWakanders
    @PatchMapping("/atualiza-status-cadastro")
    @ResponseStatus(code = HttpStatus.OK)
    public void atualizaStatusCadastroWakanders() {
        log.info("[start] WakanderAPI - atualizaStatusCadastroWakanders");
        wakanderService.atualizaStatusCadastro();
        log.debug("[finish] WakanderAPI - atualizaStatusCadastroWakanders");
    }

    @WakanderAPIDocs.RetornaEstatisticasSobreWakanders
    @GetMapping("/estatistica-wakanders")
    @ResponseStatus(code = HttpStatus.OK)
    public EstatisticaWakanderDTO estatisticaWakanders(@RequestParam(required = true) Boolean incluiCancelados) {
        log.info("[start] WakanderAPI - estatisticaWakanders");
        EstatisticaWakanderDTO estatisticas = wakanderService.buscaEstatisticasWakanders(incluiCancelados);
        log.debug("[finish] WakanderAPI - estatisticaWakanders");
        return estatisticas;
    }

    @WakanderAPIDocs.BuscaDadosWakanderNoAsaas
    @PatchMapping("/atualiza-dados-asaas")
    @ResponseStatus(code = HttpStatus.OK)
    public void atualizaDadosAsaasWakanders() {
        log.info("[start] WakanderAPI - AtualizaDadosAsaasWakanders");
        wakanderService.postaWakanderComDadosAsaasIncompletoNaFila();
        log.debug("[finish] WakanderAPI - AtualizaDadosAsaasWakanders");
    }

    @WakanderAPIDocs.CompletaCadastro
    @PatchMapping("/cadastro/{token}")
    @ResponseStatus(code = HttpStatus.OK)
    public void completaCadastro(@RequestBody WakanderCadastroCompleto request,
                                 @PathVariable String token) {
        log.info("[start] WakanderAPI - completaCadastro");
        Wakander wakander = autenticacaoService.buscaWakanderPeloToken(token);
        wakanderService.completaCadastroWakander(token, request, wakander);
        log.debug("[finish] WakanderAPI - completaCadastro");
    }

    @WakanderAPIDocs.CancelaAssinatura
    @PatchMapping(value = "{idWakander}/cancela-assinatura")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void cancelaAssinatura(@PathVariable UUID idWakander,
                                  @RequestBody WakanderCancelaAssinaturaDTO wakanderCancelaAssinatura) {
        log.info("[start] WakanderAPI - cancelaAssinatura");
        wakanderService.cancelaAssinatura(idWakander, wakanderCancelaAssinatura);
        log.debug("[finish] WakanderAPI - cancelaAssinatura");
    }

    @WakanderAPIDocs.ReverteCancelamento
    @PatchMapping(value = "{idWakander}/reverte-cancelamento")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void reverteCancelamento(@PathVariable UUID idWakander,
                                    @RequestBody WakanderCancelaAssinaturaDTO desistiDoCancelamento) {
        log.info("[start] WakanderAPI - reverteCancelamento");
        wakanderService.reverteCancelamento(idWakander, desistiDoCancelamento);
        log.debug("[finish] WakanderAPI - reverteCancelamento");
    }

    @WakanderAPIDocs.GeraLinkAtualizacaoFiador
    @PostMapping(value = "/{idWakander}/fiador/atualizacao-link")
    @ResponseStatus(code = HttpStatus.CREATED)
    public LinkGeradoResponse geraLinkAtualizacaoFiador(@PathVariable UUID idWakander) {
        log.info("[start] WakanderAPI - postGeraLinkAtualizacaoFiador");
        String link = wakanderService.geraLinkAtualizacaoFiador(idWakander);
        log.debug("[finish] WakanderAPI - postGeraLinkAtualizacaoFiador");
        return new LinkGeradoResponse(link);
    }

    @WakanderAPIDocs.IniciaOnboardingManual
    @PatchMapping(value = "/{idWakander}/inicia-onboarding-manual")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void iniciaOnboardingManual(@PathVariable UUID idWakander) {
        log.info("[start] WakanderAPI - iniciaOnboardingManual");
        wakanderService.iniciaOnboardingManual(idWakander);
        log.debug("[finish] WakanderAPI - iniciaOnboardingManual");
    }

    @WakanderAPIDocs.BuscaWakanders
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<WakanderPaginadoResponse> buscaWakanders(@ModelAttribute WakanderPaginadoRequest filtros,
                                                         @PageableDefault(size = 15, sort = "nome", direction = Sort.Direction.ASC)
                                                         Pageable pageable) {
        log.info("[start] WakanderAPI - buscaWakanders");
        Page<WakanderPaginadoResponse> wakanders = wakanderService.buscarWakanders(filtros, pageable);
        log.debug("[finish] WakanderAPI - buscaWakanders");
        return wakanders;
    }
}
