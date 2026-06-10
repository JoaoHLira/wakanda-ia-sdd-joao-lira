package academy.wakanda.wakanda_ai.wakander.domain;

import academy.wakanda.wakanda_ai.financeiro.application.api.FiadorDTO;
import academy.wakanda.wakanda_ai.financeiro.domain.assinatura.AssinaturaEvento;
import academy.wakanda.wakanda_ai.financeiro.domain.assinatura.AssinaturaListaAsaasDto;
import academy.wakanda.wakanda_ai.financeiro.domain.assinatura.ClienteAsaasDto;
import academy.wakanda.wakanda_ai.handler.APIException;
import academy.wakanda.wakanda_ai.jornadawakander.domain.JornadaWakanda;
import academy.wakanda.wakanda_ai.utils.NomeUtils;
import academy.wakanda.wakanda_ai.wakander.application.api.WakanderAlteracaoRequest;
import academy.wakanda.wakanda_ai.wakander.application.api.WakanderCadastroCompleto;
import academy.wakanda.wakanda_ai.wakander.application.api.WakanderCancelaAssinaturaDTO;
import academy.wakanda.wakanda_ai.wakander.application.api.WakanderNovoRequest;
import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "wakander", uniqueConstraints = {@UniqueConstraint(columnNames = "cpf"), @UniqueConstraint(columnNames = "id_member_kit")})
public class Wakander {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid", name = "id_wakander")
    private UUID idWakander;

    @Column(name = "nome")
    private String nome;

    @Column(name = "id_discord")
    private String idDiscord;

    @Column(name = "user_discord")
    private String userDiscord;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @CPF(message = "O CPF informado é inválido.")
    @Column(name = "cpf", unique = true)
    private String cpf;

    @Column(name = "id_member_kit", unique = true)
    private String idMemberKit;

    @Column(name = "status_cadastro")
    @Enumerated(EnumType.STRING)
    private StatusCadastro statusCadastro;

    @Column(name = "jornada_atual")
    @Enumerated(EnumType.STRING)
    private JornadaWakanda jornadaAtual;

    @Embedded
    private WakanderContato contato;

    @Embedded
    private WakanderFinanceiro financeiro;

    @Embedded
    private WakanderFiador fiador;

    @Embedded
    private WakanderAulaAssistida ultimaAulaAssistida;

    @Deprecated
    public Wakander(WakanderNovoRequest wakanderNovo) {
        this.nome = wakanderNovo.getNome();
        this.cpf = wakanderNovo.getCpf();
        this.dataNascimento = wakanderNovo.getDataNascimento();
        this.idMemberKit = wakanderNovo.getIdMemberKit();
        this.statusCadastro = StatusCadastro.COMPLETO;
        this.jornadaAtual = JornadaWakanda.JORNADA_CONHECIMENTO;
        this.contato = new WakanderContato(wakanderNovo);
        this.financeiro = new WakanderFinanceiro();
        this.fiador = new WakanderFiador(wakanderNovo);
        this.ultimaAulaAssistida = new WakanderAulaAssistida();
    }

    public Wakander(AssinaturaEvento assinaturaEvento, ClienteAsaasDto fiadorDto) {
        this.statusCadastro = StatusCadastro.INCOMPLETO;
        this.jornadaAtual = JornadaWakanda.ONBOARD;
        this.financeiro = new WakanderFinanceiro();
        this.fiador = new WakanderFiador(assinaturaEvento, fiadorDto);
        this.ultimaAulaAssistida = new WakanderAulaAssistida();
    }


    public void mudaStatusFinanceiro(WakanderStatusFinanceiro status, LocalDateTime dataHoraUltimaAtualizacao) {
        this.financeiro.mudaStatus(status, dataHoraUltimaAtualizacao);
    }

    public void atualizaUltimaAulaAssistida(UUID idAulaAssistida, LocalDateTime dataConclusao) {
        if (this.ultimaAulaAssistida.getDateTime().isBefore(dataConclusao)) {
            this.ultimaAulaAssistida = new WakanderAulaAssistida(idAulaAssistida, dataConclusao);
        }
    }

    public void atualizaProgresso(JornadaWakanda jornadaWakanda) {
        validaJornadaAtual(jornadaWakanda);
        validaWakanderRegular();
        this.jornadaAtual = jornadaWakanda;
    }

    public void validaWakanderRegular() {
        if (!this.financeiro.getStatus().equals(WakanderStatusFinanceiro.REGULAR)) {
            throw APIException.build(HttpStatus.BAD_REQUEST, "Wakander não está regularizado!");
        }
    }

    private void validaJornadaAtual(JornadaWakanda jornadaWakanda) {
        if (this.jornadaAtual.equals(jornadaWakanda)) {
            throw APIException.build(HttpStatus.CONFLICT, "Wakander já está nessa jornada da wakanda!");
        }
    }

    public void altera(WakanderAlteracaoRequest wakanderAlteracao) {
        this.nome = wakanderAlteracao.getNome();
        this.cpf = wakanderAlteracao.getCpf();
        this.contato.editaContato(wakanderAlteracao.getContato());
    }

    public void completaCadastro(WakanderCadastroCompleto wakander) {
        verificaCPFJaCadastrado(wakander);
        verificaStatusDoCadastro();
        this.nome = wakander.getNome();
        this.cpf = wakander.getCpf();
        this.dataNascimento = wakander.getDataNascimento();
        this.statusCadastro = StatusCadastro.COMPLETO;
        this.contato = new WakanderContato(wakander.getWhatsapp(), wakander.getEmail());
    }

