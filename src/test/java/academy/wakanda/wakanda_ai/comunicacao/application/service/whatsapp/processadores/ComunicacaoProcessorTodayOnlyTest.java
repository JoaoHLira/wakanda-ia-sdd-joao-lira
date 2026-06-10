package academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.processadores;

import academy.wakanda.wakanda_ai.comunicacao.application.api.MensagemRequest;
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

import static academy.wakanda.wakanda_ai.comunicacao.datahelper.ComunicacaoDataHelper.criaWhatsAppMessageDataDiferente;
import static academy.wakanda.wakanda_ai.comunicacao.datahelper.ComunicacaoDataHelper.criaWhatsAppMessageDto;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComunicacaoProcessorTodayOnlyTest {

    @InjectMocks
    private ComunicacaoProcessorTodayOnly processorTodayOnly;

    @Mock
    private ComunicacaoService comunicacaoService;

    @Test
    @DisplayName("Deve retornar true quando tipo da mensagem for TODAY_ONLY")
    void deveRetornarTrueQuandoTipoDaMensagemForTODAY_ONLY() {
        boolean resultado = processorTodayOnly.validaSeProcessaMensagem(ZApiEventype.TODAY_ONLY_MESSAGE);
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve retornar false quando tipo da mensagem não for TODAY_ONLY")
    void deveRetornarFalseQuandoTipoDaMensagemNaoForTODAY_ONLY() {
        boolean resultado = processorTodayOnly.validaSeProcessaMensagem(ZApiEventype.NORMAL_MESSAGE);
        assertFalse(resultado);
    }

    @Test
    @DisplayName("Deve enviar mensagem quando a data for hoje")
    void deveProcessarEnvioDaMensagemQuandoDataForHoje() {
        ZApiEventDto message = criaWhatsAppMessageDto();
        doNothing().when(comunicacaoService).enviaMensagemWhatsapp(any(MensagemRequest.class));

        processorTodayOnly.processaEnvioDaMensagem(message);

        verify(comunicacaoService, times(1)).enviaMensagemWhatsapp(any(MensagemRequest.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando a data não for hoje")
    void deveLancarExcecaoQuandoDataNaoForHoje() {
        ZApiEventDto message = criaWhatsAppMessageDataDiferente();

        APIException excecao = assertThrows(APIException.class,
                () -> processorTodayOnly.processaEnvioDaMensagem(message));

        assertEquals("Data do evento não é hoje!", excecao.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, excecao.getStatusException());
        verify(comunicacaoService, never()).enviaMensagemWhatsapp(any());
    }
}