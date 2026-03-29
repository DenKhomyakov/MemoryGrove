<template>
  <div class="hello">
    <h1>MemoryGrove</h1>
    <h2>Тестирование связи с бэкендом</h2>

    <div class="test-section">
      <h3>1. Регистрация тестового пользователя</h3>
      <button @click="testRegister" :disabled="registerLoading">
        {{ registerLoading ? 'Регистрация...' : 'Зарегистрировать тестового пользователя' }}
      </button>
      <pre v-if="registerResult">{{ registerResult }}</pre>
    </div>

    <div class="test-section">
      <h3>2. Вход в систему</h3>
      <button @click="testLogin" :disabled="loginLoading">
        {{ loginLoading ? 'Вход...' : 'Войти' }}
      </button>
      <pre v-if="loginResult">{{ loginResult }}</pre>
    </div>

    <div class="test-section">
      <h3>3. Получение данных пользователя</h3>
      <button @click="testGetUser" :disabled="!isAuthenticated || getUserLoading">
        {{ getUserLoading ? 'Загрузка...' : 'Получить мои данные' }}
      </button>
      <pre v-if="userResult">{{ userResult }}</pre>
    </div>

    <div class="error" v-if="error">
      <h3>❌ Ошибка:</h3>
      <p>{{ error }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import userService from '@/services/userService'
import authService from '@/services/authService'

/**
 * Состояния
 */
const registerLoading = ref(false)
const loginLoading = ref(false)
const getUserLoading = ref(false)
const registerResult = ref(null)
const loginResult = ref(null)
const userResult = ref(null)
const error = ref(null)
const isAuthenticated = ref(authService.isAuthenticated())

/**
 * Тестовые данные
 */
const testUser = {
  surname: "Тестов",
  name: "Тест",
  middleName: "Тестович",
  birthDate: "1990-01-01",
  sex: {
    id: "6b426722-1c17-4900-ab99-f3374a8e3880" // Мужской
  },
  role: {
    id: "931ff329-8baa-4353-abbc-a0f389e9b07d" // Пациент
  },
  email: "test@example.com",
  phone: "+79001234567",
  address: "г. Москва, ул. Тестовая",
  description: "Тестовый пользователь",
  password: "Test123!@#",
  confirmPassword: "Test123!@#"
}

/**
 * Тест регистрации
 */
const testRegister = async () => {
  registerLoading.value = true
  error.value = null
  registerResult.value = null

  try {
    const result = await userService.register(testUser)
    registerResult.value = result
    console.log('✅ Регистрация прошла успешно')
  } catch (err) {
    if (err.response?.status === 409) {
      error.value = 'Пользователь уже существует. Используем его для теста.'
    } else {
      error.value = err.response?.data?.message || err.message
    }
  } finally {
    registerLoading.value = false
  }
}

/**
 * Тест входа
 */
const testLogin = async () => {
  loginLoading.value = true
  error.value = null
  loginResult.value = null

  try {
    const result = await authService.login(testUser.email, testUser.password)
    loginResult.value = result
    isAuthenticated.value = true
    console.log('✅ Вход выполнен успешно')
  } catch (err) {
    error.value = err.response?.data?.message || err.message
  } finally {
    loginLoading.value = false
  }
}

/**
 * Тест получения данных
 */
const testGetUser = async () => {
  getUserLoading.value = true
  error.value = null
  userResult.value = null

  try {
    const result = await userService.getUserByEmail(testUser.email)
    userResult.value = result
    console.log('✅ Данные пользователя получены')
  } catch (err) {
    error.value = err.response?.data?.message || err.message
  } finally {
    getUserLoading.value = false
  }
}
</script>

<style scoped>
.hello {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.test-section {
  margin: 30px 0;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background-color: #f9f9f9;
}

button {
  padding: 10px 20px;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  margin: 10px 0;
}

button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

pre {
  background-color: #2c3e50;
  color: #fff;
  padding: 15px;
  border-radius: 4px;
  overflow-x: auto;
  margin-top: 15px;
}

.error {
  margin-top: 30px;
  padding: 20px;
  background-color: #ffebee;
  border: 1px solid #ffcdd2;
  border-radius: 8px;
  color: #c62828;
}
</style>
