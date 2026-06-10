package academy.wakanda.wakanda_ai.financeiro.datahelper;

import academy.wakanda.wakanda_ai.financeiro.application.api.FiadorDTO;
import academy.wakanda.wakanda_ai.financeiro.application.service.assinatura.AsaasResponse;
import academy.wakanda.wakanda_ai.wakander.application.event.AssinaturaCanceladaEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class FinanceiroDataHelper {

    public static AssinaturaCanceladaEvent criaAssinaturaCanceladaEvent() {
        return new AssinaturaCanceladaEvent(UUID.randomUUID(), "Teste Silva", "123456", "5573000000000",
                "teste@gmail.com", "Sem tempo", LocalDateTime.now());
    }

    public static FiadorDTO criaFiadorDTO() {
        return FiadorDTO.builder()
                .nome("Fiador Teste")
                .cpf("12345678901")
                .email("teste@teste")
                .telefoneFixo("5573000000000")
                .whatsapp("5573999999999")
                .endereco("Rua Teste")
                .numeroEndereco("123")
                .complemento("Apto 101")
                .bairro("Bairro Teste")
                .cep("12345000")
                .idWakander("Ref123")
                .notificacaoCobranca(false)
                .build();
    }

    public static AsaasResponse criaAsaasResponseFiador() {
    return AsaasResponse.builder()
            .id("fiador_asaas_123")
            .name("Fiador Teste")
            .url("http://asaas.com/fiador_asaas_123")
            .email("teste@teste")
            .enabled(true)
            .interrupted(false)
            .apiVersion(1)
            .hasAuthToken(true)
            .sendType("EMAIL")
            .events(List.of("CUSTOMER_CREATED", "CUSTOMER_UPDATED"))
            .build();
    }
}