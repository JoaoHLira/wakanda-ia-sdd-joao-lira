package academy.wakanda.wakanda_ai.financeiro.application.service.cobranca;

import academy.wakanda.wakanda_ai.financeiro.application.api.CobrancaAsaasDto;
import academy.wakanda.wakanda_ai.financeiro.domain.cobranca.CobrancaEvento;
import academy.wakanda.wakanda_ai.financeiro.domain.cobranca.Cobranca;
import academy.wakanda.wakanda_ai.financeiro.infra.CobrancaRepository;
import academy.wakanda.wakanda_ai.handler.APIException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
@Log4j2
@Service
@RequiredArgsConstructor
public class CobrancaAsaasApplicationService implements CobrancaAsaasService {
    private final CobrancaRepository cobrancaRepository;
    private final List<CobrancaProcessadorAsaas> processadoresCobrancaAssas;

    @Override
    public void processaEvento(CobrancaAsaasDto request) {
        log.info("[start] CobrancaAsaasApplicationService - processaEvento");
        CobrancaEvento evento = new CobrancaEvento(request);
        CobrancaProcessadorAsaas cobrancaProcessador = strategyCobrancaAsaasProcessador(evento);
        Cobranca cobranca = cobrancaProcessador.processa(evento);
        cobrancaRepository.salvaCobranca(cobranca);
        log.debug("[finish] CobrancaAsaasApplicationService - processaEvento");
    }

    private CobrancaProcessadorAsaas strategyCobrancaAsaasProcessador(CobrancaEvento request) {
        return processadoresCobrancaAssas.stream()
                .filter(p -> p.validaSeProcessa(request))
                .findFirst()
                .orElseThrow(() -> APIException.build(HttpStatus.BAD_REQUEST, "Tipo de evento não aceito!"));
    }
}
