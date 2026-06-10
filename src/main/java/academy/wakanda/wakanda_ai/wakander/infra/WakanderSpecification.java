package academy.wakanda.wakanda_ai.wakander.infra;

import academy.wakanda.wakanda_ai.wakander.application.api.WakanderPaginadoRequest;
import academy.wakanda.wakanda_ai.wakander.domain.StatusCadastro;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import academy.wakanda.wakanda_ai.wakander.domain.WakanderStatusFinanceiro;
import org.springframework.data.jpa.domain.Specification;

public class WakanderSpecification {

    private WakanderSpecification() {
    }

    public static Specification<Wakander> filtrarPor(WakanderPaginadoRequest filtros) {
        return Specification.where(nomeContem(filtros.getNome()))
                .and(cpfIgual(filtros.getCpf()))
                .and(statusCadastroIgual(filtros.getStatusCadastro()))
                .and(statusFinanceiroIgual(filtros.getStatusFinanceiro()))
                .and(emailIgual(filtros.getEmail()))
                .and(telefoneIgual(filtros.getTelefone()));
    }

    public static Specification<Wakander> nomeContem(String nome) {
        if (nome == null || nome.isBlank()) {
            return null;
        }

        return (root, query, cb) ->
                cb.like(
                        cb.lower(root.get("nome")),
                        nome.toLowerCase() + "%"
                );
    }

    public static Specification<Wakander> cpfIgual(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            return null;
        }

        return (root, query, cb) ->
                cb.equal(root.get("cpf"), cpf);
    }

    public static Specification<Wakander> statusCadastroIgual(StatusCadastro statusCadastro) {
        if (statusCadastro == null) {
            return null;
        }

        return (root, query, cb) ->
                cb.equal(root.get("statusCadastro"), statusCadastro);
    }

    public static Specification<Wakander> statusFinanceiroIgual(WakanderStatusFinanceiro statusFinanceiro) {
        if (statusFinanceiro == null) {
            return null;
        }

        return (root, query, cb) ->
                cb.equal(root.get("financeiro").get("status"), statusFinanceiro);
    }

    public static Specification<Wakander> emailIgual(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }

        return (root, query, cb) ->
                cb.equal(root.get("contato").get("email"), email);
    }

    public static Specification<Wakander> telefoneIgual(String telefone) {
        if (telefone == null || telefone.isBlank()) {
            return null;
        }

        return (root, query, cb) ->
                cb.equal(root.get("contato").get("whatsapp"), telefone);
    }
}
