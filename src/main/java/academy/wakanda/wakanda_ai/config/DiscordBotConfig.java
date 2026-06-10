package academy.wakanda.wakanda_ai.config;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

import academy.wakanda.wakanda_ai.comunicacao.infra.MemberJoinListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

@Configuration
@RequiredArgsConstructor
@Log4j2
public class DiscordBotConfig implements ApplicationListener<ApplicationReadyEvent> {

	@Value("${discord.bot.token}")
	private String botToken;

	private final MemberJoinListener memberJoinListener;
	private final JDAProvider jdaProvider;

	@Override
	public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
		log.info("[start] DiscordBotConfig - onApplicationEvent");
		log.debug("Aguardando 5 segundos para iniciar o bot Discord...");
		CompletableFuture.runAsync(() -> {
			try {
				createJdaBuilder();
			} catch (Exception e) {
				log.error("[ERRO] Falha ao criar e iniciar o bot Discord (JDA): {}", e.getMessage(), e);
			}
		}, CompletableFuture.delayedExecutor(5, TimeUnit.SECONDS));
	}

	private void createJdaBuilder() throws Exception {
		log.debug("[start] DiscordBotConfig - createJdaBuilder");
		JDA jda = JDABuilder.createDefault(botToken)
			.enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES)
			.addEventListeners(memberJoinListener)
			.addEventListeners(new ListenerAdapter() {
				@Override
				public void onReady(ReadyEvent event) {
					log.info("JDA iniciado com sucesso");
				}
			}).build();

		jda.awaitReady();
		jdaProvider.setJda(jda);
	}
}
