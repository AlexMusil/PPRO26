package cz.uhk.fim.ppro.ordinace.presentation.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PacientValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPacient {

    String message() default "Neplatná kombinace údajů pacienta";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
