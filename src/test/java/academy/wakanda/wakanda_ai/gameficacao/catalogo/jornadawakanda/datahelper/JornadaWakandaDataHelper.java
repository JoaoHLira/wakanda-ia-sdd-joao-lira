package academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.datahelper;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.application.api.JornadaCriacaoRequest;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.application.api.JornadaResponse;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.domain.JornadaWakanda;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.domain.StatusJornada;

import java.util.UUID;

public class JornadaWakandaDataHelper {

    public static JornadaWakanda criarJornadaValida() {
        return new JornadaWakanda(
                UUID.randomUUID(),
                "Jornada de Teste",
                "Descrição da Jornada de Teste",
                100,
                UUID.randomUUID(),
                StatusJornada.ATIVA,
                10,
                0);
    }

    public static JornadaWakanda criarJornadaJavaBasico() {
        return new JornadaWakanda(
                UUID.randomUUID(),
                "Jornada Java Básico",
                "Aprenda os fundamentos do Java com desafios práticos e progressivos.",
                150,
                UUID.fromString("a330c529-c5d9-408c-b07a-e882365f23f2"),
                StatusJornada.ATIVA,
                10,
                0);
    }

    public static JornadaCriacaoRequest criarJornadaCriacaoRequestJavaBasico() {
        return JornadaCriacaoRequest.builder()
                .titulo("Jornada Java Basico")
                .descricao("Aprenda os fundamentos do Java com desafios práticos e progressivos.")
                .idTrilhaWakanda(UUID.fromString("a330c529-c5d9-408c-b07a-e882365f23f2"))
                .build();

    }

    public static JornadaCriacaoRequest criarJornadaCriacaoRequestValido() {
        return JornadaCriacaoRequest.builder()
                .titulo("Jornada Genérica")
                .descricao("Descrição de uma jornada genérica válida para testes.")
                .idTrilhaWakanda(UUID.randomUUID())
                .build();
    }

    public static JornadaResponse criarJornadaResponseAtiva() {
        return JornadaResponse.builder()
                .idJornada(UUID.randomUUID())
                .titulo("Jornada Ativa")
                .descricao("Jornada ativa para testes")
                .statusJornada(StatusJornada.ATIVA)
                .build();
    }

    public static JornadaResponse criarJornadaResponseInativa() {
        return JornadaResponse.builder()
                .idJornada(UUID.randomUUID())
                .titulo("Jornada Inativa")
                .descricao("Jornada inativa para testes")
                .statusJornada(StatusJornada.INATIVA)
                .build();
    }

    public static JornadaWakanda criarJornadaComTrilhaEOrdem(UUID idTrilha, int ordem) {
        return new JornadaWakanda(
                UUID.randomUUID(),
                "Jornada " + ordem,
                "Descrição",
                100,
                idTrilha,
                StatusJornada.ATIVA,
                10,
                ordem
        );
    }

    public static JornadaWakanda criarProximaJornada(UUID idTrilha, int ordemAtual) {
        return criarJornadaComTrilhaEOrdem(idTrilha, ordemAtual + 1);
    }
}
