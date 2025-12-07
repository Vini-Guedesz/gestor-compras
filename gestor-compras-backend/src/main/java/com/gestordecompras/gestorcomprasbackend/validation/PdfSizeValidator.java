package com.gestordecompras.gestorcomprasbackend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator para verificar o tamanho de arrays de bytes (PDFs)
 */
public class PdfSizeValidator implements ConstraintValidator<PdfSize, byte[]> {

    private long maxBytes;

    @Override
    public void initialize(PdfSize constraintAnnotation) {
        this.maxBytes = constraintAnnotation.maxBytes();
    }

    @Override
    public boolean isValid(byte[] pdf, ConstraintValidatorContext context) {
        // Se o PDF for null, permite (será tratado por @NotNull se necessário)
        if (pdf == null) {
            return true;
        }

        // Verifica se o tamanho excede o limite
        if (pdf.length > maxBytes) {
            context.disableDefaultConstraintViolation();

            // Converte bytes para MB para mensagem mais legível
            double sizeMB = pdf.length / (1024.0 * 1024.0);
            double maxMB = maxBytes / (1024.0 * 1024.0);

            String message = String.format(
                "PDF muito grande (%.2f MB). Tamanho máximo permitido: %.0f MB",
                sizeMB,
                maxMB
            );

            context.buildConstraintViolationWithTemplate(message)
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
