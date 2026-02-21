<template>
  <div class="register-page">
    <div class="register-container">
      <div class="register-card">
        <div class="auth-header">
          <h2>Регистрация</h2>
          <p>Создайте новую учётную запись</p>
        </div>

        <form @submit.prevent="handleRegister" class="auth-form">
          <TextField
            v-model="registerForm.email"
            label="Электронная почта"
            type="email"
            placeholder="Введите вашу почту"
            required
            :error="errors.email"
            clearable
          />

          <TextField
            v-model="registerForm.phone"
            label="Номер телефона"
            type="tel"
            placeholder="+7 (999) 999-99-99"
            required
            :error="errors.phone"
            clearable
          />

          <TextField
            v-model="registerForm.surname"
            label="Фамилия"
            placeholder="Введите фамилию"
            required
            :error="errors.surname"
            clearable
          />

          <TextField
            v-model="registerForm.name"
            label="Имя"
            placeholder="Введите имя"
            required
            :error="errors.name"
            clearable
          />

          <TextField
            v-model="registerForm.middleName"
            label="Отчество"
            placeholder="Введите отчество"
            :error="errors.middleName"
            clearable
          />

          <DateInput
            v-model="registerForm.birthDate"
            label="Дата рождения"
            required
            :error="errors.birthDate"
          />

          <Select
            v-model="registerForm.sex"
            label="Пол"
            :options="sexOptions"
            required
            :error="errors.sex"
          />

          <Select
            v-model="registerForm.role"
            label="Роль"
            :options="roleOptions"
            required
            :error="errors.role"
          />

          <TextField
            v-model="registerForm.address"
            label="Адрес"
            placeholder="Введите адрес"
            :error="errors.address"
            clearable
          />

          <TextArea
            v-model="registerForm.description"
            label="Описание"
            placeholder="Расскажите о себе"
            :error="errors.description"
            rows="3"
          />

          <PasswordField
            v-model="registerForm.password"
            label="Пароль"
            placeholder="Придумайте пароль"
            required
            :error="errors.password"
          />

          <PasswordField
            v-model="registerForm.confirmPassword"
            label="Подтверждение пароля"
            placeholder="Повторите пароль"
            required
            :error="errors.confirmPassword"
          />

          <div class="form-actions">
            <Button
              type="submit"
              variant="primary"
              :loading="loading"
              block
            >
              Зарегистрироваться
            </Button>
          </div>
        </form>

        <div class="auth-switch">
          <span>
            Уже есть аккаунт?
            <router-link to="/login" class="link-button">
              Войти
            </router-link>
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import TextField from '@/components/UI/TextField.vue'
import TextArea from '@/components/UI/TextArea.vue'
import DateInput from '@/components/UI/DateInput.vue'
import Select from '@/components/UI/Select.vue'
import PasswordField from '@/components/UI/PasswordField.vue'
import Button from '@/components/UI/Button.vue'
import authService from '@/services/authService'
import userService from '@/services/userService'

const router = useRouter()
const loading = ref(false)
const errors = reactive({})

const sexOptions = [
  { value: '6b426722-1c17-4900-ab99-f3374a8e3880', label: 'Мужской' },
  { value: '64b6d07b-6198-4c12-976b-9ebb507680cd', label: 'Женский' }
]

const roleOptions = [
  { value: '931ff329-8baa-4353-abbc-a0f389e9b07d', label: 'Пациент' },
  { value: 'b5541f4d-1517-4848-8ea0-1e306570633b', label: 'Доктор' },
  { value: 'f722aa50-e167-4f30-a6d5-17becd3b1028', label: 'Член семьи' },
  { value: 'a4d6d594-898c-4cef-97a4-85893df09488', label: 'Партнёр' },
  { value: '6ecb8283-f720-473b-9b89-5b0d2ece01d3', label: 'Друг' },
  { value: 'ba1171bd-57d5-4a72-97f5-638be6295a2a', label: 'Коллега' }
]

const registerForm = reactive({
  email: '',
  phone: '',
  surname: '',
  name: '',
  middleName: '',
  birthDate: '',
  sex: '',
  role: '',
  address: '',
  description: '',
  password: '',
  confirmPassword: ''
})

const validateForm = () => {
  Object.keys(errors).forEach(key => delete errors[key])
  let isValid = true

  if (!registerForm.email) {
    errors.email = 'Введите email'
    isValid = false
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(registerForm.email)) {
    errors.email = 'Некорректный email'
    isValid = false
  }

  if (!registerForm.phone) {
    errors.phone = 'Введите номер телефона'
    isValid = false
  }

  if (!registerForm.surname) {
    errors.surname = 'Введите фамилию'
    isValid = false
  }

  if (!registerForm.name) {
    errors.name = 'Введите имя'
    isValid = false
  }

  if (!registerForm.birthDate) {
    errors.birthDate = 'Введите дату рождения'
    isValid = false
  }

  if (!registerForm.sex) {
    errors.sex = 'Выберите пол'
    isValid = false
  }

  if (!registerForm.role) {
    errors.role = 'Выберите роль'
    isValid = false
  }

  if (!registerForm.password) {
    errors.password = 'Введите пароль'
    isValid = false
  } else if (registerForm.password.length < 8) {
    errors.password = 'Пароль должен быть не менее 8 символов'
    isValid = false
  }

  if (!registerForm.confirmPassword) {
    errors.confirmPassword = 'Подтвердите пароль'
    isValid = false
  } else if (registerForm.password !== registerForm.confirmPassword) {
    errors.confirmPassword = 'Пароли не совпадают'
    isValid = false
  }

  return isValid
}

const handleRegister = async () => {
  if (!validateForm()) return

  loading.value = true
  try {
    // Подготавливаем данные для отправки
    const userData = {
      ...registerForm,
      sex: { id: registerForm.sex },
      role: { id: registerForm.role }
    }

    await userService.register(userData)
    // После успешной регистрации переходим на страницу входа
    router.push('/login')
  } catch (error) {
    errors.form = error.response?.data?.message || 'Ошибка регистрации'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
  min-height: calc(100vh - 80px);
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding: 2rem 1rem;
  background: var(--color-white-smoke, #f5f5f5);
}

.register-container {
  max-width: 600px;
  width: 100%;
}

.register-card {
  background: var(--color-white, #ffffff);
  border-radius: 16px;
  padding: 2.5rem;
  box-shadow: 0 4px 20px var(--shadow-color, rgba(0,0,0,0.1));
}

.auth-header {
  text-align: center;
  margin-bottom: 2rem;
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
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-bottom: 2rem;
}

.form-actions {
  margin-top: 1rem;
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
  padding-top: 1.5rem;
  border-top: 1px solid var(--color-light-gray, #e0e0e0);
  color: var(--color-gray, #9e9e9e);
  font-size: 14px;
}

.auth-switch .link-button {
  margin-left: 5px;
}

@media (max-width: 768px) {
  .register-card {
    padding: 1.5rem;
  }
}
</style>
