package academy.wakanda.wakanda_ai.gameficacao.catalogo.tipomissao.infra;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.tipomissao.domain.TipoMissao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;
public interface TipoMissaoSpringDataJpaRepository extends JpaRepository<TipoMissao, UUID> {
    boolean existsByDescricao(String descricao);

    Optional<TipoMissao> findByDescricao(String tipoMissaoWakanda);
}
