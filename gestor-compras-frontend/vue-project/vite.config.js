import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig(({ command, mode }) => ({
  plugins: [
    vue(),
    vueJsx(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  build: {
    // Otimizações de build para melhor performance
    rollupOptions: {
      output: {
        // Code splitting manual para melhor caching (formato função para rolldown-vite)
        manualChunks: (id) => {
          // Bibliotecas principais do Vue
          if (id.includes('vue') || id.includes('vue-router') || id.includes('pinia')) {
            return 'vue-vendor'
          }
          // Biblioteca HTTP
          if (id.includes('axios')) {
            return 'http-vendor'
          }
          // Views grandes
          if (id.includes('PedidosView') || id.includes('CotacoesView') || id.includes('DashboardView')) {
            return 'views-main'
          }
          // Componentes de formulário (pesados)
          if (id.includes('Form.vue')) {
            return 'forms'
          }
        },
        // Nomes de arquivo com hash para cache busting
        chunkFileNames: 'assets/js/[name]-[hash].js',
        entryFileNames: 'assets/js/[name]-[hash].js',
        assetFileNames: 'assets/[ext]/[name]-[hash].[ext]'
      }
    },
    // Configuração de chunks
    chunkSizeWarningLimit: 500, // Warning para chunks > 500kb
    // Otimização de CSS
    cssCodeSplit: true,
    // Target para melhor compatibilidade
    target: 'esnext',
    // Sourcemaps apenas em desenvolvimento
    sourcemap: mode === 'development',
    // Otimização de assets - inline pequenos arquivos como base64
    assetsInlineLimit: 4096 // 4kb - arquivos menores viram base64
  },
  // Configurações de servidor de desenvolvimento
  server: {
    headers: {
      'Cache-Control': 'no-cache'
    },
    // Performance do servidor de dev
    fs: {
      strict: false
    }
  },
  // Configurações de preview (produção local)
  preview: {
    headers: {
      // Cache agressivo para assets com hash (1 ano)
      'Cache-Control': 'public, max-age=31536000, immutable'
    }
  },
  // Plugin customizado para adicionar headers de cache no HTML gerado
  // Nota: Para produção real, configure no servidor web (.htaccess, nginx.conf, etc.)
  ...(mode === 'production' && {
    // Adiciona comentários no HTML para orientar configuração de cache
    html: {
      inject: {
        data: {
          cacheInfo: '<!-- Configure cache no servidor: HTML (no-cache), Assets (max-age=31536000) -->'
        }
      }
    }
  }),
  // Configurações de otimização de dependências
  optimizeDeps: {
    include: ['vue', 'vue-router', 'pinia', 'axios']
  }
}))
