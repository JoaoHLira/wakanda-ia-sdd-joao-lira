package academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.infra;

import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.api.ProgressoIndividualDetalhadoProjection;
import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.api.RankingWakanderProjection;
import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.domain.ProgressoWakander;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface ProgressoGameficacaoSpringDataRepository extends JpaRepository<ProgressoWakander, UUID> {

    Optional<ProgressoWakander> findByIdWakander(UUID idWakander);

    Optional<ProgressoWakander> findByIdProgressoWakander(UUID idProgressoWakander);

    @Query(value = "\n" +
            "SELECT w.id_wakander AS idWakander,\n" +
            "       w.nome AS nome,\n" +
            "       COUNT(mp.id_missao_progresso) AS missoesConcluidas,\n" +
            "       COALESCE(SUM(mp.xp_obtido), 0) AS xpTotal\n" +
            "FROM missao_progresso mp\n" +
            "JOIN progresso_wakander pw ON pw.id_progresso_wakander = mp.id_progresso_wakander\n" +
            "JOIN wakander w ON w.id_wakander = pw.id_wakander\n" +
            "WHERE mp.status = 'CONCLUIDA'\n" +
            "GROUP BY w.id_wakander, w.nome\n" +
            "ORDER BY missoesConcluidas DESC, xpTotal DESC\n",
            countQuery = "\n" +
                    "SELECT COUNT(DISTINCT w.id_wakander)\n" +
                    "FROM missao_progresso mp\n" +
                    "JOIN progresso_wakander pw ON pw.id_progresso_wakander = mp.id_progresso_wakander\n" +
                    "JOIN wakander w ON w.id_wakander = pw.id_wakander\n" +
                    "WHERE mp.status = 'CONCLUIDA'\n",
            nativeQuery = true)
    Page<RankingWakanderProjection> findRankingDestaqueGeral(Pageable pageable);

    @Query(value = "\n" +
            "SELECT w.id_wakander AS idWakander,\n" +
            "       w.nome AS nome,\n" +
            "       COUNT(mp.id_missao_progresso) AS missoesConcluidas,\n" +
            "       COALESCE(SUM(mp.xp_obtido), 0) AS xpTotal\n" +
            "FROM missao_progresso mp\n" +
            "JOIN progresso_wakander pw ON pw.id_progresso_wakander = mp.id_progresso_wakander\n" +
            "JOIN wakander w ON w.id_wakander = pw.id_wakander\n" +
            "WHERE mp.status = 'CONCLUIDA' AND mp.data_conclusao BETWEEN :inicio AND :fim\n" +
            "GROUP BY w.id_wakander, w.nome\n" +
            "ORDER BY missoesConcluidas DESC, xpTotal DESC\n",
            countQuery = "\n" +
                    "SELECT COUNT(DISTINCT w.id_wakander)\n" +
                    "FROM missao_progresso mp\n" +
                    "JOIN progresso_wakander pw ON pw.id_progresso_wakander = mp.id_progresso_wakander\n" +
                    "JOIN wakander w ON w.id_wakander = pw.id_wakander\n" +
                    "WHERE mp.status = 'CONCLUIDA' AND mp.data_conclusao BETWEEN :inicio AND :fim\n",
            nativeQuery = true)
    Page<RankingWakanderProjection> findRankingDestaquePorPeriodo(@Param("inicio") LocalDateTime inicio,
                                                                  @Param("fim") LocalDateTime fim,
                                                                  Pageable pageable);

    @Query(value = "SELECT w.nome AS nome, \n" +
            "       COALESCE(xw.nivel_atual, 0) AS nivelAtual, \n" +
            "       COALESCE(SUM(mp.xp_obtido), 0) AS xpTotal, \n" +
            "       COALESCE(SUM(mp.xp_obtido), 0) AS xpAtual, \n" +
            "       COALESCE(xw.xp_proximo_nivel, 0) AS xpProximoNivel, \n" +
            "       COALESCE(m.titulo, NULL) AS titulo, \n" +
            "       MAX(mp.ultima_atualizacao) AS ultimaAtualizacao, \n" +
            "       COALESCE(m.sab_teorico, 0) AS sabTeorico, \n" +
            "       COALESCE(m.sab_processo, 0) AS sabProcesso, \n" +
            "       COALESCE(m.sab_know_how, 0) AS sabKnowHow, \n" +
            "       COALESCE(m.sab_comportamental, 0) AS sabComportamental, \n" +
            "       COALESCE(m.sab_criativo, 0) AS sabCriativo \n" +
            "FROM wakander w \n" +
            "LEFT JOIN progresso_wakander pw ON pw.id_wakander = w.id_wakander \n" +
            "LEFT JOIN missao_progresso mp ON mp.id_progresso_wakander = pw.id_progresso_wakander \n" +
            "LEFT JOIN missao_wakanda m ON m.id_missao = mp.id_missao_wakanda \n" +
            "LEFT JOIN xp_wakander xw ON xw.id_progresso_wakander = pw.id_progresso_wakander \n" +
            "WHERE w.id_wakander = :idWakander \n" +
            "GROUP BY w.id_wakander, w.nome, xw.nivel_atual, xw.xp_proximo_nivel, m.titulo, m.sab_teorico, m.sab_processo, m.sab_know_how, m.sab_comportamental, m.sab_criativo \n" +
            "ORDER BY ultimaAtualizacao DESC\n",
            nativeQuery = true)
    Optional<ProgressoIndividualDetalhadoProjection> buscaprogressoIndividual(UUID idWakander);
}