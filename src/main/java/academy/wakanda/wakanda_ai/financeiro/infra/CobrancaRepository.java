package academy.wakanda.wakanda_ai.financeiro.infra;

import academy.wakanda.wakanda_ai.financeiro.domain.cobranca.Cobranca;
import academy.wakanda.wakanda_ai.financeiro.domain.cobranca.CobrancaEvento;

import java.util.List;
import java.util.UUID;

public interface CobrancaRepository {
    Cobranca buscaCobrancaPorIdPaymentAsaas(CobrancaEvento paymentAsaas);
    void salvaCobranca(Cobranca novaCobranca);
    List<Cobranca> buscaCobrancasVencidas(UUID idWakander);
}
