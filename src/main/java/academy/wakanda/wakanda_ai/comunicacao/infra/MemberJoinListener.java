package academy.wakanda.wakanda_ai.comunicacao.infra;

import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import academy.wakanda.wakanda_ai.config.DiscordProperties;
import academy.wakanda.wakanda_ai.constants.MensagensDiscord;
import academy.wakanda.wakanda_ai.handler.APIException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Log4j2
@Component
@RequiredArgsConstructor
public class MemberJoinListener extends ListenerAdapter {
	@Value("${aws.url}")
	private String urlInstancia;
	private final DiscordProperties discordProperties;
	
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		log.info("[inicia] MemberJoinListener - onGuildMemberJoin");
		log.debug("[discordProperties] {}", discordProperties.toString());
		atribuiCargoInicial(event);
		enviaMensagemDeBoasVindas(event);
		log.info("[finaliza] MemberJoinListener - onGuildMemberJoin");
	}

	private void atribuiCargoInicial(GuildMemberJoinEvent event) {
		Guild guild = event.getGuild();
		Member member = event.getMember();
		atualizaCargo(guild, member);
	}

	private void atualizaCargo(Guild guild, Member member) {
		validarMembroECargo(member, guild.getRoleById(discordProperties.getRoles().getMembroValidado()));
		adicionarCargo(member, guild, discordProperties.getRoles().getMembroValidado());
	}

	private void enviaMensagemDeBoasVindas(GuildMemberJoinEvent event) {
		validarGuild(event.getGuild());
		enviarMensagemPrivadaOuFallback(event);
	}

	private void enviarMensagemPrivadaOuFallback(GuildMemberJoinEvent event) {
		Member member = event.getMember();
		User user = member.getUser();
		Guild guild = event.getGuild();
		TextChannel canalOnboarding = guild.getTextChannelById(discordProperties.getChannels().getValidacao());
		enviaMensagem(member, user, guild, canalOnboarding);
	}

	private void enviarMensagemPrivada(PrivateChannel channel, User user, Member member, TextChannel canalOnboarding) {
		String mensagemPrivada = retornaMensagemPrivada(member);
		channel.sendMessage(mensagemPrivada).queue(sucesso -> {
			enviaMensagemSucesso(canalOnboarding, retornaMensagemPublica(member, channel));
		}, falha -> {
			notificarFalhaNaDM(member, channel.getJDA().getGuildById(member.getGuild().getId()));
		});
	}

	private String retornaMensagemPublica(Member member, PrivateChannel channel) {
		String mensagem = MensagensDiscord.MENSAGEM_CANAL_PUBLICO.formataMensagem(member.getAsMention(), channel.getId());
		return mensagem;
	}

	private String retornaMensagemPrivada(Member member) {
		String url = gerarUrlValidacao(member.getUser().getName(), member.getId());
		String mensagemPrivada = MensagensDiscord.MENSAGEM_PRIVADA.formataMensagem(member.getUser().getName(), url);
		return mensagemPrivada;
	}

	private void enviaMensagemSucesso(TextChannel canal, String mensagem) {
		canal.sendMessage(mensagem).queue();
	}

	private void notificarFalhaNaDM(Member member, Guild guild) {
		TextChannel canalFalha = guild.getTextChannelById(discordProperties.getChannels().getFalha());
		removerCargo(member, guild, discordProperties.getRoles().getMembroValidado());
		atualizaCargoFalha(member, guild);
		String mensagemFalha = retornaMensagemFalha(member);
		enviaMensagemSucesso(canalFalha, mensagemFalha);
	}

	private void atualizaCargoFalha(Member member, Guild guild) {
		removeCargoAntigo(member, guild);
		adicionarCargo(member, guild, discordProperties.getRoles().getFalha());
	}

	private String retornaMensagemFalha(Member member) {
		String url = gerarUrlValidacao(member.getUser().getName(), member.getId());
		String mensagemFalha = MensagensDiscord.MENSAGEM_CANAL_VALIDACAO_FALHA.formataMensagem(member.getAsMention(),
				url);
		return mensagemFalha;
	}

	private void removeCargoAntigo(Member member, Guild guild) {
		Role cargoAntigo = guild.getRoleById(discordProperties.getRoles().getMembroValidado());
		if (cargoAntigo != null) {
			guild.retrieveMemberById(member.getId()).queue(updatedMember -> {
				if (updatedMember.getRoles().contains(cargoAntigo)) {
					log.info("[removerCargo] Removendo cargo antigo de {}", updatedMember.getEffectiveName());
					removerCargo(updatedMember, guild, discordProperties.getRoles().getMembroValidado());
				} else {
					log.warn("[removerCargo] Membro {} Nao está com o cargo antigo no Discord (atualizado)",
							updatedMember.getEffectiveName());
				}
			}, error -> {
				log.error("Erro ao atualizar dados do membro: {}", error.getMessage());
			});
		}
	}

	private void enviaMensagem(Member member, User user, Guild guild, TextChannel canal) {
		user.openPrivateChannel().queue(
				privateChannel -> enviarMensagemPrivada(privateChannel, user, member, canal),
				failure -> notificarFalhaNaDM(member, guild));
	}
	
	private void adicionarCargo(Member member, Guild guild, String idCargo) {
        Role cargo = guild.getRoleById(idCargo);
        log.debug("Adicionando cargo: {}", cargo.getName());
		if (cargo != null && !member.getRoles().contains(cargo)) {
			member.getGuild().addRoleToMember(member, cargo).queue();
		}
	}

	private void removerCargo(Member member, Guild guild, String idCargo) {
        Role cargo = guild.getRoleById(idCargo);
        log.debug("Removendo cargo: {}", cargo.getName());
		if (cargo != null && member.getRoles().contains(cargo)) {
			member.getGuild().removeRoleFromMember(member, cargo).queue();
		}
	}
	
	private String gerarUrlValidacao(String username, String idDiscord) {
		return String.format("%s/wakanda-ai/api/formulario/%s/%s/associar-discord", urlInstancia, username, idDiscord);
	}

	private void validarMembroECargo(Member member, Role role) {
		if (member == null || role == null) {
			throw APIException.build(HttpStatus.NOT_FOUND, "Membro ou cargo não encontrado!");
		}
	}

	private void validarGuild(Guild guild) {
		if (guild == null) {
			throw APIException.build(HttpStatus.NOT_FOUND, "Servidor não encontrado!");
		}
	}

	@Override
	public void onButtonInteraction(ButtonInteractionEvent event) {
		log.info("[inicia] MemberJoinListener - onButtonInteraction");
		if (!event.getComponentId().equals("validar:botao")) return;
		Member member = event.getMember();
		Guild guild = event.getGuild();
		if (member == null || guild == null || member.getUser().isBot()) return;
		atualizaCargos(event, member, guild);
		enviaMensagemEventButton(member, guild);
	}

	private void enviaMensagemEventButton(Member member, Guild guild) {
		enviarMensagemPrivadaOuFallback(member, member.getUser(), guild);
		TextChannel canal = guild.getTextChannelById(discordProperties.getChannels().getIniciarValidacao());
		if (canal != null) {
		    canal.sendMessage(member.getAsMention() + " clicou para iniciar a validação. ✅").queue();
		}
	}

	private void atualizaCargos(ButtonInteractionEvent event, Member member, Guild guild) {
		event.reply("✅ Validação iniciada!")
		    .setEphemeral(false)
		    .queue(interactionHook ->
		        interactionHook.deleteOriginal().queueAfter(5, TimeUnit.SECONDS)
		    );
		log.debug("Cargos atuais de {}: {}", member.getEffectiveName(),
		         member.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
		removerCargo(member, guild, discordProperties.getRoles().getDefaultRole());
		adicionarCargo(member, guild, discordProperties.getRoles().getMembroValidado());
	}
	    
	private void enviarMensagemPrivadaOuFallback(Member member, User user, Guild guild) {
		TextChannel canal = guild.getTextChannelById(discordProperties.getChannels().getValidacao());
		enviaMensagem(member, user, guild, canal);
	}

}
