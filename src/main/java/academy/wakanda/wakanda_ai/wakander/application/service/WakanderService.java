package academy.wakanda.wakanda_ai.wakander.application.service;

import academy.wakanda.wakanda_ai.financeiro.application.api.FiadorDTO;
import academy.wakanda.wakanda_ai.jornadawakander.domain.JornadaWakanda;
import academy.wakanda.wakanda_ai.wakander.application.api.*;
import academy.wakanda.wakanda_ai.wakander.domain.StatusCadastro;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import academy.wakanda.wakanda_ai.wakander.infra.WakanderEstudo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WakanderService {
    @Deprecated
    WakanderCriadoResponse matriculaWakander(WakanderNovoRequest wakanderNovo);
    WakanderDetalhadoResponse buscaWakanderPorId(UUID idWakander);
    void regularizaWakander(UUID idWakander);
    Wakander buscaWakanderPorIdMemberKit(String idMemberKit);
    void salvaWakander(Wakander wakander);
	List<WakanderInativoResponse> buscaWakandersInativos(LocalDateTime datainatividade);
    List<WakanderEstudo>buscaWakandersSemEstudar(LocalDateTime dataLimite);
    void atualizaProgressoParaJornada(UUID idWakander, JornadaWakanda jornadaWakanda);
    void solicitaCancelamentoWakander(Wakander wakander);
    void editaWakander(UUID idWakander, WakanderAlteracaoRequest wakanderAlteracao);
    void cancelaAssinatura(UUID idWakander, WakanderCancelaAssinaturaDTO wakanderCancelaAssinatura);
    Wakander buscaWakanderPorIdAssinatura(String idAssinatura);
    void completaCadastroWakander(String token, WakanderCadastroCompleto wakander, Wakander wakander1);
    void reverteCancelamento(UUID idWaaknder, WakanderCancelaAssinaturaDTO desistiDoCancelamento);
    WakanderComDadosPessoaisOcultoResponse buscaWakanderPorIdRetornaDadosOcultos(String token);
    void atualizaDadosWakander(WakanderCadastroCompleto wakander);
    void postaWakanderComDadosAsaasIncompletoNaFila();
    void buscaDadosAsaas(Wakander wakander);
    Optional<ModelAndView> retornaFormularioCadastroCompleto(Wakander wakander);    Page<WakanderResponseDashboardDTO> buscaTodosOsWakanderComPaginacao(Pageable pageable, String busca, Boolean incluiCancelados);
    EstatisticaWakanderDTO buscaEstatisticasWakanders(Boolean incluiCancelados);
    Page<WakanderResponseDashboardDTO> buscaPorStatus(Pageable pageable, StatusCadastro statusCadastro, boolean incluiCancelados);
    void atualizaStatusCadastro();
    void solicitaEnvioDeFormularioDadosComplementares();
    void atualizaFiador(String token, FiadorDTO fiadorDTO);
    String geraLinkAtualizacaoFiador(UUID idWakander);
    void iniciaOnboardingManual(UUID idWakander);
    Page<WakanderPaginadoResponse> buscarWakanders(WakanderPaginadoRequest request, Pageable pageable);
}