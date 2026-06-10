package academy.wakanda.wakanda_ai.gameficacao.xp.xpwakander.application.service;

import academy.wakanda.wakanda_ai.gameficacao.common.dto.XpPromocaoClasseDTO;
import academy.wakanda.wakanda_ai.gameficacao.common.dto.XpWakanderEventDTO;
import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.application.service.MissaoProgressoRepository;
import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.application.service.MissaoProgressoService;
import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.domain.MissaoProgresso;
import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.service.ProgressoGameficacaoRepository;
import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.domain.ProgressoWakander;
import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.application.repository.ClasseWakandaRepository;
import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.domain.ClasseWakanda;
import academy.wakanda.wakanda_ai.gameficacao.xp.historicoclasse.application.repository.HistoricoClasseWakanderRepository;
import academy.wakanda.wakanda_ai.gameficacao.xp.historicoclasse.domain.HistoricoClasseWakander;
import academy.wakanda.wakanda_ai.gameficacao.xp.xpwakander.application.event.XpPromocaoClasseEvent;
import academy.wakanda.wakanda_ai.gameficacao.xp.xpwakander.domain.XpWakander;
import academy.wakanda.wakanda_ai.handler.APIException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class XpWakanderApplicationService implements XpWakanderService {

    private final XpWakanderRepository xpWakanderRepository;
    private final ProgressoGameficacaoRepository progressoGameficacaoRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final MissaoProgressoRepository missaoProgressoRepository;
    private final HistoricoClasseWakanderRepository historicoClasseWakanderRepository;
    private final ClasseWakandaRepository classeWakandaRepository;
    private final XpPromocaoClasseService xpPromocaoClasseService;
    private final MissaoProgressoService missaoProgressoService;

    @Override
    public void processaXP(XpWakanderEventDTO xpWakander) {
        log.info("[start] XpWakanderApplicationService - processaXP");
        ProgressoWakander progressoWakander = (progressoGameficacaoRepository)
                .buscaProgressoPorId(xpWakander.getIdProgressoWakander());
        XpWakander wakander = obterOuCriarXpWakander(progressoWakander);
        wakander.adicionarXpEAtualizarNivel(xpWakander.getXpObtido(), xpWakander.getSabedorias());
        xpWakanderRepository.salva(wakander);
        publicaEventoPromocaoClasse(wakander);
        log.debug("[finish] XpWakanderApplicationService - processaXP");
    }

    private XpWakander obterOuCriarXpWakander(ProgressoWakander progressoWakander) {
        try {
            return xpWakanderRepository
                    .buscaPorIdProgressoWakander(progressoWakander.getIdProgressoWakander());
        } catch (APIException e) {
            return XpWakander.novoComDefaults(progressoWakander.getIdProgressoWakander());
        }
    }

    private void publicaEventoPromocaoClasse(XpWakander xpWakander) {
        log.info("[start] XpWakanderApplicationService - publicaEventoPromocaoClasse");
        ProgressoWakander progressoWakander = progressoGameficacaoRepository.buscaProgressoPorId(xpWakander.getIdProgressoWakander());
        XpPromocaoClasseEvent evento = new XpPromocaoClasseEvent(xpWakander, progressoWakander);
        log.debug("[sending] Evento com valor {}", evento);
        eventPublisher.publishEvent(evento);
        log.debug("[finish] XpWakanderApplicationService - publicaEventoPromocaoClasse");
    }

    @Override
    public void processaPromocaoClasse(XpPromocaoClasseDTO xpPromocaoClasseDTO) {
        log.info("[start] XpWakanderApplicationService - processaPromocaoClasse");
        HistoricoClasseWakander historicoAtual = historicoClasseWakanderRepository
                .buscaOptionalHistoricoClasseAtual(xpPromocaoClasseDTO)
                .orElseGet(() -> criaHistoricoInicial(xpPromocaoClasseDTO));

        ClasseWakanda classeAtual = classeWakandaRepository.buscaClassePorId(historicoAtual.getIdClasse());
        classeWakandaRepository.buscaOptionalProximaClassePorOrdem(classeAtual.getOrdemClasse())
                .ifPresentOrElse(proximaClasse -> validaEPromoveClasse(xpPromocaoClasseDTO, historicoAtual, classeAtual, proximaClasse),
                        () -> log.info("[info] Usuário já está na última classe, encerrando promoção."));
        log.debug("[finish] XpWakanderApplicationService - processaPromocaoClasse");
    }

    private HistoricoClasseWakander criaHistoricoInicial(XpPromocaoClasseDTO dto) {
        log.info("[info] Criando histórico inicial da classe 1 para o Wakander durante promoção");
        ClasseWakanda classeInicial = classeWakandaRepository.buscaOptionalProximaClassePorOrdem(0)
                .orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Classe 1 não encontrada"));
        HistoricoClasseWakander historicoAtual = new HistoricoClasseWakander(classeInicial, dto);
        historicoClasseWakanderRepository.salvaHistoricoClasseWakander(historicoAtual);
        return historicoAtual;
    }

    private void validaEPromoveClasse(XpPromocaoClasseDTO dto, HistoricoClasseWakander historicoAtual, ClasseWakanda classeAtual, ClasseWakanda proximaClasse) {
        List<MissaoProgresso> missoesProgresso = buscaMissoesProgresso(classeAtual, dto.getIdProgressoWakander());
        XpWakander xpWakander = xpWakanderRepository.buscaPorIdProgressoWakander(dto.getIdProgressoWakander());
        if (!xpPromocaoClasseService.validaPromocaoClasse(dto, classeAtual, missoesProgresso, proximaClasse, xpWakander)) {
            return;
        }
        xpPromocaoClasseService.concluiClasseAtualAndIniciaProximaClasse(dto, historicoAtual, proximaClasse);
        xpWakanderRepository.salva(xpWakander);
        missaoProgressoService.reavaliaMissoesDisponiveis(dto.getIdWakander());
    }

    private List<MissaoProgresso> buscaMissoesProgresso(ClasseWakanda classeAtual, UUID idProgressoWakander) {
        return classeAtual.getMissoesNecessarias().isEmpty() ? List.of() :
                missaoProgressoRepository.buscaMissoesProgressoPorIdsMissoesEProgresso(
                        classeAtual.getMissoesNecessarias(), idProgressoWakander);
    }

}
