package academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.infra;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.MissaoStatus;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.MissaoWakanda;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.ProcessamentoStatus;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.OrdemMissao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MissaoWakandaSpringDataJPARepository extends JpaRepository<MissaoWakanda, UUID> {

    boolean existsByTitulo(String titulo);

    List<MissaoWakanda> findAllByIdJornada(UUID jornada);

    List<MissaoWakanda> findAllByIdJornadaOrderByOrdemMissaoAsc(UUID idJornada);

    Optional<MissaoWakanda> findFirstByIdJornadaAndOrdemMissao(UUID idJornada, OrdemMissao ordemProximaMissao);

    Optional<MissaoWakanda> findByIdMissaoExterna(String idMissaoExterna);

    List<MissaoWakanda> findByIdJornadaAndMissaoStatus(UUID idJornada, MissaoStatus missaoStatus);

    Integer countByIdJornadaAndMissaoStatus(UUID idJornada, MissaoStatus missaoStatus);

    List<MissaoWakanda> findByIdJornadaAndMissaoStatusOrderByOrdemMissaoAsc(UUID idJornada, MissaoStatus missaoStatus);

    Page<MissaoWakanda> findByIdJornadaAndMissaoStatusAndProcessamentoStatus(UUID idJornada, MissaoStatus missaoStatus,
            Pageable pageable, ProcessamentoStatus processamentoStatus);

    @Query("""
            select m.idMissao as idMissao, m.missaoStatus as missaoStatus
            from MissaoWakanda m
            where m.idMissao in :idsMissoes
            """)
    List<MissaoWakandaProjection> findByIdMissaoIn(@Param("idsMissoes") List<UUID> idsMissoes);

}
