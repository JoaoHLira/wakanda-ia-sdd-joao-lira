package academy.wakanda.wakanda_ai.utils;

import academy.wakanda.wakanda_ai.comunicacao.application.api.MensagemRequest;
import academy.wakanda.wakanda_ai.comunicacao.application.api.NotificacaoRequest;
import academy.wakanda.wakanda_ai.comunicacao.application.api.ZAPIPayload;
import academy.wakanda.wakanda_ai.comunicacao.infra.SqsMessageDto;
import academy.wakanda.wakanda_ai.financeiro.application.api.AssinaturaAsaasDto;
import academy.wakanda.wakanda_ai.financeiro.application.api.CobrancaAsaasDto;
import academy.wakanda.wakanda_ai.financeiro.application.api.PaymentStatusRequest;
import academy.wakanda.wakanda_ai.financeiro.domain.assinatura.AssinaturaEvento;
import academy.wakanda.wakanda_ai.financeiro.domain.assinatura.AssinaturaType;
import academy.wakanda.wakanda_ai.financeiro.domain.assinatura.ClienteAsaasDto;
import academy.wakanda.wakanda_ai.financeiro.domain.cobranca.Cobranca;
import academy.wakanda.wakanda_ai.financeiro.domain.cobranca.CobrancaEventoType;
import academy.wakanda.wakanda_ai.financeiro.domain.cobranca.CobrancaStatus;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.dto.AulaMemberKitDTO;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.dto.LoginMemberkitDto;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.dto.LoginMemberkitDto.Data;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.Sabedorias;
import academy.wakanda.wakanda_ai.jornadawakander.application.api.MemberkitUserDto;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.memberkit.MemberKitAcessoRequest;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.memberkit.MemberKitMessageEnvelope;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.memberkit.MemberKitTipoRequisicao;
import academy.wakanda.wakanda_ai.jornadawakander.domain.AulaAssistida;
import academy.wakanda.wakanda_ai.jornadawakander.domain.JornadaWakanda;
import academy.wakanda.wakanda_ai.jornadawakander.domain.OnboardingWakander;
import academy.wakanda.wakanda_ai.autenticacao.application.api.UsuarioAdmTesteDto;
import academy.wakanda.wakanda_ai.autenticacao.application.api.UsuarioAdmCadastroRequest;
import academy.wakanda.wakanda_ai.autenticacao.domain.PerfilUsuario;
import academy.wakanda.wakanda_ai.autenticacao.domain.StatusUsuario;
import academy.wakanda.wakanda_ai.autenticacao.domain.UsuarioAdm;
import academy.wakanda.wakanda_ai.wakander.application.api.*;
import academy.wakanda.wakanda_ai.wakander.application.event.AssinaturaCanceladaEvent;
import academy.wakanda.wakanda_ai.wakander.domain.*;
import academy.wakanda.wakanda_ai.wakander.infra.WakanderEstudo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sns.core.SnsNotification;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

public class DataHelper {
    private static final String MENSAGEM = "Hello SNS!";

    public static Wakander criaWakander() {
        return new Wakander(criaWakanderNovoRequest());
    }

    public static UsuarioAdm criaUsuarioAdm(PerfilUsuario perfil) {
        return new UsuarioAdm(
                UUID.randomUUID(),
                "teste",
                "nao@verificado.com",
                0,
                StatusUsuario.ATIVO,
                "senhaEncriptada",
                perfil,
                LocalDateTime.now());
    }

    public static UsuarioAdmCadastroRequest criaUsuarioAdmCadastroRequest() {
        return UsuarioAdmCadastroRequest.builder()
                .nome("Nome Teste")
                .username("email@email.com")
                .senha("123456")
                .build();
    }

    public static Wakander criaWakanderSemContato() {
        WakanderNovoRequest wakanderNovoRequestSemContato = new WakanderNovoRequest(
                "Wakander Teste",
                "12345678901",
                LocalDate.of(1995, 5, 20),
                "KIT123456",
                null,
                "wakander@teste.com",
                "cus_G7Dvo4iphUNk",
                "sub_VXJBYgP2u0eO",
                "Maria Silva dos Santos",
                "98765432100",
                null
        );
        return new Wakander(wakanderNovoRequestSemContato);
    }

