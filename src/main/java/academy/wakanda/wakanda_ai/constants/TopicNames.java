package academy.wakanda.wakanda_ai.constants;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@ToString
@Configuration
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "aws.topic")
public class TopicNames {
    private String teste;
    private String zapiRequests;
    private String memberkitRequests;
    private String asaasRequests;
    private String clintContatoRequests;
    private String discordRequest;
    private String clintRequests;
    private String progressoWakanderRequests;
    private String xpWakanderRequests;
}
