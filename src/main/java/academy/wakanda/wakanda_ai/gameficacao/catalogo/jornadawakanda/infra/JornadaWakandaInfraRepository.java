package academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.infra;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.application.api.JornadaDetalhadaProjection;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.application.service.JornadaWakandaRepository;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.domain.JornadaWakanda;
import academy.wakanda.wakanda_ai.handler.APIException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class JornadaWakandaInfraRepository implements JornadaWakandaRepository {
    private final JornadaWakandaSpringDataJpaRepository jornadaWakandaSpringDataJpaRepository;

    @Override
    public JornadaWakanda save(JornadaWakanda jornadaWakanda) {
        log.info("[start] JornadaWakandaInfraRepository - save");
        JornadaWakanda jornada = jornadaWakandaSpringDataJpaRepository.save(jornadaWakanda);
        log.debug("[finish] JornadaWakandaInfraRepository - save");
        return jornada;
    }

    @Override
    public JornadaWakanda buscaJornadaId(UUID jornada) {
        log.info("[start] JornadaWakandaInfraRepository - buscaJornadaId");
        JornadaWakanda jornadaWakanda = jornadaWakandaSpringDataJpaRepository.findById(jornada)
                .orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Jornada não encontrada"));
        log.debug("[finish] JornadaWakandaInfraRepository - buscaJornadaId");
        return jornadaWakanda;
    }

    @Override
    public List<JornadaWakanda> buscaJornadasPorIdTrilha(UUID trilhaWakanda) {
        log.info("[start] JornadaWakandaInfraRepository - buscaJornadaPorIdTrilha");
        List<JornadaWakanda> jornadas = jornadaWakandaSpringDataJpaRepository.findAllByIdTrilhaWakanda(trilhaWakanda);
        log.debug("[finish] JornadaWakandaInfraRepository - buscaJornadaPorIdTrilha");
        return jornadas;
    }

    @Override
    public JornadaDetalhadaProjection buscaJornadaDetalhada(UUID idJornada) {
        log.info("[start] JornadaWakandaInfraRepository - buscaJornadaDetalhada");
        JornadaDetalhadaProjection jornadaDetalhadaProjection = jornadaWakandaSpringDataJpaRepository.buscarJornadaComTotalDeMissoes(idJornada)
                .orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND,
                        "Jornada não encontrada!"));
        log.debug("[finish] JornadaWakandaInfraRepository - buscaJornadaDetalhada");
        return jornadaDetalhadaProjection;
    }

    @Override
    public List<JornadaWakanda> listaJornadas() {
        log.info("[start] JornadaWakandaInfraRepository - listaJornadas");
        List<JornadaWakanda> jornadas = jornadaWakandaSpringDataJpaRepository.findAll();
        log.debug("[finish] JornadaWakandaInfraRepository - listaJornadas");
        return jornadas;
    }

    @Override
    public JornadaWakanda buscaJornadaPorTitulo(String jornadaWakanda) {
        log.info("[start] JornadaWakandaInfraRepository - buscaJornadaId");
        JornadaWakanda jornadaWaka = jornadaWakandaSpringDataJpaRepository.findByTitulo(jornadaWakanda)
                .orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Jornada não encontrada"));
        log.debug("[finish] JornadaWakandaInfraRepository - buscaJornadaId");
        return jornadaWaka;
    }
}