    public static WakanderNovoRequest criaWakanderNovoRequest() {
        return new WakanderNovoRequest(
                "Wakander Teste",
                "12345678901",
                LocalDate.of(1995, 5, 20),
                "KIT123456",
                "5511987654321",
                "wakander@teste.com",
                "cus_G7Dvo4iphUNk",
                "sub_VXJBYgP2u0eO",
                "Maria Silva dos Santos",
                "98765432100",
                "5511976543210"
        );
    }

    public static Wakander criaWakanderIncompleto() {
        return new Wakander(criaAssinaturaRequest(AssinaturaType.SUBSCRIPTION_CREATED), criaClienteAsaasDto());
    }

    public static Wakander criaWakanderStatusFinalizouComeceAqui() {
        Wakander wakander = new Wakander(criaAssinaturaRequest(AssinaturaType.SUBSCRIPTION_CREATED), criaClienteAsaasDto());
        wakander.atualizaProgresso(JornadaWakanda.FINALIZOU_COMECE_AQUI);
        return wakander;
    }

    public static Wakander criaWakanderCancelado() {
        Wakander wakanderCancelado = criaWakander();
        wakanderCancelado.mudaStatusFinanceiro(WakanderStatusFinanceiro.CANCELADO, LocalDateTime.now());
        return wakanderCancelado;
    }

    public static Wakander criaWakanderCancelamentoSolicitado() {
        Wakander wakanderCancelamentoSolicitado = criaWakander();
        wakanderCancelamentoSolicitado.mudaStatusFinanceiro(WakanderStatusFinanceiro.CANCELAMENTO_SOLICITADO, LocalDateTime.now());
        return wakanderCancelamentoSolicitado;
    }

    public static MensagemRequest whatsappNaoEhDoBrasil() {
        return new MensagemRequest("11999999999", "Numero não começa com (55)");
    }

    public static AulaAssistida criaAulaAssistidaJornadaConhecimento() {
        return new AulaAssistida(
                criaWakander().getIdWakander(),
                criaAulaMemberKitDTOJornadaConhecimento()
        );
    }

    public static AulaMemberKitDTO criaAulaMemberKitDTOJornadaConhecimento() {
        return new AulaMemberKitDTO(
                "lesson_status.saved",
                criaAulaMemberKitDataJornadaConhecimento()
        );
    }

    private static AulaMemberKitDTO.Data criaAulaMemberKitDataJornadaConhecimento() {
        return new AulaMemberKitDTO.Data(
                "id",
                100,
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                criaAulaMemberKitUser(),
                criaAulaMemberKitCourse(),
                criaAulaMemberKitJornadaConhecimento(),
                null,
                null
        );
    }

    private static AulaMemberKitDTO.Lesson criaAulaMemberKitJornadaConhecimento() {
        return new AulaMemberKitDTO.Lesson(101L, "slug", "title", 1, OffsetDateTime.now(), OffsetDateTime.now());
    }

    public static AulaAssistida criaAulaAssistida() {
        return new AulaAssistida(criaWakander().getIdWakander(), criaAulaMemberKitDTO());
    }

    public static AulaMemberKitDTO criaAulaMemberKitDTO() {
        return new AulaMemberKitDTO("lesson_status.saved", criaAulaMemberKitData());
    }

    public static AulaMemberKitDTO criaAulaMemberKitDTOAulaDesmarcada() {
        return new AulaMemberKitDTO("lesson_status.saved", criaAulaMemberKitDataCompletedAtNull());
    }

    private static AulaMemberKitDTO.Data criaAulaMemberKitDataCompletedAtNull() {
        return new AulaMemberKitDTO.Data("id", 100, null, OffsetDateTime.now(), OffsetDateTime.now(),
                criaAulaMemberKitUser(), criaAulaMemberKitCourse(), criaAulaMemberKitLesson(), null, null);
    }

    private static AulaMemberKitDTO.Data criaAulaMemberKitData() {
        return new AulaMemberKitDTO.Data("id", 100, OffsetDateTime.now(), OffsetDateTime.now(), OffsetDateTime.now(),
                criaAulaMemberKitUser(), criaAulaMemberKitCourse(), criaAulaMemberKitLesson(), null, null);
    }

