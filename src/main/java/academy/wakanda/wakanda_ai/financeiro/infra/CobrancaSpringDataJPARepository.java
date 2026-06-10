package academy.wakanda.wakanda_ai.financeiro.infra;

import academy.wakanda.wakanda_ai.financeiro.domain.cobranca.Cobranca;
import academy.wakanda.wakanda_ai.financeiro.domain.cobranca.CobrancaStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CobrancaSpringDataJPARepository extends JpaRepository<Cobranca, UUID> {
    Optional<Cobranca> findByIdPaymentAsaas(String idPaymentAsaas);
    List<Cobranca> findAllByIdWakanderAndStatus(UUID idWakander, CobrancaStatus status);
}
