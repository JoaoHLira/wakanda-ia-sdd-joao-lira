package academy.wakanda.wakanda_ai.financeiro.domain.assinatura;

import academy.wakanda.wakanda_ai.financeiro.application.api.AssinaturaAsaasDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssinaturaEvento {
    @JsonProperty("event")
    private AssinaturaType event;

    @JsonProperty("dateCreated")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateCreated;

    @JsonProperty("subscription.id")
    private String id;

    @JsonProperty("subscription.customer")
    private String customer;

    public AssinaturaEvento(AssinaturaAsaasDto evento) {
        this.event = AssinaturaType.valueOf(evento.getEvent());
        this.dateCreated = converteParaLocalDateTime(evento.getDateCreated());
        this.id = evento.getSubscription().getId();
        this.customer = evento.getSubscription().getCustomer();
    }

    private LocalDateTime converteParaLocalDateTime(String date) {
        DateTimeFormatter formatacao = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(date, formatacao);
    }
}
