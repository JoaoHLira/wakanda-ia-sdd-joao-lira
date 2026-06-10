package academy.wakanda.wakanda_ai.gameficacao.catalogo.trilhawakanda.infra;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.application.service.JornadaWakandaRepository;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.trilhawakanda.application.api.TrilhaDetalhadaProjection;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.trilhawakanda.application.service.TrilhaRepository;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.trilhawakanda.domain.TrilhaWakanda;
import academy.wakanda.wakanda_ai.handler.APIException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Log4j2
public class TrilhaInfraRepository implements TrilhaRepository {

    private final TrilhaSpringDataJpaRepository trilhaSpringDataJpaRepository;
    private final JornadaWakandaRepository jornadaRepository;

    @Override
    public TrilhaWakanda buscaTrilhaPorId(UUID idTrilhaWakanda) {
        log.info("[start] TrilhaInfraRepository - buscaTrilhaPorId");
        return trilhaSpringDataJpaRepository.findById(idTrilhaWakanda)
                .orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND,
                        "Id não está associado a nenhuma trilha, verifique a requisição e tente novamente"));
    }

    @Override
    public TrilhaDetalhadaProjection buscaTrilhaDetalhada(UUID idTrilha) {
        log.info("[start] TrilhaInfraRepository - buscaTrilhaDetalhada");
        TrilhaDetalhadaProjection trilhaDetalhadaProjection = trilhaSpringDataJpaRepository.buscaTrilhaComTotalDeJornadas(idTrilha)
                .orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Trilha não encontrada!"));
        log.debug("[finish] TrilhaInfraRepository - buscaTrilhaDetalhada");
        return trilhaDetalhadaProjection;
    }

    @Override
    public List<TrilhaWakanda> listaTrilhas() {
        log.info("[start] TrilhaInfraRepository - listaTrilhas");
        List<TrilhaWakanda> trilhas = trilhaSpringDataJpaRepository.findAll();
        log.debug("[finish] TrilhaInfraRepository - listaTrilhas");
        return trilhas;
    }

    @Override
    public TrilhaWakanda save(TrilhaWakanda trilhaCriada) {
        log.info("[start] TrilhaInfraRepository - criaTrilha");
        try {
            trilhaSpringDataJpaRepository.save(trilhaCriada);
        } catch (DataIntegrityViolationException ex) {
            throw APIException.build(HttpStatus.CONFLICT, "Trilha com esse nome já cadastrada");
        }
        log.debug("[finish] TrilhaInfraRepository - criaTrilha");
        return trilhaCriada;
    }
}
