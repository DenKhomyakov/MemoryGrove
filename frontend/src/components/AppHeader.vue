<template>
  <header class="app-header">
    <div class="header-container">
      <div class="header-content">
        <router-link to="/" class="logo">
          <h1>🌳 MemoryGrove</h1>
          <span class="logo-subtitle">Роща памяти</span>
        </router-link>

        <nav class="main-nav">
          <router-link to="/" class="nav-link" active-class="active">Главная</router-link>
          <router-link to="/about" class="nav-link" active-class="active">О проекте</router-link>

          <div v-if="isAuthenticated" class="user-menu">
            <button @click="logout" class="nav-link logout-btn">
              Выйти
            </button>
          </div>
          <router-link v-else to="/login" class="nav-link login-btn">
            Войти
          </router-link>
        </nav>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import authService from '@/services/authService'

const router = useRouter()
const isAuthenticated = ref(false)

const checkAuth = () => {
  isAuthenticated.value = authService.isAuthenticated()
}

// Создаем кастомное событие для обновления авторизации
const handleAuthChange = () => {
  checkAuth()
}

onMounted(() => {
  checkAuth()
  window.addEventListener('auth-change', handleAuthChange)
  window.addEventListener('storage', checkAuth)
})

onUnmounted(() => {
  window.removeEventListener('auth-change', handleAuthChange)
  window.removeEventListener('storage', checkAuth)
})

const logout = () => {
  authService.logout()
  isAuthenticated.value = false
  // Триггерим событие обновления авторизации
  window.dispatchEvent(new Event('auth-change'))
  router.push('/login')
}
</script>

<style scoped>
.app-header {
  background-color: var(--color-white, #ffffff);
  border-bottom: 2px solid var(--color-primary, #88d424);
  box-shadow: 0 2px 8px var(--shadow-color, rgba(0,0,0,0.1));
  position: sticky;
  top: 0;
  z-index: 100;
  width: 100%;
}

.header-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0;
}

.logo {
  text-decoration: none;
  display: flex;
  flex-direction: column;
}

.logo h1 {
  font-size: 24px;
  color: var(--color-primary-dark, #6aa91c);
  margin: 0;
  line-height: 1.2;
}

.logo-subtitle {
  font-size: 12px;
  color: var(--color-gray, #9e9e9e);
  margin-top: 2px;
}

.main-nav {
  display: flex;
  gap: 20px;
  align-items: center;
}

.nav-link {
  text-decoration: none;
  color: var(--color-dark-gray, #616161);
  font-weight: 500;
  padding: 8px 16px;
  border-radius: 8px;
  transition: all 0.2s ease;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 15px;
}

.nav-link:hover {
  color: var(--color-primary, #88d424);
  background-color: rgba(136, 212, 36, 0.1);
}

.nav-link.active {
  color: var(--color-primary, #88d424);
  background-color: rgba(136, 212, 36, 0.1);
}

.login-btn {
  background-color: var(--color-primary, #88d424);
  color: white !important;
}

.login-btn:hover {
  background-color: var(--color-primary-dark, #6aa91c);
  transform: translateY(-1px);
  box-shadow: 0 4px 8px var(--shadow-color, rgba(0,0,0,0.1));
  color: white !important;
}

.logout-btn {
  color: var(--color-error, #f44336);
}

.logout-btn:hover {
  background-color: rgba(244, 67, 54, 0.1);
  color: var(--color-error, #f44336);
}

.user-menu {
  display: flex;
  align-items: center;
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    gap: 15px;
  }

  .main-nav {
    flex-wrap: wrap;
    justify-content: center;
  }
}
</style>
