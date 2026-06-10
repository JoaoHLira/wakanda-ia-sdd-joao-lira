package academy.wakanda.wakanda_ai.financeiro.domain.cobranca;

import academy.wakanda.wakanda_ai.handler.APIException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "cobranca", uniqueConstraints = {@UniqueConstraint(columnNames = "id_cobranca"), @UniqueConstraint(columnNames = "id_payment_asaas")})
public class Cobranca {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid", name = "id_cobranca")
    private UUID idCobranca;

    @Column(columnDefinition = "uuid", name = "id_wakander")
    private UUID idWakander;

    @Column(name = "id_payment_asaas", nullable = false, unique = true)
    @NotEmpty(message = "O id do pagamento Asaas não pode estar vazio.")
    private String idPaymentAsaas;

    @Column(name = "valor", nullable = false)
    @NotNull(message = "O valor da cobrança não pode estar nulo.")
    private BigDecimal valor;

    @Column(name = "valor_liquido")
    @NotNull(message = "O valor líquido da cobrança não pode estar nulo.")
    private BigDecimal valorLiquido;

    @Column(name = "data_criacao", nullable = false)
    @NotNull(message = "A data de criação da cobrança não pode estar nula.")
    private LocalDate dataCriacao;

    @Column(name = "data_vencimento", nullable = false)
    @NotNull(message = "A data de vencimento da cobrança não pode estar nula.")
    private LocalDate dataVencimento;

    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "O status da cobrança não pode estar nulo.")
    @Column(name = "status", nullable = false)
    private CobrancaStatus status;

    public Cobranca(CobrancaEvento novaCobrancaRequest, UUID idWakander) {
        this.idWakander = idWakander;
        this.idPaymentAsaas = novaCobrancaRequest.getPaymentId();
        this.valor = novaCobrancaRequest.getPaymentValue();
        this.valorLiquido = novaCobrancaRequest.getPaymentNetValue();
        this.dataCriacao = novaCobrancaRequest.getPaymentDateCreated();
        this.dataVencimento = novaCobrancaRequest.getPaymentDueDate();
        this.status = CobrancaStatus.PENDENTE;
    }

    public void alteraStatusParaConfirmado(CobrancaEvento request) {
        checaStatusAtual(CobrancaStatus.PAGAMENTO_CONFIRMADO);
        this.status = CobrancaStatus.PAGAMENTO_CONFIRMADO;
        this.dataPagamento = request.getPaymentDate();
    }

    public void alteraStatusParaNegativado() {
        checaStatusAtual(CobrancaStatus.NEGATIVADO);
        this.status = CobrancaStatus.NEGATIVADO;
    }

    public void atualizaStatusParaVencido() {
        checaStatusAtual(CobrancaStatus.PAGAMENTO_VENCIDO);
        this.status = CobrancaStatus.PAGAMENTO_VENCIDO;
    }

    private void checaStatusAtual(CobrancaStatus cobrancaStatus) {
        if (this.status.equals(cobrancaStatus)) {
            throw APIException.build(HttpStatus.CONFLICT,
                    "Conflito! O Status da cobranca já está como: " + cobrancaStatus);
        }
    }

	public Cobranca(CobrancaEvento novaCobrancaRequest) {
        this.idPaymentAsaas = novaCobrancaRequest.getPaymentId();
        this.valor = novaCobrancaRequest.getPaymentValue();
        this.valorLiquido = novaCobrancaRequest.getPaymentNetValue();
        this.dataCriacao = novaCobrancaRequest.getPaymentDateCreated();
        this.dataVencimento = novaCobrancaRequest.getPaymentDueDate();
        this.status = CobrancaStatus.PENDENTE;
	}
}
