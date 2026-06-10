package academy.wakanda.wakanda_ai.jornadawakander.domain;

import java.util.UUID;

import org.springframework.http.HttpStatus;

import academy.wakanda.wakanda_ai.handler.APIException;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "onboarding_wakander", uniqueConstraints = { @UniqueConstraint(columnNames = "id_wakander") })
public class OnboardingWakander {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(columnDefinition = "uuid", name = "id_onboarding_wakander")
	private UUID idOnboardingWakander;
	@Column(columnDefinition = "uuid", name = "id_wakander", unique = true, nullable = false)
	private UUID idWakander;
	@Column(name = "cadastro_confirmado", nullable = false)
	private Boolean cadastroConfirmado;
	@Column(name = "entrou_discord", nullable = false)
	private Boolean entrouDiscord;
	@Column(name = "entrou_grupo_whatsapp", nullable = false)
	private Boolean entrouGrupoWhatsapp;
	@Column(name = "acessou_plataforma_estudo", nullable = false)
	private Boolean acessouPlataformaEstudo;
	@Column(name = "concluiu_comece_aqui", nullable = false)
	private Boolean concluiuComeceAqui;
	@Column(name = "concluiu_primeira_aula_jornada", nullable = false)
	private Boolean concluiuPrimeiraAulaJornada;

	public OnboardingWakander(Wakander wakander) {
		this.idWakander = wakander.getIdWakander();
		this.cadastroConfirmado = false;
		this.entrouDiscord = false;
		this.entrouGrupoWhatsapp = false;
		this.acessouPlataformaEstudo = false;
		this.concluiuComeceAqui = false;
		this.concluiuPrimeiraAulaJornada = false;
	}

	public void atualizaCadastroConfirmado() {
		this.cadastroConfirmado = true;
	}

	public void atualizaEntrouGrupoWhatsapp() {
		this.entrouGrupoWhatsapp = true;
	}

	public void atualizaComeceAqui() {
		this.concluiuComeceAqui = true;
	}

	public void atualizaPrimeiraAulaJornada() {
		this.concluiuPrimeiraAulaJornada = true;
	}

	public void atualizaAcessoPlataformaEstudo() {
		this.acessouPlataformaEstudo = true;
	}

	public void atualizaEntrouNoDiscord() {
		validaDiscordAssociado();
		this.entrouDiscord = true;
	}

	public void validaDiscordAssociado() {
		if (entrouDiscord()) {
			throw APIException.build(HttpStatus.NOT_FOUND, "Seu Discord já foi associado!");
		}
	}

	public boolean entrouDiscord() {
		return this.entrouDiscord == true;
	}

}