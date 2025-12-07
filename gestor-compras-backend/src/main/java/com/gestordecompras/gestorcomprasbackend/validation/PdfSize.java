package com.gestordecompras.gestorcomprasbackend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validação customizada para verificar o tamanho de arquivos PDF
 * O tamanho máximo padrão é 10MB (10485760 bytes)
 */
@Documented
@Constraint(validatedBy = PdfSizeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface PdfSize {
    String message() default "PDF deve ter no máximo 10MB";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    /**
     * Tamanho máximo permitido em bytes
     * Padrão: 10MB = 10 * 1024 * 1024 = 10485760 bytes
     */
    long maxBytes() default 10485760L;
}
