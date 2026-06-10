package academy.wakanda.wakanda_ai.wakander.infra;

import academy.wakanda.wakanda_ai.handler.APIException;
import academy.wakanda.wakanda_ai.jornadawakander.domain.JornadaWakanda;
import academy.wakanda.wakanda_ai.wakander.application.api.WakanderPaginadoRequest;
import academy.wakanda.wakanda_ai.wakander.application.service.WakanderRepository;
import academy.wakanda.wakanda_ai.wakander.domain.StatusCadastro;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import academy.wakanda.wakanda_ai.wakander.domain.WakanderStatusFinanceiro;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Log4j2
public class WakanderInfraRepository implements WakanderRepository {
    private final WakanderSpringDataJpaRepository wakanderSpringRepository;

    @Override
    public Wakander save(Wakander wakanderCriado) {
        log.info("[start] WakanderInfraRepository - save");
        try {
            wakanderSpringRepository.save(wakanderCriado);
        } catch (DataIntegrityViolationException ex) {
            throw APIException.build(HttpStatus.CONFLICT, "Existem dados duplicados");
        }
        log.debug("[finish] WakanderInfraRepository - save");
        return wakanderCriado;
    }

    @Override
    public List<Wakander> saveAll(List<Wakander> wakanders) {
        log.info("[start] WakanderInfraRepository - saveAll");
        List<Wakander> listaSalva = wakanderSpringRepository.saveAll(wakanders);
        log.debug("[finish] WakanderInfraRepository - saveAll");
        return listaSalva;
    }

    @Override
    public Wakander buscaWakanderPorId(UUID idWakander) {
        log.info("[start] WakanderInfraRepository - buscaWakanderPorId");
        Wakander wakander = wakanderSpringRepository.findById(idWakander)
                .orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Wakander não encontrado!"));
        log.debug("[finish] WakanderInfraRepository - buscaWakanderPorId");
        return wakander;
    }

    @Override
    public Wakander buscaWakanderPorIdMemberKit(String idMemberKit) {
        log.info("[start] WakanderInfraRepository - buscaWakanderPorIdMemberKit");
        Wakander wakander = wakanderSpringRepository.findByIdMemberKit(idMemberKit)
                .orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Não há Wakander cadastrado com esse ID MemberKit!"));
        log.debug("[finish] WakanderInfraRepository - buscaWakanderPorIdMemberKit");
        return wakander;
    }

    @Override
    public List<Wakander> buscaWakandersSemEstudar(LocalDateTime dataLimite) {
        log.info("[start] WakanderInfraRepository - buscaWakandersSemEstudar");
        List<Wakander> wakanders = wakanderSpringRepository.buscaWakandersSemEstudar(dataLimite, WakanderStatusFinanceiro.REGULAR, JornadaWakanda.JORNADA_CONHECIMENTO);
        log.debug("[finish] WakanderInfraRepository - buscaWakandersSemEstudar");
        return wakanders;
    }

    @Override
    public Optional<Wakander> buscaWakanderPorIdAssinatura(String idAssinatura) {
        log.info("[start] WakanderInfraRepository - buscaWakanderPorIdAssinatura");
        Optional<Wakander> wakander = wakanderSpringRepository.findByIdAssinatura(idAssinatura);
        log.debug("[finish] WakanderInfraRepository - buscaWakanderPorIdAssinatura");
        return wakander;
    }

    @Override
    public List<Wakander> buscaWakandersSemDadosAsaasCompleto() {
        log.info("[start] WakanderInfraRepository - buscaWakandersSemDadosAsaasCompleto");
        List<Wakander> wakanderList = wakanderSpringRepository.buscaWakanderComDadosAsaasIncompleto();
        log.debug("[finish] WakanderInfraRepository - buscaWakandersSemDadosAsaasCompleto");
        return wakanderList;
    }

    @Override
    public Page<Wakander> buscaTodosWakandersPaginado(Pageable pageable) {
        log.info("[start] WakanderInfraRepository - buscaTodosWakandersPaginado");
        Page<Wakander> wakanders = wakanderSpringRepository.findAll(pageable);
        log.debug("[finish] WakanderInfraRepository - buscaTodosWakandersPaginado");
        return wakanders;
    }

    @Override
    public List<Wakander> buscaTodosWakanders() {
        log.info("[start] WakanderInfraRepository - buscaTodosWakanders");
        List<Wakander> wakanderList = wakanderSpringRepository.findAll();
        log.debug("[finish] WakanderInfraRepository - buscaTodosWakanders");
        return wakanderList;
    }

