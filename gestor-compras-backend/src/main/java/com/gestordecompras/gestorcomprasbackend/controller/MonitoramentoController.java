package com.gestordecompras.gestorcomprasbackend.controller;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller para monitoramento de performance e estatísticas do sistema.
 * <p>
 * Expõe métricas de cache (Caffeine) e estatísticas do Hibernate via endpoints HTTP.
 * </p>
 * <p>
 * <b>Endpoints disponíveis:</b>
 * <ul>
 *   <li>GET /monitoramento/cache - Estatísticas de todos os caches</li>
 *   <li>GET /monitoramento/cache/{nome} - Estatísticas de um cache específico</li>
 *   <li>GET /monitoramento/hibernate - Estatísticas do Hibernate</li>
 *   <li>GET /monitoramento/health - Resumo de saúde do sistema</li>
 * </ul>
 * </p>
 *
 * @since 1.0.0
 */
@RestController
@RequestMapping("/monitoramento")
@Tag(name = "Monitoramento", description = "Endpoints para monitoramento de performance e estatísticas")
public class MonitoramentoController {

    private final CacheManager cacheManager;
    private final EntityManagerFactory entityManagerFactory;

    public MonitoramentoController(CacheManager cacheManager, EntityManagerFactory entityManagerFactory) {
        this.cacheManager = cacheManager;
        this.entityManagerFactory = entityManagerFactory;
    }

    /**
     * Retorna estatísticas de todos os caches Caffeine.
     * <p>
     * Métricas incluem: hit rate, miss rate, evictions, load count, etc.
     * </p>
     *
     * @return Map com estatísticas de cada cache
     */
    @GetMapping("/cache")
    @Operation(summary = "Estatísticas de Cache",
               description = "Retorna métricas de performance de todos os caches (hit rate, miss rate, evictions)")
    public ResponseEntity<Map<String, Object>> getCacheStats() {
        Map<String, Object> allStats = new HashMap<>();

        cacheManager.getCacheNames().forEach(cacheName -> {
            org.springframework.cache.Cache cache = cacheManager.getCache(cacheName);
            if (cache instanceof CaffeineCache) {
                CaffeineCache caffeineCache = (CaffeineCache) cache;
                Cache<Object, Object> nativeCache = caffeineCache.getNativeCache();
                CacheStats stats = nativeCache.stats();

                Map<String, Object> cacheStats = new HashMap<>();
                cacheStats.put("hitCount", stats.hitCount());
                cacheStats.put("missCount", stats.missCount());
                cacheStats.put("hitRate", String.format("%.2f%%", stats.hitRate() * 100));
                cacheStats.put("missRate", String.format("%.2f%%", stats.missRate() * 100));
                cacheStats.put("loadCount", stats.loadCount());
                cacheStats.put("loadSuccessCount", stats.loadSuccessCount());
                cacheStats.put("loadFailureCount", stats.loadFailureCount());
                cacheStats.put("totalLoadTime", stats.totalLoadTime() / 1_000_000 + "ms");
                cacheStats.put("averageLoadPenalty", stats.averageLoadPenalty() / 1_000_000 + "ms");
                cacheStats.put("evictionCount", stats.evictionCount());
                cacheStats.put("estimatedSize", nativeCache.estimatedSize());

                allStats.put(cacheName, cacheStats);
            }
        });

        return ResponseEntity.ok(allStats);
    }

    /**
     * Retorna estatísticas de um cache específico.
     *
     * @param nome Nome do cache (ex: "fornecedores", "usuarios", "cotacoes")
     * @return Map com estatísticas detalhadas do cache
     */
    @GetMapping("/cache/{nome}")
    @Operation(summary = "Estatísticas de Cache Específico",
               description = "Retorna métricas de performance de um cache específico pelo nome")
    public ResponseEntity<Map<String, Object>> getCacheStatsByName(@PathVariable String nome) {
        org.springframework.cache.Cache cache = cacheManager.getCache(nome);

        if (cache == null) {
            return ResponseEntity.notFound().build();
        }

        if (!(cache instanceof CaffeineCache)) {
            return ResponseEntity.badRequest().build();
        }

        CaffeineCache caffeineCache = (CaffeineCache) cache;
        Cache<Object, Object> nativeCache = caffeineCache.getNativeCache();
        CacheStats stats = nativeCache.stats();

        Map<String, Object> cacheStats = new HashMap<>();
        cacheStats.put("nome", nome);
        cacheStats.put("hitCount", stats.hitCount());
        cacheStats.put("missCount", stats.missCount());
        cacheStats.put("hitRate", String.format("%.2f%%", stats.hitRate() * 100));
        cacheStats.put("missRate", String.format("%.2f%%", stats.missRate() * 100));
        cacheStats.put("loadCount", stats.loadCount());
        cacheStats.put("totalLoadTime", stats.totalLoadTime() / 1_000_000 + "ms");
        cacheStats.put("averageLoadPenalty", stats.averageLoadPenalty() / 1_000_000 + "ms");
        cacheStats.put("evictionCount", stats.evictionCount());
        cacheStats.put("estimatedSize", nativeCache.estimatedSize());

        // Dados adicionais
        cacheStats.put("requestCount", stats.hitCount() + stats.missCount());
        cacheStats.put("eficiencia", stats.hitCount() > 0
            ? String.format("%.1fx mais rápido que sem cache", 1.0 / (1 - stats.hitRate()))
            : "Sem hits ainda");

        return ResponseEntity.ok(cacheStats);
    }

