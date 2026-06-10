package academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberkitLessonDTO {
    @JsonProperty("id")
    private String id;

    @JsonProperty("course_id")
    private String courseId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("content")
    private String content;

    @JsonProperty("position")
    private Integer position;

    @JsonProperty("status")
    private String status;

    @JsonProperty("video")
    private Video video;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Video {

        @JsonProperty("uid")
        private String uid;

    }
}
