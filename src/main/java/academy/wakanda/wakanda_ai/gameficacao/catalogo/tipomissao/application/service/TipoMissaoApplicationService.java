package academy.wakanda.wakanda_ai.gameficacao.catalogo.tipomissao.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.tipomissao.application.api.TipoMissaoRequest;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.tipomissao.application.api.TipoMissaoResponse;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.tipomissao.domain.TipoMissao;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class TipoMissaoApplicationService implements TipoMissaoService {

    private final TipoMissaoRepository tipoMissaoRepository;

    @Override
    public TipoMissaoResponse insereTipoMissao(TipoMissaoRequest tipoMissaoRequest) {
        log.info("[start] TipoMissaoApplicationService - insereTipoMissao");
        TipoMissao tipoMissao = new TipoMissao(tipoMissaoRequest);
        TipoMissao tipoMissaoCriada = tipoMissaoRepository.save(tipoMissao);
        log.debug("[finish] TipoMissaoApplicationService - insereTipoMissao");
        return new TipoMissaoResponse(tipoMissaoCriada) ;
    }

    @Override
    public TipoMissaoResponse buscaTipoMissaoId(UUID tipoMissaoId) {
        log.info("[start] TipoMissaoApplicationService - buscaTipoMissaoId");
        TipoMissao tipoMissao = tipoMissaoRepository.buscaTipoMissaoId(tipoMissaoId);
        log.debug("[finish] TipoMissaoApplicationService - buscaTipoMissaoId");
        return new TipoMissaoResponse(tipoMissao);
    }

    
}
