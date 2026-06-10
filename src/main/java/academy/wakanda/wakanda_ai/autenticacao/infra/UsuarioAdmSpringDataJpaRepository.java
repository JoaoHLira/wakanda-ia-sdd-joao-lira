package academy.wakanda.wakanda_ai.autenticacao.infra;

import academy.wakanda.wakanda_ai.autenticacao.domain.UsuarioAdm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioAdmSpringDataJpaRepository extends JpaRepository<UsuarioAdm, UUID> {
    Optional<UsuarioAdm> findByUsername(String username);
}
