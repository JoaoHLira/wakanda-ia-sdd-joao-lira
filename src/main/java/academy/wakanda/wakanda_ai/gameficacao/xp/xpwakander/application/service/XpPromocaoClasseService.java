package academy.wakanda.wakanda_ai.gameficacao.xp.xpwakander.application.service;

import academy.wakanda.wakanda_ai.gameficacao.common.dto.XpPromocaoClasseDTO;
import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.domain.MissaoProgresso;
import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.domain.MissaoProgressoStatus;
import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.domain.ClasseWakanda;
import academy.wakanda.wakanda_ai.gameficacao.xp.historicoclasse.application.repository.HistoricoClasseWakanderRepository;
import academy.wakanda.wakanda_ai.gameficacao.xp.historicoclasse.domain.HistoricoClasseWakander;
import academy.wakanda.wakanda_ai.gameficacao.xp.xpwakander.domain.XpWakander;
import academy.wakanda.wakanda_ai.handler.APIException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor
public class XpPromocaoClasseService {

    private final HistoricoClasseWakanderRepository historicoClasseWakanderRepository;

    public boolean validaPromocaoClasse(XpPromocaoClasseDTO xpPromocaoClasseDTO, ClasseWakanda classeAtual,
                                      List<MissaoProgresso> missoesProgresso, ClasseWakanda proximaClasse, XpWakander xpWakander) {
        log.info("[start] XpPromocaoClasseService - validaPromocaoClasse");
        boolean isElegivel = validaSeMissoesClasseAtualConcluidas(classeAtual, missoesProgresso) &&
                validaSeNivelNecessarioProximaClasseAtingido(proximaClasse, xpPromocaoClasseDTO.getNivelAtual()) &&
                validaXpSabedoriasProximaClasseAtingido(xpWakander, proximaClasse);
        log.debug("[finish] XpPromocaoClasseService - validaPromocaoClasse");
        return isElegivel;
    }

    public void concluiClasseAtualAndIniciaProximaClasse(XpPromocaoClasseDTO xpPromocaoClasseDTO,
                                                          HistoricoClasseWakander historicoClasseAtual,
                                                          ClasseWakanda proximaClasse) {
        log.info("[start] XpPromocaoClasseService - concluiClasseAtualAndIniciaProximaClasse");
        historicoClasseAtual.concluiClasseAtual();
        historicoClasseWakanderRepository.salvaHistoricoClasseWakander(historicoClasseAtual);
        HistoricoClasseWakander historicoProximaClasse = new HistoricoClasseWakander(proximaClasse, xpPromocaoClasseDTO);
        historicoClasseWakanderRepository.salvaHistoricoClasseWakander(historicoProximaClasse);
        log.debug("[finish] XpPromocaoClasseService - concluiClasseAtualAndIniciaProximaClasse");
    }

    private boolean validaXpSabedoriasProximaClasseAtingido(XpWakander xpWakander, ClasseWakanda proximaClasse) {
        log.info("[start] XpPromocaoClasseService - validaXpSabedoriasProximaClasseAtingido");
        if (!xpWakander.validaXpSabedoriasProximaClasseAtingido(proximaClasse)) {
            log.info("[info] As sabedorias necessárias para a próxima classe não foram atingidas!");
            return false;
        }
        log.debug("[finish] XpPromocaoClasseService - validaXpSabedoriasProximaClasseAtingido");
        return true;
    }

    private boolean validaSeNivelNecessarioProximaClasseAtingido(ClasseWakanda proximaClasse, int nivelAtual) {
        log.info("[start] XpPromocaoClasseService - validaSeNivelNecessarioProximaClasseAtingido");
        if (proximaClasse.getNivelNecessario() > nivelAtual) {
            log.info("[info] O nível necessário para a próxima classe não foi atingido!");
            return false;
        }
        log.debug("[finish] XpPromocaoClasseService - validaSeNivelNecessarioProximaClasseAtingido");
        return true;
    }

    private boolean validaSeMissoesClasseAtualConcluidas(ClasseWakanda classeAtual, List<MissaoProgresso> missoesProgresso) {
        log.info("[start] XpPromocaoClasseService - validaSeMissoesClasseAtualConcluidas");
        if (!validaSeTodasMissoesCLasseAtualConcluidas(classeAtual.getMissoesNecessarias(), missoesProgresso)) {
            log.info("[info] As missões necessárias para a próxima classe não foram concluídas!");
            return false;
        }
        log.debug("[finish] XpPromocaoClasseService - validaSeMissoesClasseAtualConcluidas");
        return true;
    }

    private static boolean validaSeTodasMissoesCLasseAtualConcluidas(List<UUID> idsMissoes, List<MissaoProgresso> missoesProgresso) {
        return idsMissoes.stream()
                .allMatch(idMissao -> missoesProgresso.stream()
                        .anyMatch(p -> p.getIdMissaoWakanda().equals(idMissao)
                                && p.getStatusProgresso() == MissaoProgressoStatus.CONCLUIDA));
    }

}
