package academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberkitLessonWebhookDTO {

	private String type;
	private LessonData data;

	@Data
	@Builder
	public static class LessonData {
		private String id;
		private String title;
		private String content;
		private Video video;
		private Section section;
		private Course course;
	}

	@Data
	@Builder
	public static class Video {
		private Long id;
		private String uid;
	}

	@Data
	@Builder
	public static class Section {
		private Long id;
		private String slug;
		private String name;
		private String description;
		private Integer position;
	}

	@Data
	@Builder
	public static class Course {
		private String id;
		private String name;
		private String description;
	}

}
