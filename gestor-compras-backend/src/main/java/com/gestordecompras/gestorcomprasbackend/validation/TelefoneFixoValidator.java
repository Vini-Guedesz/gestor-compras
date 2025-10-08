package com.gestordecompras.gestorcomprasbackend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class TelefoneFixoValidator implements ConstraintValidator<TelefoneFixo, String> {

    // Padrão para telefone fixo: (XX) XXXX-XXXX ou (XX) XXXXX-XXXX ou variações sem formatação
    private static final Pattern TELEFONE_FIXO_PATTERN = Pattern.compile(
        "^(\\(?\\d{2}\\)?\\s?)?(\\d{4,5}-?\\d{4})$"
    );

    @Override
    public void initialize(TelefoneFixo constraintAnnotation) {
    }

    @Override
    public boolean isValid(String telefone, ConstraintValidatorContext context) {
        if (telefone == null || telefone.isBlank()) {
            return true; // Null/blank será tratado por @NotBlank se necessário
        }

        // Remove espaços extras
        telefone = telefone.trim();

        // Valida formato
        if (!TELEFONE_FIXO_PATTERN.matcher(telefone).matches()) {
            return false;
        }

        // Remove caracteres não numéricos para validação adicional
        String apenasNumeros = telefone.replaceAll("\\D", "");

        // Telefone fixo deve ter 10 ou 11 dígitos (DDD + número)
        // 10 dígitos: DDD (2) + número (8): (XX) XXXX-XXXX
        // 11 dígitos: DDD (2) + número (9): (XX) XXXXX-XXXX (alguns estados têm 9 dígitos)
        return apenasNumeros.length() == 10 || apenasNumeros.length() == 11;
    }
}
