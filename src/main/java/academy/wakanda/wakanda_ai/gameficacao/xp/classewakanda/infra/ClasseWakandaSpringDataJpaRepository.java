package academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.infra;

import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.domain.ClasseWakanda;
import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.domain.ClasseWakandaStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClasseWakandaSpringDataJpaRepository extends JpaRepository<ClasseWakanda, UUID> {

	boolean existsByNome(String nome);

	boolean existsByDescricao(String descricao);

	boolean existsByNivelNecessario(Integer nivelNecessario);

	@Query("SELECT MAX(c.ordemClasse) FROM ClasseWakanda c")
	Optional<Integer> findMaxOrdemClasse();

	List<ClasseWakanda> findByStatus(ClasseWakandaStatus status);

	@Query("SELECT c FROM ClasseWakanda c WHERE c.ordemClasse > :ordemAtual ORDER BY c.ordemClasse ASC LIMIT 1")
    Optional<ClasseWakanda> findProximaClassePorOrdem(@Param("ordemAtual") Integer ordemAtual);
}
