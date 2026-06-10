package academy.wakanda.wakanda_ai.gameficacao.xp.xpwakander.application.service;

import academy.wakanda.wakanda_ai.gameficacao.common.dto.XpPromocaoClasseDTO;
import academy.wakanda.wakanda_ai.gameficacao.common.dto.XpWakanderEventDTO;
import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.application.service.MissaoProgressoRepository;
import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.application.service.MissaoProgressoService;
import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.datahelper.MissaoProgressoDataHelper;
import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.domain.MissaoProgresso;
import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.service.ProgressoGameficacaoRepository;
import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.dataHelper.ProgressoWakanderDataHelper;
import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.domain.ProgressoWakander;
import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.application.repository.ClasseWakandaRepository;
import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.datahelper.ClasseWakandaDataHelper;
import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.domain.ClasseWakanda;
import academy.wakanda.wakanda_ai.gameficacao.xp.historicoclasse.application.repository.HistoricoClasseWakanderRepository;
import academy.wakanda.wakanda_ai.gameficacao.xp.historicoclasse.datahelper.HistoricoClasseWakanderDataHelper;
import academy.wakanda.wakanda_ai.gameficacao.xp.historicoclasse.domain.HistoricoClasseWakander;
import academy.wakanda.wakanda_ai.gameficacao.xp.historicoclasse.domain.HistoricoClasseWakanderStatus;
import academy.wakanda.wakanda_ai.gameficacao.xp.xpwakander.application.event.XpPromocaoClasseEvent;
import academy.wakanda.wakanda_ai.gameficacao.xp.xpwakander.datahelper.XpPromocaoClasseDTODataHelper;
import academy.wakanda.wakanda_ai.gameficacao.xp.xpwakander.datahelper.XpWakanderDataHelper;
import academy.wakanda.wakanda_ai.gameficacao.xp.xpwakander.domain.XpWakander;
import academy.wakanda.wakanda_ai.handler.APIException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class XpWakanderApplicationServiceTest {

    @InjectMocks
    private XpWakanderApplicationService xpWakanderApplicationService;

    @Mock
    private XpWakanderRepository xpWakanderRepository;

    @Mock
    private ProgressoGameficacaoRepository progressoGameficacaoRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private MissaoProgressoRepository missaoProgressoRepository;

    @Mock
    private HistoricoClasseWakanderRepository historicoClasseWakanderRepository;

    @Mock
    private ClasseWakandaRepository classeWakandaRepository;

    @Mock
    private XpPromocaoClasseService xpPromocaoClasseService;

    @Mock
    private MissaoProgressoService missaoProgressoService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deve subir de nível ao receber XP suficiente")
    void deveSubirDeNivelComXpSuficiente() {
        ProgressoWakander progresso = ProgressoWakanderDataHelper.getProgressoWakanderIniciante();
        XpWakander wakander = XpWakanderDataHelper.criarXpWakanderComXp(progresso.getIdProgressoWakander(), 34, 35);

        when(progressoGameficacaoRepository.buscaProgressoPorId(progresso.getIdProgressoWakander()))
                .thenReturn(progresso);
        when(xpWakanderRepository.buscaPorIdProgressoWakander(progresso.getIdProgressoWakander()))
                .thenReturn(wakander);

        XpWakanderEventDTO xpWakander = new XpWakanderEventDTO(progresso.getIdProgressoWakander(), 1);

        xpWakanderApplicationService.processaXP(xpWakander);

        assertEquals(2, wakander.getNivelAtual());
        assertEquals(0, wakander.getXpTotal());
        verify(xpWakanderRepository, times(1)).salva(any(XpWakander.class));
        verify(eventPublisher, times(1)).publishEvent(any(XpPromocaoClasseEvent.class));
    }

    @Test
    @DisplayName("Não deve subir de nível com XP insuficiente")
    void naoDeveSubirDeNivelComXpInsuficiente() {
        ProgressoWakander progresso = ProgressoWakanderDataHelper.getProgressoWakanderIniciante();
        XpWakander wakander = XpWakanderDataHelper.criarXpWakanderComXp(progresso.getIdProgressoWakander(), 33, 35);

        when(progressoGameficacaoRepository.buscaProgressoPorId(progresso.getIdProgressoWakander()))
                .thenReturn(progresso);
        when(xpWakanderRepository.buscaPorIdProgressoWakander(progresso.getIdProgressoWakander()))
                .thenReturn(wakander);

        XpWakanderEventDTO xpWakander = new XpWakanderEventDTO(progresso.getIdProgressoWakander(), 1);

        xpWakanderApplicationService.processaXP(xpWakander);

        assertEquals(1, wakander.getNivelAtual());
        assertEquals(34, wakander.getXpTotal());
        verify(xpWakanderRepository).salva(wakander);
        verify(eventPublisher, times(1)).publishEvent(any(XpPromocaoClasseEvent.class));
    }

    @Test
    @DisplayName("Deve criar novo XpWakander com valores default se não existir")
    void deveCriarNovoXpWakanderSeNaoExistir() {
        ProgressoWakander progresso = ProgressoWakanderDataHelper.getProgressoWakanderIniciante();

        when(progressoGameficacaoRepository.buscaProgressoPorId(progresso.getIdProgressoWakander()))
                .thenReturn(progresso);
        when(xpWakanderRepository.buscaPorIdProgressoWakander(progresso.getIdProgressoWakander()))
                .thenThrow(APIException.build(HttpStatus.NOT_FOUND,
                        "Progressão de Xp do Wakander não encontrado"));
        ClasseWakanda classeInicial = ClasseWakandaDataHelper.criarClasseWakanda(ClasseWakandaDataHelper.criarClasseWakandaRequestValida(), 0);
        when(classeWakandaRepository.buscaOptionalProximaClassePorOrdem(0))
                .thenReturn(Optional.of(classeInicial));

        XpWakanderEventDTO xpWakander = new XpWakanderEventDTO(progresso.getIdProgressoWakander(), 25);

        xpWakanderApplicationService.processaXP(xpWakander);

        ArgumentCaptor<XpWakander> captor = ArgumentCaptor.forClass(XpWakander.class);
        verify(xpWakanderRepository).salva(captor.capture());

        XpWakander novo = captor.getValue();
        assertEquals(1, novo.getNivelAtual());
        assertEquals(25, novo.getXpTotal());
        assertEquals(progresso.getIdProgressoWakander(), novo.getIdProgressoWakander());
        verify(xpWakanderRepository).salva(novo);
        verify(eventPublisher, times(1)).publishEvent(any(XpPromocaoClasseEvent.class));
    }

    @Test
    @DisplayName("Deve processar promoção de classe quando todas as validações passam")
    void deveProcessarPromocaoClasseComSucesso() {
        // Arrange
        UUID idProgressoWakander = UUID.randomUUID();
        UUID idWakander = UUID.randomUUID();
        UUID idHistoricoClasse = UUID.randomUUID();
        UUID idClasseAtual = UUID.randomUUID();
        UUID idXpWakander = UUID.randomUUID();
        UUID idProximaClasse = UUID.randomUUID();
        UUID idMissao1 = UUID.randomUUID();
        UUID idMissao2 = UUID.randomUUID();

        XpPromocaoClasseDTO dto = XpPromocaoClasseDTODataHelper.criarXpPromocaoClasseDTO(
                idXpWakander, idProgressoWakander, idWakander, 5);
        HistoricoClasseWakander historicoAtual = HistoricoClasseWakanderDataHelper.criarHistoricoClasseWakanderEmAndamento(
                idHistoricoClasse, idClasseAtual, idXpWakander, idProgressoWakander, idWakander);
        ClasseWakanda classeAtual = ClasseWakandaDataHelper.criarClasseWakandaClasseAtual(idClasseAtual, idMissao1, idMissao2);
        ClasseWakanda proximaClasse = ClasseWakandaDataHelper.criarClasseWakandaProximaClasse(idProximaClasse, idMissao1, idMissao2);
        XpWakander xpWakander = XpWakanderDataHelper.criarXpWakanderComXp(idProgressoWakander, 100, 5);
        MissaoProgresso missao1 = MissaoProgressoDataHelper.criarMissaoProgressoConcluidaComUUID(idMissao1, idProgressoWakander);
        MissaoProgresso missao2 = MissaoProgressoDataHelper.criarMissaoProgressoConcluidaComUUID(idMissao2, idProgressoWakander);
        when(historicoClasseWakanderRepository.buscaOptionalHistoricoClasseAtual(dto)).thenReturn(Optional.of(historicoAtual));
        when(classeWakandaRepository.buscaClassePorId(idClasseAtual)).thenReturn(classeAtual);
        when(missaoProgressoRepository.buscaMissoesProgressoPorIdsMissoesEProgresso(List.of(idMissao1, idMissao2), idProgressoWakander)).thenReturn(List.of(missao1, missao2));
        when(classeWakandaRepository.buscaOptionalProximaClassePorOrdem(1)).thenReturn(Optional.of(proximaClasse));
        when(xpWakanderRepository.buscaPorIdProgressoWakander(idProgressoWakander)).thenReturn(xpWakander);
        configuraMockConcluiClasseAtualAndIniciaProximaClasse(dto);

        when(xpPromocaoClasseService.validaPromocaoClasse(any(), any(), any(), any(), any())).thenReturn(true);

        // Act
        xpWakanderApplicationService.processaPromocaoClasse(dto);

        // Assert
        verify(historicoClasseWakanderRepository, times(1)).salvaHistoricoClasseWakander(historicoAtual);
        verify(missaoProgressoService, times(1)).reavaliaMissoesDisponiveis(idWakander);
        assertEquals(HistoricoClasseWakanderStatus.CONCLUIDA, historicoAtual.getStatus());
        assertNotNull(historicoAtual.getDataFim());

        ArgumentCaptor<HistoricoClasseWakander> historicoCaptor = ArgumentCaptor.forClass(HistoricoClasseWakander.class);
        verify(historicoClasseWakanderRepository, times(2)).salvaHistoricoClasseWakander(historicoCaptor.capture());

        List<HistoricoClasseWakander> historicosSalvos = historicoCaptor.getAllValues();
        HistoricoClasseWakander novoHistorico = historicosSalvos.get(1);
        assertEquals(idProximaClasse, novoHistorico.getIdClasse());
        assertEquals(HistoricoClasseWakanderStatus.EM_ANDAMENTO, novoHistorico.getStatus());
    }

    private void configuraMockConcluiClasseAtualAndIniciaProximaClasse(XpPromocaoClasseDTO dto) {
        doAnswer(invocation -> {
            HistoricoClasseWakander hist = invocation.getArgument(1);
            ClasseWakanda proxClasse = invocation.getArgument(2);
            hist.concluiClasseAtual();
            historicoClasseWakanderRepository.salvaHistoricoClasseWakander(hist);
            HistoricoClasseWakander novoHistorico = new HistoricoClasseWakander(proxClasse, dto);
            historicoClasseWakanderRepository.salvaHistoricoClasseWakander(novoHistorico);
            return null;
        }).when(xpPromocaoClasseService).concluiClasseAtualAndIniciaProximaClasse(any(), any(), any());
    }

    @Test
    @DisplayName("Não deve processar promoção de classe quando nível é insuficiente")
    void naoDeveProcessarPromocaoClasseQuandoNivelInsuficiente() {
        // Arrange
        UUID idProgressoWakander = UUID.randomUUID();
        UUID idWakander = UUID.randomUUID();
        UUID idHistoricoClasse = UUID.randomUUID();
        UUID idClasseAtual = UUID.randomUUID();
        UUID idXpWakander = UUID.randomUUID();
        UUID idProximaClasse = UUID.randomUUID();
        UUID idMissao1 = UUID.randomUUID();
        UUID idMissao2 = UUID.randomUUID();

        XpPromocaoClasseDTO dto = XpPromocaoClasseDTODataHelper.criarXpPromocaoClasseDTO(
                idXpWakander, idProgressoWakander, idWakander, 2);
        HistoricoClasseWakander historicoAtual = HistoricoClasseWakanderDataHelper.criarHistoricoClasseWakanderEmAndamento(
                idHistoricoClasse, idClasseAtual, idXpWakander, idProgressoWakander, idWakander);
        ClasseWakanda classeAtual = ClasseWakandaDataHelper.criarClasseWakandaClasseAtual(idClasseAtual, idMissao1, idMissao2);
        ClasseWakanda proximaClasse = ClasseWakandaDataHelper.criarClasseWakandaProximaClasse(idProximaClasse, idMissao1, idMissao2);
        XpWakander xpWakander = XpWakanderDataHelper.criarXpWakanderComXp(idProgressoWakander, 100, 2);
        MissaoProgresso missao1 = MissaoProgressoDataHelper.criarMissaoProgressoConcluidaComUUID(idMissao1, idProgressoWakander);
        MissaoProgresso missao2 = MissaoProgressoDataHelper.criarMissaoProgressoConcluidaComUUID(idMissao2, idProgressoWakander);

        when(historicoClasseWakanderRepository.buscaOptionalHistoricoClasseAtual(dto)).thenReturn(Optional.of(historicoAtual));
        when(classeWakandaRepository.buscaClassePorId(idClasseAtual)).thenReturn(classeAtual);
        when(missaoProgressoRepository.buscaMissoesProgressoPorIdsMissoesEProgresso(List.of(idMissao1, idMissao2), idProgressoWakander)).thenReturn(List.of(missao1, missao2));
        when(classeWakandaRepository.buscaOptionalProximaClassePorOrdem(1)).thenReturn(Optional.of(proximaClasse));
        when(xpWakanderRepository.buscaPorIdProgressoWakander(idProgressoWakander)).thenReturn(xpWakander);
        
        // Mock para simular falha na validação de nível
        when(xpPromocaoClasseService.validaPromocaoClasse(any(), any(), any(), any(), any())).thenReturn(false);

        // Act 
        xpWakanderApplicationService.processaPromocaoClasse(dto);
        
        verify(historicoClasseWakanderRepository, never()).salvaHistoricoClasseWakander(any());
    }

}
