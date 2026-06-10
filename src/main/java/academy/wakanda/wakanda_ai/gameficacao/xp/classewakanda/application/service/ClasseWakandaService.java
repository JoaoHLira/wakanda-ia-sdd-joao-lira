package academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.application.service;

import java.util.List;
import java.util.UUID;

import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.application.api.AtualizaRequisitosRequest;
import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.application.api.ClasseWakandaRequest;
import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.application.api.ClasseWakandaResponse;

public interface ClasseWakandaService {
	ClasseWakandaResponse criaNovaClasse(ClasseWakandaRequest request);


	ClasseWakandaResponse buscaClassePorId(UUID idClasse);

	void atualizaRequisitosClasse(AtualizaRequisitosRequest atualizaRequisitosRequest, UUID idClasse);

	List<ClasseWakandaResponse> buscaClassesAtivas();
}
