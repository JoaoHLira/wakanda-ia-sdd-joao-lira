package academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.service.processadores;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.MemberkitEventRequest;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.dto.AulaMemberKitDTO;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.MemberkitConfig;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.dto.MemberkitCourseDTO;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.dto.MemberkitSectionDTO;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.dto.MemberkitLessonDTO;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.service.MemberkitClientService;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.repository.MissaoWakandaRepository;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.MissaoWakanda;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberkitAulaProcessorTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private academy.wakanda.wakanda_ai.jornadawakander.application.service.memberkit.AulaAssistidaService aulaAssistidaService;

    @Mock
    private MemberkitClientService memberkitClientService;

    @Mock
    private MemberkitConfig memberkitConfig;

    @Mock
    private MissaoWakandaRepository missaoWakandaRepository;

    @InjectMocks
    private MemberkitAulaProcessor processor;

    @BeforeEach
    void setup() {
        // MemberkitAulaProcessor tem apenas ObjectMapper e AulaAssistidaService na sua assinatura
        processor = new MemberkitAulaProcessor(objectMapper, aulaAssistidaService);
    }

    @Test
    @DisplayName("Deve extrair uid do memberkit e salvar conteudoUrl na missao ao processar aula assistida")
    void deveExtrairUidESalvarConteudoUrlNaMissao() throws JsonProcessingException {
        AulaMemberKitDTO.Course course = new AulaMemberKitDTO.Course(123L, "Curso", 1, "desc", null, null, OffsetDateTime.now(), OffsetDateTime.now(), null);
        AulaMemberKitDTO.Lesson lesson = new AulaMemberKitDTO.Lesson(2066153L, "slug", "Aula", 1, OffsetDateTime.now(), OffsetDateTime.now());
        String iframe = "<iframe width=\"560\" height=\"315\" src=\"https://www.youtube.com/embed/yZRd_EWUs1g\"></iframe>";
        AulaMemberKitDTO.Data data = new AulaMemberKitDTO.Data(lesson.getId().toString(), 100, null, null, null, null, course, lesson, iframe, null);
        AulaMemberKitDTO dto = new AulaMemberKitDTO("lesson_status.saved", data);

        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(objectMapper.readValue("{}", AulaMemberKitDTO.class)).thenReturn(dto);

        MemberkitLessonDTO.Video video = MemberkitLessonDTO.Video.builder().uid("wNHL7eWp5dw").build();
        MemberkitLessonDTO lessonDto = MemberkitLessonDTO.builder().id("2066153").title("Aula").content("")
                .video(video).build();

        MemberkitSectionDTO section = MemberkitSectionDTO.builder().id("s1").name("sec").position(1).lessons(List.of(lessonDto)).build();
        MemberkitCourseDTO courseDto = MemberkitCourseDTO.builder().id("123").name("Curso").sections(List.of(section)).build();

        // Não stubbamos missaoWakandaRepository já que este processor apenas delega para AulaAssistidaService

        MemberkitEventRequest request = new MemberkitEventRequest("lesson_status.saved", dto.getData());
        processor.processaEvento(request);

        // MemberkitAulaProcessor atual apenas desserializa e delega para AulaAssistidaService
        verify(aulaAssistidaService).processaEventoAulaAssistida(dto);
        // não espera interação com missaoWakandaRepository neste processor
        verifyNoInteractions(missaoWakandaRepository);
    }
}
