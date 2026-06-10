package academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SabedoriaValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SabedoriaAnnotation {
    String message() default "Ao menos uma Sabedoria deve ser informada com valor maior ou igual a 0(zero).";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
