<!--
  Componente LoadingSpinner - Indicador de Carregamento

  Componente reutilizável que exibe um spinner de loading com overlay.

  Props:
  - show (Boolean): Controla a visibilidade do spinner
  - message (String): Mensagem opcional a ser exibida abaixo do spinner

  Características:
  - Overlay que cobre toda a tela
  - Centralizado na tela
  - Z-index alto para ficar sobre outros elementos
  - Animação CSS para rotação contínua
  - Suporte a mensagem personalizada
-->
<template>
  <!-- Overlay que cobre toda a tela quando show=true -->
  <div class="loading-overlay" v-if="show">
    <div class="loading-spinner">
      <!-- Spinner com animação de rotação -->
      <div class="spinner"></div>
      <!-- Mensagem opcional -->
      <p v-if="message" class="loading-message">{{ message }}</p>
    </div>
  </div>
</template>

<script setup>
/**
 * Definição das props do componente
 *
 * O Vue 3 com <script setup> permite definir props
 * diretamente com defineProps()
 */
defineProps({
  show: {
    type: Boolean,
    default: false,
    // Controla se o spinner deve ser exibido
    required: false
  },
  message: {
    type: String,
    default: '',
    // Mensagem opcional para exibir abaixo do spinner
    required: false
  }
})
</script>

<style scoped>
/*
  Estilos do LoadingSpinner

  Usa position fixed para criar um overlay sobre toda a aplicação
*/

/* Overlay que cobre toda a tela */
.loading-overlay {
  position: fixed;          /* Fica fixo na tela */
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(255, 255, 255, 0.8); /* Fundo semi-transparente */
  display: flex;
  justify-content: center;  /* Centraliza horizontalmente */
  align-items: center;      /* Centraliza verticalmente */
  z-index: 9999;           /* Fica sobre outros elementos */
}

/* Container do spinner e mensagem */
.loading-spinner {
  text-align: center;
}

/* O spinner propriamente dito */
.spinner {
  border: 4px solid #f3f3f3;           /* Borda cinza clara */
  border-top: 4px solid #007bff;       /* Borda azul na parte superior */
  border-radius: 50%;                  /* Torna circular */
  width: 40px;
  height: 40px;
  animation: spin 2s linear infinite;   /* Animação contínua */
  margin: 0 auto 10px;                 /* Centraliza e adiciona margem inferior */
}

/* Animação de rotação do spinner */
@keyframes spin {
  0% { transform: rotate(0deg); }      /* Inicia sem rotação */
  100% { transform: rotate(360deg); }  /* Termina com rotação completa */
}

/* Estilo da mensagem de loading */
.loading-message {
  color: #666;         /* Cor cinza suave */
  font-size: 14px;     /* Tamanho pequeno */
  margin: 0;           /* Remove margens padrão */
}
</style>
