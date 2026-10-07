package cz.uhk.fim.ppro.ordinace.presentation.validation;

import cz.uhk.fim.ppro.ordinace.application.validation.RodneCisloValidator;
import cz.uhk.fim.ppro.ordinace.presentation.dto.PacientCreateDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PacientValidator implements ConstraintValidator<ValidPacient, PacientCreateDto> {

    @Override
    public boolean isValid(PacientCreateDto dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true;
        }

        boolean valid = true;

        if (dto.getRokNarozeni() != null && dto.getCisloPojistence() != null && !dto.getCisloPojistence().trim().isEmpty()) {
            if (RodneCisloValidator.isValid(dto.getCisloPojistence())) {
                if (!RodneCisloValidator.matchesBirthYear(dto.getCisloPojistence(), dto.getRokNarozeni())) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate("Rok narození (" + dto.getRokNarozeni() + ") neodpovídá roku uvedenému v rodném čísle")
                            .addPropertyNode("rokNarozeni")
                            .addConstraintViolation();
                    valid = false;
                }
            }
        }

        return valid;
    }
}
