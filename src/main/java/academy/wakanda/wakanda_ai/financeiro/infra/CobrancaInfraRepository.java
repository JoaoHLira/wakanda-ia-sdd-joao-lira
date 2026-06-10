package academy.wakanda.wakanda_ai.financeiro.infra;

import academy.wakanda.wakanda_ai.financeiro.domain.cobranca.Cobranca;
import academy.wakanda.wakanda_ai.financeiro.domain.cobranca.CobrancaStatus;
import academy.wakanda.wakanda_ai.financeiro.domain.cobranca.CobrancaEvento;
import academy.wakanda.wakanda_ai.handler.APIException;
import academy.wakanda.wakanda_ai.wakander.application.service.WakanderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Log4j2
@Repository
@RequiredArgsConstructor
public class CobrancaInfraRepository implements CobrancaRepository {

    private final CobrancaSpringDataJPARepository cobrancaSpringDataJPARepository;
    private final WakanderRepository wakanderRepository;

    @Override
    public Cobranca buscaCobrancaPorIdPaymentAsaas(CobrancaEvento paymentAsaas) {
        log.info("[start] CobrancaInfraRepository - buscaCobrancaPorIdPaymentAsaas");
        Cobranca cobranca = cobrancaSpringDataJPARepository.findByIdPaymentAsaas(paymentAsaas.getPaymentId())
                .orElseGet(() -> criaCobrancaSeNaoExistir(paymentAsaas));
        log.debug("[finish] CobrancaInfraRepository - buscaCobrancaPorIdPaymentAsaas");
        return cobranca;
    }

    public Cobranca criaCobrancaSeNaoExistir(CobrancaEvento paymentAsaas) {
        log.info("[start] CobrancaInfraRepository - criaCobrancaSeNaoExistir");
        log.info("[info] Cobranca nao encontrada, criando nova cobranca com id de assinatura: {}", paymentAsaas.getSubscriptionId());
        Cobranca cobranca = geraNovaCobranca(paymentAsaas);
        salvaCobranca(cobranca);
        log.debug("[finish] CobrancaInfraRepository - criaCobrancaSeNaoExistir");
        return cobranca;
    }

	private Cobranca geraNovaCobranca(CobrancaEvento paymentAsaas) {
        log.info("[start] CobrancaInfraRepository - geraNovaCobranca");
		return wakanderRepository.buscaWakanderPorIdAssinatura(paymentAsaas.getSubscriptionId())
        .map(wakander -> new Cobranca(paymentAsaas, wakander.getIdWakander()))
        .orElseGet(() -> {
            log.error("Wakander não encontrado para assinatura: {}", paymentAsaas.getSubscriptionId());
            return new Cobranca(paymentAsaas);
        });
	}

    @Override
    public void salvaCobranca(Cobranca novaCobranca) {
        log.info("[start] CobrancaInfraRepository - salvaCobranca");
        try {
            cobrancaSpringDataJPARepository.save(novaCobranca);
        } catch (DataIntegrityViolationException ex) {
            throw APIException.build(HttpStatus.CONFLICT, "Erro ao salvar cobrança! Existem dados duplicados.");
        }
        log.debug("[finish] CobrancaInfraRepository - salvaCobranca");
    }

    @Override
    public List<Cobranca> buscaCobrancasVencidas(UUID idWakander) {
        log.info("[start] CobrancaInfraRepository - buscaCobrancasVencidas");
        List<Cobranca> cobrancasVencidas = cobrancaSpringDataJPARepository
                .findAllByIdWakanderAndStatus(idWakander, CobrancaStatus.PAGAMENTO_VENCIDO);
        log.debug("[finish] CobrancaInfraRepository - buscaCobrancasVencidas");
        return cobrancasVencidas;
    }
}
