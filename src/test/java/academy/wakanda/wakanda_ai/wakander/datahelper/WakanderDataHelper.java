package academy.wakanda.wakanda_ai.wakander.datahelper;

import academy.wakanda.wakanda_ai.wakander.application.api.WakanderCadastroCompleto;
import academy.wakanda.wakanda_ai.wakander.application.api.WakanderCancelaAssinaturaDTO;

import java.time.LocalDate;
import java.util.UUID;

public class WakanderDataHelper {

    public static WakanderCancelaAssinaturaDTO criaWakanderCancelaAssinaturaDTO() {
        return WakanderCancelaAssinaturaDTO.builder()
                .dataCancelamento(LocalDate.parse("2025-03-30"))
                .motivoCancelamento("Troca de plano")
                .build();
    }

    public static WakanderCancelaAssinaturaDTO criaWakanderCancelaAssinaturaDTOSemDataCancelamento() {
        return WakanderCancelaAssinaturaDTO.builder()
                .dataCancelamento(null)
                .motivoCancelamento("Troca de plano")
                .build();
    }

    public static WakanderCadastroCompleto criaWakanderCadastroCompleto(){
        return WakanderCadastroCompleto.builder()
                .nomeFiador("João Fiador")
                .idWakander(UUID.randomUUID())
                .nome("Carlos Silva")
                .cpf("123.456.789-00")
                .dataNascimento(LocalDate.of(1990, 5, 20))
                .whatsapp("+55 11 91234-5678")
                .email("carlos.silva@email.com")
                .build();
    }
}
