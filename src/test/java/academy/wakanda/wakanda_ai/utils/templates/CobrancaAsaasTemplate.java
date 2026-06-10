package academy.wakanda.wakanda_ai.utils.templates;

import academy.wakanda.wakanda_ai.financeiro.application.api.CobrancaAsaasDto;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;

import java.time.LocalDate;
import java.util.UUID;

import static br.com.six2six.fixturefactory.Fixture.of;

public class CobrancaAsaasTemplate implements TemplateLoader{
    public static final String COBRANCA = UUID.randomUUID().toString();
    
    @Override
    public void load() {
        of(CobrancaAsaasDto.class).addTemplate(COBRANCA, new Rule() {{
            add("id", UUID.randomUUID().toString());
            add("event", "PAYMENT_DUNNING_RECEIVED");
            add("dateCreated", LocalDate.now().toString());
        }});
    }
}