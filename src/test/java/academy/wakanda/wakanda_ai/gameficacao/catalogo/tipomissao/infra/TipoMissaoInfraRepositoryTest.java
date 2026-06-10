package academy.wakanda.wakanda_ai.gameficacao.catalogo.tipomissao.infra;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.tipomissao.datahelper.TipoMissaoDataHelper;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.tipomissao.domain.TipoMissao;
import academy.wakanda.wakanda_ai.handler.APIException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TipoMissaoInfraRepositoryTest {

    @Mock
    private TipoMissaoSpringDataJpaRepository tipoMissaoSpringDataJpaRepository;

    @InjectMocks
    private TipoMissaoInfraRepository tipoMissaoInfraRepository;

    @Test
    @DisplayName("Não deve salvar Tipo Missao com descrição duplicada")
    void naoDeveSalvarTipoMissaoComDescricaoDuplicada() {
        TipoMissao tipoMissao = new TipoMissao(TipoMissaoDataHelper.criarTipoMissaoRequestValido());
        when(tipoMissaoSpringDataJpaRepository.existsByDescricao(tipoMissao.getDescricao())).thenReturn(true);

        APIException exception = assertThrows(APIException.class, () -> {
            tipoMissaoInfraRepository.save(tipoMissao);
        });

        assertEquals(HttpStatus.CONFLICT, exception.getStatusException());
        assertEquals("Já existe outro Tipo de Missão com a mesma descrição.", exception.getMessage());
        verify(tipoMissaoSpringDataJpaRepository, times(1)).existsByDescricao(tipoMissao.getDescricao());
        verify(tipoMissaoSpringDataJpaRepository, never()).save(any(TipoMissao.class));
    }
}
