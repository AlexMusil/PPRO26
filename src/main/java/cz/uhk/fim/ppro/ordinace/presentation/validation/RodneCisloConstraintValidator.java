package cz.uhk.fim.ppro.ordinace.presentation.validation;

import cz.uhk.fim.ppro.ordinace.application.validation.RodneCisloValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RodneCisloConstraintValidator implements ConstraintValidator<ValidRodneCislo, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true; // Prázdné hodnoty řeší @NotBlank
        }
        return RodneCisloValidator.isValid(value);
    }
}
