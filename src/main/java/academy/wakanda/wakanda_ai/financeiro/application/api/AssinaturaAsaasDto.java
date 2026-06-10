package academy.wakanda.wakanda_ai.financeiro.application.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AssinaturaAsaasDto {
    private String id;
    private String event;
    private String dateCreated;
    private SubscriptionDTO subscription;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubscriptionDTO {
        @JsonProperty("object")
        private String objectType;
        private String id;
        private String dateCreated;
        private String customer;
        private String paymentLink;
        private BigDecimal value;
        private String nextDueDate;
        private String cycle;
        private String description;
        private String billingType;
        private boolean deleted;
        private String status;
        private String externalReference;
        private boolean sendPaymentByPostalService;
        private DiscountDTO discount;
        private FineDTO fine;
        private InterestDTO interest;
        private List<SplitDTO> split;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiscountDTO {
        private BigDecimal value;
        private String limitDate;
        private int dueDateLimitDays;
        private String type;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FineDTO {
        private BigDecimal value;
        private String type;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InterestDTO {
        private BigDecimal value;
        private String type;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SplitDTO {
        private String walletId;
        private BigDecimal fixedValue;
        private BigDecimal percentualValue;
        private String externalReference;
        private String description;
    }
}
