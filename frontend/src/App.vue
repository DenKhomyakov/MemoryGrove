<template>
  <div id="app">
    <AppHeader />
    <main class="main-content">
      <div class="content-container">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script setup>
import AppHeader from '@/components/AppHeader.vue'
</script>

<style>
*,
*::before,
*::after {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
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
}

.main-content {
  flex: 1;
  width: 100%;
  background: var(--color-white-smoke, #f5f5f5);
}

.content-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
  width: 100%;
}
</style>
