package academy.wakanda.wakanda_ai.financeiro.application.service.assinatura;

import academy.wakanda.wakanda_ai.financeiro.domain.assinatura.AssinaturaEvento;

public interface AssinaturaProcessorAsaas {
    boolean validaSeEventoProcessa(AssinaturaEvento assinaturaEvento);
    void processaEvento(AssinaturaEvento assinaturaEvento);
}
