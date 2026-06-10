package academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.application.service;

import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.application.api.MissaoConcluidaResponse;
import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.application.api.MissaoProgressoRequest;
import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.application.api.MissaoProgressoResponse;
import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.application.api.MissaoDisponibilidadeResponse;
import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.service.ProgressoWakanderEventDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface MissaoProgressoService {
    void concluiMissao(ProgressoWakanderEventDto progressoWakander);

    void concluiMissaoManualmente(UUID idMissaoProgresso);

    MissaoProgressoResponse criaProgressoDeMissao(MissaoProgressoRequest missaoProgressoRequest);

    void reavaliaMissoesDisponiveis(UUID idWakander);

    List<MissaoDisponibilidadeResponse> listaDisponibilidadeMissoes(UUID idWakander, UUID idJornada);

    Page<MissaoConcluidaResponse> listaMissoesConcluidas(UUID idProgressoWakander, Pageable pageable);
}
