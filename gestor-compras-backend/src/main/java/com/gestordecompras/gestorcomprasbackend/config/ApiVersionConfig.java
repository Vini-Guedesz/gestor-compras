package com.gestordecompras.gestorcomprasbackend.config;

/**
 * Configuração centralizada de versionamento de API
 *
 * Mantém as versões da API em um único local para facilitar manutenção
 * e futuras migrações de versão.
 */
public final class ApiVersionConfig {

    /**
     * Versão atual da API (v1)
     */
    public static final String API_V1 = "/api/v1";

    /**
     * Prefixo de relatórios (não versionado por compatibilidade)
     */
    public static final String RELATORIOS = "/relatorios";

    /**
     * Prefixo de autenticação (não versionado)
     */
    public static final String AUTH = "/auth";

    // Construtor privado para evitar instanciação
    private ApiVersionConfig() {
        throw new UnsupportedOperationException("Classe de constantes não pode ser instanciada");
    }
}
