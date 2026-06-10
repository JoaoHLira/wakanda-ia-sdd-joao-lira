package academy.wakanda.wakanda_ai.gameficacao.catalogo.tipomissao.application.service;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.tipomissao.application.api.TipoMissaoRequest;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.tipomissao.application.api.TipoMissaoResponse;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.tipomissao.datahelper.TipoMissaoDataHelper;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.tipomissao.domain.TipoMissao;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static jakarta.validation.Validation.buildDefaultValidatorFactory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TipoMissaoApplicationServiceTest {

    @Mock
    private TipoMissaoRepository tipoMissaoRepository;

    @InjectMocks
    private TipoMissaoApplicationService tipoMissaoApplicationService;

    ValidatorFactory factory = buildDefaultValidatorFactory();

    @Test
    @DisplayName("Deve inserir um Tipo de Missao com sucesso")
    void deveInserirTipoMissaoComSucesso() {
        TipoMissaoRequest tipoMissaoRequest = TipoMissaoDataHelper.criarTipoMissaoRequestValido();
        TipoMissao tipoMissao = new TipoMissao(tipoMissaoRequest);

        when(tipoMissaoRepository.save(any(TipoMissao.class))).thenReturn(tipoMissao);

        TipoMissaoResponse response = tipoMissaoApplicationService.insereTipoMissao(tipoMissaoRequest);

        verify(tipoMissaoRepository, times(1)).save(any(TipoMissao.class));
        assertEquals(tipoMissao.getIdTipoMissao(), response.getIdTipoMissao());
        assertEquals(tipoMissao.getDescricao(), response.getDescricao());
    }

    @Test
    @DisplayName("Não deve inserir Tipo de Missão sem descrição")
    void naoDeveInserirTipoMissaoSemDescricao() {
        Validator validator = factory.getValidator();

        TipoMissaoRequest tipoMissaoRequest = TipoMissaoDataHelper.criarTipoMissaoRequestSemDescricao();

        var constraintViolations = validator.validate(tipoMissaoRequest);
        assertFalse(constraintViolations.isEmpty(), "Deveria haver violação pois a descrição do Tipo de Missão não foi informada");
        verifyNoInteractions(tipoMissaoRepository);
    }

}
