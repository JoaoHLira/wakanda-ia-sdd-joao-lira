package academy.wakanda.wakanda_ai.comunicacao.application.service;

import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ComunicacaoWhatsappApplicationService;
import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventDto;
import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventype;
import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.processadores.ComunicacaoProcessorWhatsapp;
import academy.wakanda.wakanda_ai.handler.APIException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComunicacaoWhatsappApplicationServiceTest {

    @InjectMocks
    private ComunicacaoWhatsappApplicationService comunicacaoWhatsappApplicationService;

    @Mock
    private ComunicacaoProcessorWhatsapp processorTodayOnly;

    @Mock
    private ComunicacaoProcessorWhatsapp processorNormal;

    @BeforeEach
    void setUp() {
        List<ComunicacaoProcessorWhatsapp> processors = List.of(processorTodayOnly, processorNormal);
        ReflectionTestUtils.setField(comunicacaoWhatsappApplicationService, "comunicacaoProcessorWhatsapp", processors);
    }

    @Test
    @DisplayName("Deve processar mensagem com tipo suportado")
    void deveProcessarMensagemComTipoSuportado() {
        ZApiEventDto message = new ZApiEventDto(ZApiEventype.TODAY_ONLY_MESSAGE, "5573912345678", "Mensagem teste");

        when(processorTodayOnly.validaSeProcessaMensagem(ZApiEventype.TODAY_ONLY_MESSAGE)).thenReturn(true);
        doNothing().when(processorTodayOnly).processaEnvioDaMensagem(message);

        comunicacaoWhatsappApplicationService.processaPorTipoMensagem(message);

        verify(processorTodayOnly, times(1)).validaSeProcessaMensagem(ZApiEventype.TODAY_ONLY_MESSAGE);
        verify(processorTodayOnly, times(1)).processaEnvioDaMensagem(message);
        verify(processorNormal, never()).validaSeProcessaMensagem(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nenhum processador for encontrado")
    void deveLancarExcecaoQuandoNenhumProcessadorEncontrado() {
        ZApiEventDto message = new ZApiEventDto(ZApiEventype.NORMAL_MESSAGE, "5573912345678", "Outra mensagem");

        when(processorTodayOnly.validaSeProcessaMensagem(ZApiEventype.NORMAL_MESSAGE)).thenReturn(false);
        when(processorNormal.validaSeProcessaMensagem(ZApiEventype.NORMAL_MESSAGE)).thenReturn(false);

        APIException exception = assertThrows(APIException.class, () ->
                comunicacaoWhatsappApplicationService.processaPorTipoMensagem(message));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusException());
        assertEquals("Tipo da mensagem nao é processada", exception.getMessage());

        verify(processorTodayOnly, times(1)).validaSeProcessaMensagem(ZApiEventype.NORMAL_MESSAGE);
        verify(processorNormal, times(1)).validaSeProcessaMensagem(ZApiEventype.NORMAL_MESSAGE);
        verifyNoMoreInteractions(processorTodayOnly, processorNormal);
    }
}