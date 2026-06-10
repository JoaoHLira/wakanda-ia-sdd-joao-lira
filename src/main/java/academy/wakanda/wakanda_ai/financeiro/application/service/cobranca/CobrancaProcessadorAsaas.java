package academy.wakanda.wakanda_ai.financeiro.application.service.cobranca;

import academy.wakanda.wakanda_ai.financeiro.domain.cobranca.Cobranca;
import academy.wakanda.wakanda_ai.financeiro.domain.cobranca.CobrancaEvento;

public interface CobrancaProcessadorAsaas {
    boolean validaSeProcessa(CobrancaEvento request);
    Cobranca processa(CobrancaEvento request);
}
