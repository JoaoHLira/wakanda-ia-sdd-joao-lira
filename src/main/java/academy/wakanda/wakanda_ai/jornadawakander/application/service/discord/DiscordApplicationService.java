package academy.wakanda.wakanda_ai.jornadawakander.application.service.discord;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import academy.wakanda.wakanda_ai.config.DiscordProperties;
import academy.wakanda.wakanda_ai.config.JDAProvider;
import academy.wakanda.wakanda_ai.handler.APIException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

@Log4j2
@Service
@RequiredArgsConstructor
public class DiscordApplicationService implements DiscordService {
	private final JDAProvider jdaProvider;
	private final DiscordProperties discordProperties;

	@PostConstruct
	public void enviarMensagemInicialComBotao() {
		CompletableFuture.runAsync(() -> {
			jdaProvider.getJda().ifPresentOrElse(jda -> {
				String canalId = discordProperties.getChannels().getIniciarValidacao();
				TextChannel canal = jda.getTextChannelById(canalId);
				if (canal == null) {
					log.warn("Canal de validação não encontrado (ID: {})", canalId);
					return;
				}
				log.debug("Verificando se já existe mensagem do bot no canal: {}", canal.getName());
				criaButtonCanal(canalId, canal);
			}, () -> log.warn("JDA ainda não disponível, mensagem inicial não será enviada agora!"));
		}, CompletableFuture.delayedExecutor(10, TimeUnit.SECONDS));
	}

	private void criaButtonCanal(String canalId, TextChannel canal) {
		canal.getIterableHistory().takeAsync(10).thenAccept(messages -> {
			boolean jaTemMensagem = messages.stream().anyMatch(msg -> msg.getAuthor().isBot());
			if (!jaTemMensagem) {
				log.info("Enviando mensagem de validação para o canal {}", canal.getName());
				canal.sendMessage("**🎯 Para validar sua conta e ter novamente acesso aos canais:**\n"
						+ "Clique no botão abaixo para iniciar sua validação:")
						.setActionRow(Button.primary("validar:botao", "✅ Validar Agora"))
						.queue(success -> log.info("Mensagem enviada com sucesso"),
								error -> log.error("Erro ao enviar mensagem: {}", error.getMessage()));
			} else {
				log.debug("Mensagem de validação já existe no canal {}", canal.getName());
			}
		}).exceptionally(ex -> {
			log.error("Erro ao buscar mensagens no canal (ID: {}): {}", canalId, ex.getMessage());
			return null;
		});
	}

	private Guild getGuildById() {
		return jdaProvider.getJdaOrThrow().getGuildById(discordProperties.getGuildWakanda().getId());
	}

	public void atualizaCargoParaWakander(String idDiscord) {
		log.info("[inicia] DiscordApplicationService - atualizaCargoParaWakander");
		log.debug("[discordProperties] {}", discordProperties.toString());
		Guild guild = getGuildById();
		validaGuild(guild);
		atualizaCargo(idDiscord, guild);
	}

	private void atualizaCargo(String idDiscord, Guild guild) {
		guild.retrieveMemberById(idDiscord).queue(member -> {
			validaSeMembroExiste(member);
			atualizaRoles(guild, member);
			deletaMensagemCanais(member);
		}, failure -> {
			log.warn("Falha ao buscar membro com ID {}: {}", idDiscord, failure.getMessage());
			throw APIException.build(HttpStatus.NOT_FOUND, "Membro não encontrado!");
		});
	}

	private void deletaMensagemCanais(Member member) {
		removeMensagens(member, discordProperties.getChannels().getValidacao(),
				discordProperties.getChannels().getIniciarValidacao(), discordProperties.getChannels().getFalha());
	}

	private void atualizaRoles(Guild guild, Member member) {
		Role roleWakander = guild.getRoleById(discordProperties.getRoles().getWakander());
		Role roleValidado = guild.getRoleById(discordProperties.getRoles().getMembroValidado());
		Role roleFalha = guild.getRoleById(discordProperties.getRoles().getFalha());
		validaRole(roleWakander, roleValidado);
		removeRole(guild, member, roleFalha, roleValidado);
		guild.addRoleToMember(member, roleWakander).queue();
	}

	private void removeMensagens(Member member, String... idCanais) {
		for (String idCanal : idCanais) {
			TextChannel channel = jdaProvider.getJdaOrThrow().getTextChannelById(idCanal);			
			if (channel != null) {
				channel.getHistory().retrievePast(100).queue(messages -> {
					messages.stream()
				    .filter(msg -> msg.getMentions().getUsers().contains(member.getUser()))
				    .filter(msg -> !msg.getAuthor().isBot() || msg.getActionRows().stream()
				        .noneMatch(row -> row.getButtons().stream()
				            .anyMatch(button -> "validar:botao".equals(button.getId()))
				        )
				    )
				    .forEach(msg -> msg.delete().queue());
				});
			}
		}
	}

	private void validaSeMembroExiste(Member member) {
		if (member == null) {
			log.warn("Membro ainda é null após retrieveMemberById");
			throw APIException.build(HttpStatus.NOT_FOUND, "Membro não encontrado!");
		}
	}

	private void removeRole(Guild guild, Member member, Role... roles) {
		for (Role role : roles) {
			if (role != null && member.getRoles().contains(role)) {
				guild.removeRoleFromMember(member, role).queue();
			}
		}
	}

	private void validaRole(Role... roles) {
		for (Role role : roles) {
			if (role == null) {
				throw APIException.build(HttpStatus.NOT_FOUND, "Cargo não encontrado!");
			}
		}
	}

	private void validaGuild(Guild guild) {
		if (guild == null) {
			throw APIException.build(HttpStatus.NOT_FOUND, "Servidor não encontrado!");
		}
	}

	@Override
	public void cancelaMembroDoServidor(String idDiscord) {
		log.info("[start] DiscordApplicationService - cancelaMembroDoServidor");
		Guild guild = getGuildById();
		Role roleMembroCancelado = guild.getRoleById(discordProperties.getRoles().getMembroCancelado());
		guild.retrieveMemberById(idDiscord).queue(member -> {
			atualizaRoleCancelamento(guild, roleMembroCancelado, member);
		}, failure -> {
			log.warn("Falha ao buscar membro com ID {}: {}", idDiscord, failure.getMessage());
		});
		log.debug("[finish] ComunicacaoDiscordApplicationService - cancelaMembroDoServidor");
	}

	private void atualizaRoleCancelamento(Guild guild, Role roleMembroCancelado, Member member) {
		if (!member.getUser().isBot()) {
			guild.modifyMemberRoles(member, Collections.singleton(roleMembroCancelado)).queue();
		}
	}

}