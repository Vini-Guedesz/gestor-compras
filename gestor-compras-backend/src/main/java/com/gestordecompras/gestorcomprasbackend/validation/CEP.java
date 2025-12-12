package com.gestordecompras.gestorcomprasbackend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validação customizada para CEP (Código de Endereçamento Postal)
 * Verifica o formato 8 dígitos e opcionalmente consulta existência na API ViaCEP
 */
@Documented
@Constraint(validatedBy = CEPValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CEP {
    String message() default "CEP inválido ou não encontrado";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    boolean verificarExistencia() default true;
}
