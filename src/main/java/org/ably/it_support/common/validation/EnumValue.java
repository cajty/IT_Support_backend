package org.ably.it_support.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumValidator.class)
@Documented
public @interface EnumValue {
    Class<? extends Enum<?>> enumClass();
    String message() default "Invalid value. Must be one of: ${validValues}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
