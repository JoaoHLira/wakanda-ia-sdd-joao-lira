package academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class LoginMemberkitDto {
	private String type;
	private Data data;
	
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	@ToString
	public static class Data{
        @JsonProperty("id")
		private String idMemberKit;
        @JsonProperty("full_name")
		private String fullName;
		private String email;
        @JsonProperty("sign_in_count")
		private String signInCount;
        @JsonProperty("current_sign_in_at")
		private String currentSignInAt;
        @JsonProperty("last_seen_at")
		private String lastSeenAt;
        @JsonProperty("created_at")
		private String createdAt;
        @JsonProperty("updated_at")
		private String updatedAt;
		private String password;
        @JsonProperty("phone_local_code")
		private String phoneLocalCode;
        @JsonProperty("phone_number")
		private String phoneNumber;
	}
}
