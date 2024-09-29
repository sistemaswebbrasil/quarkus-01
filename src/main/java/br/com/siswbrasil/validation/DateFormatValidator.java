package br.com.siswbrasil.validation;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateFormatValidator implements ConstraintValidator<ValidDateFormat, String> {

    private static final List<DateTimeFormatter> FORMATTERS = Arrays.asList(
        DateTimeFormatter.ISO_OFFSET_DATE_TIME, // ISO_OFFSET_DATE_TIME lida com 'Z' e offsets
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX") // Adiciona o formato com 'X' para o offset
    );

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // NotNull should handle this case
        }
        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                OffsetDateTime.parse(value, formatter);
                return true;
            } catch (DateTimeParseException e) {
                // Continue to the next formatter
            }
        }
        return false;
    }
}