package academy.wakanda.wakanda_ai.utils.templates;

import academy.wakanda.wakanda_ai.jornadawakander.domain.JornadaWakanda;
import academy.wakanda.wakanda_ai.wakander.domain.*;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;

import java.time.LocalDateTime;
import java.util.UUID;

import static br.com.six2six.fixturefactory.Fixture.of;

public class WakanderTemplate implements TemplateLoader { 
    public static final String WAKANDER = UUID.randomUUID().toString();

    @Override
    public void load() {
        of(Wakander.class).addTemplate(WAKANDER, new Rule() {{
            add("idWakander", UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
            add("nome", random("Wakander 1", "Wakander 2", "Wakander 3"));
            add("idDiscord", "123456789012345678");
            add("cpf", "10855604069");
            add("idMemberKit", "123456");
            add("statusCadastro", StatusCadastro.INCOMPLETO);
            add("jornadaAtual", JornadaWakanda.ONBOARD);
            add("ultimaAulaAssistida", new WakanderAulaAssistida(
                    UUID.fromString("7c893139-6aa7-46dd-8a0b-3bf35303a335"),
                    LocalDateTime.now()
            ));
            add("fiador", new WakanderFiador(
                    "cus1231232",
                    "sub432412",
                    "Raimundo",
                    "123456789100",
                    "5573912345678"
            ));
            add("financeiro", new WakanderFinanceiro());
            add("contato", new WakanderContato("5573912345678", "email@email.com"));
        }});
    }
}
