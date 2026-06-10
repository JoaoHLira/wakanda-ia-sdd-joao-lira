package academy.wakanda.wakanda_ai.financeiro.application.service.assinatura;

import academy.wakanda.wakanda_ai.autenticacao.application.service.AutenticacaoService;
import academy.wakanda.wakanda_ai.financeiro.application.api.AssinaturaAsaasDto;
import academy.wakanda.wakanda_ai.financeiro.application.api.FiadorDTO;
import academy.wakanda.wakanda_ai.financeiro.domain.assinatura.AssinaturaEvento;
import academy.wakanda.wakanda_ai.financeiro.infra.AsaasClient;
import academy.wakanda.wakanda_ai.handler.APIException;
import academy.wakanda.wakanda_ai.wakander.application.service.WakanderService;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class AssinaturaAsaasApplicationService implements AssinaturaAsaasService {

    private final List<AssinaturaProcessorAsaas> assinaturaProcessorAsaas;
    private final WakanderService wakanderService;
    private final AsaasClient asaasClient;
    private final AutenticacaoService autenticacaoService;


    @Override
    public void processaAssinaturaPorEvento(AssinaturaAsaasDto evento) {
        log.info("[start] AssinaturaAsaasApplicationService - processaAssinaturaPorEvento");
        AssinaturaEvento assinaturaEvento = new AssinaturaEvento(evento);
        AssinaturaProcessorAsaas assinaturaProcessor = strategyAssinaturaAsaasProcessor(assinaturaEvento);
        assinaturaProcessor.processaEvento(assinaturaEvento);
        log.debug("[finish] AssinaturaAsaasApplicationService - processaAssinaturaPorEvento");
    }

    private AssinaturaProcessorAsaas strategyAssinaturaAsaasProcessor(AssinaturaEvento assinaturaEvento) {
        return assinaturaProcessorAsaas.stream()
                .filter(a -> a.validaSeEventoProcessa(assinaturaEvento))
                .findFirst()
                .orElseThrow(() -> APIException.build(HttpStatus.BAD_REQUEST, "O evento não corresponde a nenhuma estrategia!"));
    }

    @Override
    public void atualizaDadosFiador(String token, FiadorDTO fiadorDTO) {
        log.info("[start] AssinaturaAsaasApplicationService - atualizaDadosFiador");
        Wakander wakander = autenticacaoService.buscaWakanderPeloToken(token);
        AsaasResponse asaasResponse = asaasClient.atualizaDadosFiador(wakander.getFiador().getIdAsaas(), fiadorDTO);
        if (asaasResponse != null) {
            wakanderService.atualizaFiador(token, fiadorDTO);
            log.debug("[finish] AssinaturaAsaasApplicationService - atualizaDadosFiador");
        }
    }
}
