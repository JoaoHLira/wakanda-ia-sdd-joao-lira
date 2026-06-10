package academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.api;

import academy.wakanda.wakanda_ai.docs.swagger.GamificacaoAPIDocs;
import academy.wakanda.wakanda_ai.docs.swagger.MissaoWakandaAPIDocs;
import academy.wakanda.wakanda_ai.docs.swagger.WakanderAPIDocs;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.service.MissaoWakandaService;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.MissaoStatus;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Log4j2
@RequestMapping("/missoes")
@RestController
@RequiredArgsConstructor
@Tag(name = "MissaoWakandaAPI", description = "Controle responsavel pelas operações da missão Wakanda.")
public class MissaoWakandaAPI {

    private final MissaoWakandaService missaoService;

    @WakanderAPIDocs.CriaNovaMissao
    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public MissaoWakandaResponse postNovaMissao(@RequestBody @Valid MissaoWakandaRequest missaoRequest) {
        log.info("[start] MissaoWakandaAPI - postNovaMissao");
        MissaoWakandaResponse missaoResponse = missaoService.criaMissao(missaoRequest);
        log.debug("[finish] MissaoWakandaAPI - postNovaMissao");
        return missaoResponse;
    }

    @GamificacaoAPIDocs.AtualizaMissaoComDadosIA
    @PatchMapping("/{idMissao}/atualiza-missao-com-ia")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void atualizaMissaoIA(@PathVariable UUID idMissao,
                                 @RequestBody @Valid AtualizaMissaoRequest request) {
        log.info("[start] MissaoWakandaAPI - atualizaMissaoIA");
        missaoService.atualizaMissaoComDadosIA(idMissao, request);
        log.debug("[finish] MissaoWakandaAPI - atualizaMissaoIA");
    }

    @WakanderAPIDocs.BuscaMissaoPorId
    @GetMapping("/{idMissao}")
    @ResponseStatus(code = HttpStatus.OK)
    public MissaoWakandaDetalhadoResponse getMissaoPorId(@PathVariable UUID idMissao) {
        log.info("[start] MissaoWakandaAPI - getMissaoPorId");
        MissaoWakandaDetalhadoResponse missaoDetalhadoResponse = missaoService.buscaMissaoPorId(idMissao);
        log.debug("[finish] MissaoWakandaAPI - getMissaoPorId");
        return missaoDetalhadoResponse;
    }

    @GamificacaoAPIDocs.Catalogo.DesativaMissao
    @PatchMapping("/{idMissao}/status/desativar")
    @ResponseStatus(code = HttpStatus.OK)
    public void desativaMissao(@PathVariable UUID idMissao) {
        log.info("[start] MissaoWakandaAPI - desativaMissao");
        missaoService.desativaMissao(idMissao);
        log.debug("[finish] MissaoWakandaAPI - desativaMissao");
    }

    @GamificacaoAPIDocs.AlteraXpBaseMissao
    @PatchMapping("/{idMissao}/pontuacao")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void patchAlteraXpBase(@PathVariable UUID idMissao,
                                  @RequestBody @Valid MissaoAlteracaoXpBaseRequest missaoAlteracaoXpBase) {
        log.info("[start] MissaoWakandaAPI - patchAlteraXpBase");
        missaoService.alteraXpBase(idMissao, missaoAlteracaoXpBase);
        log.info("[finish] MissaoWakandaAPI - patchAlteraXpBase");
    }

    @GamificacaoAPIDocs.AlteraOrdemMissao
    @PatchMapping("/{idMissao}/ordem/{posicao}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void modificaOrdemDaMissao(@PathVariable UUID idMissao,
                                      @PathVariable Integer posicao) {
        log.info("[start] MissaoWakandaAPI - modificaOrdemDaMissao");
        missaoService.alteraPosicaoMissao(idMissao, posicao);
        log.debug("[finish] MissaoWakandaAPI - modificaOrdemDaMissao");
    }

    @MissaoWakandaAPIDocs.BuscaMissoesPaginadas
    @GetMapping("/{idJornada}/missoes")
    @ResponseStatus(code = HttpStatus.OK)
    public Page<MissaoWakandaDetalhadoResponse> buscaMissoesPaginadas(@PageableDefault(size = 10, sort = "ordemMissao.ordem") Pageable pageable,
                                                                      @PathVariable UUID idJornada,
                                                                      @RequestParam(defaultValue = "ATIVA") MissaoStatus missaoStatus) {
        log.info("[start] MissaoWakandaAPI - buscaMissoes");
        Page<MissaoWakandaDetalhadoResponse> missoesPage =
                missaoService.buscaMissoesPaginadas(pageable, idJornada, missaoStatus);
        log.debug("[finish] MissaoWakandaAPI - buscaMissoes");
        return missoesPage;
    }
}
