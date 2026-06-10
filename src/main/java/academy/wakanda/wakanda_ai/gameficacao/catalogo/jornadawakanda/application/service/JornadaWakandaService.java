package academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.application.service;

import java.util.List;
import java.util.UUID;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.application.api.JornadaCriacaoRequest;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.application.api.JornadaDetalhadaResponse;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.application.api.JornadaListResponse;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.application.api.JornadaResponse;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.domain.JornadaWakanda;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.TipoRecalculo;


public interface JornadaWakandaService {
    JornadaResponse criarJornada(JornadaCriacaoRequest jornadaCriacaoRequest);
    JornadaWakanda buscaJornadaPorId(UUID idJornada);
    void removerXpJornada(UUID idJornada, Integer xpMissao);
    JornadaResponse buscaJornada(UUID jornada);
    void recalculaXpJornada(UUID idJornada, TipoRecalculo tipoRecalculo, Integer xpAntigoMissao, Integer novoXpBaseMissao);
    List<JornadaListResponse> buscaJornadasPorIdTrilha(UUID idTrilha);
    JornadaDetalhadaResponse buscaJornadaDetalhada(UUID idJornada);
    List<JornadaListResponse> listaJornadas();
}
