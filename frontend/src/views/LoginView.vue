<template>
  <div class="login-page">
    <Card class="login-card">
      <div class="auth-header">
        <h2>Вход</h2>
        <p>Войдите в свою учётную запись</p>
      </div>

      <form @submit.prevent="handleLogin" class="auth-form">
        <TextField
          v-model="loginForm.email"
          label="Электронная почта"
          type="email"
          placeholder="Введите вашу почту"
          required
          :error="errors.email"
          clearable
        />

        <PasswordField
          v-model="loginForm.password"
          label="Пароль"
          placeholder="Введите ваш пароль"
          required
          :error="errors.password"
        />

        <div class="form-actions">
          <button type="button" class="link-button">
            Забыли пароль?
          </button>
          <Button
            type="submit"
            variant="primary"
            :loading="loading"
          >
            Войти
          </Button>
        </div>
      </form>

      <div class="auth-switch">
        <span>
          Нет аккаунта?
          <router-link to="/register" class="link-button">
            Зарегистрироваться
          </router-link>
        </span>
      </div>
    </Card>

    <Notification
      v-if="showNotification"
      :type="notificationType"
      :message="notificationMessage"
      :duration="3000"
      @close="showNotification = false"
    />
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import Card from '@/components/UI/Card.vue'
import TextField from '@/components/UI/TextField.vue'
import PasswordField from '@/components/UI/PasswordField.vue'
import Button from '@/components/UI/Button.vue'
import Notification from '@/components/UI/Notification.vue'
import authService from '@/services/authService'

const router = useRouter()
const loading = ref(false)
const showNotification = ref(false)
const notificationMessage = ref('')
const notificationType = ref('error')
const errors = reactive({})

const loginForm = reactive({
  email: '',
  password: ''
})

const validateLogin = () => {
  Object.keys(errors).forEach(key => delete errors[key])
  let isValid = true

  if (!loginForm.email) {
    errors.email = 'Введите email'
    isValid = false
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(loginForm.email)) {
    errors.email = 'Некорректный email'
    isValid = false
  }

  if (!loginForm.password) {
    errors.password = 'Введите пароль'
    isValid = false
  } else if (loginForm.password.length < 8) {
    errors.password = 'Пароль должен быть не менее 8 символов'
    isValid = false
  }

  return isValid
}

const handleLogin = async () => {
  if (!validateLogin()) return

  loading.value = true
  try {
    await authService.login(loginForm.email, loginForm.password)
    router.push('/')
  } catch (error) {
    console.error('❌ Ошибка входа:', error)

    // Показываем уведомление об ошибке
    notificationMessage.value = 'Неверные учётные данные для входа'
    notificationType.value = 'error'
    showNotification.value = true

    errors.form = error.response?.data?.message || 'Ошибка входа'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: calc(100vh - 200px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.login-card {
  max-width: 400px;
  width: 100%;
  padding: 40px;
}

.auth-header {
  text-align: center;
  margin-bottom: 30px;
}

.auth-header h2 {
  font-size: 28px;
  font-weight: 600;
  color: var(--color-primary-dark, #6aa91c);
  margin: 0 0 8px;
}

.auth-header p {
  color: var(--color-gray, #9e9e9e);
  margin: 0;
}

.auth-form {
  margin-bottom: 20px;
}

.form-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 30px;
}

.link-button {
  background: none;
  border: none;
  color: var(--color-primary, #88d424);
  cursor: pointer;
  font-size: 14px;
  padding: 0;
  text-decoration: none;
  transition: color 0.2s ease;
}

.link-button:hover {
  color: var(--color-primary-dark, #6aa91c);
  text-decoration: underline;
}

.auth-switch {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid var(--color-light-gray, #e0e0e0);
  color: var(--color-gray, #9e9e9e);
  font-size: 14px;
}

.auth-switch .link-button {
  margin-left: 5px;
}
</style>
