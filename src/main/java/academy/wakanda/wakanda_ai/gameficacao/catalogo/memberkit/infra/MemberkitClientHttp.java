package academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.infra;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.dto.MemberkitCourseDTO;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.dto.MemberkitLessonDTO;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.service.MemberkitClientService;
import academy.wakanda.wakanda_ai.handler.APIException;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class MemberkitClientHttp implements MemberkitClientService {

    private final WebClient webClient;

    @Value("${memberkit.url-api}")
    private String memberkitApiUrl;

    public MemberkitClientHttp(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public List<MemberkitCourseDTO> getCourses(String apiKey) {
        log.info("[start] MemberkitClientHttp - getCourses");
        log.info("URL base do Memberkit: {}", memberkitApiUrl);
        log.info("API Key: {}", apiKey != null ? "[HIDDEN]" : "null");

        try {
            URI uri = UriComponentsBuilder.fromHttpUrl(memberkitApiUrl).path("/courses").queryParam("api_key", apiKey)
                    .build().toUri();

            log.info("URL completa: {}", uri);

            MemberkitCourseDTO[] courses = webClient.get().uri(uri)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).retrieve()
                    .bodyToMono(MemberkitCourseDTO[].class).block();

            log.debug("[finish] MemberkitClientHttp - getCourses");
            return courses != null ? Arrays.asList(courses) : List.of();
        } catch (WebClientResponseException e) {
            log.error("[error] Erro ao buscar cursos: {}", e.getResponseBodyAsString());
            throw APIException.build(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    public MemberkitCourseDTO getCourseById(String courseId, String apiKey) {
        log.info("[start] MemberkitClientHttp - getCourseById - courseId: {}", courseId);

        try {
            URI uri = UriComponentsBuilder.fromHttpUrl(memberkitApiUrl).path("/courses/{id}")
                    .queryParam("api_key", apiKey).buildAndExpand(courseId).toUri();
            MemberkitCourseDTO course = webClient.get().uri(uri)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).retrieve()
                    .bodyToMono(MemberkitCourseDTO.class).block();

            log.info("[finish] MemberkitClientHttp - getCourseById - courseId: {}", courseId);
            return course;

        } catch (WebClientResponseException e) {
            log.error("Erro ao buscar curso por ID {}: {}", courseId, e.getMessage(), e);
            throw APIException.build(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    public List<MemberkitLessonDTO> getLessonsByCourse(String courseId, String apiKey) {
        log.info("[start] MemberkitClientHttp - getLessonsByCourse - courseId: {}", courseId);

        try {
            URI uri = UriComponentsBuilder.fromHttpUrl(memberkitApiUrl).path("/courses/{id}")
                    .queryParam("api_key", apiKey).buildAndExpand(courseId).toUri();

            MemberkitCourseDTO course = webClient.get().uri(uri)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).retrieve()
                    .bodyToMono(MemberkitCourseDTO.class).block();

            if (course == null || course.getSections() == null) {
                return List.of();
            }

            return course.getSections().stream().filter(section -> section.getLessons() != null)
                    .flatMap(section -> section.getLessons().stream()).collect(Collectors.toList());
        } catch (WebClientResponseException e) {
            log.error("Erro ao buscar aulas do curso {}: {}", courseId, e.getMessage(), e);
            throw APIException.build(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

}
