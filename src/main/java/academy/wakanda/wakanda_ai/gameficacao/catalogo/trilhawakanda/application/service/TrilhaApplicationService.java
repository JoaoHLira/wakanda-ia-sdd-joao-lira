package academy.wakanda.wakanda_ai.gameficacao.catalogo.trilhawakanda.application.service;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.TipoRecalculo;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.trilhawakanda.application.api.*;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.trilhawakanda.domain.TrilhaWakanda;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class TrilhaApplicationService implements TrilhaService {
    private final TrilhaRepository trilhaRepository;

    @Override
    public TrilhaCriadaResponse criaTrilha(TrilhaWakandaRequest novaTrilha) {
        log.info("[start] TrilhaApplicationService - criaTrilha");
        TrilhaWakanda trilhaCriada = new TrilhaWakanda(novaTrilha);
        trilhaRepository.save(trilhaCriada);
        log.debug("[finish] TrilhaApplicationService - criaTrilha");
        return new TrilhaCriadaResponse(trilhaCriada);
    }

    @Override
    public TrilhaWakanda buscaTrilhaPorId(UUID idTrilhaWakanda) {
        log.info("[start] TrilhaApplicationService - buscaTrilhaPorId");
        TrilhaWakanda trilhaWakanda = trilhaRepository.buscaTrilhaPorId(idTrilhaWakanda);
        log.debug("[finish] TrilhaApplicationService - buscaTrilhaPorId");
        return trilhaWakanda;
    }

    @Override
    public TrilhaWakanda salvaTrilha(TrilhaWakanda trilha) {
        log.info("[start] TrilhaApplicationService - salvaTrilha");
        TrilhaWakanda trilhaWakanda = trilhaRepository.save(trilha);
        log.debug("[finish] TrilhaApplicationService - salvaTrilha");
        return trilhaWakanda;
    }

    @Override
    public void recalculaXpTrilha(UUID idTrilhaWakanda, Integer xpMissao) {
        log.info("[start] TrilhaApplicationService - recalculaXpTrilha");
        TrilhaWakanda trilha = buscaTrilhaPorId(idTrilhaWakanda);
        trilha.atualizaXp(xpMissao);
        trilhaRepository.save(trilha);
        log.info("[finish] TrilhaApplicationService - recalculaXpTrilha");
    }

    @Override
    public void recalculaXpTotalTrilha(UUID idTrilhaWakanda, TipoRecalculo tipoRecalculo, Integer xpAntigoJornada, Integer xpNovoJornada) {
        log.info("[start] TrilhaApplicationService - recalculaXpTotalTrilha");
        TrilhaWakanda trilha = buscaTrilhaPorId(idTrilhaWakanda);
        trilha.recalculaXpTrilha(tipoRecalculo, xpAntigoJornada, xpNovoJornada);
        trilhaRepository.save(trilha);
        log.info("[finish] TrilhaApplicationService - recalculaXpTotalTrilha");
    }

    @Override
    public TrilhaDetalhadaResponse buscaTrilhaDetalhada(UUID idTrilha) {
        log.info("[start] TrilhaApplicationService - buscaTrilhaDetalhada");
        TrilhaDetalhadaProjection trilhaDetalhadaProjection = trilhaRepository.buscaTrilhaDetalhada(idTrilha);
        log.debug("[finish] TrilhaApplicationService - buscaTrilhaDetalhada");
        return new TrilhaDetalhadaResponse(trilhaDetalhadaProjection);
    }

    @Override
    public List<TrilhaListResponse> listaTrilhas() {
        log.info("[start] TrilhaApplicationService - listaTrilhas");
        List<TrilhaWakanda> trilhas = trilhaRepository.listaTrilhas();
        log.debug("[finish] TrilhaApplicationService - listaTrilhas");
        return TrilhaListResponse.converte(trilhas);
    }
}
