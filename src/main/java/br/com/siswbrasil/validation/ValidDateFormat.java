package br.com.siswbrasil.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = DateFormatValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDateFormat {
    String message() default "Formato de data inválido. O formato correto é yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}