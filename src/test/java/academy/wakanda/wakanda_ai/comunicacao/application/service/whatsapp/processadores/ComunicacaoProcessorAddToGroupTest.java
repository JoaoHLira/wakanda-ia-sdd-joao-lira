package academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.processadores;

import academy.wakanda.wakanda_ai.comunicacao.application.api.ZAPIPayloadAdiconaAoGrupo;
import academy.wakanda.wakanda_ai.comunicacao.application.service.ComunicacaoService;
import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventDto;
import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventype;
import academy.wakanda.wakanda_ai.handler.APIException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static academy.wakanda.wakanda_ai.comunicacao.datahelper.ComunicacaoDataHelper.criaWhatsAppMessageDto;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComunicacaoProcessorAddToGroupTest {

    @InjectMocks
    private ComunicacaoProcessorAddToGroup processorAddToGroup;

    @Mock
    private ComunicacaoService comunicacaoService;

    @DisplayName("Deve Retornar True Quando Tipo da Mensagem For ADD_TO_GROUP")
    @Test
    void deveRetornarTrueQuandoTipoDaMensagemForADD_TO_GROUP() {
        boolean resultado = processorAddToGroup.validaSeProcessaMensagem(ZApiEventype.ADD_TO_GROUP);
        assertTrue(resultado);
    }

    @DisplayName("Deve Retornar False Quando Tipo da Mensagem Não For ADD_TO_GROUP")
    @Test
    void deveRetornarFalseQuandoTipoDaMensagemNaoForADD_TO_GROUP() {
        boolean resultado = processorAddToGroup.validaSeProcessaMensagem(ZApiEventype.NORMAL_MESSAGE);
        assertFalse(resultado);
    }

    @DisplayName("Deve Processar o Envio da Mensagem com Sucesso Quando Dados Corretos")
    @Test
    void processaEnvioDaMensagem_Sucesso() {

        ZApiEventDto ZApiEventDto = criaWhatsAppMessageDto();

        doNothing().when(comunicacaoService).adicionaWakanderAoGrupo(any(ZAPIPayloadAdiconaAoGrupo.class));

        processorAddToGroup.processaEnvioDaMensagem(ZApiEventDto);

        verify(comunicacaoService, times(1)).adicionaWakanderAoGrupo(any(ZAPIPayloadAdiconaAoGrupo.class));
    }

    @DisplayName("Deve Lançar Exceção Quando Falha ao Adicionar Wakander ao Grupo")
    @Test
    void processaEnvioDaMensagem_FalhaAoAdicionarAoGrupo() {
        ZApiEventDto ZApiEventDto = criaWhatsAppMessageDto();

        doThrow(APIException.build(HttpStatus.BAD_REQUEST, "Erro ao adicionar Wakander ao grupo"))
                .when(comunicacaoService)
                .adicionaWakanderAoGrupo(any(ZAPIPayloadAdiconaAoGrupo.class));

        assertThrows(APIException.class, () -> {
            processorAddToGroup.processaEnvioDaMensagem(ZApiEventDto);
        });
    }
}