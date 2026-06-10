package academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.datahelper;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.Sabedorias;
import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.application.api.AtualizaRequisitosRequest;
import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.application.api.ClasseWakandaRequest;
import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.domain.ClasseWakanda;
import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.domain.ClasseWakandaStatus;

import java.util.List;
import java.util.UUID;

public class ClasseWakandaDataHelper {

	public static ClasseWakandaRequest criarClasseWakandaRequestValida() {
		return ClasseWakandaRequest.builder().nome("Classe Iniciante").descricao("Classe inicial para novos Wakanders")
				.nivelNecessario(1).sabedorias(new Sabedorias(10, 10, 10, 10, 10))
				.missoesNecessarias(List.of(UUID.randomUUID())).build();
	}

	public static ClasseWakanda criarClasseWakanda(ClasseWakandaRequest request, int ordemClasse) {
		return new ClasseWakanda(request, ordemClasse);
	}

	public static ClasseWakandaRequest criarClasseWakandaRequestComNivel(Integer nivelNecessario) {
		return ClasseWakandaRequest.builder().nome("Classe Iniciante").descricao("Classe inicial para novos Wakanders")
				.nivelNecessario(nivelNecessario).sabedorias(new Sabedorias(10, 10, 10, 10, 10))
				.missoesNecessarias(List.of(UUID.randomUUID())).build();
	}

	public static ClasseWakandaRequest criarClasseWakandaRequestSemSabedorias() {
		return ClasseWakandaRequest.builder().nome("Classe Iniciante").descricao("Classe inicial para novos Wakanders")
				.nivelNecessario(1).sabedorias(null).missoesNecessarias(List.of(UUID.randomUUID())).build();
	}

	public static ClasseWakandaRequest criarClasseWakandaRequestSemMissoes() {
		return ClasseWakandaRequest.builder().nome("Classe Iniciante").descricao("Classe inicial para novos Wakanders")
				.nivelNecessario(1).sabedorias(new Sabedorias(10, 10, 10, 10, 10)).missoesNecessarias(List.of())
				.build();
	}

	public static AtualizaRequisitosRequest patchParcial() {
		return AtualizaRequisitosRequest.builder().nome("Nome OK").descricao("Descricao OK")
				.missoesNecessarias(List.of(UUID.randomUUID())).build();
	}

	public static AtualizaRequisitosRequest patchParcialSemMissoes() {
		return AtualizaRequisitosRequest.builder().nome("Nome OK").descricao("Descricao OK").missoesNecessarias(List.of())
				.build();
	}

    public static ClasseWakanda criarClasseWakandaClasseAtual(UUID idClasseAtual, UUID idMissao1, UUID idMissao2 ) {
        return new ClasseWakanda(
                idClasseAtual,
                "Classe Atual",
                "Classe atual do wakander",
                1,
                1,
                new Sabedorias(5, 5, 5, 5, 5),
                List.of(idMissao1, idMissao2),
                ClasseWakandaStatus.ATIVA
        );
    }

    public static ClasseWakanda criarClasseWakandaProximaClasse(UUID idProximaClasse, UUID idMissao1, UUID idMissao2) {
        return new ClasseWakanda(
                idProximaClasse,
                "Próxima Classe",
                "Próxima classe do wakander",
                4,
                2,
                new Sabedorias(10, 10, 10, 10, 10),
                List.of(idMissao1, idMissao2),
                ClasseWakandaStatus.ATIVA
        );
    }

}

