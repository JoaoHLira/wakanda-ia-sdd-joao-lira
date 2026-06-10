package academy.wakanda.wakanda_ai.financeiro.infra;

import academy.wakanda.wakanda_ai.financeiro.application.api.FiadorDTO;
import academy.wakanda.wakanda_ai.financeiro.application.service.assinatura.AsaasResponse;
import academy.wakanda.wakanda_ai.financeiro.domain.assinatura.AssinaturaAsaasDto;
import academy.wakanda.wakanda_ai.financeiro.domain.assinatura.AssinaturaListaAsaasDto;
import academy.wakanda.wakanda_ai.financeiro.domain.assinatura.ClienteAsaasDto;

public interface AsaasClient {
    ClienteAsaasDto buscaCliente(String customer);
    AssinaturaAsaasDto buscaClientePorAssinatura(String subscription);
    AssinaturaListaAsaasDto buscaAssinatura(String customer);
    AsaasResponse atualizaDadosFiador(String idAsaas, FiadorDTO fiadorDTO);
}
