package academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.infra;

import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.dataHelper.ProgressoWakanderDataHelper;
import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.domain.ProgressoWakander;
import academy.wakanda.wakanda_ai.wakander.application.service.WakanderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProgressoGameficacaoInfraRepositoryTest {

    @Mock
    private ProgressoGameficacaoSpringDataRepository springDataRepository;

    @Mock
    private WakanderRepository wakanderRepository;

    @InjectMocks
    private ProgressoGameficacaoInfraRepository infraRepository;

    private UUID idWakander;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        idWakander = ProgressoWakanderDataHelper.getIdWakander();
    }

    @Test
    void deveSalvarNovoProgresso() {
        ProgressoWakander progresso = ProgressoWakanderDataHelper.getProgressoWakanderIniciante();
        when(springDataRepository.save(any(ProgressoWakander.class))).thenReturn(progresso);

        ProgressoWakander salvo = infraRepository.novoProgresso(progresso);

        assertThat(salvo).isEqualTo(progresso);
        verify(springDataRepository).save(progresso);
    }

    @Test
    void deveBuscarProgressoPorIdQuandoExiste() {
        ProgressoWakander progresso = ProgressoWakanderDataHelper.getProgressoWakanderIniciante();

        when(springDataRepository.findByIdWakander(idWakander)).thenReturn(Optional.of(progresso));

        ProgressoWakander encontrado = infraRepository.buscaProgressoPorIdWakander(idWakander);
        assertThat(encontrado).isEqualTo(progresso);
        verify(springDataRepository).findByIdWakander(idWakander);
    }

    @Test
    void deveRetornarNullQuandoProgressoNaoExiste() {
        // Mock do método correto
        when(springDataRepository.findByIdWakander(idWakander)).thenReturn(Optional.empty());

        ProgressoWakander encontrado = infraRepository.buscaProgressoPorIdWakander(idWakander);

        assertThat(encontrado).isNull();

        // Verifica o método correto
        verify(springDataRepository).findByIdWakander(idWakander);
    }
}