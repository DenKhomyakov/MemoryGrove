<template>
  <div id="app">
    <AppHeader />
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import AppHeader from '@/components/AppHeader.vue'
</script>

<style>
/* Сброс отступов у body и html */
html, body {
  margin: 0 !important;
  padding: 0 !important;
  width: 100%;
  overflow-x: hidden; /* Предотвращаем горизонтальный скролл */
}

*,
*::before,
*::after {
  box-sizing: border-box;
}

body {
  min-height: 100vh;
  color: var(--color-dark-gray, #616161);
  background: var(--color-white-smoke, #f5f5f5);
  line-height: 1.6;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu,
  Cantarell, 'Fira Sans', 'Droid Sans', 'Helvetica Neue', sans-serif;
  font-size: 15px;
  text-rendering: optimizeLegibility;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  width: 100%;
  margin: 0;
  padding: 0;
}

.main-content {
  flex: 1;
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
  background: var(--color-white-smoke, #f5f5f5);
}

/* Убираем отступы у контейнеров внутри main-content */
.main-content > * {
  width: 100%;
}
</style>
