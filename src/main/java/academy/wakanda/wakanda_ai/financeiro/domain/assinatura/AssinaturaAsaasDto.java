package academy.wakanda.wakanda_ai.financeiro.domain.assinatura;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class AssinaturaAsaasDto {

    private String object;
    private String id;
    private String dateCreated;
    private String customer;
    private String paymentLink;
    private double value;
    private String nextDueDate;
    private String cycle;
    private String description;
    private String billingType;
    private boolean deleted;
    private AssinaturaStatus status;
    private String externalReference;
    private String checkoutSession;
    private boolean sendPaymentByPostalService;
    private Fine fine;
    private Interest interest;
    private String split;

    @Getter
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Fine {
        private double value;
        private String type;
    }

    @Getter
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Interest {
        private double value;
        private String type;
    }
}
