package com.gestordecompras.gestorcomprasbackend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CelularValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Celular {
    String message() default "Celular inválido. Formato esperado: (XX) 9XXXX-XXXX";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
