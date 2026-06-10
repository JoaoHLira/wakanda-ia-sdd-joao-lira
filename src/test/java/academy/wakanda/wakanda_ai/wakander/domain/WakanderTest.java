package academy.wakanda.wakanda_ai.wakander.domain;

import academy.wakanda.wakanda_ai.handler.APIException;
import academy.wakanda.wakanda_ai.jornadawakander.domain.JornadaWakanda;
import academy.wakanda.wakanda_ai.utils.DataHelper;
import academy.wakanda.wakanda_ai.wakander.domain.StatusCadastro;
import academy.wakanda.wakanda_ai.wakander.domain.WakanderStatusFinanceiro;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WakanderTest {

    @Test
    @DisplayName("Deve atualizar a jornada do Wakander quando for diferente da atual")
    void atualizaStatusJornada_QuandoJornadaForDiferente_AtualizaComSucesso() {
        Wakander wakander = DataHelper.criaWakander();
        ReflectionTestUtils.setField(wakander, "jornadaAtual", JornadaWakanda.ONBOARD);

        wakander.atualizaStatusJornada(JornadaWakanda.FINALIZOU_COMECE_AQUI);

        assertEquals(JornadaWakanda.FINALIZOU_COMECE_AQUI, wakander.getJornadaAtual());
    }

    @Test
    @DisplayName("Deve lançar exceção quando tentar atualizar para mesma jornada atual")
    void atualizaStatusJornada_QuandoJornadaForIgual_LancaExcecao() {

        Wakander wakander = DataHelper.criaWakander();
        JornadaWakanda mesmaJornada = JornadaWakanda.FINALIZOU_COMECE_AQUI;
        ReflectionTestUtils.setField(wakander, "jornadaAtual", mesmaJornada);

        APIException exception = assertThrows(APIException.class,
                () -> wakander.atualizaStatusJornada(mesmaJornada));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusException());
    }

    @Test
    @DisplayName("Deve validar elegibilidade para onboarding manual quando wakander estiver apto")
    void validaElegibilidadeOnboardingManual_QuandoWakanderEstiverApto_NaoLancaExcecao() {
        Wakander wakander = DataHelper.criaWakanderIncompleto();

        wakander.validaElegibilidadeOnboardingManual();

        assertEquals(StatusCadastro.INCOMPLETO, wakander.getStatusCadastro());
    }

    @Test
    @DisplayName("Deve lançar exceção quando onboarding manual for iniciado com cadastro completo")
    void validaElegibilidadeOnboardingManual_QuandoCadastroNaoForIncompleto_LancaExcecao() {
        Wakander wakander = DataHelper.criaStatusWakander(StatusCadastro.COMPLETO);

        APIException exception = assertThrows(APIException.class, wakander::validaElegibilidadeOnboardingManual);

        assertEquals(HttpStatus.CONFLICT, exception.getStatusException());
    }

    @Test
    @DisplayName("Deve lançar exceção quando onboarding manual for iniciado com status financeiro diferente de regular")
    void validaElegibilidadeOnboardingManual_QuandoStatusFinanceiroNaoForRegular_LancaExcecao() {
        Wakander wakander = DataHelper.criaWakanderIncompleto();
        wakander.mudaStatusFinanceiro(WakanderStatusFinanceiro.CANCELADO, LocalDateTime.now());

        APIException exception = assertThrows(APIException.class, wakander::validaElegibilidadeOnboardingManual);

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusException());
    }
}
