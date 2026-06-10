package academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.service;

import java.util.UUID;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.api.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.MissaoStatus;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.MissaoWakanda;
import jakarta.validation.Valid;

public interface MissaoWakandaService {
    MissaoWakandaResponse criaMissao(MissaoWakandaRequest missaoRequest);

    MissaoWakandaDetalhadoResponse buscaMissaoPorId(UUID idMissao);

    void desativaMissao(UUID idMissao);

    void alteraXpBase(UUID idMissao, @Valid MissaoAlteracaoXpBaseRequest missaoAlteracaoXpBase);

    void alteraPosicaoMissao(UUID idMissao, Integer posicaoRequest);

    Page<MissaoWakandaDetalhadoResponse> buscaMissoesPaginadas(Pageable pageable, UUID idJornada, MissaoStatus missaoStatus);

    MissaoWakanda adicionaMissaoAntesDasInativasESalva(MissaoWakanda novaMissao, boolean emLote);

    MissaoWakanda criaMissaoExterna(MissaoExternaDTO dto, boolean emLote);

    void atualizaMissaoComDadosIA(UUID idMissao, @Valid AtualizaMissaoRequest request);
}
