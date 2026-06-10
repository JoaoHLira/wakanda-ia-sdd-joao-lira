package academy.wakanda.wakanda_ai.gameficacao.progresso.jornadaprogresso.application.service;

import academy.wakanda.wakanda_ai.comunicacao.infra.PublicadorNotificacaoSns;
import academy.wakanda.wakanda_ai.constants.TopicNames;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.application.service.JornadaWakandaRepository;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.datahelper.JornadaWakandaDataHelper;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.domain.JornadaWakanda;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.repository.MissaoWakandaRepository;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.datahelper.MissaoWakandaDataHelper;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.MissaoStatus;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.MissaoWakanda;
import academy.wakanda.wakanda_ai.gameficacao.common.dto.XpWakanderEventDTO;
import academy.wakanda.wakanda_ai.gameficacao.progresso.jornadaprogresso.datahelper.JornadaProgressoDataHelper;
import academy.wakanda.wakanda_ai.gameficacao.progresso.jornadaprogresso.domain.JornadaProgresso;
import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.application.service.MissaoProgressoRepository;
import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.datahelper.MissaoProgressoDataHelper;
import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.domain.MissaoProgresso;
import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.service.ProgressoWakanderEventDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JornadaProgressoApplicationServiceTest {

    @InjectMocks
    private JornadaProgressoApplicationService progressoApplicationService;

    @Mock
    private JornadaProgressoRepository jornadaProgressoRepository;

    @Mock
    private PublicadorNotificacaoSns publicadorNotificacaoSns;

    @Mock
    private MissaoWakandaRepository missaoWakandaRepository;

    @Mock
    private JornadaWakandaRepository jornadaWakandaRepository;

    @Mock
    private MissaoProgressoRepository missaoProgressoRepository;

    @Mock
    private TopicNames topicNames;

    @Test
    @DisplayName("Deve concluir a jornada quando todas missões concluídas")
    void deveConcluirJornadaQuandoTodasMissoesConcluidas() {
        JornadaWakanda jornadaWakanda = JornadaWakandaDataHelper.criarJornadaValida();
        UUID idJornada = jornadaWakanda.getIdJornada();
        UUID idMissao1 = UUID.randomUUID();
        UUID idMissao2 = UUID.randomUUID();
        UUID idProgressoWakander = UUID.randomUUID();

        MissaoWakanda missao1 = MissaoWakandaDataHelper.criaMissaoWakandaAtivaComUUID(idMissao1, idJornada);
        MissaoWakanda missao2 = MissaoWakandaDataHelper.criaMissaoWakandaAtivaComUUID(idMissao2, idJornada);
        List<MissaoWakanda> missoes = Arrays.asList(missao1, missao2);

        JornadaProgresso jornadaProgresso = JornadaProgressoDataHelper.criarJornadaProgressoEmAndamento(idJornada, idProgressoWakander);

        MissaoProgresso missaoProgresso1 = MissaoProgressoDataHelper.criarMissaoProgressoConcluidaComUUID(idMissao1, idProgressoWakander);
        MissaoProgresso missaoProgresso2 = MissaoProgressoDataHelper.criarMissaoProgressoConcluidaComUUID(idMissao2, idProgressoWakander);
        List<MissaoProgresso> missoesProgresso = Arrays.asList(missaoProgresso1, missaoProgresso2);

        when(missaoWakandaRepository.buscaMissoesAtivasPorIdJornada(eq(idJornada), eq(MissaoStatus.ATIVA))).thenReturn(missoes);
        when(missaoProgressoRepository.buscaMissoesProgressoPorIdsMissoes(anyList())).thenReturn(missoesProgresso);
        when(jornadaProgressoRepository.buscaJornadaProgresso(eq(idJornada), eq(idProgressoWakander))).thenReturn(jornadaProgresso);
        when(topicNames.getProgressoWakanderRequests()).thenReturn("qualquer-topico");
        when(jornadaWakandaRepository.buscaJornadaId(idJornada)).thenReturn(jornadaWakanda);

        progressoApplicationService.concluiJornadaSeMissoesConcluidas(idJornada, idProgressoWakander);

        verify(publicadorNotificacaoSns).enviaNotificacaoSns(anyString(), any(ProgressoWakanderEventDto.class), eq("qualquer-topico"));
    }

    @Test
    @DisplayName("Deve concluir jornadaProgresso e publicar XpWakander")
    void deveConcluirJornadaProgressoEPublicarXpWakander() {
        JornadaProgresso jornadaProgresso = JornadaProgressoDataHelper.criarJornadaProgressoEmAndamento(UUID.randomUUID(), UUID.randomUUID());
        JornadaWakanda jornadaWakanda = JornadaWakandaDataHelper.criarJornadaValida();
        ProgressoWakanderEventDto eventDto = JornadaProgressoDataHelper.criarProgressoWakanderEventDto(jornadaProgresso);

        when(jornadaProgressoRepository.buscaJornadaProgressoPorId(any())).thenReturn(jornadaProgresso);
        when(jornadaWakandaRepository.buscaJornadaId(any())).thenReturn(jornadaWakanda);
        when(topicNames.getXpWakanderRequests()).thenReturn("xp-topico");
        doNothing().when(jornadaProgressoRepository).salvaJornadaProgresso(any(JornadaProgresso.class));

        progressoApplicationService.concluiJornadaProgresso(eventDto);

        verify(jornadaProgressoRepository, times(1)).salvaJornadaProgresso(any(JornadaProgresso.class));
        verify(publicadorNotificacaoSns).enviaNotificacaoSns(anyString(), any(XpWakanderEventDTO.class), eq("xp-topico"));
    }

    @Test
    @DisplayName("Deve ativar próxima jornada quando a atual for concluída")
    void deveAtivarProximaJornada() {
        UUID idProgressoWakander = UUID.randomUUID();
        UUID idTrilha = UUID.fromString("a330c529-c5d9-408c-b07a-e882365f23f2");

        JornadaWakanda jornadaAtual = JornadaWakandaDataHelper.criarJornadaComTrilhaEOrdem(idTrilha ,0);
        JornadaWakanda proximaJornada = JornadaWakandaDataHelper.criarProximaJornada(idTrilha ,0);


        when(jornadaWakandaRepository.buscaJornadasPorIdTrilha(idTrilha)).thenReturn(List.of(jornadaAtual, proximaJornada));
        doNothing().when(jornadaProgressoRepository).salvaJornadaProgresso(any(JornadaProgresso.class));

        progressoApplicationService.ativaProximaJornada(idProgressoWakander, idTrilha, jornadaAtual);

        verify(jornadaProgressoRepository).salvaJornadaProgresso(any(JornadaProgresso.class));
        verify(jornadaWakandaRepository).buscaJornadasPorIdTrilha(idTrilha);
        verify(jornadaProgressoRepository, times(1)).salvaJornadaProgresso(any(JornadaProgresso.class));
    }

}
