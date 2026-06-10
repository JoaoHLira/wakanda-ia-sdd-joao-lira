package academy.wakanda.wakanda_ai.autenticacao.infra;

import academy.wakanda.wakanda_ai.autenticacao.domain.Autenticacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AutenticacaoSpringDataJPARepository extends JpaRepository<Autenticacao, String> {
    Optional<Autenticacao> findByToken(String token);
    void deleteByDataExpiracaoBefore(LocalDateTime dataBase);
}
