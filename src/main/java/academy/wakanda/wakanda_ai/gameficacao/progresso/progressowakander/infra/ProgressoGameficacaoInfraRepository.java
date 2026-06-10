package academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.infra;

import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.api.ProgressoIndividualDetalhadoProjection;
import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.api.RankingWakanderProjection;
import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.service.ProgressoGameficacaoRepository;
import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.domain.ProgressoWakander;
import academy.wakanda.wakanda_ai.handler.APIException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Log4j2
public class ProgressoGameficacaoInfraRepository implements ProgressoGameficacaoRepository {
    private final ProgressoGameficacaoSpringDataRepository progressoSpringDataRepository;

    @Override
    public ProgressoWakander novoProgresso(ProgressoWakander progressoWakander) {
        log.info("[start] ProgressoGameficacaoInfraRepository - novoProgresso");
        ProgressoWakander progresso = progressoSpringDataRepository.save(progressoWakander);
        log.debug("[finish] ProgressoGameficacaoInfraRepository - novoProgresso");
        return progresso;
    }

    @Override
    public ProgressoWakander buscaProgressoPorIdWakander(UUID idWakander) {
        log.info("[start] ProgressoGameficacaoInfraRepository - buscaProgressoPorIdWakander");
        ProgressoWakander progresso = progressoSpringDataRepository.findByIdWakander(idWakander).orElse(null);
        log.debug("[finish] ProgressoGameficacaoInfraRepository - buscaProgressoPorIdWakander");
        return progresso;
    }

    @Override
    public Page<RankingWakanderProjection> rankingDestaqueGeral(Pageable pageable) {
        log.info("[start] ProgressoGameficacaoInfraRepository - rankingDestaqueGeral");
        Page<RankingWakanderProjection> page = progressoSpringDataRepository.findRankingDestaqueGeral(pageable);
        log.debug("[finish] ProgressoGameficacaoInfraRepository - rankingDestaqueGeral");
        return page;
    }

    @Override
    public Page<RankingWakanderProjection> rankingDestaquePorPeriodo(LocalDateTime inicio, LocalDateTime fim, Pageable pageable) {
        log.info("[start] ProgressoGameficacaoInfraRepository - rankingDestaquePorPeriodo");
        Page<RankingWakanderProjection> page = progressoSpringDataRepository.findRankingDestaquePorPeriodo(inicio, fim, pageable);

        Page<RankingWakanderProjection> result = Optional.of(page)
                .filter(p -> !p.isEmpty())
                .orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Nenhum Wakander se destacou neste período"));

        log.debug("[finish] ProgressoGameficacaoInfraRepository - rankingDestaquePorPeriodo");
        return result;
    }

    @Override
    public ProgressoWakander buscaProgressoPorId(UUID idProgressoWakander) {
        log.info("[start] ProgressoGameficacaoInfraRepository - buscarIdProgresso");
        ProgressoWakander idProgresso = progressoSpringDataRepository.findByIdProgressoWakander(idProgressoWakander)
                .orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Progresso de Wakander nao encontrado"));
        log.debug("[finish] ProgressoGameficacaoInfraRepository - buscarIdProgresso");
        return idProgresso;
    }

    @Override
    public ProgressoIndividualDetalhadoProjection buscaprogressoIndividual(UUID idWakander) {
        log.info("[start] ProgressoGameficacaoInfraRepository - buscaprogressoIndividual");
        ProgressoIndividualDetalhadoProjection progressoIndividualProjection = progressoSpringDataRepository.buscaprogressoIndividual(idWakander)
                .orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Progresso individual do Wakander nao encontrado"));
        log.debug("[finish] ProgressoGameficacaoInfraRepository - buscaprogressoIndividual");
        return progressoIndividualProjection;
    }
}