package academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.infra;

import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.application.repository.ClasseWakandaRepository;
import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.domain.ClasseWakanda;
import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.domain.ClasseWakandaStatus;
import academy.wakanda.wakanda_ai.handler.APIException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Log4j2
@RequiredArgsConstructor
public class ClasseWakandaInfraRepository implements ClasseWakandaRepository {

	private final ClasseWakandaSpringDataJpaRepository classeWakandaSpringDataJpaRepository;

	@Override
	public ClasseWakanda salva(ClasseWakanda classeWakanda) {
		log.info("[start] ClasseWakandaInfraRepository - salva");
		try {
			classeWakandaSpringDataJpaRepository.save(classeWakanda);
		} catch (DataIntegrityViolationException ex) {
			throw APIException.build(HttpStatus.CONFLICT, "Classe já existe!");
		}
		log.debug("[finish] ClasseWakandaInfraRepository - salva");
		return classeWakanda;
	}

	@Override
	public ClasseWakanda buscaClassePorId(UUID idClasse) {
		log.info("[start] ClasseWakandaInfraRepository - buscaClassePorId");
		ClasseWakanda classeWakanda = classeWakandaSpringDataJpaRepository.findById(idClasse)
				.orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Classe Wakanda não encontrada!"));
		log.debug("[finish] ClasseWakandaInfraRepository - buscaClassePorId");
		return classeWakanda;
	}

	@Override
	public boolean verificaSeExistePorNome(String nome) {
		log.info("[start] ClasseWakandaInfraRepository - verificaSeExistePorNome");
		boolean existe = classeWakandaSpringDataJpaRepository.existsByNome(nome);
		log.debug("[finish] ClasseWakandaInfraRepository - verificaSeExistePorNome");
		return existe;
	}

	@Override
	public boolean verificaSeExistePorDescricao(String descricao) {
		log.info("[start] ClasseWakandaInfraRepository - verificaSeExistePorDescricao");
		boolean existe = classeWakandaSpringDataJpaRepository.existsByDescricao(descricao);
		log.debug("[finish] ClasseWakandaInfraRepository - verificaSeExistePorDescricao");
		return existe;
	}

	@Override
	public boolean verificaSeExistePorNivelNecessario(Integer nivelNecessario) {
		log.info("[start] ClasseWakandaInfraRepository - verificaSeExistePorNivelNecessario");
		boolean existe = classeWakandaSpringDataJpaRepository.existsByNivelNecessario(nivelNecessario);
		log.debug("[finish] ClasseWakandaInfraRepository - verificaSeExistePorNivelNecessario");
		return existe;
	}

	@Override
	public Optional<Integer> buscaMaiorOrdemClasse() {
		log.info("[start] ClasseWakandaInfraRepository - buscaMaiorOrdemClasse");
		Optional<Integer> maiorOrdem = classeWakandaSpringDataJpaRepository.findMaxOrdemClasse();
		log.debug("[finish] ClasseWakandaInfraRepository - buscaMaiorOrdemClasse");
		return maiorOrdem;
	}

	@Override
	public List<ClasseWakanda> buscaPorStatus(ClasseWakandaStatus status) {
		log.info("[start] ClasseWakandaInfraRepository - buscaPorStatus status={}", status);
		List<ClasseWakanda> classes = classeWakandaSpringDataJpaRepository.findByStatus(status);
		log.debug("[finish] ClasseWakandaInfraRepository - buscaPorStatus total={}", classes.size());
		return classes;
	}

    @Override
    public ClasseWakanda buscaProximaClassePorOrdem(Integer ordemAtual) {
        log.info("[start] ClasseWakandaInfraRepository - buscaProximaClassePorOrdem");
        ClasseWakanda proximaClasse = classeWakandaSpringDataJpaRepository.findProximaClassePorOrdem(ordemAtual)
                .orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Próxima ClasseWakanda não encontrada!"));
        log.debug("[finish] ClasseWakandaInfraRepository - buscaProximaClassePorOrdem");
        return proximaClasse;
    }

    @Override
    public Optional<ClasseWakanda> buscaOptionalProximaClassePorOrdem(Integer ordemAtual) {
        log.info("[start] ClasseWakandaInfraRepository - buscaOptionalProximaClassePorOrdem");
        Optional<ClasseWakanda> proximaClasse = classeWakandaSpringDataJpaRepository.findProximaClassePorOrdem(ordemAtual);
        log.debug("[finish] ClasseWakandaInfraRepository - buscaOptionalProximaClassePorOrdem");
        return proximaClasse;
    }
}