    private static AulaMemberKitDTO.Lesson criaAulaMemberKitLesson() {
        return new AulaMemberKitDTO.Lesson(202L, "slug", "title", 1, OffsetDateTime.now(), OffsetDateTime.now());
    }

    private static AulaMemberKitDTO.Course criaAulaMemberKitCourse() {
        return new AulaMemberKitDTO.Course(101L, "Curso", 1, "Descrição", "url", "url", OffsetDateTime.now(),
                OffsetDateTime.now(), null);
    }

    private static AulaMemberKitDTO.User criaAulaMemberKitUser() {
        return new AulaMemberKitDTO.User("id", "Wakander", "email@email.com", 1,
                OffsetDateTime.now(), OffsetDateTime.now(), "url", null, OffsetDateTime.now(), OffsetDateTime.now());
    }

    public static SnsNotification<String> criaSnsNotification() {
        return SnsNotification.builder(MENSAGEM)
                .deduplicationId(UUID.randomUUID().toString()).groupId("teste-group").build();
    }

    public static NotificacaoRequest criaNotificatioRequest() {
        return new NotificacaoRequest("teste-grupo", MENSAGEM);
    }

    public static SqsMessageDto criaMensagemDTO() {
        return new SqsMessageDto("teste-id", "teste-topic.fifo",
                "{ \\\"idWakander\\\": \\\"\" + 61d1ecfb-9bac-4cfa-987d-b121a5f8f9a0 + \"\\\", \" +\r\n"
                        + "                \"\\\"nome\\\": \\\"\" + Maria + \"\\\", \" +\r\n"
                        + "                \"\\\"whatsapp\\\": \\\"\" + 5573988000000 + \"\\\" }",
                Instant.now());
    }

    public static List<Wakander> criaListaWakanders() {
        return List.of(criaWakander(), criaWakander());
    }

    public static ZAPIPayload criaZAPIPayload() {
        return new ZAPIPayload("5573988000000", MENSAGEM);
    }

    public static SqsMessageDto criaMensagemDTOComDataPassada() {
        return new SqsMessageDto(
                "teste-id",
                "teste-topic.fifo",
                "{ \"idWakander\": \"b7df31a1-34f0-476c-b52c-ebedad715a62\", " +
                        "\"nome\": \"Joao\", " +
                        "\"whatsapp\": \"5573988000000\" }",
                Instant.now().minus(1, ChronoUnit.DAYS)
        );
    }

    public static SqsMessageDto criaMensagemDTOComDataAtual() {
        return new SqsMessageDto(
                "teste-id",
                "teste-topic.fifo",
                "{ \"idWakander\": \"61d1ecfb-9bac-4cfa-987d-b121a5f8f9a0\", " +
                        "\"nome\": \"maria\", " +
                        "\"whatsapp\": \"5573988000000\" }",
                Instant.now()
        );
    }

    public static SqsMessageDto criaMensagemDTOComNumeroInvalido() {
        return new SqsMessageDto(
                "teste-id",
                "teste-topic.fifo",
                "{ \"idWakander\": \"b7df31a1-34f0-476c-b52c-ebedad715a62\", " +
                        "\"nome\": \"Joao\", " +
                        "\"whatsapp\": \"40123456789\" }",
                Instant.now()
        );

    }

    public static Cobranca criaCobranca(CobrancaStatus status) {
        return new Cobranca(
                UUID.randomUUID(),
                UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
                "pay_080225913252",
                new BigDecimal("200.00"),
                new BigDecimal("200.00"),
                LocalDate.now(),
                LocalDate.now().plusDays(7),
                null,
                status
        );
    }

    public static AssinaturaEvento criaAssinaturaRequest(AssinaturaType type) {
        return new AssinaturaEvento(
                type,
                LocalDateTime.now(),
                "sub_VXJBYgP2u0eO",
                "Joaquim"
        );
    }


    public static PaymentStatusRequest criaPaymentDTO(String paymentStatus) {
        return new PaymentStatusRequest("evt_52156525913252", paymentStatus, "pay_080225913252");
    }

