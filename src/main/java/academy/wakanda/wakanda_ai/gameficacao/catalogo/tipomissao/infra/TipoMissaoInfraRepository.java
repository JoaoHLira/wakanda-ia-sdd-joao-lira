package academy.wakanda.wakanda_ai.gameficacao.catalogo.tipomissao.infra;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.tipomissao.application.service.TipoMissaoRepository;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.tipomissao.domain.TipoMissao;
import academy.wakanda.wakanda_ai.handler.APIException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

@Log4j2
@Repository
@RequiredArgsConstructor
public class TipoMissaoInfraRepository implements TipoMissaoRepository {
    private final TipoMissaoSpringDataJpaRepository tipoMissaoSpringDataJpaRepository;

    @Override
    public TipoMissao save(TipoMissao tipoMissao) {
        log.info("[start] TipoMissaoInfraRepository - save");
        validaDescricaoUnica(tipoMissao);
        tipoMissaoSpringDataJpaRepository.save(tipoMissao);
        log.debug("[finish] TipoMissaoInfraRepository - save");
        return tipoMissao;
    }

    private void validaDescricaoUnica(TipoMissao tipoMissao) {
        log.info("[start] TipoMissaoInfraRepository - validaDescricaoUnica");
        if (tipoMissaoSpringDataJpaRepository.existsByDescricao(tipoMissao.getDescricao())) {
            throw APIException.build(HttpStatus.CONFLICT, "Já existe outro Tipo de Missão com a mesma descrição.");
        }
        log.debug("[finish] TipoMissaoInfraRepository - validaDescricaoUnica");
    }

    @Override
    public TipoMissao buscaTipoMissaoId(UUID idTipoMissao) {
        log.info("[start] TipoMissaoInfraRepository - buscaTipoMissaoId");
        TipoMissao tipoMissao = tipoMissaoSpringDataJpaRepository.findById(idTipoMissao).orElseThrow(
                () -> APIException.build(HttpStatus.NOT_FOUND, "Tipo de Missão não encontrado"));
        log.debug("[finish] TipoMissaoInfraRepository - buscaTipoMissaoId");
        return tipoMissao;
    }

    @Override
    public TipoMissao buscaTipoMissaoPorDescricao(String tipoMissaoWakanda) {
        log.info("[start] TipoMissaoInfraRepository - buscaTipoMissaoPorDescricao");
        TipoMissao tipoMissao = tipoMissaoSpringDataJpaRepository.findByDescricao(tipoMissaoWakanda).orElseThrow(
                () -> APIException.build(HttpStatus.NOT_FOUND, "Tipo de Missão não encontrado"));
        log.debug("[finish] TipoMissaoInfraRepository - buscaTipoMissaoPorDescricao");
        return tipoMissao;
    }
}
