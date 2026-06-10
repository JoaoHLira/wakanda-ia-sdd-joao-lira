package academy.wakanda.wakanda_ai.financeiro.infra;

import academy.wakanda.wakanda_ai.financeiro.application.api.FiadorDTO;
import academy.wakanda.wakanda_ai.financeiro.application.service.assinatura.AsaasResponse;
import academy.wakanda.wakanda_ai.financeiro.domain.assinatura.AssinaturaAsaasDto;
import academy.wakanda.wakanda_ai.financeiro.domain.assinatura.AssinaturaListaAsaasDto;
import academy.wakanda.wakanda_ai.financeiro.domain.assinatura.ClienteAsaasDto;
import academy.wakanda.wakanda_ai.handler.APIException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Log4j2
@Component
public class AsaasWebClient implements AsaasClient {
    private final WebClient webClient;

    @Value("${asaas.url}")
    private String asaasApiUrl;

    @Value("${asaas.token}")
    private String asaasToken;

    public AsaasWebClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public ClienteAsaasDto buscaCliente(String customer) {
        log.info("[start] AsaasWebClient - buscaCliente");
        String uri = asaasApiUrl + "/v3/customers/" + customer;
        try {
            ClienteAsaasDto clienteAsaasDto = webClient.get()
                    .uri(uri)
                    .header("access_token", asaasToken)
                    .retrieve()
                    .bodyToMono(ClienteAsaasDto.class)
                    .block();
            log.debug("[finish] AsaasWebClient - buscaCliente");
            return clienteAsaasDto;
        } catch (WebClientResponseException e) {
            log.error("[error] Erro inesperado ao buscar cliente: {}", e.getMessage());
            throw APIException.build(HttpStatus.valueOf(e.getStatusCode().value()), e.getMessage());
        } catch (Exception e) {
            log.error("[error] Erro inesperado ao buscar cliente: {}", e.getMessage());
            throw APIException.build(HttpStatus.INTERNAL_SERVER_ERROR, "Erro inesperado ao buscar cliente: ", e);
        }
    }

    @Override
    public AssinaturaAsaasDto buscaClientePorAssinatura(String subscription) {
        log.info("[start] AsaasWebClient - buscaClientePorAssinatura");
        String uri = asaasApiUrl + "/v3/subscriptions/" + subscription;
        try {
            AssinaturaAsaasDto clienteAsaasDto = webClient.get()
                    .uri(uri)
                    .header("access_token", asaasToken)
                    .retrieve()
                    .bodyToMono(AssinaturaAsaasDto.class)
                    .block();
            log.debug("[finish] AsaasWebClient - buscaClientePorAssinatura");
            return clienteAsaasDto;
        } catch (WebClientResponseException e) {
            log.error("[error] Erro inesperado ao buscar assinatura: {}", e.getMessage());
            throw APIException.build(HttpStatus.valueOf(e.getStatusCode().value()), e.getMessage());
        } catch (Exception e) {
            log.error("[error] Erro inesperado ao buscar assinatura: {}", e.getMessage());
            throw APIException.build(HttpStatus.INTERNAL_SERVER_ERROR, "Erro inesperado ao buscar assinatura: ", e);
        }
    }

    @Override
    public AssinaturaListaAsaasDto buscaAssinatura(String customer) {
        log.info("[start] AsaasWebClient - buscaAssinatura");
        String uri = asaasApiUrl + "/v3/subscriptions/?limit=100&customer=" + customer;
        try {
            AssinaturaListaAsaasDto listaAssinaturasPorCliente = webClient.get()
                    .uri(uri)
                    .header("access_token", asaasToken)
                    .retrieve()
                    .bodyToMono(AssinaturaListaAsaasDto.class)
                    .block();
            log.debug("[finish] AsaasWebClient - buscaAssinatura");
            return listaAssinaturasPorCliente;
        } catch (WebClientResponseException e) {
            log.error("[error] Erro inesperado ao buscar assinatura: {}", e.getMessage());
            throw APIException.build(HttpStatus.valueOf(e.getStatusCode().value()), e.getMessage());
        } catch (Exception e) {
            log.error("[error] Erro inesperado ao buscar assinatura: {}", e.getMessage());
            throw APIException.build(HttpStatus.INTERNAL_SERVER_ERROR, "Erro inesperado ao buscar assinatura: ", e);
        }
    }

    @Override
    public AsaasResponse atualizaDadosFiador(String idAsaas, FiadorDTO fiadorDTO) {
        log.info("[start] AsaasWebClient - atualizaDadosFiador");

        String uri = asaasApiUrl + "/v3/customers/" + idAsaas;
        try {
            AsaasResponse asaasResponse = webClient.put()
                    .uri(uri)
                    .header("access_token", asaasToken)
                    .bodyValue(fiadorDTO)
                    .retrieve()
                    .bodyToMono(AsaasResponse.class)
                    .block();
            log.debug("[finish] AsaasWebClient - atualizaDadosFiador");
            return asaasResponse;
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw APIException.build(HttpStatus.BAD_REQUEST, "Requisição inválida ao atualizar fiador.");
            }
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw APIException.build(HttpStatus.UNAUTHORIZED, "Falha de autenticação ao atualizar fiador.");
            }
            throw APIException.build(HttpStatus.INTERNAL_SERVER_ERROR, "Erro inesperado ao atualizar fiador.");
        }
    }
}
