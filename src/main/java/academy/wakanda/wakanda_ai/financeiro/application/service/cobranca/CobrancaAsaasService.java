package academy.wakanda.wakanda_ai.financeiro.application.service.cobranca;

import academy.wakanda.wakanda_ai.financeiro.application.api.CobrancaAsaasDto;

public interface CobrancaAsaasService {
    void processaEvento(CobrancaAsaasDto request);
}
