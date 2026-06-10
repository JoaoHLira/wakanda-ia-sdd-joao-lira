package academy.wakanda.wakanda_ai.constants;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "memberkit")
public class MemberkitProperties {
    private String url;
    private String urlMemberkitWakanda;
    private String apiKey;
    private Integer membershipLevelId;
    private List<Integer> classroomIds;
}