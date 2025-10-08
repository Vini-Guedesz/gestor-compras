package com.gestordecompras.gestorcomprasbackend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class CelularValidator implements ConstraintValidator<Celular, String> {

    // Padrão para celular: (XX) 9XXXX-XXXX ou variações sem formatação
    // Celular brasileiro sempre começa com 9
    private static final Pattern CELULAR_PATTERN = Pattern.compile(
        "^(\\(?\\d{2}\\)?\\s?)?9\\d{4}-?\\d{4}$"
    );

    @Override
    public void initialize(Celular constraintAnnotation) {
    }

    @Override
    public boolean isValid(String celular, ConstraintValidatorContext context) {
        if (celular == null || celular.isBlank()) {
            return true; // Null/blank será tratado por @NotBlank se necessário
        }

        // Remove espaços extras
        celular = celular.trim();

        // Valida formato
        if (!CELULAR_PATTERN.matcher(celular).matches()) {
            return false;
        }

        // Remove caracteres não numéricos para validação adicional
        String apenasNumeros = celular.replaceAll("\\D", "");

        // Celular deve ter exatamente 11 dígitos (DDD + 9 + 8 dígitos)
        if (apenasNumeros.length() != 11) {
            return false;
        }

        // Verifica se o terceiro dígito é 9 (após o DDD)
        return apenasNumeros.charAt(2) == '9';
    }
}
