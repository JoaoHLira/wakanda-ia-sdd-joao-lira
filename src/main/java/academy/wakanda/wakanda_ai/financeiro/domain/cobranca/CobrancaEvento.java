package academy.wakanda.wakanda_ai.financeiro.domain.cobranca;

import academy.wakanda.wakanda_ai.financeiro.application.api.CobrancaAsaasDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@ToString
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CobrancaEvento {
    private CobrancaEventoType event;
    @NotEmpty(message = "O Payment ID do Asaas não pode estar vazio.")
    private String paymentId;
    @NotEmpty(message = "O Subscription ID do Asaas não pode estar vazio.")
    private String subscriptionId;
    @NotNull(message = "A data de criação não pode estar nula.")
    private LocalDate paymentDateCreated;
    @NotNull(message = "A data de vencimento não pode estar nula.")
    private LocalDate paymentDueDate;
    @NotNull(message = "O valor da cobrança não pode estar nulo.")
    private BigDecimal paymentValue;
    @NotNull(message = "A data de pagamento não pode estar nula.")
    private BigDecimal paymentNetValue;
    private LocalDate paymentDate;

    public CobrancaEvento(CobrancaAsaasDto request) {
        this.event = CobrancaEventoType.valueOf(request.getEvent());
        this.paymentId = request.getPayment().getId();
        this.subscriptionId = request.getPayment().getSubscription();
        this.paymentDateCreated = request.getPayment().getDateCreated();
        this.paymentDueDate = request.getPayment().getDueDate();
        this.paymentValue = request.getPayment().getValue();
        this.paymentNetValue = request.getPayment().getNetValue();
        this.paymentDate = request.getPayment().getPaymentDate();
    }
}
