package com.gestordecompras.gestorcomprasbackend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.Map;
import java.util.regex.Pattern;

public class CEPValidator implements ConstraintValidator<CEP, String> {

    private static final Pattern CEP_PATTERN = Pattern.compile("^\\d{5}-?\\d{3}$");
    private boolean verificarExistencia;

    @Override
    public void initialize(CEP constraintAnnotation) {
        this.verificarExistencia = constraintAnnotation.verificarExistencia();
    }

    @Override
    public boolean isValid(String cep, ConstraintValidatorContext context) {
        if (cep == null || cep.isBlank()) {
            return true; // Null/blank será tratado por @NotBlank se necessário
        }

        // Remove espaços extras
        cep = cep.trim();

        // Valida formato do CEP
        if (!CEP_PATTERN.matcher(cep).matches()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("CEP deve estar no formato XXXXX-XXX ou XXXXXXXX")
                    .addConstraintViolation();
            return false;
        }

        // Se não precisa verificar existência, retorna true
        if (!verificarExistencia) {
            return true;
        }

        // Remove hífen para consulta na API
        String cepLimpo = cep.replaceAll("-", "");

        // Verifica se o CEP existe consultando a API ViaCEP
        return verificarCEPViaCEP(cepLimpo, context);
    }

    private boolean verificarCEPViaCEP(String cep, ConstraintValidatorContext context) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://viacep.com.br/ws/" + cep + "/json/";

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            // Se a resposta contém "erro": true, o CEP não existe
            if (response != null && response.containsKey("erro")) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("CEP não encontrado na base de dados dos Correios")
                        .addConstraintViolation();
                return false;
            }

            return response != null && response.containsKey("cep");

        } catch (RestClientException e) {
            // Em caso de erro na API (timeout, sem internet, etc), não bloqueia a validação
            // Apenas loga o erro e permite que o CEP seja aceito se estiver no formato correto
            System.err.println("Erro ao validar CEP via ViaCEP: " + e.getMessage());
            return true;
        }
    }
}
