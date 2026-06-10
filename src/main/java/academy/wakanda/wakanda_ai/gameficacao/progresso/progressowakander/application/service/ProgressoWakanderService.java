package academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.service;

import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.api.ProgressoIndividualResponse;
import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.api.ProgressoWakanderResponse;
import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.api.RankingWakanderProjection;
import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.api.SincronizacaoProgressoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.UUID;

public interface ProgressoWakanderService {
    ProgressoWakanderResponse novoProgresso(UUID idWakander);
    void processaPorTipoProgresso(ProgressoWakanderEventDto progressoWakander);
    Page<RankingWakanderProjection> rankingDestaqueGeral(Pageable pageable);
    Page<RankingWakanderProjection> rankingDestaquePorPeriodo(LocalDate dataInicio, LocalDate dataFim, Pageable pageable);
    SincronizacaoProgressoResponse sincronizaWakandersAntigos();

    void sincronizaWakanderAntigo(UUID idWakander);
    ProgressoIndividualResponse progressoIndividual(UUID idWakander);
}