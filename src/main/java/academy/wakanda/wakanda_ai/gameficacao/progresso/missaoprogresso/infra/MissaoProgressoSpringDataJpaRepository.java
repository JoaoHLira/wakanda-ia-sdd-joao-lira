package academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.infra;

import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.application.api.MissaoConcluidaProjection;
import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.domain.MissaoProgresso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MissaoProgressoSpringDataJpaRepository extends JpaRepository<MissaoProgresso, UUID> {
    @Query("SELECT m FROM MissaoProgresso m WHERE m.idMissaoWakanda = :idMissao AND m.idProgressoWakander = :idProgressoWakander")
    Optional<MissaoProgresso> findByMissaoAndProgressoWakander(
            @Param("idMissao") UUID idMissao,
            @Param("idProgressoWakander") UUID idProgressoWakander);

    List<MissaoProgresso> findByIdMissaoWakandaIn(List<UUID> idsMissoes);

    List<MissaoProgresso> findByIdMissaoWakandaInAndIdProgressoWakander(List<UUID> idsMissoes, UUID idProgressoWakander);

    boolean existsByIdMissaoWakandaAndIdProgressoWakander(UUID idMissaoWakanda, UUID idProgressoWakander);

    Optional<MissaoProgresso> findByIdMissaoProgresso(UUID idMissaoProgresso);

    @Query(value = """
            SELECT mp.id_missao_progresso AS idMissaoProgresso,
                   mw.id_missao AS idMissao,
                   mw.titulo AS titulo,
                   mp.xp_obtido AS xpObtido,
                   mp.data_conclusao AS dataConclusao,
                   mp.sab_teorico AS sabTeorico,
                   mp.sab_processo AS sabProcesso,
                   mp.sab_know_how AS sabKnowHow,
                   mp.sab_comportamental AS sabComportamental,
                   mp.sab_criativo AS sabCriativo
            FROM missao_progresso mp
            JOIN missao_wakanda mw ON mw.id_missao = mp.id_missao_wakanda
            WHERE mp.id_progresso_wakander = :idProgressoWakander
              AND mp.status = 'CONCLUIDA'
            ORDER BY mp.data_conclusao DESC
            """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM missao_progresso mp
                    WHERE mp.id_progresso_wakander = :idProgressoWakander
                      AND mp.status = 'CONCLUIDA'
                    """,
            nativeQuery = true)
    Page<MissaoConcluidaProjection> findMissoesConcluidasByIdProgressoWakander(
            @Param("idProgressoWakander") UUID idProgressoWakander,
            Pageable pageable);
}
