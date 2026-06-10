package academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.infra;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.repository.MissaoWakandaRepository;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.MissaoStatus;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.MissaoWakanda;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.OrdemMissao;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.ProcessamentoStatus;
import academy.wakanda.wakanda_ai.handler.APIException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Repository
@Log4j2
@RequiredArgsConstructor
public class MissaoWakandaInfraRepository implements MissaoWakandaRepository {

    private final MissaoWakandaSpringDataJPARepository missaoWakandaSpringDataJPARepository;

    @Override
    public MissaoWakanda salvaMissao(MissaoWakanda novaMissao) {
        log.info("[start] MissaoWakandaInfraRepository - salvaMissao");
        try {
            missaoWakandaSpringDataJPARepository.save(novaMissao);
        } catch (DataIntegrityViolationException ex) {
            throw APIException.build(HttpStatus.CONFLICT, "Missão já existe!");
        }
        log.debug("[finish] MissaoWakandaInfraRepository - salvaMissao");
        return novaMissao;
    }

    @Override
    public MissaoWakanda buscaMissaoPorId(UUID idMissao) {
        log.info("[start] MissaoWakandaInfraRepository - buscaMissaoPorId");
        MissaoWakanda missao = missaoWakandaSpringDataJPARepository.findById(idMissao)
                .orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Missão não encontrada!"));
        log.debug("[finish] MissaoWakandaInfraRepository - buscaMissaoPorId");
        return missao;
    }

    @Override
    public boolean validaTitulo(String titulo) {
        log.info("[start] MissaoWakandaInfraRepository - validaTitulo");
        boolean exists = missaoWakandaSpringDataJPARepository.existsByTitulo(titulo);
        log.debug("[finish] MissaoWakandaInfraRepository - validaTitulo");
        return exists;
    }

    @Override
    public List<MissaoWakanda> buscaMissoesPorIdJornada(UUID jornada) {
        log.info("[start] MissaoWakandaInfraRepository - buscaMissoesPorIdJornada");
        List<MissaoWakanda> missoes = missaoWakandaSpringDataJPARepository.findAllByIdJornada(jornada);
        log.debug("[finish] MissaoWakandaInfraRepository - buscaMissoesPorIdJornada");
        return missoes;
    }

    @Override
    public Integer contarMissoesAtivasPorJornada(UUID idJornada) {
        log.info("[start] MissaoWakandaInfraRepository - contarMissoesAtivasPorJornada");
        Integer quantidadeMissoesAtivas = missaoWakandaSpringDataJPARepository
                .countByIdJornadaAndMissaoStatus(idJornada, MissaoStatus.ATIVA);
        log.debug("[finish] MissaoWakandaInfraRepository - contarMissoesAtivasPorJornada");
        return quantidadeMissoesAtivas;
    }

    @Override
    public List<MissaoWakanda> buscarMissoesAtivasOrdenadas(UUID idJornada) {
        log.info("[start] MissaoWakandaInfraRepository - buscarMissoesOrdenadas");
        List<MissaoWakanda> missoes = missaoWakandaSpringDataJPARepository
                .findByIdJornadaAndMissaoStatusOrderByOrdemMissaoAsc(idJornada, MissaoStatus.ATIVA);
        log.debug("[finish] MissaoWakandaInfraRepository - buscarMissoesOrdenadas");
        return missoes;
    }

    public List<MissaoWakanda> buscarTodasMissoesOrdenadas(UUID idJornada) {
        log.info("[start] MissaoWakandaInfraRepository - buscarTodasMissoesOrdenadas");
        List<MissaoWakanda> missoes = missaoWakandaSpringDataJPARepository
                .findAllByIdJornadaOrderByOrdemMissaoAsc(idJornada);
        log.debug("[finish] MissaoWakandaInfraRepository - buscarTodasMissoesOrdenadas");
        return missoes;
    }

    @Override
    public MissaoWakanda buscaMissaoPorIdMissaoExterna(String idMissaoExterna) {
        log.info("[start] MissaoWakandaInfraRepository - buscaMissaoPorIdMissaoExterna");
        MissaoWakanda missaoWakanda = missaoWakandaSpringDataJPARepository.findByIdMissaoExterna(idMissaoExterna)
                .orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Missão Wakanda não encontrado!"));
        log.debug("[finish] MissaoWakandaInfraRepository - buscaMissaoPorIdMissaoExterna");
        return missaoWakanda;
    }

    @Override
    public List<MissaoWakanda> buscaMissoesOrdenadasPorJornada(UUID idJornada) {
        log.info("[start] MissaoWakandaInfraRepository - buscaMissoesOrdenadasPorJornada");
        List<MissaoWakanda> listaMissoes = missaoWakandaSpringDataJPARepository
                .findAllByIdJornadaOrderByOrdemMissaoAsc(idJornada);
        log.debug("[finish] MissaoWakandaInfraRepository - buscaMissoesOrdenadasPorJornada");
        return listaMissoes;
    }

