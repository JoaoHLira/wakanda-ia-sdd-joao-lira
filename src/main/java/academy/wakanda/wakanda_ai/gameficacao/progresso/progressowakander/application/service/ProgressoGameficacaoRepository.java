package academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.service;

import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.api.ProgressoIndividualDetalhadoProjection;
import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.api.RankingWakanderProjection;
import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.domain.ProgressoWakander;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ProgressoGameficacaoRepository {
    ProgressoWakander novoProgresso(ProgressoWakander progressoWakander);
    ProgressoWakander buscaProgressoPorIdWakander(UUID idWakander);
    ProgressoWakander buscaProgressoPorId(UUID idProgressoWakander);
    Page<RankingWakanderProjection> rankingDestaqueGeral(Pageable pageable);
    Page<RankingWakanderProjection> rankingDestaquePorPeriodo(LocalDateTime inicio, LocalDateTime fim, Pageable pageable);
    ProgressoIndividualDetalhadoProjection buscaprogressoIndividual(UUID idWakander);
}