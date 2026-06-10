package academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.application.service;

import java.util.List;
import java.util.UUID;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.application.api.JornadaDetalhadaProjection;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.domain.JornadaWakanda;

public interface JornadaWakandaRepository {
    JornadaWakanda save(JornadaWakanda jornadaWakanda);

    JornadaWakanda buscaJornadaId(UUID jornada);

    List<JornadaWakanda> buscaJornadasPorIdTrilha(UUID trilhaWakanda);

    JornadaDetalhadaProjection buscaJornadaDetalhada(UUID idJornada);

    List<JornadaWakanda> listaJornadas();

    JornadaWakanda buscaJornadaPorTitulo(String jornadaWakanda);
}
