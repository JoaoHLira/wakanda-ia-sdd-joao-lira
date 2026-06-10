package academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.application.service.JornadaWakandaRepository;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.MemberkitEventRequest;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.dto.MemberkitCourseDTO;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.dto.MissaoOrigemDTO;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.service.processadores.MemberkitProcessor;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.tipomissao.application.service.TipoMissaoRepository;
import academy.wakanda.wakanda_ai.handler.APIException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class MemberkitApplicationService implements MemberkitService {

    private final TipoMissaoRepository tipoMissaoRepository;
    private final JornadaWakandaRepository jornadaWakandaService;
    private final List<MemberkitProcessor> memberkitProcessor;
    private final MemberkitIntegrationImporter memberkitIntegrationImporter;

    @Override
    public void importaMemberkit(UUID idTipoMissao, UUID idJornada) {
        log.info("[start] MemberkitApplicationService - Importando dados do Memberkit");
        try {
            memberkitIntegrationImporter.validarConfiguracoes();
            UUID jornada = jornadaWakandaService.buscaJornadaId(idJornada).getIdJornada();
            UUID tipoMissao = tipoMissaoRepository.buscaTipoMissaoId(idTipoMissao).getIdTipoMissao();
            Map<String, List<MissaoOrigemDTO>> titulosDuplicados = new HashMap<>();
            List<MemberkitCourseDTO> cursos = memberkitIntegrationImporter.buscarCursosDoMemberkit();
            validaSeExistemCursos(cursos);
            processarCursos(cursos, titulosDuplicados, tipoMissao, jornada);
            exibeDuplicados(titulosDuplicados);
            log.debug("[finish] MemberkitApplicationService - Importação concluída com sucesso");

        } catch (APIException e) {
            throw e;
        } catch (Exception e) {
            throw APIException.build(HttpStatus.INTERNAL_SERVER_ERROR, "Erro na importação", e);
        }
    }

    private void exibeDuplicados(Map<String, List<MissaoOrigemDTO>> titulosDuplicados) {
        log.warn("Títulos duplicados detectados:");
        titulosDuplicados.entrySet().stream().filter(entry -> entry.getValue().size() > 1).forEach(entry -> {
            String titulo = entry.getKey();
            List<MissaoOrigemDTO> origens = entry.getValue();
            log.warn(" - Título: {}", titulo);
            origens.forEach(origem -> log.warn("     ->  [{}] ID: {} | Curso: {}", origem.tipo(), origem.id(),
                    origem.cursoPai()));
        });
    }

    private void processarCursos(List<MemberkitCourseDTO> cursos, Map<String, List<MissaoOrigemDTO>> titulosDuplicados,
            UUID tipoMissao, UUID idJornada) {
        log.info("Processando {} cursos", cursos.size());
        cursos.forEach(
                curso -> memberkitIntegrationImporter.processarCurso(curso, titulosDuplicados, tipoMissao, idJornada));
    }

    private void validaSeExistemCursos(List<MemberkitCourseDTO> cursos) {
        if (cursos.isEmpty()) {
            throw APIException.build(HttpStatus.NO_CONTENT, "Nenhum curso encontrado para importação");
        }
    }

    public void processaWebhook(MemberkitEventRequest request) {
        log.info("[start] MemberkitApplicationService - processaWebhook");
        MemberkitProcessor memberkitProc = strategyMemberkitProcessor(request);
        memberkitProc.processaEvento(request);
        log.debug("[finish] MemberkitApplicationService - processaWebhook");
    }

    private MemberkitProcessor strategyMemberkitProcessor(MemberkitEventRequest memberkitEvento) {
        return memberkitProcessor.stream().filter(a -> a.validaSeEventoProcessa(memberkitEvento.getType())).findFirst()
                .orElseThrow(() -> APIException.build(HttpStatus.BAD_REQUEST,
                        "O evento não corresponde a nenhuma estrategia!"));
    }

}
