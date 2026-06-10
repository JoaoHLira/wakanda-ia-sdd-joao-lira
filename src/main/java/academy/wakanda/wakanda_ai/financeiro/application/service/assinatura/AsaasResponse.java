package academy.wakanda.wakanda_ai.financeiro.application.service.assinatura;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Builder
public class AsaasResponse {
    private String id;
    private String name;
    private String url;
    private String email;
    private boolean enabled;
    private boolean interrupted;
    private int apiVersion;
    private boolean hasAuthToken;
    private String sendType;
    private List<String> events;
}
