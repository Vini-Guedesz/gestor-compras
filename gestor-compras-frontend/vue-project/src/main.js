/**
 * Arquivo principal de inicialização da aplicação Vue.js
 *
 * Este arquivo é responsável por:
 * - Criar a instância principal da aplicação Vue
 * - Configurar o gerenciamento de estado global (Pinia)
 * - Configurar o sistema de rotas (Vue Router)
 * - Montar a aplicação no DOM
 */

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'

// Cria a instância principal da aplicação Vue
const app = createApp(App)

// Configura o Pinia para gerenciamento de estado global
// Pinia é o substituto oficial do Vuex para Vue 3
app.use(createPinia())

// Configura o Vue Router para navegação entre páginas
app.use(router)

// Monta a aplicação no elemento HTML com id "app"
app.mount('#app')
