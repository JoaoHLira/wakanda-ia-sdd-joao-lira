package academy.wakanda.wakanda_ai.comunicacao.infra;

import academy.wakanda.wakanda_ai.comunicacao.application.api.ClintContatoRequest;
import academy.wakanda.wakanda_ai.comunicacao.application.service.ComunicacaoService;
import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ComunicacaoWhatsappService;
import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventDto;
import academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp.ZApiEventype;
import academy.wakanda.wakanda_ai.comunicacao.datahelper.ComunicacaoDataHelper;
import academy.wakanda.wakanda_ai.handler.APIException;
import academy.wakanda.wakanda_ai.utils.DataHelper;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComunicacaoConsumerSqsTest {

    @InjectMocks
    private ComunicacaoConsumerSqs consumerSqs;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ComunicacaoWhatsappService comunicacaoWhatsappService;

    @Mock
    private ComunicacaoService comunicacaoService;

    private SqsMessageDto sqsMessageDto;
    private ZApiEventDto ZApiEventDto;

    @BeforeEach
    void setUp() {
        sqsMessageDto = DataHelper.criaMensagemDTO();

        ZApiEventDto = new ZApiEventDto(
                ZApiEventype.NORMAL_MESSAGE,
                "5573988000000",
                "Olá!"
        );
    }

    @Test
    @DisplayName("Deve consumir mensagem da fila SQS e processar corretamente")
    void deveConsumirMensagemDaFilaSqsComSucesso() throws JsonProcessingException {
        when(objectMapper.readValue(sqsMessageDto.getMessage(), ZApiEventDto.class))
                .thenReturn(ZApiEventDto);

        consumerSqs.consomeQueueDoWhatsapp(sqsMessageDto);

        verify(comunicacaoWhatsappService, times(1)).processaPorTipoMensagem(ZApiEventDto);
    }

    @Test
    @DisplayName("Deve lançar exceção quando ocorrer erro na desserialização do JSON")
    void deveLancarExcecaoQuandoErroDesserializacaoJson() throws JsonProcessingException {
        when(objectMapper.readValue(sqsMessageDto.getMessage(), ZApiEventDto.class))
                .thenThrow(new JsonProcessingException("Erro de parsing") {
                });

        APIException exception = assertThrows(APIException.class,
                () -> consumerSqs.consomeQueueDoWhatsapp(sqsMessageDto));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusException());
        assertEquals("[error] Não foi possível mapear o json!", exception.getMessage());
        verify(comunicacaoWhatsappService, never()).processaPorTipoMensagem(any());
    }

    @Test
    @DisplayName("Deve consumir mensagem da fila SQS da Clint e processar contato com sucesso")
    void consomeQueueDaClintContato() throws JsonProcessingException {
        ClintContatoRequest clintContatoRequest = ComunicacaoDataHelper.criaClintContatoRequest();
        when(objectMapper.readValue(sqsMessageDto.getMessage(), ClintContatoRequest.class))
                .thenReturn(clintContatoRequest);

        consumerSqs.consomeQueueDaClintContato(sqsMessageDto);

        verify(comunicacaoService, times(1))
                .enviaContatoParaClint(clintContatoRequest);
    }

    @Test
    @DisplayName("Deve enviar mensagem de falha quando não cancelado na Clint (DLQ)")
    void deveEnviarMensagemDeFalhaQuandoNaoCanceladoNaClint_DLQ() throws Exception {
        String mensagem = "qualquer-coisa";
        SqsMessageDto sqsMessageDto = new SqsMessageDto(
                "id-teste",
                "topic-teste",
                mensagem,
                Instant.now());
        Wakander wakander = DataHelper.criaWakander();
        when(objectMapper.readValue(anyString(), eq(Wakander.class))).thenReturn(wakander);

        consumerSqs.consomeQueueClintDlq(sqsMessageDto);

        verify(comunicacaoService, times(1))
                .enviaMsgCancelamentoClint(wakander, false);
    }
}