    /**
     * Retorna estatísticas do Hibernate (queries, cache L2, entidades).
     * <p>
     * Requer que hibernate.generate_statistics=true esteja configurado.
     * </p>
     *
     * @return Map com estatísticas do Hibernate
     */
    @GetMapping("/hibernate")
    @Operation(summary = "Estatísticas do Hibernate",
               description = "Retorna métricas de queries, cache de segundo nível e entidades do Hibernate")
    public ResponseEntity<Map<String, Object>> getHibernateStats() {
        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        Statistics stats = sessionFactory.getStatistics();

        if (!stats.isStatisticsEnabled()) {
            Map<String, Object> error = new HashMap<>();
            error.put("erro", "Hibernate Statistics está desabilitado");
            error.put("solucao", "Configure hibernate.generate_statistics=true no application.properties");
            return ResponseEntity.ok(error);
        }

        Map<String, Object> hibernateStats = new HashMap<>();

        // Estatísticas gerais
        hibernateStats.put("startTime", stats.getStartTime());
        hibernateStats.put("sessionOpenCount", stats.getSessionOpenCount());
        hibernateStats.put("sessionCloseCount", stats.getSessionCloseCount());
        hibernateStats.put("transactionCount", stats.getTransactionCount());
        hibernateStats.put("successfulTransactionCount", stats.getSuccessfulTransactionCount());

        // Estatísticas de queries
        Map<String, Object> queryStats = new HashMap<>();
        queryStats.put("queryExecutionCount", stats.getQueryExecutionCount());
        queryStats.put("queryExecutionMaxTime", stats.getQueryExecutionMaxTime() + "ms");
        queryStats.put("queryExecutionMaxTimeQueryString", stats.getQueryExecutionMaxTimeQueryString());
        queryStats.put("queryCacheHitCount", stats.getQueryCacheHitCount());
        queryStats.put("queryCacheMissCount", stats.getQueryCacheMissCount());
        queryStats.put("queryCachePutCount", stats.getQueryCachePutCount());
        hibernateStats.put("queries", queryStats);

        // Estatísticas de entidades
        Map<String, Object> entityStats = new HashMap<>();
        entityStats.put("entityLoadCount", stats.getEntityLoadCount());
        entityStats.put("entityFetchCount", stats.getEntityFetchCount());
        entityStats.put("entityInsertCount", stats.getEntityInsertCount());
        entityStats.put("entityUpdateCount", stats.getEntityUpdateCount());
        entityStats.put("entityDeleteCount", stats.getEntityDeleteCount());
        hibernateStats.put("entidades", entityStats);

        // Estatísticas de cache L2
        Map<String, Object> cacheL2Stats = new HashMap<>();
        cacheL2Stats.put("secondLevelCacheHitCount", stats.getSecondLevelCacheHitCount());
        cacheL2Stats.put("secondLevelCacheMissCount", stats.getSecondLevelCacheMissCount());
        cacheL2Stats.put("secondLevelCachePutCount", stats.getSecondLevelCachePutCount());
        hibernateStats.put("cacheL2", cacheL2Stats);

        // Estatísticas de conexões
        Map<String, Object> connectionStats = new HashMap<>();
        connectionStats.put("connectCount", stats.getConnectCount());
        connectionStats.put("prepareStatementCount", stats.getPrepareStatementCount());
        connectionStats.put("closeStatementCount", stats.getCloseStatementCount());
        hibernateStats.put("conexoes", connectionStats);

        return ResponseEntity.ok(hibernateStats);
    }

    /**
     * Retorna um resumo de saúde do sistema com métricas principais.
     *
     * @return Map com resumo de performance
     */
    @GetMapping("/health")
    @Operation(summary = "Resumo de Saúde do Sistema",
               description = "Retorna um resumo executivo das principais métricas de performance")
    public ResponseEntity<Map<String, Object>> getHealthSummary() {
        Map<String, Object> health = new HashMap<>();

        // Resumo de cache
        Map<String, String> cacheHealth = new HashMap<>();
        cacheManager.getCacheNames().forEach(cacheName -> {
            org.springframework.cache.Cache cache = cacheManager.getCache(cacheName);
            if (cache instanceof CaffeineCache) {
                CaffeineCache caffeineCache = (CaffeineCache) cache;
                CacheStats stats = caffeineCache.getNativeCache().stats();
                String hitRate = String.format("%.1f%%", stats.hitRate() * 100);
                cacheHealth.put(cacheName, hitRate + " hit rate");
            }
        });
        health.put("cache", cacheHealth);

        // Resumo de Hibernate
        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        Statistics stats = sessionFactory.getStatistics();

        if (stats.isStatisticsEnabled()) {
            Map<String, Object> hibernateHealth = new HashMap<>();
            hibernateHealth.put("totalQueries", stats.getQueryExecutionCount());
            hibernateHealth.put("slowestQuery", stats.getQueryExecutionMaxTime() + "ms");
            hibernateHealth.put("activeTransactions",
                stats.getTransactionCount() - stats.getSuccessfulTransactionCount());
            health.put("hibernate", hibernateHealth);
        } else {
            health.put("hibernate", "Statistics desabilitado");
        }

        health.put("status", "OK");
        health.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(health);
    }

    /**
     * Reseta as estatísticas do Hibernate (útil para testes).
     */
    @GetMapping("/hibernate/reset")
    @Operation(summary = "Resetar Estatísticas do Hibernate",
               description = "Reseta todas as estatísticas do Hibernate para zero (útil para testes)")
    public ResponseEntity<Map<String, String>> resetHibernateStats() {
        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        Statistics stats = sessionFactory.getStatistics();

        if (!stats.isStatisticsEnabled()) {
            return ResponseEntity.badRequest().body(
                Map.of("erro", "Hibernate Statistics está desabilitado")
            );
        }

        stats.clear();
        return ResponseEntity.ok(Map.of(
            "status", "Estatísticas resetadas com sucesso",
            "timestamp", String.valueOf(System.currentTimeMillis())
        ));
    }
}
