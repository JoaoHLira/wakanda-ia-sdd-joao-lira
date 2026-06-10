package academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.processadores;

import academy.wakanda.wakanda_ai.comunicacao.application.api.MensagemRequest;
import academy.wakanda.wakanda_ai.comunicacao.application.service.ComunicacaoService;
import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventDto;
import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventype;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static academy.wakanda.wakanda_ai.comunicacao.datahelper.ComunicacaoDataHelper.criaWhatsAppMessageDto;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComunicacaoProcessorNormalTest {

    @InjectMocks
    private ComunicacaoProcessorNormal processorNormal;

    @Mock
    private ComunicacaoService comunicacaoService;

    @Test
    @DisplayName("Deve retornar true quando tipo da mensagem for NORMAL")
    void deveRetornarTrueQuandoTipoDaMensagemForNORMAL() {
        boolean resultado = processorNormal.validaSeProcessaMensagem(ZApiEventype.NORMAL_MESSAGE);
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve retornar false quando tipo da mensagem não for NORMAL")
    void deveRetornarFalseQuandoTipoDaMensagemNaoForNORMAL() {
        boolean resultado = processorNormal.validaSeProcessaMensagem(ZApiEventype.TODAY_ONLY_MESSAGE);
        assertFalse(resultado);
    }

    @Test
    @DisplayName("Deve enviar mensagem para o número correto via comunicacaoService")
    void deveProcessarEnvioDaMensagem() {
        ZApiEventDto message = criaWhatsAppMessageDto();
        doNothing().when(comunicacaoService).enviaMensagemWhatsapp(any(MensagemRequest.class));

        processorNormal.processaEnvioDaMensagem(message);

        verify(comunicacaoService, times(1)).enviaMensagemWhatsapp(any(MensagemRequest.class));
    }
}