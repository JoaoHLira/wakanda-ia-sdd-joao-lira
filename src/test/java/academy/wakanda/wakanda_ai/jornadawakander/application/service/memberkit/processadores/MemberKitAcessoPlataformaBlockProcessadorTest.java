package academy.wakanda.wakanda_ai.jornadawakander.application.service.memberkit.processadores;

import academy.wakanda.wakanda_ai.handler.APIException;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.memberkit.MemberKitMessageEnvelope;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.memberkit.MemberKitTipoRequisicao;
import academy.wakanda.wakanda_ai.jornadawakander.infra.JornadaWakanderClient;
import academy.wakanda.wakanda_ai.utils.DataHelper;
import academy.wakanda.wakanda_ai.wakander.application.event.AssinaturaCanceladaEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberKitAcessoPlataformaBlockProcessadorTest {

    @InjectMocks
    MemberKitAcessoPlataformaBlockProcessador processador;

    @Mock
    JornadaWakanderClient jornadaWakanderClient;
    @Mock
    ObjectMapper objectMapper;

    @Test
    void deveRetornarTrueSeForOMesmoTipoRequisicao() throws JsonProcessingException {
        MemberKitMessageEnvelope envelope = DataHelper.criaEnvelopeDoMemberKit(MemberKitTipoRequisicao.ACESSO_BLOQUEADO);
        boolean resultado = processador.validaSeProcessa(envelope.getTipo());
        assertTrue(resultado);
    }

    @Test
    void deveRetornarFalseQuandoTipoRequisicaoForDiferente() throws JsonProcessingException {
        MemberKitMessageEnvelope envelope = DataHelper.criaEnvelopeDoMemberKit(MemberKitTipoRequisicao.CADASTRO_NOVO_MEMBRO);
        boolean resultado = processador.validaSeProcessa(envelope.getTipo());
        assertFalse(resultado);
    }

    @Test
    void deveFazerRequisicaoParaOMemberKit() throws JsonProcessingException {
        MemberKitMessageEnvelope envelope = DataHelper.criaEnvelopeDoMemberKit(MemberKitTipoRequisicao.ACESSO_BLOQUEADO);
        AssinaturaCanceladaEvent event = DataHelper.criaAssinaturaCanceladaEvent();

        when(objectMapper.treeToValue(envelope.getPayload(), AssinaturaCanceladaEvent.class)).thenReturn(event);
        when(jornadaWakanderClient.requisicaoPostParaOMemberKit(any(), eq(Void.class))).thenReturn(null);
        processador.processaEvento(envelope);
    }

    @Test
    void lancaExcecaoQuandoDeserializacaoDaErrado() throws JsonProcessingException {
        JsonProcessingException erro = new JsonProcessingException("falha na desserialização") {
        };
        doThrow(erro).when(objectMapper).treeToValue(any(JsonNode.class), eq(AssinaturaCanceladaEvent.class));

        JsonNode payloadInvalido = new TextNode("não importa o conteúdo");
        MemberKitMessageEnvelope envelopeInvalido = new MemberKitMessageEnvelope(
                MemberKitTipoRequisicao.ACESSO_BLOQUEADO,
                payloadInvalido
        );

        APIException exception = assertThrows(APIException.class, () -> processador.processaEvento(envelopeInvalido));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusException());
        assertEquals("Erro ao deserializar json!", exception.getMessage());
    }
}