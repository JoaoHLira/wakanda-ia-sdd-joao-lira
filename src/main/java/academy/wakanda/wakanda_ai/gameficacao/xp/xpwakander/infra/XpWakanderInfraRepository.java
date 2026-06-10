package academy.wakanda.wakanda_ai.gameficacao.xp.xpwakander.infra;

import academy.wakanda.wakanda_ai.gameficacao.xp.xpwakander.application.service.XpWakanderRepository;
import academy.wakanda.wakanda_ai.gameficacao.xp.xpwakander.domain.XpWakander;
import academy.wakanda.wakanda_ai.handler.APIException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Log4j2
public class XpWakanderInfraRepository implements XpWakanderRepository {

	private final XpWakanderSpringDataRepository xpWakanderSpringDataRepository;

	@Override
	public XpWakander buscaPorIdProgressoWakander(UUID idProgressoWakander) {
		log.info("[start] XpWakanderInfraRepository - buscaPorIdProgressoWakander");
		XpWakander buscaProgressoWakander = xpWakanderSpringDataRepository
				.findByIdProgressoWakander(idProgressoWakander).orElseThrow(
						() -> APIException.build(HttpStatus.NOT_FOUND, "Progressão de Xp do Wakander não encontrado!"));
		log.debug("[finish] XpWakanderInfraRepository - buscaPorIdProgressoWakander");
		return buscaProgressoWakander;
	}

	@Override
	public void salva(XpWakander wakander) {
		log.info("[start] XpWakanderInfraRepository - salva");
		try {
			xpWakanderSpringDataRepository.save(wakander);
		} catch (Exception ex) {
			throw APIException.build(HttpStatus.BAD_REQUEST, "Erro ao salvar XP do Wakander, verifique os dados!");
		}
		log.debug("[finish] XpWakanderInfraRepository - salva");
	}

}
