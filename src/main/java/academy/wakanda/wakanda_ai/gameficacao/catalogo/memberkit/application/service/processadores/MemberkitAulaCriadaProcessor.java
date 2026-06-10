package academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.service.processadores;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.application.service.JornadaWakandaRepository;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.MemberkitEventRequest;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.MemberkitLessonWebhookDTO;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.service.MemberkitEventType;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.service.MemberkitClientService;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.dto.MemberkitCourseDTO;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.dto.MemberkitLessonDTO;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.adapter.MemberkitAulaAdapter;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.MemberkitConfig;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.api.MissaoExternaDTO;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.repository.MissaoWakandaRepository;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.service.MissaoWakandaService;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.MissaoWakanda;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.tipomissao.application.service.TipoMissaoRepository;
import academy.wakanda.wakanda_ai.handler.APIException;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
@RequiredArgsConstructor
public class MemberkitAulaCriadaProcessor implements MemberkitProcessor {

    @Value("${wakanda.importacao.jornada}")
    private String jornadaWakanda;

    @Value("${wakanda.importacao.tipo-missao}")
    private String tipoMissaoWakanda;

    private static final Pattern PADRAO_IFRAME = Pattern.compile("https://www\\.youtube\\.com/embed/([a-zA-Z0-9_-]+)");
    private static final String PREFIXO_YOUTUBE = "https://www.youtube.com/watch?v=";

    private final ObjectMapper objectMapper;
    private final MissaoWakandaService missaoWakandaService;
    private final MissaoWakandaRepository missaoWakandaRepository;
    private final TipoMissaoRepository tipoMissaoRepository;
    private final JornadaWakandaRepository jornadaWakandaRepository;
    private final MemberkitClientService memberkitClientService;
    private final MemberkitConfig memberkitConfig;

	@Override
	public boolean validaSeEventoProcessa(String type) {
		return type.equals(MemberkitEventType.AULA_CRIADA.getDescricao());
	}

    @Override
    public void processaEvento(MemberkitEventRequest request) {
        log.info("[start] MemberkitAulaCriadaProcessor - processaEvento");
        log.debug("[request] {}", toJson(request));

        MemberkitLessonWebhookDTO dto = objectMapper.convertValue(request, MemberkitLessonWebhookDTO.class);
        handleLesson(dto);

        log.info("[finish] MemberkitAulaCriadaProcessor - processaEvento");
    }

    private void handleLesson(MemberkitLessonWebhookDTO dto) {
        MemberkitLessonWebhookDTO.LessonData data = dto.getData();
        String idAula = data.getId();

        garanteAulaNaoExiste(idAula);
        UUID idTipoMissao = obtemIdTipoMissao();
        UUID idJornada = obtemIdJornada();
        criaOuAtualizaMissoes(data, idAula, idTipoMissao, idJornada);
    }

    private UUID obtemIdTipoMissao() {
        return tipoMissaoRepository.buscaTipoMissaoPorDescricao(tipoMissaoWakanda).getIdTipoMissao();
    }

    private UUID obtemIdJornada() {
        return jornadaWakandaRepository.buscaJornadaPorTitulo(jornadaWakanda).getIdJornada();
    }

    private void criaOuAtualizaMissoes(MemberkitLessonWebhookDTO.LessonData data, String idAula, UUID idTipoMissao, UUID idJornada) {
        String descricao = normalizaDescricao(obtemDescricaoCurso(data));
        String conteudoUrl = extraiConteudoUrlDoPayload(data);
        if (conteudoUrl == null) {
            conteudoUrl = buscaConteudoUrlAula(obtemIdCurso(data), idAula);
        }
        UUID idMissaoPai = findOrCreateParent(data, idTipoMissao, idJornada, descricao);
        mapeiaECriaMissaoFilha(idAula, data.getTitle(), idMissaoPai, idTipoMissao, idJornada, descricao, conteudoUrl);
    }

    private String extraiConteudoUrlDoPayload(MemberkitLessonWebhookDTO.LessonData data) {
        String uid = extraiVideoUid(data);
        if (StringUtils.isNotBlank(uid)) return PREFIXO_YOUTUBE + uid.trim();
        String iframeId = extraiIdVideoDoIframe(data.getContent());
        if (iframeId != null) return PREFIXO_YOUTUBE + iframeId;
        return null;
    }

    private String extraiVideoUid(MemberkitLessonWebhookDTO.LessonData data) {
        if (data.getVideo() != null && StringUtils.isNotBlank(data.getVideo().getUid())) {
            return data.getVideo().getUid().trim();
        }
        return null;
    }

