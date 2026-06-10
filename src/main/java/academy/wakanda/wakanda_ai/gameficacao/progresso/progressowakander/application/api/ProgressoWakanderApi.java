package academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.api;

import academy.wakanda.wakanda_ai.docs.swagger.ProgressoWakanderAPIDocs;
import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.service.ProgressoWakanderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@Log4j2
@RestController
@RequestMapping("/gameficacao/progresso")
@RequiredArgsConstructor
@Tag(name = "ProgressoAPI", description = "Controle responsavel pelas operações de progresso do Wakander")
public class ProgressoWakanderApi {

    private final ProgressoWakanderService progressoWakanderService;

    @ProgressoWakanderAPIDocs.NovoProgresso
    @PostMapping("/{idWakander}")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ProgressoWakanderResponse novoProgresso(@PathVariable UUID idWakander) {
        log.info("[start] ProgressoWakanderApi - NovoProgresso");
        ProgressoWakanderResponse progressoWakander = progressoWakanderService.novoProgresso(idWakander);
        log.debug("[finish] ProgressoWakanderApi - NovoProgresso");
        return progressoWakander;
    }

    @ProgressoWakanderAPIDocs.RankingWakanders
    @GetMapping("/ranking-wakanders")
    @ResponseStatus(code = HttpStatus.OK)
    public Page<RankingWakanderProjection> getRankingMaiorDestaqueGeral(
            Pageable pageable,
            @RequestParam(value = "dataInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(value = "dataFim", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        log.info("[start] ProgressoWakanderApi - getRankingMaiorDestaqueGeral");
        Page<RankingWakanderProjection> ranking = (dataInicio != null && dataFim != null)
                ? progressoWakanderService.rankingDestaquePorPeriodo(dataInicio, dataFim, pageable)
                : progressoWakanderService.rankingDestaqueGeral(pageable);
        log.debug("[finish] ProgressoWakanderApi - getRankingMaiorDestaqueGeral");
        return ranking;
    }

    @ProgressoWakanderAPIDocs.SincronizaWakandersAntigos
    @PostMapping("/sincroniza-antigos")
    @ResponseStatus(code = HttpStatus.OK)
    public SincronizacaoProgressoResponse sincronizaAntigos() {
        log.info("[start] ProgressoWakanderApi - sincronizaAntigos");
        SincronizacaoProgressoResponse resumo = progressoWakanderService.sincronizaWakandersAntigos();
        log.debug("[finish] ProgressoWakanderApi - sincronizaAntigos");
        return resumo;
    }

    @ProgressoWakanderAPIDocs.SincronizaWakanderAntigo
    @PostMapping("/sincroniza-antigo/{idWakander}")
    @ResponseStatus(code = HttpStatus.OK)
    public void sincronizaAntigo(@PathVariable UUID idWakander) {
        log.info("[start] ProgressoWakanderApi - sincronizaAntigo");
        progressoWakanderService.sincronizaWakanderAntigo(idWakander);
        log.debug("[finish] ProgressoWakanderApi - sincronizaAntigo");
    }

    @GetMapping("/{idWakander}")
    @ResponseStatus(HttpStatus.OK)
    public ProgressoIndividualResponse progressoIndividual(@PathVariable UUID idWakander) {
        log.info("[start] ProgressoWakanderApi - progressoIndividual");
        ProgressoIndividualResponse progressoIndividual = progressoWakanderService.progressoIndividual(idWakander);
        log.debug("[finish] ProgressoWakanderApi - progressoIndividual");
        return progressoIndividual;
    }
}
