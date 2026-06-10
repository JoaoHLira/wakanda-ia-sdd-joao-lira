package academy.wakanda.wakanda_ai.wakander.infra;

import academy.wakanda.wakanda_ai.jornadawakander.domain.JornadaWakanda;
import academy.wakanda.wakanda_ai.wakander.domain.StatusCadastro;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import academy.wakanda.wakanda_ai.wakander.domain.WakanderStatusFinanceiro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WakanderSpringDataJpaRepository extends JpaRepository<Wakander, UUID>, JpaSpecificationExecutor<Wakander> {

    Optional<Wakander> findByIdMemberKit(String idMemberKit);

    @Query("SELECT w FROM Wakander w WHERE w.ultimaAulaAssistida.dateTime < :dataLimite AND w.financeiro.status = :wakanderStatusFinanceiro AND w.jornadaAtual = :jornadaWakanda")
    List<Wakander> buscaWakandersSemEstudar(LocalDateTime dataLimite, WakanderStatusFinanceiro wakanderStatusFinanceiro, JornadaWakanda jornadaWakanda);

    @Query("SELECT w FROM Wakander w WHERE w.fiador.idAssinatura = :idAssinatura")
    Optional<Wakander> findByIdAssinatura(String idAssinatura);

    @Query("SELECT w FROM Wakander w WHERE ((w.fiador.nome IS NULL OR w.fiador.nome = '') OR (w.fiador.cpf IS NULL OR w.fiador.cpf = '') OR (w.fiador.telefone IS NULL OR w.fiador.telefone = '') OR (w.fiador.idAsaas IS NULL OR w.fiador.idAsaas = '') OR (w.fiador.idAssinatura IS NULL OR w.fiador.idAssinatura = '')) AND w.statusCadastro = 'INCOMPLETO' AND w.financeiro.status = 'REGULAR'")
    List<Wakander> buscaWakanderComDadosAsaasIncompleto();

    @Query("SELECT w FROM Wakander w WHERE (w.cpf LIKE CONCAT(:busca, '%') OR w.contato.whatsapp LIKE CONCAT('%', :busca, '%') OR LOWER(w.nome) LIKE LOWER(CONCAT(:busca, '%'))) AND (:incluiCancelados = true OR w.financeiro.status = 'REGULAR')")
    Page<Wakander> buscaPorQueryCpfOuTelefone(Pageable pageable, @Param("busca") String busca, @Param("incluiCancelados") boolean incluiCancelados);

    @Query("SELECT w FROM Wakander w WHERE w.statusCadastro = :statusCadastro AND (:incluiCancelados = true OR w.financeiro.status = 'REGULAR')")
    Page<Wakander> findByStatusCadastro(Pageable pageable, @Param("statusCadastro") StatusCadastro statusCadastro, @Param("incluiCancelados") boolean incluiCancelados);

    @Query("SELECT w FROM Wakander w WHERE w.financeiro.status = :status")
    Page<Wakander> buscaWakanderRegularesPaginado(Pageable pageable, @Param("status") WakanderStatusFinanceiro wakanderStatusFinanceiro);

    @Query("SELECT w FROM Wakander w WHERE w.financeiro.status = :status")
    List<Wakander> buscaWakanderRegulares(@Param("status") WakanderStatusFinanceiro status);

    @Query("SELECT w FROM Wakander w WHERE w.contato.email = :email")
    Optional<Wakander> findByEmail(String email);

    @Query("SELECT w FROM Wakander w WHERE (w.financeiro.status = 'REGULAR') AND ((w.nome IS NULL OR w.nome = '') OR (w.cpf IS NULL OR w.cpf = '') OR (w.contato.whatsapp IS NULL OR w.contato.whatsapp = '') OR (w.contato.email IS NULL OR w.contato.email = '') OR (w.dataNascimento IS NULL))")
    List<Wakander> buscarWakandersComDadosIncompletos();

    @Query("SELECT w FROM Wakander w WHERE w.financeiro.status = 'REGULAR' AND NOT EXISTS (SELECT 1 FROM ProgressoWakander p WHERE p.idWakander = w.idWakander)")
    List<Wakander> buscaWakandersRegularesSemProgresso();

    @Query("SELECT w FROM Wakander w WHERE w.financeiro.status <> 'REGULAR'")
    List<Wakander> buscaWakandersIrregulares();

    @Query("SELECT w FROM Wakander w WHERE w.financeiro.status = 'REGULAR' AND EXISTS (SELECT 1 FROM ProgressoWakander p WHERE p.idWakander = w.idWakander)")
    List<Wakander> buscaWakandersRegularesComProgresso();
}
