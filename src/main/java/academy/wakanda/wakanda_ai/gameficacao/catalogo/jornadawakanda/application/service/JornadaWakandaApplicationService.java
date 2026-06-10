package academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.application.service;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.application.api.JornadaCriacaoRequest;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.application.api.JornadaListResponse;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.application.api.JornadaResponse;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.application.api.*;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.domain.JornadaWakanda;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.domain.StatusJornada;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.TipoRecalculo;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.trilhawakanda.application.service.TrilhaService;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.trilhawakanda.domain.TrilhaWakanda;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
@Log4j2
@RequiredArgsConstructor
public class JornadaWakandaApplicationService implements JornadaWakandaService {
    private final JornadaWakandaRepository jornadaWakandaRepository;
    private final TrilhaService trilhaService;

    @Override
    public JornadaResponse criarJornada(JornadaCriacaoRequest jornadaCriacaoRequest) {
        log.info("[start] JornadaApplicationService - criarJornada");
        int posicaoJornada = jornadaWakandaRepository
                .buscaJornadasPorIdTrilha(jornadaCriacaoRequest.getIdTrilhaWakanda()).size();
        JornadaWakanda jornadaWakanda = new JornadaWakanda(jornadaCriacaoRequest, posicaoJornada);
        TrilhaWakanda trilhaWakanda = buscaTrilhaPorId(jornadaWakanda.getIdTrilhaWakanda());
        jornadaWakanda = jornadaWakandaRepository.save(jornadaWakanda);
        log.debug("[finish] JornadaApplicationService - criarJornada");
        return new JornadaResponse(jornadaWakanda, trilhaWakanda, posicaoJornada);
    }

    private TrilhaWakanda buscaTrilhaPorId(UUID idTrilhaWakanda) {
        TrilhaWakanda trilhaWakanda = trilhaService.buscaTrilhaPorId(idTrilhaWakanda);
        return trilhaWakanda;
    }

    @Override
    public JornadaWakanda buscaJornadaPorId(UUID idJornada) {
        log.info("[start] JornadaApplicationService - buscaJornadaPorId");
        log.debug("[IdJornada] {}", idJornada);
        JornadaWakanda jornadaWakanda = jornadaWakandaRepository.buscaJornadaId(idJornada);
        log.debug("[finish] JornadaApplicationService - buscaJornadaPorId");
        return jornadaWakanda;
    }

    @Override
    public void removerXpJornada(UUID idJornada, Integer xpMissao) {
        log.info("[start] JornadaApplicationService - removerXpJornada");
        JornadaWakanda jornadaWakanda = buscaJornadaPorId(idJornada);
        jornadaWakanda.subtraiXpMissao(xpMissao);
        jornadaWakandaRepository.save(jornadaWakanda);
        recalculaXpTrilha(jornadaWakanda.getIdTrilhaWakanda(), xpMissao);
        log.debug("[finish] JornadaApplicationService - removerXpJornada");
    }

    private void recalculaXpTrilha(UUID idTrilhaWakanda, Integer xpMissao) {
        log.debug("[start] JornadaApplicationService - recalculaXpTrilha");
        trilhaService.recalculaXpTrilha(idTrilhaWakanda, xpMissao);
        log.debug("[finish] JornadaApplicationService - recalculaXpTrilha");
    }

    @Override
    public JornadaResponse buscaJornada(UUID jornada) {
        log.info("[start] JornadaApplicationService - buscaJornada");
        JornadaWakanda jornadaWakanda = jornadaWakandaRepository.buscaJornadaId(jornada);
        log.info("[finish] JornadaApplicationService - buscaJornada");
        return new JornadaResponse(jornadaWakanda);
    }

    @Override
    public void recalculaXpJornada(UUID idJornada, TipoRecalculo tipoRecalculo, Integer xpAntigoMissao, Integer novoXpBaseMissao) {
        log.info("[start] JornadaApplicationService - recalculaXpTotalJornada");
        JornadaWakanda jornadaWakanda = buscaJornadaPorId(idJornada);
        int xpAntigoJornada = jornadaWakanda.getXpTotal();
        jornadaWakanda.recalculaXpJornada(tipoRecalculo, xpAntigoMissao, novoXpBaseMissao);
        jornadaWakandaRepository.save(jornadaWakanda);
        trilhaService.recalculaXpTotalTrilha(jornadaWakanda.getIdTrilhaWakanda(), tipoRecalculo, xpAntigoJornada, jornadaWakanda.getXpTotal());
        log.info("[finish] JornadaApplicationService - recalculaXpTotalJornada");
    }

    @Override
    public List<JornadaListResponse> buscaJornadasPorIdTrilha(UUID idTrilha) {
        log.info("[start] JornadaApplicationService - buscaJornadasPorIdTrilha");
        List<JornadaWakanda> jornadaWakanda = jornadaWakandaRepository.buscaJornadasPorIdTrilha(idTrilha);
        log.info("[finish] JornadaApplicationService - buscaJornadasPorIdTrilha");
        return JornadaListResponse.converte(jornadaWakanda);
    }

    @Override
    public JornadaDetalhadaResponse buscaJornadaDetalhada(UUID idJornada) {
        log.info("[start] JornadaWakandaApplicationService - buscaJornadaDetalhada");
        JornadaDetalhadaProjection jornadaDetalhadaProjection = jornadaWakandaRepository.buscaJornadaDetalhada(idJornada);
        log.debug("[finish] JornadaWakandaApplicationService - buscaJornadaDetalhada");
        return new JornadaDetalhadaResponse(jornadaDetalhadaProjection);
    }

    @Override
    public List<JornadaListResponse> listaJornadas() {
        log.info("[start] JornadaWakandaApplicationService - listaJornadas");
        List<JornadaWakanda> jornadas = jornadaWakandaRepository.listaJornadas();
        log.debug("[finish] JornadaWakandaApplicationService - listaJornadas");
        return JornadaListResponse.converte(jornadas);
    }
}