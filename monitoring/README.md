# Monitoramento - Gestor de Compras

Sistema de monitoramento completo com métricas, logs e visualização.

## Componentes

- **Prometheus** (http://localhost:9090) - Coleta de métricas
- **Grafana** (http://localhost:3000) - Dashboards e visualização
- **Loki** (http://localhost:3100) - Armazenamento de logs
- **Promtail** - Coleta automática de logs
- **Node Exporter** - Métricas do sistema (CPU, RAM, Disk)
- **PostgreSQL Exporter** - Métricas do banco de dados
- **Alertmanager** (http://localhost:9093) - Gerenciamento de alertas

## Iniciar Monitoramento

```bash
# Subir todos os containers
docker-compose -f docker-compose-monitoring.yml up -d

# Verificar status
docker-compose -f docker-compose-monitoring.yml ps

# Ver logs
docker-compose -f docker-compose-monitoring.yml logs -f
```

## Parar Monitoramento

```bash
docker-compose -f docker-compose-monitoring.yml down
```

## Acessar Grafana

1. Abra http://localhost:3000
2. Login: `admin` / Senha: `admin123`
3. Dashboards disponíveis (automaticamente carregados):
   - **Overview Completo** - Visão geral do sistema
   - **Spring Boot & JVM** - Métricas da aplicação
   - **Logs (Loki)** - Análise de logs
   - **Node Exporter** - Métricas do sistema

## Fontes de Dados (Datasources)

Configuradas automaticamente:
- **Prometheus** - Métricas (padrão)
- **Loki** - Logs

## Estrutura de Arquivos

```
monitoring/
├── prometheus.yml              # Configuração do Prometheus
├── loki-config.yml            # Configuração do Loki
├── promtail-config.yml        # Configuração do Promtail
├── alertmanager.yml           # Configuração de alertas
├── grafana/
│   ├── dashboards/
│   │   ├── dashboard.yml      # Provisionamento de dashboards
│   │   ├── overview-completo-dashboard.json
│   │   ├── spring-boot-jvm-dashboard.json
│   │   ├── logs-loki-dashboard.json
│   │   └── node-exporter-dashboard.json
│   └── datasources/
│       └── datasources.yml    # Provisionamento de datasources
└── README.md
```

## Métricas Coletadas

### Backend (Spring Boot)
- Requisições HTTP (latência, taxa de erro)
- JVM (heap, threads, GC)
- Cache (Caffeine - hit rate)
- Connection Pool (HikariCP)
- Métricas customizadas via Actuator

### PostgreSQL
- Conexões ativas
- Queries por segundo
- Tamanho do banco
- Cache hit rate

### Sistema (Node Exporter)
- CPU, RAM, Disco
- Network I/O
- Load Average

## Logs

Os logs são coletados automaticamente do Spring Boot em:
```
gestor-compras-backend/logs/gestor-compras.log
```

Para visualizar logs no Grafana:
1. Acesse o dashboard "Logs (Loki)"
2. Ou use o Explorer com a query: `{application="gestor-compras-backend"}`

## Troubleshooting

**Grafana não carrega dashboards:**
```bash
# Reinicie o Grafana
docker-compose -f docker-compose-monitoring.yml restart grafana
```

**Logs não aparecem:**
1. Verifique se o backend está gerando logs em `gestor-compras-backend/logs/`
2. Reinicie o Promtail: `docker-compose -f docker-compose-monitoring.yml restart promtail`

**Métricas não aparecem:**
1. Verifique se o backend está rodando
2. Acesse http://localhost:8081/actuator/prometheus para ver se as métricas estão sendo expostas
3. Verifique targets no Prometheus: http://localhost:9090/targets
