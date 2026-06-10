package academy.wakanda.wakanda_ai.wakander.domain;

import academy.wakanda.wakanda_ai.financeiro.application.api.FiadorDTO;
import academy.wakanda.wakanda_ai.financeiro.domain.assinatura.AssinaturaEvento;
import academy.wakanda.wakanda_ai.financeiro.domain.assinatura.AssinaturaListaAsaasDto;
import academy.wakanda.wakanda_ai.financeiro.domain.assinatura.AssinaturaStatus;
import academy.wakanda.wakanda_ai.financeiro.domain.assinatura.ClienteAsaasDto;
import academy.wakanda.wakanda_ai.wakander.application.api.WakanderNovoRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;

import static org.apache.logging.log4j.util.Strings.isBlank;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Getter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WakanderFiador {
    @Column(name = "id_asaas")
    private String idAsaas;

    @Column(name = "id_assinatura", unique = true)
    private String idAssinatura;

    @Column(name = "nome_fiador")
    private String nome;

    @CPF(message = "O CPF informado é inválido.")
    @Column(name = "cpf_fiador")
    private String cpf;

    @Column(name = "telefone_fiador")
    private String telefone;

    public WakanderFiador(WakanderNovoRequest wakanderNovo) {
        this.idAsaas = wakanderNovo.getIdAsaasFiador();
        this.idAssinatura = wakanderNovo.getIdAssinaturaFiador();
        this.nome = wakanderNovo.getNomeFiador();
        this.cpf = wakanderNovo.getCpfFiador();
        this.telefone = wakanderNovo.getTelefoneFiador();
    }

    public WakanderFiador(AssinaturaEvento assinaturaEvento, ClienteAsaasDto fiadorDto) {
        this.idAsaas = assinaturaEvento.getCustomer();
        this.idAssinatura = assinaturaEvento.getId();
        this.nome = fiadorDto.getName();
        this.cpf = fiadorDto.getCpfCnpj();
        if (fiadorDto.getMobilePhone() == null || fiadorDto.getMobilePhone().isEmpty()) {
            this.telefone = fiadorDto.getPhone();
        } else {
            this.telefone = fiadorDto.getMobilePhone();
        }
    }

    public void atualizaDados(ClienteAsaasDto clienteAsaasDto) {
        this.nome = clienteAsaasDto.getName();
        this.cpf = clienteAsaasDto.getCpfCnpj();
        this.telefone = isBlank(clienteAsaasDto.getMobilePhone()) ? clienteAsaasDto.getPhone() : clienteAsaasDto.getMobilePhone();
        this.idAsaas = isBlank(clienteAsaasDto.getId()) ? this.idAsaas : clienteAsaasDto.getId();
    }

    public void atualizaAssinatura(AssinaturaListaAsaasDto assinaturaListaAsaasDto) {
        assinaturaListaAsaasDto.getData().stream()
                .filter(x -> x.getStatus().equals(AssinaturaStatus.ACTIVE))
                .findFirst()
                .ifPresent(ass -> this.idAssinatura = ass.getId());
    }

    public StatusDadosFiador verificaStatusPreenchimento() {
        return isNotBlank(this.idAsaas) &&
                isNotBlank(this.nome) &&
                isNotBlank(this.cpf) &&
                isNotBlank(this.telefone) ?
                StatusDadosFiador.COMPLETO : StatusDadosFiador.INCOMPLETO;
    }

    public void atualizaFiador(FiadorDTO fiadorDTO) {
        this.nome = fiadorDTO.getNome();
        this.cpf = fiadorDTO.getCpf();
        this.telefone = fiadorDTO.getWhatsapp();
    }
}
