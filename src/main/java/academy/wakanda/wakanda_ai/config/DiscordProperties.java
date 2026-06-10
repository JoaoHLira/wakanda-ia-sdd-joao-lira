package academy.wakanda.wakanda_ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "discord")
public class DiscordProperties {

	private String apiUrl;
	private Bot bot;
	private GuildWakanda guildWakanda;
	private Channels channels;
	private Roles roles;

	@ToString
	@Getter
	@Setter
	public static class Bot {
		private String token;
	}

	@ToString
	@Getter
	@Setter
	public static class GuildWakanda {
		private String id;
	}

	@ToString
	@Getter
	@Setter
	public static class Channels {
		private String id;
		private String validacao;
		private String falha;
		private String iniciarValidacao;
	}

	@ToString
	@Getter
	@Setter
	public static class Roles {
		private String membroValidado;
		private String defaultRole;
		private String wakander;
		private String falha;
		private String membroCancelado;
	}
}