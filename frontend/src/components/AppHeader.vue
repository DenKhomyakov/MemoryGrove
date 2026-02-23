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
          <router-link v-if="isAuthenticated" to="/profile" class="nav-link" active-class="active">Мой профиль</router-link>
          <router-link to="/about" class="nav-link" active-class="active">О проекте</router-link>
          <router-link to="/help" class="nav-link" active-class="active">🫂 Помощь</router-link>

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

// Функция проверки авторизации
const checkAuth = () => {
  const newAuthState = authService.isAuthenticated()
  console.log('🔄 Проверка авторизации:', newAuthState ? 'авторизован' : 'не авторизован')
  isAuthenticated.value = newAuthState
}

// Слушаем изменения в localStorage
const handleStorageChange = (event) => {
  if (event.key === 'accessToken' || event.key === 'refreshToken') {
    console.log('🔄 Изменение в localStorage, обновляем состояние')
    checkAuth()
  }
}

// Кастомное событие для обновления авторизации
const handleAuthChange = () => {
  console.log('🔄 Получено событие auth-change')
  checkAuth()
}

onMounted(() => {
  checkAuth()

  // Слушаем изменения в localStorage (между вкладками)
  window.addEventListener('storage', handleStorageChange)

  // Слушаем кастомное событие (внутри текущей вкладки)
  window.addEventListener('auth-change', handleAuthChange)

  // Периодическая проверка (на всякий случай)
  const interval = setInterval(checkAuth, 1000)

  onUnmounted(() => {
    window.removeEventListener('storage', handleStorageChange)
    window.removeEventListener('auth-change', handleAuthChange)
    clearInterval(interval)
  })
})

const logout = () => {
  authService.logout()
  checkAuth()
  // Триггерим событие для других компонентов
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
  left: 0;
  right: 0;
  z-index: 1000;
  width: 100vw; /* Полная ширина viewport */
  margin: 0;
  padding: 0;
  transform: translateZ(0); /* Для фиксации позиции */
  backdrop-filter: blur(8px); /* Красивый эффект при прокрутке */
  background-color: rgba(255, 255, 255, 0.95); /* Полупрозрачный фон */
}

.header-container {
  max-width: 1400px;
  width: 100%;
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
