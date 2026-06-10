package academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.application.service;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.datahelper.MissaoWakandaDataHelper;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.MissaoStatus;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.MissaoWakanda;
import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.application.api.MissaoDisponibilidadeResponse;
import academy.wakanda.wakanda_ai.gameficacao.progresso.missaoprogresso.application.api.MissaoDisponibilidadeStatus;
import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.application.repository.ClasseWakandaRepository;
import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.datahelper.ClasseWakandaDataHelper;
import academy.wakanda.wakanda_ai.gameficacao.xp.classewakanda.domain.ClasseWakanda;
import academy.wakanda.wakanda_ai.gameficacao.xp.historicoclasse.application.repository.HistoricoClasseWakanderRepository;
import academy.wakanda.wakanda_ai.gameficacao.xp.historicoclasse.datahelper.HistoricoClasseWakanderDataHelper;
import academy.wakanda.wakanda_ai.gameficacao.xp.historicoclasse.domain.HistoricoClasseWakander;
import academy.wakanda.wakanda_ai.handler.APIException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MissaoDisponibilidadeServiceTest {

    @InjectMocks
    private MissaoDisponibilidadeService missaoDisponibilidadeService;

    @Mock
    private HistoricoClasseWakanderRepository historicoClasseWakanderRepository;

    @Mock
    private ClasseWakandaRepository classeWakandaRepository;

    @Test
    @DisplayName("Deve buscar a classe atual do Wakander a partir do histórico em andamento")
    void deveBuscarClasseAtualDoWakander() {
        UUID idWakander = UUID.randomUUID();
        UUID idClasseAtual = UUID.randomUUID();

        HistoricoClasseWakander historicoAtual = HistoricoClasseWakanderDataHelper
                .criarHistoricoClasseWakanderEmAndamento(UUID.randomUUID(), idClasseAtual,
                        UUID.randomUUID(), UUID.randomUUID(), idWakander);
        ClasseWakanda classeAtual = ClasseWakandaDataHelper
                .criarClasseWakandaClasseAtual(idClasseAtual, UUID.randomUUID(), UUID.randomUUID());

        when(historicoClasseWakanderRepository.buscaOptionalClasseAtualPorWakander(idWakander)).thenReturn(Optional.of(historicoAtual));
        when(classeWakandaRepository.buscaClassePorId(idClasseAtual)).thenReturn(classeAtual);

        ClasseWakanda resposta = missaoDisponibilidadeService.buscaClasseAtual(idWakander);

        assertEquals(idClasseAtual, resposta.getIdClasse());
    }

    @Test
    @DisplayName("Deve retornar missão como LIBERADA quando a classe mínima for atendida")
    void deveRetornarMissaoLiberadaQuandoClasseMinimaForAtendida() {
        UUID idClasseAtual = UUID.randomUUID();
        UUID idClasseMinima = UUID.randomUUID();
        UUID idJornada = UUID.randomUUID();

        ClasseWakanda classeAtual = ClasseWakandaDataHelper
                .criarClasseWakandaProximaClasse(idClasseAtual, UUID.randomUUID(), UUID.randomUUID());
        ClasseWakanda classeMinima = ClasseWakandaDataHelper
                .criarClasseWakandaClasseAtual(idClasseMinima, UUID.randomUUID(), UUID.randomUUID());
        MissaoWakanda missao = MissaoWakandaDataHelper.criaMissaoWakandaAtivaComUUID(UUID.randomUUID(), idJornada);
        ReflectionTestUtils.setField(missao, "idClasseMinima", idClasseMinima);

        when(classeWakandaRepository.buscaClassePorId(idClasseMinima)).thenReturn(classeMinima);

        MissaoDisponibilidadeResponse resposta = missaoDisponibilidadeService.montaDisponibilidadeMissao(missao,
                classeAtual, false, new HashMap<>());

        assertEquals(MissaoDisponibilidadeStatus.LIBERADA, resposta.getStatusDisponibilidade());
    }

    @Test
    @DisplayName("Deve retornar missão INATIVA como BLOQUEADA mesmo com progresso")
    void deveRetornarMissaoInativaComoBloqueadaMesmoComProgresso() {
        ClasseWakanda classeAtual = ClasseWakandaDataHelper
                .criarClasseWakandaClasseAtual(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        MissaoWakanda missao = MissaoWakandaDataHelper.criaMissaoWakandaAtivaComUUID(UUID.randomUUID(),
                UUID.randomUUID());
        ReflectionTestUtils.setField(missao, "missaoStatus", MissaoStatus.INATIVA);

        MissaoDisponibilidadeResponse resposta = missaoDisponibilidadeService.montaDisponibilidadeMissao(missao,
                classeAtual, true, new HashMap<>());

        assertEquals(MissaoDisponibilidadeStatus.BLOQUEADA, resposta.getStatusDisponibilidade());
        verify(classeWakandaRepository, never()).buscaClassePorId(any());
    }

    @Test
    @DisplayName("Deve impedir execução quando a classe atual for inferior à mínima da missão")
    void deveImpedirExecucaoQuandoClasseAtualForInferiorAMinima() {
        UUID idClasseAtual = UUID.randomUUID();
        UUID idClasseMinima = UUID.randomUUID();
        UUID idJornada = UUID.randomUUID();

        ClasseWakanda classeAtual = ClasseWakandaDataHelper
                .criarClasseWakandaClasseAtual(idClasseAtual, UUID.randomUUID(), UUID.randomUUID());
        ClasseWakanda classeMinima = ClasseWakandaDataHelper
                .criarClasseWakandaProximaClasse(idClasseMinima, UUID.randomUUID(), UUID.randomUUID());
        MissaoWakanda missao = MissaoWakandaDataHelper.criaMissaoWakandaAtivaComUUID(UUID.randomUUID(), idJornada);
        ReflectionTestUtils.setField(missao, "idClasseMinima", idClasseMinima);

        Map<UUID, ClasseWakanda> cacheClassesMinimas = new HashMap<>();
        when(classeWakandaRepository.buscaClassePorId(idClasseMinima)).thenReturn(classeMinima);

        APIException exception = assertThrows(APIException.class, () ->
                missaoDisponibilidadeService.validaMissaoLiberadaParaClasseAtual(missao, classeAtual,
                        cacheClassesMinimas));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusException());
        assertEquals("Missão não está liberada para a classe atual", exception.getMessage());
    }
}
