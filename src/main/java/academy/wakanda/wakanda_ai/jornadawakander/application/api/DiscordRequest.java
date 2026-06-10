package academy.wakanda.wakanda_ai.jornadawakander.application.api;

import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DiscordRequest {
	private String nome;
	private String idDiscord; 
    @Email(message = "O email deve ser válido.")
	private String email;
}