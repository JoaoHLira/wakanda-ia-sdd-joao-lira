package academy.wakanda.wakanda_ai.gameficacao.catalogo.trilhawakanda.datahelper;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.trilhawakanda.application.api.TrilhaWakandaRequest;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.trilhawakanda.domain.TrilhaWakanda;

import java.util.UUID;

public class TrilhaWakandaDataHelper {

    public static TrilhaWakanda criaTrilhaWakandaValida() {
        return new TrilhaWakanda(
                UUID.randomUUID(),
                "Profissão Programador",
                "Trilha de Formação em Desenvolvimento Back-End",
                0
        );
    }

    public static TrilhaWakandaRequest criaTrilhaWakandaRequest() {
        return TrilhaWakandaRequest.builder()
                .nome("Profissão Programador")
                .descricao("Trilha de Formação em Desenvolvimento Back-End")
                .xpTotal(0)
                .build();
    }
}
