package academy.wakanda.wakanda_ai.financeiro.application.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CobrancaAsaasDto {
    private String id;
    private String event;
    private String dateCreated;
    private Payment payment;

    @Getter
    @ToString
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Payment {
        private String object;
        private String id;
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate dateCreated;
        private String customer;
        private String subscription;
        private String installment;
        private String paymentLink;
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate dueDate;
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate originalDueDate;
        private BigDecimal value;
        private BigDecimal netValue;
        private BigDecimal originalValue;
        private BigDecimal interestValue;
        private String nossoNumero;
        private String description;
        private String externalReference;
        private String billingType;
        private String status;
        private String pixTransaction;
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate confirmedDate;
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate paymentDate;
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate clientPaymentDate;
        private Integer installmentNumber;
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate creditDate;
        private String custody;
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate estimatedCreditDate;
        private String invoiceUrl;
        private String bankSlipUrl;
        private String transactionReceiptUrl;
        private String invoiceNumber;
        private Boolean deleted;
        private Boolean anticipated;
        private Boolean anticipable;
        private String lastInvoiceViewedDate;
        private String lastBankSlipViewedDate;
        private Boolean postalService;
        private CreditCard creditCard;
        private Discount discount;
        private Fine fine;
        private Interest interest;
        private List<Split> split;
        private Chargeback chargeback;
        private List<Refund> refunds;
        }

    @Getter
    @ToString
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CreditCard {
        private String creditCardNumber;
        private String creditCardBrand;
        private String creditCardToken;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Discount {
        private BigDecimal value;
        private Integer dueDateLimitDays;
        private String limitedDate;
        private String type;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Fine {
        private BigDecimal value;
        private String type;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Interest {
        private BigDecimal value;
        private String type;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Split {
        private String id;
        private String walletId;
        private BigDecimal fixedValue;
        private BigDecimal percentualValue;
        private String status;
        private String refusalReason;
        private String externalReference;
        private String description;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Chargeback {
        private String status;
        private String reason;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Refund {
        private String dateCreated;
        private String status;
        private BigDecimal value;
        private String endToEndIdentifier;
        private String description;
        private String effectiveDate;
        private String transactionReceiptUrl;
        private List<RefundedSplit> refundedSplits;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RefundedSplit {
        private String id;
        private BigDecimal value;
        private Boolean done;
    }
}
