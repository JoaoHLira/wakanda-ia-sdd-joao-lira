package academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.dto;

import academy.wakanda.wakanda_ai.handler.APIException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

@Getter
@ToString
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Representação dos dados para registrar uma aula assistida pelo Wakander.")
public class AulaMemberKitDTO {
    @JsonProperty("type")
    @Schema(description = "Tipo de status", example = "lesson_status.saved")
    private String type;
    @JsonProperty("data")
    private Data data;

    @Getter
    @ToString
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data {
        @JsonProperty("id")
        private String id;
        @JsonProperty("progress")
        private Integer progress;
        @JsonProperty("completed_at")
        @Schema(description = "Data e hora em que a aula foi concluída.", example = "2025-01-07T14:30:00Z")
        private OffsetDateTime completedAt;
        @JsonProperty("created_at")
        private OffsetDateTime createdAt;
        @JsonProperty("updated_at")
        private OffsetDateTime updatedAt;
        @JsonProperty("user")
        private User user;
        @JsonProperty("course")
        private Course course;
        @JsonProperty("lesson")
        private Lesson lesson;
        @JsonProperty("content")
        private String content;
        @JsonProperty("video")
        private Video video;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class User {
        @JsonProperty("id")
        @Schema(description = "Identificador único do Wakander que assistiu à aula.", example = "01234567")
        private String id;
        @JsonProperty("full_name")
        private String fullName;
        @JsonProperty("email")
        private String email;
        @JsonProperty("sign_in_count")
        private Integer signInCount;
        @JsonProperty("current_sign_in_at")
        private OffsetDateTime currentSignInAt;
        @JsonProperty("last_seen_at")
        private OffsetDateTime lastSeenAt;
        @JsonProperty("profile_image_url")
        private String profileImageUrl;
        @JsonProperty("metadata")
        private Object metadata;
        @JsonProperty("created_at")
        private OffsetDateTime createdAt;
        @JsonProperty("updated_at")
        private OffsetDateTime updatedAt;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Course {
        @JsonProperty("id")
        @Schema(description = "Identificador único do curso ao qual a aula pertence.", example = "101")
        private Long id;
        @JsonProperty("name")
        private String name;
        @JsonProperty("position")
        private Integer position;
        @JsonProperty("description")
        private String description;
        @JsonProperty("page_checkout_url")
        private String pageCheckoutUrl;
        @JsonProperty("image_url")
        private String imageUrl;
        @JsonProperty("created_at")
        private OffsetDateTime createdAt;
        @JsonProperty("updated_at")
        private OffsetDateTime updatedAt;
        @JsonProperty("category")
        private Category category;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Category {
        @JsonProperty("id")
        private String id;
        @JsonProperty("name")
        private String name;
        @JsonProperty("position")
        private Integer position;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Lesson {
        @JsonProperty("id")
        @Schema(description = "Identificador único da aula assistida.", example = "202")
        private Long id;
        @JsonProperty("slug")
        private String slug;
        @JsonProperty("title")
        private String title;
        @JsonProperty("position")
        private Integer position;
        @JsonProperty("created_at")
        private OffsetDateTime createdAt;
        @JsonProperty("updated_at")
        private OffsetDateTime updatedAt;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Video {
        @JsonProperty("id")
        private Long id;
        @JsonProperty("source")
        private String source;
        @JsonProperty("uid")
        private String uid;
        @JsonProperty("duration")
        private Integer duration;
        @JsonProperty("image")
        private String image;
    }

    public void validaSeAulaAssistida() {
        if (validaStatusAula() && desmarcouAulaConcluida()) {
            throw APIException.build(HttpStatus.BAD_REQUEST, "Aula não assistida ou inválida.");
        }
    }

    private boolean validaStatusAula() {
        return this.getType().equalsIgnoreCase("lesson_status.saved");
    }

    private boolean desmarcouAulaConcluida() {
        return this.data.getCompletedAt() == null;
    }
}
