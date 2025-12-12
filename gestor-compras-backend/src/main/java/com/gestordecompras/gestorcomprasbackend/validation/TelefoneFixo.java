package com.gestordecompras.gestorcomprasbackend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validação customizada para números de telefone fixo brasileiros
 * Verifica o formato (XX) XXXX-XXXX
 */
@Documented
@Constraint(validatedBy = TelefoneFixoValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface TelefoneFixo {
    String message() default "Telefone fixo inválido. Formato esperado: (XX) XXXX-XXXX";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
