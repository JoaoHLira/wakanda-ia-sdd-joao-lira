package academy.wakanda.wakanda_ai.jornadawakander.application.service.memberkit.processadores;

import academy.wakanda.wakanda_ai.constants.MemberkitProperties;
import academy.wakanda.wakanda_ai.handler.APIException;
import academy.wakanda.wakanda_ai.jornadawakander.application.api.CadastraMembroRequest;
import academy.wakanda.wakanda_ai.jornadawakander.application.api.MemberkitUserDto;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.memberkit.MemberKitMessageEnvelope;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.memberkit.MemberKitTipoRequisicao;
import academy.wakanda.wakanda_ai.jornadawakander.infra.JornadaWakanderClient;
import academy.wakanda.wakanda_ai.utils.DataHelper;
import academy.wakanda.wakanda_ai.utils.templates.Templates;
import academy.wakanda.wakanda_ai.wakander.application.event.CadastroCompletoEvent;
import academy.wakanda.wakanda_ai.wakander.application.service.WakanderRepository;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import br.com.six2six.fixturefactory.Fixture;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;

import static academy.wakanda.wakanda_ai.utils.templates.WakanderTemplate.WAKANDER;
import static br.com.six2six.fixturefactory.loader.FixtureFactoryLoader.loadTemplates;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberKitCadastraNovoMembroProcessadorTest {

    @InjectMocks
    private MemberKitCadastraNovoMembroProcessador processador;

    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private WakanderRepository wakanderRepository;
    @Mock
    private JornadaWakanderClient jornadaWakanderClient;
    @Mock
    private MemberkitProperties memberkitProperties;

    @BeforeAll
    static void setUp() {
        loadTemplates(Templates.BASE_PACKAGE);
    }

    @Test
    @DisplayName("validaSeProcessa retorna true apenas para CADASTRO_NOVO_MEMBRO")
    void deveValidarTipoCorreto() {
        assertTrue(processador.validaSeProcessa(MemberKitTipoRequisicao.CADASTRO_NOVO_MEMBRO));
        assertFalse(processador.validaSeProcessa(MemberKitTipoRequisicao.ACESSO_BLOQUEADO));
    }

    @Test
    @DisplayName("processaEvento executa fluxo completo com sucesso")
    void processaEventoSucesso() throws JsonProcessingException {
        MemberKitMessageEnvelope envelope = DataHelper.criaEnvelopeDoMemberKit(MemberKitTipoRequisicao.CADASTRO_NOVO_MEMBRO);
        Wakander wakander = Fixture.from(Wakander.class).gimme(WAKANDER);
        CadastroCompletoEvent event = new CadastroCompletoEvent(wakander);

        when(objectMapper.treeToValue(envelope.getPayload(), CadastroCompletoEvent.class)).thenReturn(event);
        when(memberkitProperties.getMembershipLevelId()).thenReturn(42);
        when(memberkitProperties.getClassroomIds()).thenReturn(List.of(1, 2, 3));
        MemberkitUserDto userDto = DataHelper.criaMemberkitUserDto();
        when(jornadaWakanderClient.requisicaoPostParaOMemberKit(any(CadastraMembroRequest.class), eq(MemberkitUserDto.class)))
                .thenReturn(userDto);

        when(wakanderRepository.buscaWakanderPorId(wakander.getIdWakander())).thenReturn(wakander);

        processador.processaEvento(envelope);

        verify(objectMapper).treeToValue(eq(envelope.getPayload()), eq(CadastroCompletoEvent.class));
        verify(jornadaWakanderClient).requisicaoPostParaOMemberKit(any(CadastraMembroRequest.class), eq(MemberkitUserDto.class));
        verify(wakanderRepository).buscaWakanderPorId(wakander.getIdWakander());
        verify(wakanderRepository).save(wakander);
    }

    @Test
    @DisplayName("processaEvento lança APIException se payload inválido")
    void processaEventoPayloadInvalido() throws JsonProcessingException {
        MemberKitMessageEnvelope envelope = DataHelper.criaEnvelopeDoMemberKit(MemberKitTipoRequisicao.CADASTRO_NOVO_MEMBRO);

        when(objectMapper.treeToValue(eq(envelope.getPayload()), eq(CadastroCompletoEvent.class)))
                .thenThrow(new JsonProcessingException("bad payload") {});

        APIException ex = assertThrows(APIException.class, () -> processador.processaEvento(envelope));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusException());
        assertTrue(ex.getMessage().contains("Erro ao deserializar json!"));
    }
}
