package com.gestordecompras.gestorcomprasbackend.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuração de cache em memória usando Caffeine.
 * <p>
 * Caffeine é um cache de alto desempenho baseado em memória com políticas
 * de expiração e tamanho máximo configuráveis.
 * </p>
 * <p>
 * <b>Caches configurados:</b>
 * <ul>
 *   <li>fornecedores - TTL: 10 minutos, Max: 500 entradas</li>
 *   <li>usuarios - TTL: 5 minutos, Max: 100 entradas</li>
 *   <li>cotacoes - TTL: 2 minutos, Max: 1000 entradas</li>
 * </ul>
 * </p>
 * <p>
 * <b>IMPORTANTE:</b> Use @Cacheable nos métodos de serviço para habilitar o cache.
 * Use @CacheEvict para invalidar quando dados forem atualizados.
 * </p>
 *
 * @since 1.0.0
 * @see org.springframework.cache.annotation.Cacheable
 * @see org.springframework.cache.annotation.CacheEvict
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Configura o gerenciador de cache Caffeine.
     * <p>
     * Políticas padrão:
     * <ul>
     *   <li>Expiração após escrita: 10 minutos</li>
     *   <li>Tamanho máximo: 1000 entradas</li>
     *   <li>Métricas habilitadas para monitoramento</li>
     * </ul>
     * </p>
     *
     * @return CacheManager configurado com Caffeine
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                "fornecedores",
                "usuarios",
                "cotacoes",
                "solicitacoes"
        );

        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .recordStats() // Habilita métricas de hit/miss rate
        );

        return cacheManager;
    }

    /**
     * Configuração específica para cache de fornecedores.
     * <p>
     * Fornecedores mudam raramente, então podem ser cacheados por mais tempo.
     * </p>
     *
     * @return Caffeine configurado para fornecedores
     */
    @Bean
    public Caffeine<Object, Object> fornecedoresCaffeineConfig() {
        return Caffeine.newBuilder()
                .maximumSize(500)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .recordStats();
    }

    /**
     * Configuração específica para cache de usuários.
     * <p>
     * Usuários mudam com menos frequência, mas TTL menor para refletir mudanças
     * de permissões rapidamente.
     * </p>
     *
     * @return Caffeine configurado para usuários
     */
    @Bean
    public Caffeine<Object, Object> usuariosCaffeineConfig() {
        return Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .recordStats();
    }

    /**
     * Configuração específica para cache de cotações.
     * <p>
     * Cotações mudam frequentemente (preços, prazos), então TTL mais curto.
     * </p>
     *
     * @return Caffeine configurado para cotações
     */
    @Bean
    public Caffeine<Object, Object> cotacoesCaffeineConfig() {
        return Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(2, TimeUnit.MINUTES)
                .recordStats();
    }
}
