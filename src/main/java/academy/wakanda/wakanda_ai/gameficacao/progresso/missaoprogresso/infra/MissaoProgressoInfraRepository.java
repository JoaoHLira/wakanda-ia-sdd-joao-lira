package academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.infra;

import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.application.api.MissaoConcluidaProjection;
import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.application.service.MissaoProgressoRepository;
import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.domain.MissaoProgresso;
import academy.wakanda.wakanda_ai.handler.APIException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Log4j2
public class MissaoProgressoInfraRepository implements MissaoProgressoRepository {

    private final MissaoProgressoSpringDataJpaRepository missaoProgressoSpringDataJpaRepository;

    @Override
    public MissaoProgresso buscaMissaoProgresso(UUID idMissao, UUID idProgressoWakander) {
        log.info("[start] MissaoProgressoInfraRepository - buscaMissaoProgresso");
        MissaoProgresso missaoProgresso = missaoProgressoSpringDataJpaRepository.findByMissaoAndProgressoWakander(idMissao, idProgressoWakander)
                .orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Missão Progresso não encontrado!"));
        log.debug("[finish] MissaoProgressoInfraRepository - buscaMissaoProgresso");
        return missaoProgresso;
    }

    @Override
    public Optional<MissaoProgresso> buscaOptionalMissaoProgresso(UUID idMissao, UUID idProgressoWakander) {
        log.info("[start] MissaoProgressoInfraRepository - buscaOptionalMissaoProgresso");
        Optional<MissaoProgresso> missaoProgresso = missaoProgressoSpringDataJpaRepository.findByMissaoAndProgressoWakander(idMissao, idProgressoWakander);
        log.debug("[finish] MissaoProgressoInfraRepository - buscaOptionalMissaoProgresso");
        return missaoProgresso;
    }

    @Override
    public MissaoProgresso salvaProgressoMissao(MissaoProgresso missaoProgresso) {
        log.info("[start] MissaoProgressoInfraRepository - salvaMissao");
        try {
            missaoProgressoSpringDataJpaRepository.save(missaoProgresso);
        } catch (DataIntegrityViolationException ex) {
            throw APIException.build(HttpStatus.CONFLICT, "Missão Progresso já existe!");
        }
        log.debug("[finish] MissaoProgressoInfraRepository - salvaMissao");
        return missaoProgresso;
    }

    @Override
    public boolean existeMissaoProgresso(UUID idMissao, UUID idProgressoWakander) {
        log.info("[start] MissaoProgressoInfraRepository - existeMissaoProgresso");
        boolean existeMissaoProgresso = missaoProgressoSpringDataJpaRepository
                .existsByIdMissaoWakandaAndIdProgressoWakander(idMissao, idProgressoWakander);
        log.debug("[finish] MissaoProgressoInfraRepository - existeMissaoProgresso");
        return existeMissaoProgresso;
    }

    @Override
    public List<MissaoProgresso> buscaMissoesProgressoPorIdsMissoes(List<UUID> idsMissoes) {
        log.info("[start] MissaoProgressoInfraRepository - buscaMissoesProgressoPorIdsMissoes");
        List<MissaoProgresso> missoesProgresso = missaoProgressoSpringDataJpaRepository.findByIdMissaoWakandaIn(idsMissoes);
        if (missoesProgresso.isEmpty()) {
            throw APIException.build(HttpStatus.NOT_FOUND, "Nenhum registro em Missão Progresso foi encontrado!");
        }
        log.debug("[finish] MissaoProgressoInfraRepository - buscaMissoesProgressoPorIdsMissoes");
        return missoesProgresso;
    }

    @Override
    public List<MissaoProgresso> buscaMissoesProgressoPorIdsMissoesEProgresso(List<UUID> idsMissoes, UUID idProgressoWakander) {
        log.info("[start] MissaoProgressoInfraRepository - buscaMissoesProgressoPorIdsMissoesEProgresso");
        List<MissaoProgresso> missoesProgresso = missaoProgressoSpringDataJpaRepository
                .findByIdMissaoWakandaInAndIdProgressoWakander(idsMissoes, idProgressoWakander);
        log.debug("[finish] MissaoProgressoInfraRepository - buscaMissoesProgressoPorIdsMissoesEProgresso");
        return missoesProgresso;
    }

    @Override
    public MissaoProgresso buscaMissaoProgressoPorId(UUID idMissaoProgresso) {
        log.info("[start] MissaoProgressoInfraRepository - buscaMissaoProgressoPorId");
        MissaoProgresso missaoProgresso = missaoProgressoSpringDataJpaRepository.findByIdMissaoProgresso(idMissaoProgresso)
                .orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Progresso de missão não encontrado!"));
        log.debug("[finish] MissaoProgressoInfraRepository - buscaMissaoProgressoPorId");
        return missaoProgresso;
    }

    @Override
    public Page<MissaoConcluidaProjection> listaMissoesConcluidasPorProgresso(UUID idProgressoWakander, Pageable pageable) {
        log.info("[start] MissaoProgressoInfraRepository - listaMissoesConcluidasPorProgresso");
        Page<MissaoConcluidaProjection> missoesConcluidas = missaoProgressoSpringDataJpaRepository.findMissoesConcluidasByIdProgressoWakander(idProgressoWakander, pageable);
        log.debug("[finish] MissaoProgressoInfraRepository - listaMissoesConcluidasPorProgresso");
        return missoesConcluidas;
    }
}
