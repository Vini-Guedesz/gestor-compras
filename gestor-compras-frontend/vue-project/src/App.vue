<!--
  Componente raiz da aplicação

  Este é o componente principal que envolve toda a aplicação.
  Ele define:
  - A estrutura básica do HTML
  - Os estilos globais da aplicação
  - O ponto de montagem para as diferentes páginas/rotas
-->
<script setup>
// Este componente não possui lógica JavaScript específica,
// serve apenas como container principal
</script>

<template>
  <div id="app">
    <!-- RouterView renderiza o componente correspondente à rota atual -->
    <!-- Cada página (Login, Dashboard, etc.) será exibida aqui -->
    <RouterView />
  </div>
</template>

<style>
/* Reset CSS global - remove margens e paddings padrão dos navegadores */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box; /* Inclui padding e border no cálculo da largura total */
}

/* Estilos base para o corpo da página */
body {
  font-family: Arial, sans-serif;
  background-color: #f8f9fa; /* Cor de fundo clara */
}

/* Container principal da aplicação */
#app {
  width: 100%;
  min-height: 100vh; /* Garante que a aplicação ocupe pelo menos a altura da tela */
}
</style>