    private void verificaCPFJaCadastrado(WakanderCadastroCompleto wakander) {
        if (Objects.equals(this.cpf, wakander.getCpf())) {
            throw APIException.build(HttpStatus.CONFLICT, "CPF já cadastrado para este Wakander.");
        }
    }

    public void verificaStatusDoCadastro() {
        if (this.statusCadastro.equals(StatusCadastro.COMPLETO)) {
            throw APIException.build(HttpStatus.CONFLICT, "O Wakander ja está com cadastro completo!");
        }
    }

    public void cancelaAssinatura(WakanderCancelaAssinaturaDTO wakanderCancelaAssinatura) {
        LocalDateTime dataCancelamento = wakanderCancelaAssinatura.getDataCancelamento().atTime(LocalTime.now());
        this.financeiro.mudaStatusParaCancelado(wakanderCancelaAssinatura.getMotivoCancelamento(), dataCancelamento);
    }

    public void alteraIdMemberkit(String idMemberKit) {
        this.idMemberKit = idMemberKit;
    }

    public void atualizaStatusJornada(JornadaWakanda jornadaWakanda) {
        validaJornadaAtual(jornadaWakanda);
        this.jornadaAtual = jornadaWakanda;
    }

    public void reverteCancelamentoParaRegular(WakanderCancelaAssinaturaDTO desistiDoCancelamento) {
        LocalDateTime dataDesistencia = desistiDoCancelamento.getDataCancelamento().atTime(LocalTime.now());
        this.financeiro.reverteParaRegular(WakanderStatusFinanceiro.REGULAR, dataDesistencia);
    }

    public boolean validaStatusCadastroWakander(StatusCadastro statusCadastro) {
        return this.statusCadastro.equals(statusCadastro);
    }

    public void validaDadosPessoaisJaEstaoCompletos() {
        if (estaComDadosPessoaisCompletos()) {
            throw APIException.build(
                    HttpStatus.BAD_REQUEST,
                    "Wakander já possui os dados pessoais completos."
            );
        }
        verificaStatusDoCadastro();
    }

    public void validaElegibilidadeOnboardingManual() {
        verificaStatusDoCadastro();
        validaWakanderRegular();
    }

    private boolean estaComDadosPessoaisCompletos() {
        return estaPreenchido(nome)
                && estaPreenchido(cpf)
                && dataNascimento != null
                && contato != null
                && estaPreenchido(contato.getEmail())
                && estaPreenchido(contato.getWhatsapp());
    }

    private boolean estaComDadosMemberKitCompletos() {
        return estaPreenchido(idMemberKit);
    }

    private boolean estaPreenchido(String campo) {
        return campo != null && !campo.trim().isEmpty();
    }

    public String retornaCpfOculto() {
        if (cpf == null || cpf.length() != 11) {
            return "";
        }
        String primeirosTres = cpf.substring(0, 3);
        return primeirosTres + "********";
    }

    public void preencheDadosPessoaisIncompletos(WakanderCadastroCompleto wakanderCadastroCompleto) {
        this.nome = estaPreenchido(wakanderCadastroCompleto.getNome()) ? NomeUtils.capitalizarNome(wakanderCadastroCompleto.getNome()) : nome;
        this.cpf = !estaPreenchido(this.cpf) ? wakanderCadastroCompleto.getCpf() : cpf;
        this.dataNascimento = this.dataNascimento == null ? wakanderCadastroCompleto.getDataNascimento() : dataNascimento;
        this.contato = this.getContato() == null ? new WakanderContato() : this.getContato();

        this.contato.preencheContatoIncompleto(
                wakanderCadastroCompleto.getEmail(),
                wakanderCadastroCompleto.getWhatsapp()
        );
    }

    public void validaStatusCancelado() {
        if (this.financeiro.getStatus().equals(WakanderStatusFinanceiro.CANCELADO)) {
            throw APIException.build(HttpStatus.BAD_REQUEST, "Wakander está com status CANCELADO!");
        }
    }

    public void atualizaDadosFiadorAsaas(ClienteAsaasDto clienteAsaasDto) {
        fiador.atualizaDados(clienteAsaasDto);
    }

    public void atualizaDadosFiadorAsaas(AssinaturaListaAsaasDto assinaturaListaAsaasDto) {
        fiador.atualizaAssinatura(assinaturaListaAsaasDto);
    }

    public StatusDadosFiador verificaStatusPreenchimentoDadosPessoais() {
        return this.estaComDadosPessoaisCompletos() ? StatusDadosFiador.COMPLETO : StatusDadosFiador.INCOMPLETO;
    }

    public void atualizaStatusCadastro() {
        statusCadastro = (estaComDadosPessoaisCompletos()
                && estaComDadosMemberKitCompletos()
                && fiador != null
                && fiador.verificaStatusPreenchimento() == StatusDadosFiador.COMPLETO)
                ? StatusCadastro.COMPLETO : StatusCadastro.INCOMPLETO;
    }

    public void atualizaFiador(FiadorDTO fiadorDTO) {
        fiador.atualizaFiador(fiadorDTO);
    }

    public void validaIdDiscord() {
        if (idDiscord == null) {
            throw APIException.build(HttpStatus.BAD_REQUEST, "O Id Discord é nulo!");
        }
    }

    public void associarDiscord(String idDiscord, String userDiscord) {
        this.idDiscord = idDiscord;
        this.userDiscord = userDiscord;
    }
}
