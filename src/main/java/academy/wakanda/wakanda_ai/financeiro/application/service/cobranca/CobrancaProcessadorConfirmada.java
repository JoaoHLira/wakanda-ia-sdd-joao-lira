package academy.wakanda.wakanda_ai.financeiro.application.service.cobranca;

import academy.wakanda.wakanda_ai.financeiro.domain.cobranca.Cobranca;
import academy.wakanda.wakanda_ai.financeiro.domain.cobranca.CobrancaEvento;
import academy.wakanda.wakanda_ai.financeiro.domain.cobranca.CobrancaEventoType;
import academy.wakanda.wakanda_ai.financeiro.infra.CobrancaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class CobrancaProcessadorConfirmada implements CobrancaProcessadorAsaas {

    private final CobrancaRepository cobrancaRepository;

    @Override
    public boolean validaSeProcessa(CobrancaEvento request) {
        return request.getEvent().equals(CobrancaEventoType.PAYMENT_CONFIRMED);
    }

    @Override
    public Cobranca processa(CobrancaEvento request) {
        log.info("[start] CobrancaProcessadorConfirmada - processa");
        Cobranca cobranca = cobrancaRepository.buscaCobrancaPorIdPaymentAsaas(request);
        cobranca.alteraStatusParaConfirmado(request);
        log.debug("[finish] CobrancaProcessadorConfirmada - processa");
        return cobranca;
    }
}
