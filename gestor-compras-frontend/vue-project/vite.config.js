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
    sourcemap: mode === 'development'
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
      'Cache-Control': 'public, max-age=31536000'
    }
  },
  // Configurações de otimização de dependências
  optimizeDeps: {
    include: ['vue', 'vue-router', 'pinia', 'axios']
  }
}))
