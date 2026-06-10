package academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.application.service;

import academy.wakanda.wakanda_ai.comunicacao.infra.PublicadorNotificacaoSns;
import academy.wakanda.wakanda_ai.constants.TopicNames;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.application.service.JornadaWakandaRepository;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.domain.JornadaWakanda;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.repository.MissaoWakandaRepository;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.MissaoWakanda;
import academy.wakanda.wakanda_ai.gameficacao.common.dto.XpWakanderEventDTO;
import academy.wakanda.wakanda_ai.gameficacao.progresso.jornadaprogresso.application.service.JornadaProgressoService;
import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.application.api.MissaoConcluidaResponse;
import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.application.api.MissaoDisponibilidadeResponse;
import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.application.api.MissaoProgressoRequest;
import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.application.api.MissaoProgressoResponse;
import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.domain.MissaoProgresso;
import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.service.ProgressoGameficacaoRepository;
import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.service.ProgressoWakanderEventDto;
import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.domain.ProgressoWakander;
import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.domain.ClasseWakanda;
import academy.wakanda.wakanda_ai.handler.APIException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class MissaoProgressoApplicationService implements MissaoProgressoService {

    private final MissaoProgressoRepository missaoProgressoRepository;
    private final MissaoWakandaRepository missaoWakandaRepository;
    private final PublicadorNotificacaoSns publicadorNotificacaoSns;
    private final ProgressoGameficacaoRepository progressoGameficacaoRepository;
    private final JornadaProgressoService jornadaProgressoService;
    private final JornadaWakandaRepository jornadaWakandaRepository;
    private final MissaoDisponibilidadeService missaoDisponibilidadeService;
    private final TopicNames topicNames;

    @Override
    public void concluiMissao(ProgressoWakanderEventDto progressoWakanderEvent) {
        log.info("[start] MissaoProgressoApplicationService - concluiMissao");
        MissaoWakanda missaoAtual = missaoWakandaRepository.buscaMissaoPorIdMissaoExterna(progressoWakanderEvent.getIdMissaoExterna());
        ProgressoWakander progressoDoWakander = progressoGameficacaoRepository.buscaProgressoPorIdWakander(progressoWakanderEvent.getIdWakander());
        ClasseWakanda classeAtual = validaMissaoParaClasseAtual(missaoAtual, progressoDoWakander.getIdWakander());
        finalizaMissaoAtualEPublicaXp(buscaOuCriaMissaoProgresso(missaoAtual, progressoDoWakander), missaoAtual);
        processaProximosPassosDaJornada(missaoAtual, progressoDoWakander, classeAtual);
        log.debug("[finish] MissaoProgressoApplicationService - concluiMissao");
    }

    @Override
    public MissaoProgressoResponse criaProgressoDeMissao(MissaoProgressoRequest request) {
        log.info("[start] MissaoProgressoApplicationService - criaProgressoDeMissao");
        MissaoWakanda missao = missaoWakandaRepository.buscaMissaoPorId(request.getIdMissao());
        ProgressoWakander progressoDoWakander = progressoGameficacaoRepository.buscaProgressoPorIdWakander(request.getIdWakander());
        validaMissaoParaClasseAtual(missao, request.getIdWakander());
        MissaoProgresso missaoProgresso = MissaoProgresso.criarEmAndamento(missao.getIdMissao(), progressoDoWakander.getIdProgressoWakander());
        missaoProgressoRepository.salvaProgressoMissao(missaoProgresso);
        log.debug("[finish] MissaoProgressoApplicationService - criaProgressoDeMissao");
        return new MissaoProgressoResponse(missaoProgresso);
    }

    @Override
    public void concluiMissaoManualmente(UUID idMissaoProgresso) {
        log.info("[start] MissaoProgressoApplicationService - concluiMissaoManualmente");
        MissaoProgresso missaoProgresso = missaoProgressoRepository.buscaMissaoProgressoPorId(idMissaoProgresso);
        MissaoWakanda missaoWakanda = missaoWakandaRepository.buscaMissaoPorId(missaoProgresso.getIdMissaoWakanda());
        ProgressoWakander progressoDoWakander = progressoGameficacaoRepository.buscaProgressoPorId(missaoProgresso.getIdProgressoWakander());
        validaMissaoParaClasseAtual(missaoWakanda, progressoDoWakander.getIdWakander());
        finalizaMissaoAtualEPublicaXp(missaoProgresso, missaoWakanda);
        log.debug("[finish] MissaoProgressoApplicationService - concluiMissaoManualmente");
    }

    @Override
    public void reavaliaMissoesDisponiveis(UUID idWakander) {
        log.info("[start] MissaoProgressoApplicationService - reavaliaMissoesDisponiveis");
        ProgressoWakander progressoDoWakander = buscaProgressoDoWakander(idWakander);
        ClasseWakanda classeAtual = missaoDisponibilidadeService.buscaClasseAtual(idWakander);
        List<JornadaWakanda> jornadasAtivas = jornadaWakandaRepository.listaJornadas().stream().filter(jornada -> jornada.getStatusJornada().isAtiva()).toList();
        jornadasAtivas.forEach(jornada -> criaProgressoParaMissoesElegiveisDaJornada(jornada.getIdJornada(), progressoDoWakander, classeAtual));
        log.debug("[finish] MissaoProgressoApplicationService - reavaliaMissoesDisponiveis");
    }

    @Override
    public List<MissaoDisponibilidadeResponse> listaDisponibilidadeMissoes(UUID idWakander, UUID idJornada) {
        log.info("[start] MissaoProgressoApplicationService - listaDisponibilidadeMissoes");
        ProgressoWakander progressoDoWakander = buscaProgressoDoWakander(idWakander);
        ClasseWakanda classeAtual = missaoDisponibilidadeService.buscaClasseAtual(idWakander);
        List<MissaoWakanda> missoesDaJornada = buscaMissoesDaJornada(idJornada);
        Set<UUID> idsMissoesComProgresso = buscaIdsMissoesComProgresso(missoesDaJornada, progressoDoWakander);
        Map<UUID, ClasseWakanda> cacheClassesMinimas = new HashMap<>();
        List<MissaoDisponibilidadeResponse> disponibilidade = montaDisponibilidadeMissoes(missoesDaJornada, classeAtual, idsMissoesComProgresso, cacheClassesMinimas);
        log.debug("[finish] MissaoProgressoApplicationService - listaDisponibilidadeMissoes");
        return disponibilidade;
    }

    @Override
    public Page<MissaoConcluidaResponse> listaMissoesConcluidas(UUID idProgressoWakander, Pageable pageable) {
        log.info("[start] MissaoProgressoApplicationService - listaMissoesConcluidas");
        Pageable pageableSemSort = pageable.isPaged()
                ? PageRequest.of(pageable.getPageNumber(), pageable.getPageSize())
                : Pageable.unpaged();
        Page<MissaoConcluidaResponse> missoesConcluidas = missaoProgressoRepository
                .listaMissoesConcluidasPorProgresso(idProgressoWakander, pageableSemSort)
                .map(MissaoConcluidaResponse::new);
        log.debug("[finish] MissaoProgressoApplicationService - listaMissoesConcluidas");
        return missoesConcluidas;
    }

    private ProgressoWakander buscaProgressoDoWakander(UUID idWakander) {
        return progressoGameficacaoRepository.buscaProgressoPorIdWakander(idWakander);
    }

    private List<MissaoWakanda> buscaMissoesDaJornada(UUID idJornada) {
        return missaoWakandaRepository.buscaMissoesOrdenadasPorJornada(idJornada);
    }

    private Set<UUID> buscaIdsMissoesComProgresso(List<MissaoWakanda> missoesDaJornada, ProgressoWakander progressoDoWakander) {
        List<UUID> idsMissoes = missoesDaJornada.stream().map(MissaoWakanda::getIdMissao).toList();
        return missaoProgressoRepository
                .buscaMissoesProgressoPorIdsMissoesEProgresso(idsMissoes, progressoDoWakander.getIdProgressoWakander())
                .stream()
                .map(MissaoProgresso::getIdMissaoWakanda)
                .collect(Collectors.toSet());
    }

    private List<MissaoDisponibilidadeResponse> montaDisponibilidadeMissoes(List<MissaoWakanda> missoesDaJornada, ClasseWakanda classeAtual, Set<UUID> idsMissoesComProgresso, Map<UUID, ClasseWakanda> cacheClassesMinimas) {
        return missoesDaJornada.stream()
                .map(missao -> montaDisponibilidadeMissao(missao, classeAtual, idsMissoesComProgresso.contains(missao.getIdMissao()), cacheClassesMinimas))
                .toList();
    }

    private void publicaXpMissaoProgresso(XpWakanderEventDTO xpWakanderEventDTO) {
        log.info("[start] MissaoProgressoApplicationService - publicaXpMissaoProgresso");
        publicadorNotificacaoSns.enviaNotificacaoSns(UUID.randomUUID().toString(), xpWakanderEventDTO, topicNames.getXpWakanderRequests());
        log.debug("[finish] MissaoProgressoApplicationService - publicaXpMissaoProgresso");
    }

    private void finalizaMissaoAtualEPublicaXp(MissaoProgresso progressoMissao, MissaoWakanda missaoAtual) {
        log.info("[start] MissaoProgressoApplicationService - finalizaMissaoAtualEPublicaXp");
        progressoMissao.concluiMissao(missaoAtual.getXpBase(), missaoAtual.getSabedorias());
        missaoProgressoRepository.salvaProgressoMissao(progressoMissao);
        publicaXpMissaoProgresso(
                XpWakanderEventDTO.onMissaaoProgresso(progressoMissao, progressoMissao.getSabedorias().toCatalogo()));
        log.debug("[finish] MissaoProgressoApplicationService - finalizaMissaoAtualEPublicaXp");
    }

    private ClasseWakanda validaMissaoParaClasseAtual(MissaoWakanda missao, UUID idWakander) {
        ClasseWakanda classeAtual = missaoDisponibilidadeService.buscaClasseAtual(idWakander);
        missaoDisponibilidadeService.validaMissaoLiberadaParaClasseAtual(missao, classeAtual, new HashMap<>());
        return classeAtual;
    }

    private MissaoProgresso buscaOuCriaMissaoProgresso(MissaoWakanda missaoAtual, ProgressoWakander progressoDoWakander) {
        return missaoProgressoRepository.buscaOptionalMissaoProgresso(missaoAtual.getIdMissao(), progressoDoWakander.getIdProgressoWakander())
                .orElseGet(() -> MissaoProgresso.criarEmAndamento(missaoAtual.getIdMissao(), progressoDoWakander.getIdProgressoWakander()));
    }

    private void processaProximosPassosDaJornada(MissaoWakanda missaoAtual, ProgressoWakander progressoDoWakander, ClasseWakanda classeAtual) {
        processaProximaMissao(missaoAtual, progressoDoWakander, classeAtual, new HashMap<>());
        jornadaProgressoService.concluiJornadaSeMissoesConcluidas(missaoAtual.getIdJornada(), progressoDoWakander.getIdProgressoWakander());
    }

    private void processaProximaMissao(MissaoWakanda missaoAtual, ProgressoWakander progressoDoWakander, ClasseWakanda classeAtual, Map<UUID, ClasseWakanda> cacheClassesMinimas) {
        log.info("[start] MissaoProgressoApplicationService - processaProximaMissao");
        List<MissaoWakanda> missoesOrdenadas = buscaMissoesDaJornada(missaoAtual.getIdJornada());
        Optional<MissaoWakanda> proximaMissaoOpt = determinaProximaMissao(missaoAtual);
        validaSequenciaDaProximaMissao(missoesOrdenadas, missaoAtual, proximaMissaoOpt);
        proximaMissaoOpt.ifPresent(pm -> liberaProximaMissao(pm, progressoDoWakander, classeAtual, cacheClassesMinimas));
        log.debug("[finish] MissaoProgressoApplicationService - processaProximaMissao");

    }

    private Optional<MissaoWakanda> determinaProximaMissao(MissaoWakanda missaoAtual) {
        log.info("[start] MissaoProgressoApplicationService - determinaProximaMissao");
        int ordemProximaMissao = missaoAtual.getOrdemMissao().getOrdem() + 1;
        return missaoWakandaRepository.buscaProximaMissao(missaoAtual.getIdJornada(), ordemProximaMissao);
    }

    private void validaSequenciaDaProximaMissao(List<MissaoWakanda> missoesOrdenadas, MissaoWakanda missaoAtual, Optional<MissaoWakanda> proximaMissaoOpt) {
        log.info("[start] MissaoProgressoApplicationService - validaSequenciaDaProximaMissao");
        int ordemEsperada = missaoAtual.getOrdemMissao().getOrdem() + 1;
        boolean existeMissaoPosterior = missoesOrdenadas.stream()
                .anyMatch(m -> m.getOrdemMissao().getOrdem() > ordemEsperada);
        if (proximaMissaoOpt.isEmpty() && existeMissaoPosterior) {
            throw APIException.build(HttpStatus.CONFLICT, "Missão Fora de Sequência");
        }
    }

    private void liberaProximaMissao(MissaoWakanda proximaMissao, ProgressoWakander progressoDoWakander, ClasseWakanda classeAtual, Map<UUID, ClasseWakanda> cacheClassesMinimas) {
        log.info("[start] MissaoProgressoApplicationService - liberaProximaMissao");
        missaoDisponibilidadeService.validaMissaoLiberadaParaClasseAtual(proximaMissao, classeAtual,
                cacheClassesMinimas);
        criaProgressoMissaoSeNaoExiste(proximaMissao, progressoDoWakander);
        log.debug("[finish] MissaoProgressoApplicationService - liberaProximaMissao");
    }

    private void criaProgressoParaMissoesElegiveisDaJornada(UUID idJornada, ProgressoWakander progressoDoWakander, ClasseWakanda classeAtual) {
        List<MissaoWakanda> missoesDaJornada = buscaMissoesDaJornada(idJornada);
        Set<UUID> idsMissoesComProgresso = buscaIdsMissoesComProgresso(missoesDaJornada, progressoDoWakander);
        Map<UUID, ClasseWakanda> cacheClassesMinimas = new HashMap<>();
        missoesDaJornada.forEach(missao -> {
            boolean missaoLiberada = missaoDisponibilidadeService
                    .verificaSeMissaoLiberadaParaClasseAtual(missao, classeAtual, cacheClassesMinimas);
            if (missaoLiberada && !idsMissoesComProgresso.contains(missao.getIdMissao())) {
                criaNovoProgressoMissao(missao, progressoDoWakander);
            }
        });
    }

    private MissaoDisponibilidadeResponse montaDisponibilidadeMissao(MissaoWakanda missao, ClasseWakanda classeAtual, boolean possuiProgresso, Map<UUID, ClasseWakanda> cacheClassesMinimas) {
        return missaoDisponibilidadeService.montaDisponibilidadeMissao(missao, classeAtual, possuiProgresso,
                cacheClassesMinimas);
    }

    private void criaProgressoMissaoSeNaoExiste(MissaoWakanda missao, ProgressoWakander progressoDoWakander) {
        if (missaoProgressoRepository.existeMissaoProgresso(missao.getIdMissao(), progressoDoWakander.getIdProgressoWakander())) {
            return;
        }
        criaNovoProgressoMissao(missao, progressoDoWakander);
    }

    private void criaNovoProgressoMissao(MissaoWakanda missao, ProgressoWakander progressoDoWakander) {
        MissaoProgresso missaoProgresso = MissaoProgresso
                .criarEmAndamento(missao.getIdMissao(), progressoDoWakander.getIdProgressoWakander());
        missaoProgressoRepository.salvaProgressoMissao(missaoProgresso);
    }
}
