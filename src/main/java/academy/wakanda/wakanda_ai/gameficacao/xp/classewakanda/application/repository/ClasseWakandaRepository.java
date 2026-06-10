package academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.application.repository;

import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.domain.ClasseWakanda;
import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.domain.ClasseWakandaStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClasseWakandaRepository {
	ClasseWakanda salva(ClasseWakanda classeWakanda);
	ClasseWakanda buscaClassePorId(UUID idClasse);
	boolean verificaSeExistePorNome(String nome);
	boolean verificaSeExistePorDescricao(String descricao);
	boolean verificaSeExistePorNivelNecessario(Integer nivelNecessario);
	Optional<Integer> buscaMaiorOrdemClasse();
	List<ClasseWakanda> buscaPorStatus(ClasseWakandaStatus classeWakandaStatus);
    ClasseWakanda buscaProximaClassePorOrdem(Integer ordemAtual);
    Optional<ClasseWakanda> buscaOptionalProximaClassePorOrdem(Integer ordemAtual);
}
