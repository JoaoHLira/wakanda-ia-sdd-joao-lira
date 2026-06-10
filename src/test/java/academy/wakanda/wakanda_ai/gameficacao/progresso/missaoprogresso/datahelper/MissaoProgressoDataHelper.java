package academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.datahelper;

import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.application.api.MissaoProgressoRequest;
import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.domain.MissaoProgresso;
import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.domain.MissaoProgressoStatus;
import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.domain.SabedoriasMissaoProgresso;
import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.service.ProgressoWakanderEventDto;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

public class MissaoProgressoDataHelper {

    public static MissaoProgresso criarMissaoProgressoEmAndamento() {
        return new MissaoProgresso(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                MissaoProgressoStatus.EM_ANDAMENTO,
                1,
                0,
                null,
                null,
                new SabedoriasMissaoProgresso(0, 0, 0, 0, 0)
        );
    }

    public static MissaoProgresso criarMissaoProgressoConcluida() {
        return new MissaoProgresso(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                MissaoProgressoStatus.CONCLUIDA,
                100,
                1,
                null,
                null,
                new SabedoriasMissaoProgresso(10, 10, 10, 10, 10)
        );
    }

    public static ProgressoWakanderEventDto criarProgressoWakanderEventDto() {
        return ProgressoWakanderEventDto.onMissaoProgresso(
                UUID.randomUUID(),
                "12345"
        );
    }

    public static MissaoProgresso criarMissaoProgressoConcluidaComUUID(UUID idMissao, UUID idProgressoWakander) {
        return new MissaoProgresso(
                UUID.randomUUID(),
                idMissao,
                idProgressoWakander,
                MissaoProgressoStatus.CONCLUIDA,
                10,
                0,
                null,
                null,
                new SabedoriasMissaoProgresso(10, 10, 10, 10, 10)
        );
    }

    public static MissaoProgressoRequest criarMissaoProgressoRequest(UUID idMissao, UUID idWakander) {
        MissaoProgressoRequest request = new MissaoProgressoRequest();
        ReflectionTestUtils.setField(request, "idMissao", idMissao);
        ReflectionTestUtils.setField(request, "idWakander", idWakander);
        return request;
    }
}
