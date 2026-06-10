package academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.service;

import java.util.List;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.dto.MemberkitCourseDTO;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.dto.MemberkitLessonDTO;

public interface MemberkitClientService {

    List<MemberkitCourseDTO> getCourses(String apiKey);

    MemberkitCourseDTO getCourseById(String courseId, String apiKey);

    List<MemberkitLessonDTO> getLessonsByCourse(String courseId, String apiKey);
}
