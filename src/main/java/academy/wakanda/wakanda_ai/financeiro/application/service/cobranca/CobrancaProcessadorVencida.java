package academy.wakanda.wakanda_ai.financeiro.application.service.cobranca;

import academy.wakanda.wakanda_ai.financeiro.domain.cobranca.Cobranca;
import academy.wakanda.wakanda_ai.financeiro.domain.cobranca.CobrancaEvento;
import academy.wakanda.wakanda_ai.financeiro.domain.cobranca.CobrancaEventoType;
import academy.wakanda.wakanda_ai.financeiro.infra.CobrancaRepository;
import academy.wakanda.wakanda_ai.wakander.application.service.WakanderRepository;
import academy.wakanda.wakanda_ai.wakander.application.service.WakanderService;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class CobrancaProcessadorVencida implements CobrancaProcessadorAsaas {
    private final CobrancaRepository cobrancaRepository;
    private final WakanderRepository wakanderRepository;
    private final WakanderService wakanderService;

    @Override
    public boolean validaSeProcessa(CobrancaEvento request) {
        return request.getEvent().equals(CobrancaEventoType.PAYMENT_OVERDUE);
    }

    @Override
    public Cobranca processa(CobrancaEvento request) {
        log.info("[start] CobrancaVencidaProcessador - processa");
        Cobranca cobrancaById = cobrancaRepository.buscaCobrancaPorIdPaymentAsaas(request);
        cobrancaById.atualizaStatusParaVencido();
        checaCobrancaEstaVinculadaAoWakander(cobrancaById);
        log.debug("[finish] CobrancaVencidaProcessador - processa");
        return cobrancaById;
    }

	private void checaCobrancaEstaVinculadaAoWakander(Cobranca cobrancaById) {
		if(cobrancaById.getIdWakander() != null) {
			List<Cobranca> cobrancasVencidas = cobrancaRepository.buscaCobrancasVencidas(cobrancaById.getIdWakander());
			validaSeCancelaWakander(cobrancasVencidas, cobrancaById);
		}
	}

    private void validaSeCancelaWakander(List<Cobranca> cobrancasVencidas, Cobranca cobrancaById) {
        if (!cobrancasVencidas.isEmpty()) {
            Wakander wakander = wakanderRepository.buscaWakanderPorId(cobrancaById.getIdWakander());
            wakanderService.solicitaCancelamentoWakander(wakander);
        }
    }
}