    private String extraiIdVideoDoIframe(String content) {
        if (content == null) return null;
        Matcher matcher = PADRAO_IFRAME.matcher(content);
        if (matcher.find()) return matcher.group(1);
        return null;
    }

    private String obtemIdCurso(MemberkitLessonWebhookDTO.LessonData data) {
        return data.getCourse() != null ? data.getCourse().getId() : null;
    }

    private String obtemDescricaoCurso(MemberkitLessonWebhookDTO.LessonData data) {
        return data.getCourse() != null ? data.getCourse().getDescription() : null;
    }

    private UUID findOrCreateParent(MemberkitLessonWebhookDTO.LessonData data, UUID idTipoMissao, UUID idJornada, String descricao) {
        String idCurso = obtemIdCurso(data);
        String nomeCurso = data.getCourse() != null ? data.getCourse().getName() : "-";
        Optional<MissaoWakanda> opt = missaoWakandaRepository.buscaIdMissaoWakandaExterno(idCurso);
        return mapeiaECriaMissaoPai(opt, idCurso, nomeCurso, idTipoMissao, idJornada, descricao);
    }

    private void mapeiaECriaMissaoFilha(String idAula, String tituloAula, UUID idMissaoPai, UUID idTipoMissao,
            UUID idJornadaWakanda, String descricao, String conteudoUrl) {
        MissaoExternaDTO dtoFilha = MissaoExternaDTO.builder().idExterno(idAula).titulo(tituloAula)
                .idTipoMissao(idTipoMissao).idJornada(idJornadaWakanda).descricao(descricao)
                .conteudoUrl(conteudoUrl)
                .idExternoPai(Optional.ofNullable(idMissaoPai)).build();

        missaoWakandaService.criaMissaoExterna(dtoFilha, false);
    }

	private UUID mapeiaECriaMissaoPai(Optional<MissaoWakanda> optMissaoPai, String idCurso, String nomeCurso, UUID idTipoMissao, UUID idJornadaWakanda, String descricao) {
		return optMissaoPai.map(MissaoWakanda::getIdMissao).orElseGet(() -> {
			MissaoExternaDTO dtoPai = MissaoExternaDTO.builder().idExterno(idCurso).titulo(nomeCurso)
					.idTipoMissao(idTipoMissao).idJornada(idJornadaWakanda).descricao(descricao)
					.idExternoPai(Optional.empty()).build();
			MissaoWakanda missaoPai = missaoWakandaService.criaMissaoExterna(dtoPai, false);
			return missaoPai.getIdMissao();
		});
	}

    private void garanteAulaNaoExiste(String idAula) {
        if (missaoWakandaRepository.buscaIdMissaoWakandaExterno(idAula).isPresent()) {
            throw APIException.build(HttpStatus.CONFLICT, "Missão já existe: " + idAula);
        }
    }

	public static String toJson(Object obj) {
		try {
			return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			return obj.toString();
		}
	}

    private String normalizaDescricao(String descricaoOriginal) {
        return (descricaoOriginal == null || descricaoOriginal.isBlank()) ? "Sem descrição disponível"
                : descricaoOriginal;
    }

    private String buscaConteudoUrlAula(String idCurso, String idAula) {
        try {
            return buscaAula(idCurso, idAula)
                    .map(this::extrairConteudoUrl)
                    .orElse(null);
        } catch (Exception e) {
            log.warn("[warn] Falha ao buscar detalhe da aula {} no curso {} para extrair link: {}", idAula, idCurso, e.getMessage());
            return null;
        }
    }

    private Optional<MemberkitLessonDTO> buscaAula(String idCurso, String idAula) {
        return buscaLessons(idCurso)
                .filter(lesson -> idAula.equals(lesson.getId()))
                .findFirst();
    }

    private Stream<MemberkitLessonDTO> buscaLessons(String idCurso) {
        MemberkitCourseDTO course = memberkitClientService.getCourseById(idCurso, memberkitConfig.getApiKey());
        if (course == null || course.getSections() == null) return Stream.empty();
        return course.getSections().stream()
                .filter(section -> section.getLessons() != null)
                .flatMap(section -> section.getLessons().stream());
    }

    private String extrairConteudoUrl(MemberkitLessonDTO lesson) {
        return new MemberkitAulaAdapter(lesson).toMissaoData(null, null, null).conteudoUrl();
    }

}
