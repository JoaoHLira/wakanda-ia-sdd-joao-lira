package academy.wakanda.wakanda_ai.config;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.stereotype.Component;

import net.dv8tion.jda.api.JDA;

@Component
public class JDAProvider {

	private final AtomicReference<JDA> jdaRef = new AtomicReference<>();

	public void setJda(JDA jda) {
		this.jdaRef.set(jda);
	}

	public Optional<JDA> getJda() {
		return Optional.ofNullable(jdaRef.get());
	}

	public JDA getJdaOrThrow() {
		return Optional.ofNullable(jdaRef.get())
			.orElseThrow(() -> new IllegalStateException("JDA ainda não foi inicializado"));
	}
}