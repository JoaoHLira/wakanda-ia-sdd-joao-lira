package academy.wakanda.wakanda_ai.wakander.application.service;

import academy.wakanda.wakanda_ai.wakander.application.api.WakanderPaginadoRequest;
import academy.wakanda.wakanda_ai.wakander.domain.StatusCadastro;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import academy.wakanda.wakanda_ai.wakander.domain.WakanderStatusFinanceiro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WakanderRepository {
    Wakander save(Wakander wakanderCriado);

    List<Wakander> saveAll(List<Wakander> wakanders);

    Wakander buscaWakanderPorId(UUID idWakander);

    Wakander buscaWakanderPorIdMemberKit(String idMemberKit);

    List<Wakander> buscaWakandersSemEstudar(LocalDateTime dataLimite);

    Optional<Wakander> buscaWakanderPorIdAssinatura(String idAssinatura);

    List<Wakander> buscaWakandersSemDadosAsaasCompleto();

    Page<Wakander> buscaTodosWakandersPaginado(Pageable pageable);

    List<Wakander> buscaTodosWakanders();

    Page<Wakander> buscaPorQueryCpfTelefoneOuNome(Pageable pageable, String busca, boolean incluiCancelados);

    Page<Wakander> buscaWakandersPorStatus(Pageable pageable, StatusCadastro statusCadastro, boolean incluiCancelados);

    List<Wakander> buscaWakanderPorStatusFinanceiro(WakanderStatusFinanceiro statusFinanceiro);

    Page<Wakander> buscaWakandersRegularesPaginado(Pageable pageable);

    Wakander buscaWakanderPorEmail(String email);

    List<Wakander> buscaWakanderComDadosPessoaisIncompletos();

    List<Wakander> buscaWakandersRegularesSemProgresso();

    List<Wakander> buscaWakandersIrregulares();

    List<Wakander> buscaWakandersRegularesComProgresso();

    Page<Wakander> buscarWakanders(WakanderPaginadoRequest request, Pageable pageable);
}