    @Override
    public Page<Wakander> buscaPorQueryCpfTelefoneOuNome(Pageable pageable, String busca, boolean incluiCancelados) {
        log.info("[start] WakanderInfraRepository - buscaPorQueryCpf");
        Page<Wakander> wakanderList = wakanderSpringRepository.buscaPorQueryCpfOuTelefone(pageable, busca, incluiCancelados);
        log.debug("[finish] WakanderInfraRepository - buscaPorQueryCpf");
        return wakanderList;
    }

    @Override
    public Page<Wakander> buscaWakandersPorStatus(Pageable pageable, StatusCadastro statusCadastro, boolean incluiCancelados) {
        log.info("[start] WakanderInfraRepository - buscaWakandersPorStatus");
        Page<Wakander> listWakanders = wakanderSpringRepository.findByStatusCadastro(pageable, statusCadastro, incluiCancelados);
        log.debug("[finish] WakanderInfraRepository - buscaWakandersPorStatus");
        return listWakanders;
    }

    @Override
    public List<Wakander> buscaWakanderPorStatusFinanceiro(WakanderStatusFinanceiro statusFinanceiro) {
        log.info("[start] WakanderInfraRepository - buscaTodosWakanderRegulares");
        List<Wakander> wakanderList = wakanderSpringRepository.buscaWakanderRegulares(statusFinanceiro);
        log.debug("[finish] WakanderInfraRepository - buscaTodosWakanderRegulares");
        return wakanderList;
    }

    @Override
    public Page<Wakander> buscaWakandersRegularesPaginado(Pageable pageable) {
        log.info("[start] WakanderInfraRepository - buscaWakandersRegularesPaginado");
        Page<Wakander> wakanderList = wakanderSpringRepository.buscaWakanderRegularesPaginado(pageable, WakanderStatusFinanceiro.REGULAR);
        log.debug("[finish] WakanderInfraRepository - buscaWakandersRegularesPaginado");
        return wakanderList;
    }

    @Override
    public Wakander buscaWakanderPorEmail(String email) {
        log.info("[start] WakanderInfraRepository - buscaWakanderPorEmail");
        Wakander wakander = wakanderSpringRepository.findByEmail(email)
                .orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Email invalido!"));
        log.debug("[finish] WakanderInfraRepository - buscaWakanderPorEmail");
        return wakander;
    }

    @Override
    public List<Wakander> buscaWakanderComDadosPessoaisIncompletos() {
        log.info("[start] WakanderInfraRepository - buscaWakanderComDadosPessoaisIncompletos");
        List<Wakander> buscaWakandersComDadosIncompleto = wakanderSpringRepository.buscarWakandersComDadosIncompletos();
        log.debug("[finish] WakanderInfraRepository - buscaWakanderComDadosPessoaisIncompletos");
        return buscaWakandersComDadosIncompleto;
    }

    @Override
    public List<Wakander> buscaWakandersRegularesSemProgresso() {
        log.info("[start] WakanderInfraRepository - buscaWakandersRegularesSemProgresso");
        List<Wakander> regularesSemProgresso = wakanderSpringRepository.buscaWakandersRegularesSemProgresso();
        log.debug("[finish] WakanderInfraRepository - buscaWakandersRegularesSemProgresso");
        return regularesSemProgresso;
    }

    @Override
    public List<Wakander> buscaWakandersIrregulares() {
        log.info("[start] WakanderInfraRepository - buscaWakandersIrregulares");
        List<Wakander> irregulares = wakanderSpringRepository.buscaWakandersIrregulares();
        log.debug("[finish] WakanderInfraRepository - buscaWakandersIrregulares");
        return irregulares;
    }

    @Override
    public List<Wakander> buscaWakandersRegularesComProgresso() {
        log.info("[start] WakanderInfraRepository - buscaWakandersRegularesComProgresso");
        List<Wakander> regularesComProgresso = wakanderSpringRepository.buscaWakandersRegularesComProgresso();
        log.debug("[finish] WakanderInfraRepository - buscaWakandersRegularesComProgresso");
        return regularesComProgresso;
    }

    @Override
    public Page<Wakander> buscarWakanders(WakanderPaginadoRequest filtros, Pageable pageable) {
        log.info("[start] WakanderInfraRepository - buscarWakanders");
        Page<Wakander> wakanders = wakanderSpringRepository.findAll(WakanderSpecification.filtrarPor(filtros), pageable);
        log.debug("[finish] WakanderInfraRepository - buscarWakanders");
        return wakanders;
    }
}
