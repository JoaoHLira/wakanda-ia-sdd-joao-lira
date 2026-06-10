package academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.service;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.application.api.JornadaResponse;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.application.service.JornadaWakandaService;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.api.*;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.repository.MissaoWakandaRepository;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.MissaoStatus;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.MissaoWakanda;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.OrdemMissao;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.TipoRecalculo;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.tipomissao.application.service.TipoMissaoService;
import academy.wakanda.wakanda_ai.handler.APIException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingInt;

@Service
@Log4j2
@RequiredArgsConstructor
public class MissaoWakandaApplicationService implements MissaoWakandaService {

    private final MissaoWakandaRepository missaoRepository;
    private final JornadaWakandaService jornadaService;
    private final TipoMissaoService tipoMissaoService;
    private final MissaoWebhookService missaoWebhookService;
    private final Map<UUID, List<MissaoWakanda>> cacheMissoesPorJornada = new HashMap<>();

    @Override
    public void desativaMissao(UUID idMissao) {
        log.info("[start] MissaoWakandaApplicationService - desativaMissao");
        log.debug("[idMissao] {}", idMissao);
        MissaoWakanda missao = missaoRepository.buscaMissaoPorId(idMissao);
        missao.desativaMissao();
        missaoRepository.salvaMissao(missao);
        moveMissoesSeHouverInativasParaFinal(missao.getIdJornada());
        recalculaXpJornada(missao);
        log.debug("[finish] MissaoWakandaApplicationService - desativaMissao");
    }

    private void recalculaXpJornada(MissaoWakanda missao) {
        log.debug("[start] MissaoWakandaApplicationService - recalculaXpJornada");
        jornadaService.removerXpJornada(missao.getIdJornada(), missao.getXpBase());
        log.debug("[finish] MissaoWakandaApplicationService - recalculaXpJornada");
    }

    @Override
    public MissaoWakandaResponse criaMissao(MissaoWakandaRequest missaoRequest) {
        log.info("[start] MissaoWakandaApplicationService - criaMissao");
        int quantidadeMissoesAtivas = missaoRepository.contarMissoesAtivasPorJornada(missaoRequest.getIdJornada());
        tipoMissaoService.buscaTipoMissaoId(missaoRequest.getIdTipoMissao());
        validaMissao(missaoRequest);
        MissaoWakanda missaoPai = buscaMissaoWakandaPai(missaoRequest);
        MissaoWakanda novaMissao = new MissaoWakanda(missaoRequest, quantidadeMissoesAtivas, missaoPai);
        missaoRepository.salvaMissao(novaMissao);
        recalculaXpTotalJornada(novaMissao);
        moveMissoesSeHouverInativasParaFinal(missaoRequest.getIdJornada());
        enviaWebhookN8N(novaMissao);
        log.debug("[finish] MissaoWakandaApplicationService - criaMissao");
        return new MissaoWakandaResponse(novaMissao.getIdMissao());
    }

    private void enviaWebhookN8N(MissaoWakanda novaMissao) {
        log.info("[start] MissaoWakandaApplicationService - enviaWebhookN8N");
        MissaoWebhookDTO missaoWebhookDTO = new MissaoWebhookDTO(novaMissao);
        missaoWebhookService.enviaWebhookProcessaMissao(missaoWebhookDTO);
        log.debug("[finish] MissaoWakandaApplicationService - enviaWebhookN8N");
    }

    @Nullable
    private MissaoWakanda buscaMissaoWakandaPai(MissaoWakandaRequest missaoRequest) {
        return Optional.ofNullable(missaoRequest.getIdMissaoPai()).flatMap(missaoRepository::buscaMissaoPai)
                .orElse(null);
    }

    private void recalculaXpTotalJornada(MissaoWakanda novaMissao) {
        log.debug("[start] MissaoWakandaApplicationService - recalculaXpTotalJornada");
        jornadaService.recalculaXpJornada(novaMissao.getIdJornada(), TipoRecalculo.MISSAO_ADICIONADA, 0,
                novaMissao.getXpBase());
        log.debug("[finish] MissaoWakandaApplicationService - recalculaXpTotalJornada");
    }