    public static CobrancaAsaasDto criaEventoDeCobrancaDoAsaas(CobrancaEventoType eventoType) {
        return new CobrancaAsaasDto(
                "evt_52156525913252",
                eventoType.toString(),
                "2025-02-14 17:59:28",
                criaCobrancaAsaasPayment()
        );
    }

    private static CobrancaAsaasDto.Payment criaCobrancaAsaasPayment() {
        return new CobrancaAsaasDto.Payment(
                "payment",
                "pay_080225913252",
                LocalDate.now(),
                "cus_G7Dvo4iphUNk",
                "sub_VXJBYgP2u0eO",
                "installment",
                "paymentLink",
                LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(7),
                new BigDecimal("200.00"),
                new BigDecimal("200.00"),
                new BigDecimal("200.00"),
                new BigDecimal("0.00"),
                "nossoNumero",
                "description",
                "externalReference",
                "billingType",
                "status",
                "pixTransaction",
                LocalDate.now(),
                LocalDate.now(),
                LocalDate.now(),
                1,
                LocalDate.now(),
                "custody",
                LocalDate.now(),
                "invoiceUrl",
                "bankSlipUrl",
                "transactionReceiptUrl",
                "invoiceNumber",
                false,
                false,
                false,
                "lastInvoiceViewedDate",
                "lastBankSlipViewedDate",
                false,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public static AssinaturaAsaasDto criaAssinaturaAsaasDto(AssinaturaType type) {
        return new AssinaturaAsaasDto(
                "evt_6561b631fa5580caadd00bbe3b858607&9193",
                type.toString(),
                "2024-10-16 11:11:04",
                new AssinaturaAsaasDto.SubscriptionDTO(
                        "subscription",
                        "sub_m5gdy1upm25fbwgx",
                        "16/10/2024",
                        "cus_000000008773",
                        null,
                        new BigDecimal("19.9"),
                        "22/11/2024",
                        "MONTHLY",
                        "Assinatura Plano Pró",
                        "BOLETO",
                        false,
                        "ACTIVE",
                        null,
                        false,
                        new AssinaturaAsaasDto.DiscountDTO(new BigDecimal("10"), "2024-10-20", 0, "PERCENTAGE"),
                        new AssinaturaAsaasDto.FineDTO(new BigDecimal("1"), "PERCENTAGE"),
                        new AssinaturaAsaasDto.InterestDTO(new BigDecimal("2"), "PERCENTAGE"),
                        List.of(new AssinaturaAsaasDto.SplitDTO("a0188304-4860-4d97-9178-4da0cde5fdc1", null, new BigDecimal("20"), null, null))
                )
        );
    }

    public static WakanderAlteracaoRequest createWakanderAlteracaoRequest() {
        return WakanderAlteracaoRequest.builder()
                .nome("João Silva")
                .cpf("49383316080")
                .contato(createWakanderContatoAlteracaoRequest())
                .build();
    }

    public static WakanderContatoAlteracaoRequest createWakanderContatoAlteracaoRequest() {
        return WakanderContatoAlteracaoRequest.builder()
                .whatsapp("+5511987654321")
                .email("joao.silva@example.com")
                .build();
    }

    public static ClienteAsaasDto criaClienteAsaasDto() {
        return new ClienteAsaasDto(
                "cus_000006488748",
                "2025-02-03",
                "Raimundo",
                "teste@gmail.com",
                "1138524795",
                "7738521226",
                "Av. Paulista",
                "1000",
                "Sala 123",
                "Bela Vista",
                "São Paulo",
                "São Paulo",
                "SP",
                "Brasil",
                "01310-100",
                "96740932069",
                "FISICA",
                false,
                "financeiro@teste.com",
                "cliente-01",
                false,
                "Cliente VIP",
                false
        );
    }

    public static WakanderFinanceiro criaWakanderFinanceiroCancelado() {
        WakanderFinanceiro wakanderFinanceiro = new WakanderFinanceiro();
        wakanderFinanceiro.mudaStatus(WakanderStatusFinanceiro.CANCELADO, LocalDateTime.now());
        return wakanderFinanceiro;
    }

    public static WakanderEstudo criaWakanderEstudo(String nome, String whatsapp) {
        return new WakanderEstudo(
                UUID.randomUUID().toString(),
                nome,
                whatsapp
        );
    }

    public static LoginMemberkitDto criaLoginMemberkit(String type) {
        return new LoginMemberkitDto(type, getDataLoginMemberkit());
    }

    private static Data getDataLoginMemberkit() {
        return new Data("123456", "Fulano Costa", "fulano@wakanda.com",
                "0", null, null, "2024-09-04T08:45:21.249-03:00",
                "2024-09-04T08:45:22.270-03:00", "123456789",
                "73", "36068464"
        );
    }

    public static MemberkitUserDto criaMemberkitUserDto() {
        return new MemberkitUserDto(
                123L,
                "João Silva",
                "email@email.com",
                "Bio do usuário",
                "https://example.com/profile.jpg",
                false,
                false,
                5,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(10),
                LocalDateTime.now().minusDays(5),
                new MemberkitUserDto.Metadata("12345678901", "55", "11987654321"),
                List.of(new MemberkitUserDto.Enrollment(1L, "active", 101L, 201L, LocalDateTime.now().plusDays(30).toString())),
                List.of(new MemberkitUserDto.Membership(1L, "active", 301L, LocalDateTime.now().plusDays(30).toString()))
        );
    }

    public static OnboardingWakander criaOnboardingWakander(Wakander wakander) {
        return new OnboardingWakander(wakander);
    }

    public static Wakander criaStatusWakander(StatusCadastro statusCadastro) {
        Wakander wakander = new Wakander(
                UUID.randomUUID(),
                "João Silva",
                "123456789012345678",
                "joaodiscord",
                LocalDate.of(1995, 5, 20),
                "12345678901",
                "KIT123456",
                statusCadastro,
                null, // jornadaAtual
                null, // contato
                null, // financeiro
                null, // fiador
                null  // ultimaAulaAssistida
        );
        return wakander;
    }

    public static String geraLinkSuporteWhatsapp(Wakander wakander, String numeroSuporte) {
        String mensagem = "Olá, eu sou o " + wakander.getNome() + " e preciso de ajuda!";
        return "https://wa.me/" + numeroSuporte + "?text=" +
                java.net.URLEncoder.encode(mensagem, java.nio.charset.StandardCharsets.UTF_8);
    }

    public static MemberKitMessageEnvelope criaEnvelopeDoMemberKit(MemberKitTipoRequisicao tipo) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonPayload = "{ \"mensagem\": \"Mensagem de teste para o payload\" }";
        JsonNode payload = mapper.readTree(jsonPayload);
        return new MemberKitMessageEnvelope(tipo, payload);
    }

    public static MemberKitAcessoRequest criaMemberKitAcessoRequestBloqueado() {
        return new MemberKitAcessoRequest("test@gmail.com", true);
    }

    public static AssinaturaCanceladaEvent criaAssinaturaCanceladaEvent() {
        Wakander wakander = criaWakander();
        return new AssinaturaCanceladaEvent(wakander);
    }

    public static WakanderCadastroCompleto atualizaWakanderCadastroCompleto(UUID idWakander) {
        return new WakanderCadastroCompleto(
                "João da Silva",
                idWakander,
                "Maria Wakander",
                "10855604069",
                LocalDate.of(1995, 5, 20),
                "11999999999",
                "maria.wakander@wakanda.com"
        );
    }

    public static WakanderDetalhadoResponse criaWakanderDetalhadoResponse() {
        return new WakanderDetalhadoResponse(
                UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
                "João Silva dos Santos",
                LocalDate.of(1990, 1, 15),
                "63920752082",
                "12345678",
                StatusCadastro.COMPLETO,
                JornadaWakanda.JORNADA_CONHECIMENTO,
                new WakanderContato("5511998765432", "teste@teste"),
                new WakanderFinanceiro(WakanderStatusFinanceiro.REGULAR, LocalDateTime.now(), "Teste"),
                new WakanderFiador(
                        "cus_ABCDEF123456",
                        "Carlos Alberto",
                        "98765432100",
                        "11998765432",
                        "73988000000"
                )
        );
    }
}
