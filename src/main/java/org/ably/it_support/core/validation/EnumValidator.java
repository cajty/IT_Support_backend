package org.ably.it_support.core.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class EnumValidator implements ConstraintValidator<EnumValue, Object> {
    private Set<String> allowedValues;
    private Class<? extends Enum<?>> enumClass;

    @Override
    public void initialize(EnumValue annotation) {
        this.enumClass = annotation.enumClass();
        this.allowedValues = Arrays.stream(enumClass.getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Allow null values (handled separately with @NotNull)
        }

        String valueStr;
        if (value instanceof Enum<?>) {
            valueStr = ((Enum<?>) value).name(); // Convert Enum to String
        } else if (value instanceof String) {
            valueStr = ((String) value).toUpperCase(); // Convert input String to uppercase
        } else {
            return false; // Invalid type
        }

        boolean isValid = allowedValues.contains(valueStr);

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Invalid value. Must be one of: " + String.join(", ", allowedValues))
                    .addConstraintViolation();
        }

        return isValid;
    }
}