    private void validaMissao(MissaoWakandaRequest missaoRequest) {
        validaJornada(missaoRequest);
        validaTitulo(missaoRequest);
    }

    private void validaTitulo(MissaoWakandaRequest missaoRequest) {
        if (missaoRepository.validaTitulo(missaoRequest.getTitulo())) {
            throw APIException.build(HttpStatus.CONFLICT, "Já existe uma missão com esse título!");
        }
    }

    private void validaJornada(MissaoWakandaRequest missaoRequest) {
        JornadaResponse jornada = jornadaService.buscaJornada(missaoRequest.getIdJornada());
        if (jornada.getStatusJornada() == null || !jornada.getStatusJornada().isAtiva()) {
            throw APIException.build(HttpStatus.BAD_REQUEST, "Jornada não está ativa!");
        }
    }

    @Override
    public MissaoWakandaDetalhadoResponse buscaMissaoPorId(UUID idMissao) {
        log.info("[start] MissaoWakandaApplicationService - buscaMissaoPorId");
        log.info("[idMissao]: {}", idMissao);
        MissaoWakanda missao = missaoRepository.buscaMissaoPorId(idMissao);
        log.debug("[finish] MissaoWakandaApplicationService - buscaMissaoPorId");
        return new MissaoWakandaDetalhadoResponse(missao);
    }

    @Override
    public void alteraXpBase(UUID idMissao, MissaoAlteracaoXpBaseRequest missaoAlteracaoXpBase) {
        log.info("[start] MissaoWakandaApplicationService - alteraXpBase");
        MissaoWakanda missao = missaoRepository.buscaMissaoPorId(idMissao);
        int xpAntigoMissao = missao.getXpBase();
        missao.alteraXpBase(missaoAlteracaoXpBase);
        missaoRepository.salvaMissao(missao);
        jornadaService.recalculaXpJornada(missao.getIdJornada(), TipoRecalculo.XP_ALTERADO, xpAntigoMissao,
                missao.getXpBase());
        log.info("Alteração de XPBase: Missão {}, de {} para {}, Data/Hora: {}", missao.getIdMissao(), xpAntigoMissao,
                missao.getXpBase(), LocalDateTime.now());
        log.debug("[finish] MissaoWakandaApplicationService - alteraXpBase");
    }

    @Override
    public void alteraPosicaoMissao(UUID idMissao, Integer posicaoRequest) {
        log.info("[start] MissaoWakandaApplicationService - alteraPosicaoMissao");
        MissaoWakanda missao = missaoRepository.buscaMissaoPorId(idMissao);
        List<MissaoWakanda> missoesOrdenadas = missaoRepository.buscarMissoesAtivasOrdenadas(missao.getIdJornada());
        Map<UUID, Integer> ordensOriginais = missoesOrdenadas.stream()
                .collect(Collectors.toMap(MissaoWakanda::getIdMissao, m -> m.getOrdemMissao().getOrdem()));
        mudaPosicao(missao, posicaoRequest, missoesOrdenadas);
        salvaSomenteMissoesAlteradas(missoesOrdenadas, ordensOriginais);
        log.debug("[finish] MissaoWakandaApplicationService - alteraPosicaoMissao");
    }

    private void mudaPosicao(MissaoWakanda missao, Integer novaPosicao, List<MissaoWakanda> missoesList) {
        log.info("[start] mudaPosicao");
        int posicaoAtual = missao.getOrdemMissao().getOrdem();
        int totalMissoes = missoesList.size();
        OrdemMissao.validarNovaPosicao(novaPosicao, totalMissoes, posicaoAtual, missao.getMissaoStatus());
        OrdemMissao.ajustarPosicoesIntermediarias(missoesList, posicaoAtual, novaPosicao);
        missao.alterarOrdem(OrdemMissao.criar(novaPosicao));
        missoesList.sort(comparingInt(m -> m.getOrdemMissao().getOrdem()));
        log.debug("[finish] mudaPosicao");
    }

