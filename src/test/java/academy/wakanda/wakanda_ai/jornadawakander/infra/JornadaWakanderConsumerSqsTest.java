package academy.wakanda.wakanda_ai.jornadawakander.infra;

import academy.wakanda.wakanda_ai.comunicacao.infra.SqsMessageDto;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.memberkit.MemberKitMessageEnvelope;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.memberkit.MemberKitRequestProcessor;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.memberkit.MemberKitTipoRequisicao;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.memberkit.processadores.MemberKitCadastraNovoMembroProcessador;
import academy.wakanda.wakanda_ai.utils.DataHelper;
import academy.wakanda.wakanda_ai.wakander.application.service.WakanderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JornadaWakanderConsumerSqsTest {
    @InjectMocks
    private JornadaWakanderConsumerSqs jornadaWakanderConsumerSqs;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private WakanderRepository wakanderRepository;
    @Mock
    private JornadaWakanderClient jornadaWakanderClient;
    @Mock
    private MemberKitRequestProcessor memberKitRequestProcessor;
    @Mock
    private MemberKitCadastraNovoMembroProcessador novoMembroProcessador;

    @BeforeEach
    void setUp() {
        List<MemberKitRequestProcessor> processadores = List.of(memberKitRequestProcessor, novoMembroProcessador);
        ReflectionTestUtils.setField(jornadaWakanderConsumerSqs, "processors", processadores);
    }

    @Test
    void deveConsumirMensagemSqsComSucesso() throws JsonProcessingException {
        SqsMessageDto sqsMessageDto = DataHelper.criaMensagemDTO();
//        Wakander wakander = Fixture.from(Wakander.class).gimme(WAKANDER);
        MemberKitMessageEnvelope envelope = DataHelper.criaEnvelopeDoMemberKit(MemberKitTipoRequisicao.CADASTRO_NOVO_MEMBRO);

        when(objectMapper.readValue(sqsMessageDto.getMessage(), MemberKitMessageEnvelope.class)).thenReturn(envelope);
//        when(wakanderRepository.buscaWakanderPorId(wakander.getIdWakander())).thenReturn(wakander);
//        when(jornadaWakanderClient.requisicaoPostParaOMemberKit(any(), any())).thenReturn(DataHelper.criaMemberkitUserDto());
//        when(wakanderRepository.save(wakander)).thenReturn(wakander);

        jornadaWakanderConsumerSqs.consomeMensagemMemberkitRequests(sqsMessageDto);

//        verify(wakanderRepository, times(1)).buscaWakanderPorId(wakander.getIdWakander());
//        verify(jornadaWakanderClient, times(1)).requisicaoPostParaOMemberKit(any(), any());
//        verify(wakanderRepository, times(1)).save(wakander);
    }
}
