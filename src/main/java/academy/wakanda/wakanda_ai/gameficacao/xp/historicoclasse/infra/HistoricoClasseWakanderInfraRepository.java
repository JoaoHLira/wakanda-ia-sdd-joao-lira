package academy.wakanda.wakanda_ai.gameficacao.xp.historicoclasse.infra;

import academy.wakanda.wakanda_ai.gameficacao.common.dto.XpPromocaoClasseDTO;
import academy.wakanda.wakanda_ai.gameficacao.xp.historicoclasse.application.repository.HistoricoClasseWakanderRepository;
import academy.wakanda.wakanda_ai.gameficacao.xp.historicoclasse.domain.HistoricoClasseWakander;
import academy.wakanda.wakanda_ai.gameficacao.xp.historicoclasse.domain.HistoricoClasseWakanderStatus;
import academy.wakanda.wakanda_ai.handler.APIException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@Log4j2
@RequiredArgsConstructor
public class HistoricoClasseWakanderInfraRepository implements HistoricoClasseWakanderRepository {

    private final HistoricoClasseWakanderSpringDataRepository historicoClasseWakanderSpringDataRepository;

    @Override
    public void salvaHistoricoClasseWakander(HistoricoClasseWakander historicoClasse) {
        log.info("[start] HistoricoClasseWakanderInfraRepository - salvaHistoricoClasseWakander");
        historicoClasseWakanderSpringDataRepository.save(historicoClasse);
        log.debug("[finish] HistoricoClasseWakanderInfraRepository - salvaHistoricoClasseWakander");
    }

    @Override
    public HistoricoClasseWakander buscaHistoricoClasseAtual(XpPromocaoClasseDTO xpPromocaoClasseDTO) {
        log.info("[start] HistoricoClasseWakanderInfraRepository - buscaHistoricoClasseAtual");
        HistoricoClasseWakander historicoClasseAtual = historicoClasseWakanderSpringDataRepository
                .findAllByIdXpWakanderAndStatus(xpPromocaoClasseDTO.getIdXpWakander(), HistoricoClasseWakanderStatus.EM_ANDAMENTO)
                .orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Histórico de classe do Wakander com status EM_ANDAMENTO não encontrado!"));
        log.debug("[finish] HistoricoClasseWakanderInfraRepository - buscaHistoricoClasseAtual");
        return historicoClasseAtual;
    }

    @Override
    public HistoricoClasseWakander buscaClasseAtualPorWakander(UUID idWakander) {
        log.info("[start] HistoricoClasseWakanderInfraRepository - buscaClasseAtualPorWakander");
        HistoricoClasseWakander historicoClasseAtual = historicoClasseWakanderSpringDataRepository
                .findByIdWakanderAndStatus(idWakander, HistoricoClasseWakanderStatus.EM_ANDAMENTO)
                .orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND,
                        "Classe atual do Wakander não encontrada!"));
        log.debug("[finish] HistoricoClasseWakanderInfraRepository - buscaClasseAtualPorWakander");
        return historicoClasseAtual;
    }

    @Override
    public Optional<HistoricoClasseWakander> buscaOptionalHistoricoClasseAtual(XpPromocaoClasseDTO xpPromocaoClasseDTO) {
        log.info("[start] HistoricoClasseWakanderInfraRepository - buscaOptionalHistoricoClasseAtual");
        Optional<HistoricoClasseWakander> historico = historicoClasseWakanderSpringDataRepository
                .findAllByIdXpWakanderAndStatus(xpPromocaoClasseDTO.getIdXpWakander(), HistoricoClasseWakanderStatus.EM_ANDAMENTO);
        log.debug("[finish] HistoricoClasseWakanderInfraRepository - buscaOptionalHistoricoClasseAtual");
        return historico;
    }

    @Override
    public Optional<HistoricoClasseWakander> buscaOptionalClasseAtualPorWakander(UUID idWakander) {
        log.info("[start] HistoricoClasseWakanderInfraRepository - buscaOptionalClasseAtualPorWakander");
        Optional<HistoricoClasseWakander> historico = historicoClasseWakanderSpringDataRepository
                .findByIdWakanderAndStatus(idWakander, HistoricoClasseWakanderStatus.EM_ANDAMENTO);
        log.debug("[finish] HistoricoClasseWakanderInfraRepository - buscaOptionalClasseAtualPorWakander");
        return historico;
    }

}
