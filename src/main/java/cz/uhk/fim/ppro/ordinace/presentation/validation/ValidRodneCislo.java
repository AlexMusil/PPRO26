package cz.uhk.fim.ppro.ordinace.presentation.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RodneCisloConstraintValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRodneCislo {

    String message() default "Neplatné číslo pojištěnce (rodné číslo musí mít platný formát RRMMDD/XXXX a kontrolní součet modulo 11)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