    private void moveMissoesSeHouverInativasParaFinal(UUID idJornada) {
        log.info("[start] MissaoWakandaApplicationService - moveMissoesSeHouverInativasParaFinal");
        List<MissaoWakanda> todasMissoes = missaoRepository.buscarTodasMissoesOrdenadas(idJornada);
        boolean haMissoesInativas = todasMissoes.stream().anyMatch(m -> m.getMissaoStatus().equals(MissaoStatus.INATIVA));
        if (haMissoesInativas) reordenaMissoesInativas(todasMissoes);
        log.debug("[finish] MissaoWakandaApplicationService - moveMissoesSeHouverInativasParaFinal");
    }

    private void reordenaMissoesInativas(List<MissaoWakanda> todasMissoes) {
        log.info("[start] MissaoWakandaApplicationService - reordenaMissoesInativas");
        Map<UUID, Integer> ordensOriginais = todasMissoes.stream()
                .collect(Collectors.toMap(MissaoWakanda::getIdMissao, m -> m.getOrdemMissao().getOrdem()));
        List<MissaoWakanda> missoesReordenadas = criarListaReordenada(todasMissoes);
        salvaSomenteMissoesAlteradas(missoesReordenadas, ordensOriginais);
        log.debug("[finish] MissaoWakandaApplicationService - reordenaMissoesInativas");
    }

    private List<MissaoWakanda> criarListaReordenada(List<MissaoWakanda> todasMissoes) {
        return OrdemMissao.reorganizarMissoesComInativasNoFinal(todasMissoes);
    }

    private List<MissaoWakanda> filtrarMissoesComOrdemAlterada(List<MissaoWakanda> missoes,
                                                                Map<UUID, Integer> ordensOriginais) {
        return missoes.stream()
                .filter(m -> ordensOriginais.getOrDefault(m.getIdMissao(), Integer.MIN_VALUE)
                        != m.getOrdemMissao().getOrdem())
                .collect(Collectors.toList());
    }

    private void salvaSomenteMissoesAlteradas(List<MissaoWakanda> missoes,
                                              Map<UUID, Integer> ordensOriginais) {
        List<MissaoWakanda> alteradas = filtrarMissoesComOrdemAlterada(missoes, ordensOriginais);
        if (alteradas.isEmpty()) {
            return;
        }
        missaoRepository.salvaMissoes(alteradas);
    }

    @Override
    public Page<MissaoWakandaDetalhadoResponse> buscaMissoesPaginadas(Pageable pageable, UUID idJornada,
                                                                      MissaoStatus missaoStatus) {
        log.info("[start] MissaoWakandaApplicationService - buscaMissoes");
        Page<MissaoWakanda> missoesPaginadas = missaoRepository.buscaMissoesPaginadas(pageable, idJornada,
                missaoStatus);
        Page<MissaoWakandaDetalhadoResponse> missoesDetalhadasPage = missoesPaginadas
                .map(MissaoWakandaDetalhadoResponse::new);
        log.debug("[finish] MissaoWakandaApplicationService - buscaMissoes");
        return missoesDetalhadasPage;
    }

    @Override
    public MissaoWakanda adicionaMissaoAntesDasInativasESalva(MissaoWakanda novaMissao, boolean emLote) {
        log.info("[start] MissaoWakandaApplicationService - adicionaMissaoAntesDasInativasESalva");
        UUID jornadaId = novaMissao.getIdJornada();
        List<MissaoWakanda> missoesAtuais = cacheMissoesPorJornada.computeIfAbsent(jornadaId,
                missaoRepository::buscaMissoesPorIdJornada);

        int novaOrdem = calculaOrdemParaInsercao(missoesAtuais);
        novaMissao.alterarOrdem(OrdemMissao.criar(novaOrdem));
        MissaoWakanda salvaMissao = missaoRepository.salvaMissao(novaMissao);
        missoesAtuais.add(salvaMissao);
        validaTipoDeEntrada(emLote, jornadaId);
        log.debug("[finish] MissaoWakandaApplicationService - adicionaMissaoAntesDasInativasESalva");
        return salvaMissao;
    }

