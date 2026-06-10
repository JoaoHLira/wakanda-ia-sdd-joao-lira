package academy.wakanda.wakanda_ai.wakander.domain;

import academy.wakanda.wakanda_ai.handler.APIException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Embeddable
@AllArgsConstructor
public class WakanderFinanceiro {
    @Column(name = "status_financeiro")
    @Enumerated(EnumType.STRING)
    private WakanderStatusFinanceiro status;
    @Column(name = "ultima_atualizacao_financeiro")
    private LocalDateTime ultimaAtualizacao;
    @Column(name = "motivo_cancelamento")
    private String motivoCancelamento;

    public WakanderFinanceiro() {
        this.status = WakanderStatusFinanceiro.REGULAR;
        this.ultimaAtualizacao = LocalDateTime.now();
    }

    public void mudaStatus(WakanderStatusFinanceiro status, LocalDateTime dataHoraUltimaAtualizacao) {
        validaStatusAtual(status);
        this.status = status;
        this.ultimaAtualizacao = dataHoraUltimaAtualizacao;
    }

    public void validaStatusAtual(WakanderStatusFinanceiro status) {
        if (this.status.equals(status)) {
            String errorMessage = String.format("O status financeiro do Wakander já está %s.", status);
            throw APIException.build(HttpStatus.CONFLICT, errorMessage);
        }
    }

    public void mudaStatusParaCancelado(String motivoCancelamento, LocalDateTime dataHoraUltimaAtualizacao) {
        validaSepodeCancelar();
        this.status = WakanderStatusFinanceiro.CANCELADO;
        this.ultimaAtualizacao = dataHoraUltimaAtualizacao;
        this.motivoCancelamento = motivoCancelamento;
    }

    private void validaSepodeCancelar() {
        if (!this.status.equals(WakanderStatusFinanceiro.CANCELAMENTO_SOLICITADO)) {
            throw APIException.build(HttpStatus.BAD_REQUEST,
                    "Não é possível cancelar a assinatura. O status atual é: " + status +
                            " e não permite cancelamento. Apenas assinaturas com status CANCELAMENTO_SOLICITADO podem ser canceladas.");
        }
    }

    public void reverteParaRegular(WakanderStatusFinanceiro wakanderStatusFinanceiro, LocalDateTime dataDesistencia) {
        validaSePodeReverter();
        this.status = wakanderStatusFinanceiro;
        this.ultimaAtualizacao = dataDesistencia;
        this.motivoCancelamento = "Desistiu do Cancelamento.";
    }

    private void validaSePodeReverter() {
        if (!this.status.equals(WakanderStatusFinanceiro.CANCELAMENTO_SOLICITADO)) {
            throw APIException.build(HttpStatus.BAD_REQUEST,
                    "Não é possível reverter o cancelamento. O status atual é: " + status +
                            " e não permite reversão. Apenas assinaturas com status CANCELAMENTO_SOLICITADO podem ter o cancelamento revertido.");
        }
    }
}