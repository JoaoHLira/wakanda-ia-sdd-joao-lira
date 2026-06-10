package academy.wakanda.wakanda_ai.gameficacao.catalogo.trilhawakanda.infra;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.trilhawakanda.application.api.TrilhaDetalhadaProjection;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.trilhawakanda.domain.TrilhaWakanda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface TrilhaSpringDataJpaRepository extends JpaRepository<TrilhaWakanda, UUID> {

    @Query("""
                select t.nome as nome, t.descricao as descricao, t.xpTotal as xpTotal, cast(count(j) as int) as totalDeJornadas
                from TrilhaWakanda t
                left join JornadaWakanda j
                     on j.idTrilhaWakanda = t.idTrilha
                where t.idTrilha = :idTrilha
                group by t.idTrilha
            """)
    Optional<TrilhaDetalhadaProjection> buscaTrilhaComTotalDeJornadas(UUID idTrilha);
}
