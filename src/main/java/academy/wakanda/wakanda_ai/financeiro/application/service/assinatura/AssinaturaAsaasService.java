package academy.wakanda.wakanda_ai.financeiro.application.service.assinatura;

import academy.wakanda.wakanda_ai.financeiro.application.api.AssinaturaAsaasDto;
import academy.wakanda.wakanda_ai.financeiro.application.api.FiadorDTO;

public interface AssinaturaAsaasService {
    void processaAssinaturaPorEvento(AssinaturaAsaasDto assinaturaEvento);
    void atualizaDadosFiador(String token, FiadorDTO fiadorDTO);
}
