package academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.service;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.application.service.JornadaWakandaRepository;
import academy.wakanda.wakanda_ai.gameficacao.progresso.jornadaprogresso.application.service.JornadaProgressoRepository;
import academy.wakanda.wakanda_ai.gameficacao.progresso.jornadaprogresso.domain.JornadaProgresso;
import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.api.*;
import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.service.progresso.processadores.ProgressoWakanderProcessor;
import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.domain.ProgressoWakander;
import academy.wakanda.wakanda_ai.handler.APIException;
import academy.wakanda.wakanda_ai.wakander.application.service.WakanderRepository;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class ProgressoWakanderApplicationService implements ProgressoWakanderService {

    private final ProgressoGameficacaoRepository progressoRepository;
    private final WakanderRepository wakanderRepository;
    private final JornadaProgressoRepository jornadaProgressoRepository;
    private final JornadaWakandaRepository jornadaWakandaRepository;
    private final List<ProgressoWakanderProcessor> progressoWakanderProcessor;

    @Override
    public ProgressoWakanderResponse novoProgresso(UUID idWakander) {
        log.info("[start] ProgressoWakanderApplicationService - novoProgresso");
        Optional.ofNullable(progressoRepository.buscaProgressoPorIdWakander(idWakander))
                .ifPresent(p -> {
                    throw APIException.build(HttpStatus.BAD_REQUEST, "Já existe progresso para este Wakander");
                });
        ProgressoWakander progressoWakander = new ProgressoWakander(idWakander);
        log.debug("[finish] ProgressoWakanderApplicationService - novoProgresso");
        return new ProgressoWakanderResponse(progressoRepository.novoProgresso(progressoWakander));
    }

    @Override
    public void processaPorTipoProgresso(ProgressoWakanderEventDto progressoWakander) {
        log.info("[start] ProgressoWakanderApplicationService - processaPorTipoProgresso");
        ProgressoWakanderProcessor progressoProcessor = strategyProgressoWakanderProcessor(progressoWakander);
        progressoProcessor.processaProgressoWakander(progressoWakander);
        log.debug("[finish] ProgressoWakanderApplicationService - processaPorTipoProgresso");
    }

    @Override
    public Page<RankingWakanderProjection> rankingDestaqueGeral(Pageable pageable) {
        log.info("[start] ProgressoWakanderApplicationService - rankingDestaqueGeral");
        Page<RankingWakanderProjection> page = progressoRepository.rankingDestaqueGeral(pageable);
        log.debug("[finish] ProgressoWakanderApplicationService - rankingDestaqueGeral");
        return page;
    }

    @Override
    public Page<RankingWakanderProjection> rankingDestaquePorPeriodo(LocalDate dataInicio, LocalDate dataFim, Pageable pageable) {
        log.info("[start] ProgressoWakanderApplicationService - rankingDestaquePorPeriodo");
        LocalDateTime inicio = dataInicio.atStartOfDay();
        LocalDateTime fim = dataFim.atTime(LocalTime.MAX);
        Page<RankingWakanderProjection> page = progressoRepository.rankingDestaquePorPeriodo(inicio, fim, pageable);
        log.debug("[finish] ProgressoWakanderApplicationService - rankingDestaquePorPeriodo");
        return page;
    }

    private ProgressoWakanderProcessor strategyProgressoWakanderProcessor(ProgressoWakanderEventDto progressoWakander) {
        return progressoWakanderProcessor.stream()
                .filter(m -> m.validaProcessaProgresso(progressoWakander.getTypeProgressoWakander()))
                .findFirst()
                .orElseThrow(() -> APIException.build(HttpStatus.BAD_REQUEST, "Tipo de progresso nao é processado."));
    }

    @Override
    public SincronizacaoProgressoResponse sincronizaWakandersAntigos() {
        log.info("[start] ProgressoWakanderApplicationService - sincronizaAntigos");
        List<Wakander> elegiveis = wakanderRepository.buscaWakandersRegularesSemProgresso();
        List<IgnoradoProgressoDetalhe> ignorados = montaIgnorados();
        int criados = criaRegistros(elegiveis);
        log.debug("[finish] ProgressoWakanderApplicationService - sincronizaAntigos");
        return new SincronizacaoProgressoResponse(criados, ignorados.size());
    }

    private int criaRegistros(List<Wakander> elegiveis) {
        elegiveis.forEach(w -> {
            ProgressoWakander p = progressoRepository.novoProgresso(new ProgressoWakander(w.getIdWakander()));
            criaJornadasPara(p);
        });
        return elegiveis.size();
    }

    private void criaJornadasPara(ProgressoWakander progresso) {
        jornadaWakandaRepository.listaJornadas().stream()
                .filter(j -> j.getStatusJornada().isAtiva())
                .forEach(j -> jornadaProgressoRepository.salvaJornadaProgresso(
                        JornadaProgresso.criarEmAndamento(j.getIdJornada(), progresso.getIdProgressoWakander())));
    }

    private List<IgnoradoProgressoDetalhe> montaIgnorados() {
        List<Wakander> irregular = wakanderRepository.buscaWakandersIrregulares();
        List<Wakander> comProgresso = wakanderRepository.buscaWakandersRegularesComProgresso();
        List<IgnoradoProgressoDetalhe> irregulares = irregular.stream().map(w -> IgnoradoProgressoDetalhe.irregular(w)).toList();
        List<IgnoradoProgressoDetalhe> possuiProgresso = comProgresso.stream().map(w -> IgnoradoProgressoDetalhe.jaPossuiProgresso(w)).toList();
        return new java.util.ArrayList<>() {{
            addAll(irregulares);
            addAll(possuiProgresso);
        }};
    }

    public void sincronizaWakanderAntigo(UUID idWakander) {
        log.info("[start] ProgressoWakanderApplicationService - sincronizaWakanderAntigo");
        Wakander wakander = wakanderRepository.buscaWakanderPorId(idWakander);
        wakander.validaWakanderRegular();
        Optional.ofNullable(progressoRepository.buscaProgressoPorIdWakander(idWakander))
                .ifPresent(p -> {
                    throw APIException.build(HttpStatus.BAD_REQUEST, "Já existe progresso para este Wakander");
                });
        ProgressoWakander progresso = progressoRepository.novoProgresso(new ProgressoWakander(idWakander));
        criaJornadasPara(progresso);
        log.debug("[finish] ProgressoWakanderApplicationService - sincronizaWakanderAntigo");
    }

    @Override
    public ProgressoIndividualResponse progressoIndividual(UUID idWakander) {
        log.info("[start] ProgressoWakanderApplicationService - progressoIndividual");
        ProgressoIndividualDetalhadoProjection projection = progressoRepository.buscaprogressoIndividual(idWakander);
        log.debug("[finish] ProgressoWakanderApplicationService - progressoIndividual");
        return new ProgressoIndividualResponse(projection);
    }
}
