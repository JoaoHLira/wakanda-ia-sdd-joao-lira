package academy.wakanda.wakanda_ai.financeiro.application.service.cobranca;

import org.springframework.stereotype.Component;

import academy.wakanda.wakanda_ai.financeiro.domain.cobranca.Cobranca;
import academy.wakanda.wakanda_ai.financeiro.domain.cobranca.CobrancaEvento;
import academy.wakanda.wakanda_ai.financeiro.domain.cobranca.CobrancaEventoType;
import academy.wakanda.wakanda_ai.wakander.application.service.WakanderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@RequiredArgsConstructor
public class CobrancaProcessadorCriada implements CobrancaProcessadorAsaas {
    private final WakanderRepository wakanderRepository;

    @Override
    public boolean validaSeProcessa(CobrancaEvento request) {
        return request.getEvent().equals(CobrancaEventoType.PAYMENT_CREATED);
    }

    @Override
    public Cobranca processa(CobrancaEvento request) {
        log.info("[start] CobrancaCriadorProcessador - processa");
        Cobranca novaCobranca = geraNovaCobranca(request);
        log.debug("[finish] CobrancaCriadorProcessador - processa");
        return novaCobranca;
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
}
