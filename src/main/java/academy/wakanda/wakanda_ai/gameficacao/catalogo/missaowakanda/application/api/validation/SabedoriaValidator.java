package academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.api.validation;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.Sabedorias;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SabedoriaValidator implements ConstraintValidator<SabedoriaAnnotation, Sabedorias> {

    @Override
    public boolean isValid(Sabedorias sabedorias, ConstraintValidatorContext context) {
        if (sabedorias == null) return false;
        return validaSabedoria(sabedorias.getTeorico())
                || validaSabedoria(sabedorias.getProcesso())
                || validaSabedoria(sabedorias.getKnowHow())
                || validaSabedoria(sabedorias.getComportamental())
                || validaSabedoria(sabedorias.getCriativo());
    }

    private boolean validaSabedoria(Integer valor) {
        return valor != null && valor >= 0;
    }
}
