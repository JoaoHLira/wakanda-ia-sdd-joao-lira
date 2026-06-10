package academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.infra;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.application.api.JornadaDetalhadaProjection;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.domain.JornadaWakanda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JornadaWakandaSpringDataJpaRepository extends JpaRepository<JornadaWakanda, UUID> {

    List<JornadaWakanda> findAllByIdTrilhaWakanda(UUID trilhaWakanda);

    @Query("""
            select j.titulo as titulo, j.descricao as descricao, j.xpTotal as xpTotal, j.statusJornada as statusJornada,
            j.xpBonus as xpBonus, count(m) as totalDeMissoes
            from JornadaWakanda j
            left join MissaoWakanda m
            on m.idJornada = j.idJornada
            where j.idJornada = :idJornada
            GROUP BY j.titulo, j.descricao, j.xpTotal, j.statusJornada, j.xpBonus
            """)
    Optional<JornadaDetalhadaProjection> buscarJornadaComTotalDeMissoes(UUID idJornada);

    Optional<JornadaWakanda> findByTitulo(String jornadaWakanda);
}