    @Override
    public Optional<MissaoWakanda> buscaProximaMissao(UUID idJornada, int ordemProximaMissao) {
        log.info("[start] MissaoWakandaInfraRepository - buscaProximaMissao");
        Optional<MissaoWakanda> proximaMissao = missaoWakandaSpringDataJPARepository
                .findFirstByIdJornadaAndOrdemMissao(idJornada, OrdemMissao.criar(ordemProximaMissao));
        log.debug("[finish] MissaoWakandaInfraRepository - buscaProximaMissao");
        return proximaMissao;
    }

    @Override
    public List<MissaoWakanda> buscaMissoesAtivasPorIdJornada(UUID idJornada, MissaoStatus missaoStatus) {
        log.info("[start] MissaoWakandaInfraRepository - buscaMissoesAtivasPorIdJornada");
        List<MissaoWakanda> missoesWakanda = missaoWakandaSpringDataJPARepository
                .findByIdJornadaAndMissaoStatus(idJornada, missaoStatus);
        if (missoesWakanda.isEmpty()) {
            throw APIException.build(HttpStatus.NOT_FOUND,
                    "Nenhuma missão ativa para a jornada " + idJornada + " foi encontrada!");
        }
        log.debug("[finish] MissaoWakandaInfraRepository - buscaMissoesAtivasPorIdJornada");
        return missoesWakanda;
    }

    @Override
    public Optional<MissaoWakanda> buscaMissaoPai(UUID idMissaoPai) {
        log.info("[start] MissaoWakandaInfraRepository - buscaMissaoPai");
        log.info("[idMissaoPai]: {}", idMissaoPai);
        Optional<MissaoWakanda> missaoPai = missaoWakandaSpringDataJPARepository.findById(idMissaoPai).or(() -> {
            log.warn("Missão pai não encontrada para o id: {}", idMissaoPai);
            return Optional.empty();
        });
        log.debug("[finish] MissaoWakandaInfraRepository - buscaMissaoPai");
        return missaoPai;
    }

    @Override
    public Page<MissaoWakanda> buscaMissoesPaginadas(Pageable pageable, UUID idJornada, MissaoStatus missaoStatus) {
        log.info("[start] MissaoWakandaInfraRepository - buscaMissoes");
        Page<MissaoWakanda> missoesPaginadas = missaoWakandaSpringDataJPARepository
                .findByIdJornadaAndMissaoStatusAndProcessamentoStatus(idJornada, missaoStatus, pageable,
                        ProcessamentoStatus.COMPLETO);
        log.debug("[finish] MissaoWakandaInfraRepository - buscaMissoes");
        return missoesPaginadas;
    }

    @Override
    public Optional<MissaoWakanda> buscaIdMissaoWakandaExterno(String idMissao) {
        log.info("[start] MissaoWakandaInfraRepository - buscaIdMissaoWakandaExterno");
        Optional<MissaoWakanda> missaoEx = missaoWakandaSpringDataJPARepository.findByIdMissaoExterna(idMissao)
                .or(() -> {
                    log.warn("Missão não encontrada para o id: {}", idMissao);
                    return Optional.empty();
                });
        log.debug("[finish] MissaoWakandaInfraRepository - buscaIdMissaoWakandaExterno");
        return missaoEx;
    }

    @Override
    public List<MissaoWakandaProjection> buscaMissoesPorIds(List<UUID> idsMissoes) {
        log.info("[start] MissaoWakandaInfraRepository - buscaMissoesPorIds");
        List<MissaoWakandaProjection> missoes = missaoWakandaSpringDataJPARepository.findByIdMissaoIn(idsMissoes);
        missoes.forEach(missao -> log.debug("Missão encontrada - ID: {}, Status: {}", missao.getIdMissao(),
                missao.getMissaoStatus()));
        log.debug("[finish] MissaoWakandaInfraRepository - buscaMissoesPorIds");
        return missoes;
    }

    @Override
    public List<MissaoWakanda> salvaMissoes(List<MissaoWakanda> missoes) {
        log.info("[start] MissaoWakandaInfraRepository - salvaMissoes (batch)");
        try {
            List<MissaoWakanda> saved = missaoWakandaSpringDataJPARepository.saveAll(missoes);
            log.debug("[finish] MissaoWakandaInfraRepository - salvaMissoes (batch)");
            return saved;
        } catch (DataIntegrityViolationException ex) {
            throw APIException.build(HttpStatus.CONFLICT, "Erro ao salvar missões em lote");
        }
    }
}