    private void validaTipoDeEntrada(boolean emLote, UUID jornadaId) {
        if (!emLote) {
            cacheMissoesPorJornada.remove(jornadaId);
        }
    }

    private int calculaOrdemParaInsercao(List<MissaoWakanda> missoesAtuais) {
        OptionalInt minInativa = missoesAtuais.stream().filter(m -> m.getMissaoStatus().equals(MissaoStatus.INATIVA))
                .mapToInt(m -> m.getOrdemMissao().getOrdem()).min();

        if (minInativa.isPresent()) {
            int minimo = minInativa.getAsInt();
            List<MissaoWakanda> aDeslocar = identificaMissoesParaDeslocar(missoesAtuais, minimo);
            deslocaESalva(aDeslocar);
            return minimo;
        }

        return calculaNovaOrdemParaAtiva(missoesAtuais);
    }

    private int calculaNovaOrdemParaAtiva(List<MissaoWakanda> missoesAtuais) {
        return missoesAtuais.stream().filter(m -> m.getMissaoStatus().equals(MissaoStatus.ATIVA))
                .mapToInt(m -> m.getOrdemMissao().getOrdem()).max().orElse(0) + 1;
    }

    private void deslocaESalva(List<MissaoWakanda> aDeslocar) {
        aDeslocar.forEach(m -> m.alterarOrdem(OrdemMissao.criar(m.getOrdemMissao().getOrdem() + 1)));
        missaoRepository.salvaMissoes(aDeslocar);
    }

    private List<MissaoWakanda> identificaMissoesParaDeslocar(List<MissaoWakanda> missoesAtuais, int minimo) {
        return missoesAtuais.stream().filter(m -> m.getOrdemMissao().getOrdem() >= minimo).collect(Collectors.toList());
    }

    @Override
    public MissaoWakanda criaMissaoExterna(MissaoExternaDTO dto, boolean emLote) {
        log.info("[start] MissaoWakandaApplicationService - criaMissaoExterna");
        MissaoWakandaRequest request = MissaoWakandaRequest.builder().titulo(dto.getTitulo())
                .descricao(dto.getDescricao()).idTipoMissao(dto.getIdTipoMissao()).idJornada(dto.getIdJornada())
                .idMissaoExterna(dto.getIdExterno()).idMissaoPai(dto.getIdExternoPai().orElse(null)).conteudoUrl(dto.getConteudoUrl()).xpBase(0).build();
        MissaoWakanda missaoExterna = new MissaoWakanda(request, 0, null, false);
        adicionaMissaoAntesDasInativasESalva(missaoExterna, emLote);
        log.info("[success] Missao criada com sucesso: {} (ID: {})", dto.getTitulo(), dto.getIdExterno());
        log.debug("[finish] MissaoWakandaApplicationService - criaMissaoExterna");
        return missaoExterna;
    }

    @Override
    public void atualizaMissaoComDadosIA(UUID idMissao, AtualizaMissaoRequest request) {
        log.info("[start] MissaoWakandaApplicationService - processaMissaoIA");
        MissaoWakanda missao = missaoRepository.buscaMissaoPorId(idMissao);
        int xpAntigoMissao = missao.getXpBase();
        missao.atualizaMissaoComDadosIA(request);
        missaoRepository.salvaMissao(missao);
        jornadaService.recalculaXpJornada(missao.getIdJornada(), TipoRecalculo.XP_ALTERADO, xpAntigoMissao,
                missao.getXpBase());
        log.debug("[finish] MissaoWakandaApplicationService - processaMissaoIA");
    }
}
