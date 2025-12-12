package com.gestordecompras.gestorcomprasbackend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validação customizada para números de celular brasileiros
 * Verifica o formato (XX) 9XXXX-XXXX e a presença do 9º dígito
 */
@Documented
@Constraint(validatedBy = CelularValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Celular {
    String message() default "Celular inválido. Formato esperado: (XX) 9XXXX-XXXX";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
