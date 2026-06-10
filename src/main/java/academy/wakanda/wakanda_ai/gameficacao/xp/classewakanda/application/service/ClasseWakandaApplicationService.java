package academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.application.api.AtualizaRequisitosRequest;
import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.application.api.ClasseWakandaRequest;
import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.application.api.ClasseWakandaResponse;
import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.application.repository.ClasseWakandaRepository;
import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.domain.ClasseWakanda;
import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.domain.ClasseWakandaStatus;
import academy.wakanda.wakanda_ai.handler.APIException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class ClasseWakandaApplicationService implements ClasseWakandaService {

	private final ClasseWakandaRepository classeWakandaRepository;
	private final ValidadorMissoesClasse validadorMissoesClasse;

	@Override
	public ClasseWakandaResponse criaNovaClasse(ClasseWakandaRequest request) {
		log.info("[start] ClasseWakandaApplicationService - criaNovaClasse");
		validaNomeEDescricaoUnicos(request.getNome(), request.getDescricao());
		validaNivelNecessarioUnico(request.getNivelNecessario());
		validadorMissoesClasse.valida(request.getMissoesNecessarias());
		Integer ordemClasse = calculaProximaOrdemClasse();
		ClasseWakanda novaClasse = new ClasseWakanda(request, ordemClasse);
		classeWakandaRepository.salva(novaClasse);
		log.debug("[finish] ClasseWakandaApplicationService - criaNovaClasse");
		return new ClasseWakandaResponse(novaClasse);
	}

	@Override
	public ClasseWakandaResponse buscaClassePorId(UUID idClasse) {
		log.info("[start] ClasseWakandaApplicationService - buscaClassePorId");
		ClasseWakanda classeWakanda = classeWakandaRepository.buscaClassePorId(idClasse);
		log.debug("[finish] ClasseWakandaApplicationService - buscaClassePorId");
		return new ClasseWakandaResponse(classeWakanda);
	}

	@Override
	public List<ClasseWakandaResponse> buscaClassesAtivas() {
		log.info("[start] ClasseWakandaApplicationService - buscaClassesAtivas");
		List<ClasseWakanda> classesAtivas = classeWakandaRepository.buscaPorStatus(ClasseWakandaStatus.ATIVA);
		List<ClasseWakandaResponse> response = classesAtivas.stream().map(ClasseWakandaResponse::new).toList();
		log.info("[auditoria] buscaClassesAtivas - totalClassesRetornadas={}", response.size());
		log.debug("[finish] ClasseWakandaApplicationService - buscaClassesAtivas");
		return response;
	}

	private void validaNomeEDescricaoUnicos(String nome, String descricao) {
		boolean nomeDuplicado = classeWakandaRepository.verificaSeExistePorNome(nome);
		boolean descricaoDuplicada = classeWakandaRepository.verificaSeExistePorDescricao(descricao);
		if (nomeDuplicado || descricaoDuplicada) {
			throw APIException.build(HttpStatus.CONFLICT, mensagemDuplicidade(nomeDuplicado, descricaoDuplicada));
		}
	}

	private String mensagemDuplicidade(boolean nomeDuplicado, boolean descricaoDuplicada) {
		if (nomeDuplicado && descricaoDuplicada) {
			return "Já existe uma classe com esse nome e essa descrição!";
		}
		return nomeDuplicado ? "Já existe uma classe com esse nome!" : "Já existe uma classe com essa descrição!";
	}

	private void validaNivelNecessarioUnico(Integer nivelNecessario) {
		boolean nivelDuplicado = classeWakandaRepository.verificaSeExistePorNivelNecessario(nivelNecessario);
		if (nivelDuplicado) {
			throw APIException.build(HttpStatus.CONFLICT, "Já existe uma classe com esse nível necessário!");
		}
	}

	private Integer calculaProximaOrdemClasse() {
        return classeWakandaRepository.buscaMaiorOrdemClasse().orElse(0) + 1;
	}

	@Override
	public void atualizaRequisitosClasse(AtualizaRequisitosRequest atualizaRequisitosRequest, UUID idClasse) {
		log.info("[start] ClasseWakandaApplicationService - atualizaRequisitosClasse");
		ClasseWakanda classeWakanda = classeWakandaRepository.buscaClassePorId(idClasse);
		validaNomeEDescricaoUnicos(atualizaRequisitosRequest.getNome(), atualizaRequisitosRequest.getDescricao());
		if (atualizaRequisitosRequest.getNivelNecessario() != null) {
			validaNivelNecessarioUnico(atualizaRequisitosRequest.getNivelNecessario());
		}
		classeWakanda.atualizaParcialmente(atualizaRequisitosRequest, validadorMissoesClasse);
		classeWakandaRepository.salva(classeWakanda);
		log.debug("[finish] ClasseWakandaApplicationService - atualizaRequisitosClasse");
	}
}
