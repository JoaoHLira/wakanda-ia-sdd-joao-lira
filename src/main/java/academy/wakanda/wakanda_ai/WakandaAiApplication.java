package academy.wakanda.wakanda_ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@SpringBootApplication
public class WakandaAiApplication {
    public static void main(String[] args) {
        SpringApplication.run(WakandaAiApplication.class, args);
    }
}