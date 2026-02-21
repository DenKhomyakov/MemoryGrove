<template>
  <div class="register-page">
    <div class="register-card">
      <div class="auth-header">
        <h2>Регистрация</h2>
        <p>Создайте новую учётную запись</p>
      </div>

      <!-- Сообщение об ошибке -->
      <div v-if="errors.form" class="error-banner">
        {{ errors.form }}
      </div>

      <!-- Сообщение об успехе -->
      <div v-if="successMessage" class="success-banner">
        {{ successMessage }}
      </div>

      <form @submit.prevent="handleRegister" class="auth-form">
        <div class="form-row">
          <TextField
            v-model="registerForm.email"
            label="Электронная почта"
            type="email"
            placeholder="example@mail.com"
            required
            :error="errors.email"
            clearable
          />
        </div>

        <div class="form-row">
          <TextField
            v-model="registerForm.phone"
            label="Номер телефона"
            type="tel"
            placeholder="+7 (999) 999-99-99"
            required
            :error="errors.phone"
            clearable
          />
        </div>

        <div class="form-row">
          <TextField
            v-model="registerForm.surname"
            label="Фамилия"
            placeholder="Иванов"
            required
            :error="errors.surname"
            clearable
          />
        </div>

        <div class="form-row">
          <TextField
            v-model="registerForm.name"
            label="Имя"
            placeholder="Иван"
            required
            :error="errors.name"
            clearable
          />
        </div>

        <div class="form-row">
          <TextField
            v-model="registerForm.middleName"
            label="Отчество"
            placeholder="Иванович"
            :error="errors.middleName"
            clearable
          />
        </div>

        <div class="form-row">
          <DateInput
            v-model="registerForm.birthDate"
            label="Дата рождения"
            required
            :error="errors.birthDate"
          />
        </div>

        <div class="form-row">
          <Select
            v-model="registerForm.sex"
            label="Пол"
            :options="sexOptions"
            required
            :error="errors.sex"
          />
        </div>

        <div class="form-row">
          <Select
            v-model="registerForm.role"
            label="Роль"
            :options="roleOptions"
            required
            :error="errors.role"
          />
        </div>

        <div class="form-row">
          <TextField
            v-model="registerForm.address"
            label="Адрес проживания"
            placeholder="г. Москва, ул. Ленина, д. 1, кв. 1"
            :error="errors.address"
            clearable
          />
        </div>

        <div class="form-row">
          <TextArea
            v-model="registerForm.description"
            label="О себе"
            placeholder="Расскажите немного о себе..."
            :error="errors.description"
            rows="3"
          />
        </div>

        <div class="form-row">
          <PasswordField
            v-model="registerForm.password"
            label="Пароль"
            placeholder="Придумайте пароль"
            required
            :error="errors.password"
          />
        </div>

        <div class="form-row">
          <PasswordField
            v-model="registerForm.confirmPassword"
            label="Подтверждение пароля"
            placeholder="Повторите пароль"
            required
            :error="errors.confirmPassword"
          />
        </div>

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
import userService from '@/services/userService'

const router = useRouter()
const loading = ref(false)
const successMessage = ref('')
const errors = reactive({
  form: '',
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

// Опции для выпадающих списков
const sexOptions = [
  { value: '6b426722-1c17-4900-ab99-f3374a8e3880', label: 'Мужской' },
  { value: '64b6d07b-6198-4c12-976b-9ebb507680cd', label: 'Женский' }
]

const roleOptions = [
  { value: '931ff329-8baa-4353-abbc-a0f389e9b07d', label: 'Пациент' },
  { value: 'b5541f4d-1517-4848-8ea0-1e306570633b', label: 'Доктор' },
  { value: 'f722aa50-e167-4f30-a6d5-17becd3b1028', label: 'Член семьи' },
  { value: 'a4d6d594-898c-4cef-97a4-85893df09488', label: 'Вторая половинка / партнёр' },
  { value: '6ecb8283-f720-473b-9b89-5b0d2ece01d3', label: 'Друг' },
  { value: 'ba1171bd-57d5-4a72-97f5-638be6295a2a', label: 'Коллега' },
  { value: 'f3dc7b85-d1b1-4246-bb22-4103070fb697', label: 'Другой вариант' }
]

// Форма регистрации
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

// Валидация формы
const validateForm = () => {
  // Очищаем предыдущие ошибки
  Object.keys(errors).forEach(key => {
    if (key !== 'form') errors[key] = ''
  })
  errors.form = ''

  let isValid = true

  // Email
  if (!registerForm.email) {
    errors.email = 'Введите email'
    isValid = false
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(registerForm.email)) {
    errors.email = 'Некорректный email'
    isValid = false
  }

  // Телефон
  if (!registerForm.phone) {
    errors.phone = 'Введите номер телефона'
    isValid = false
  } else if (!/^\+?[1-9]\d{1,14}$/.test(registerForm.phone)) {
    errors.phone = 'Некорректный формат телефона'
    isValid = false
  }

  // Фамилия
  if (!registerForm.surname) {
    errors.surname = 'Введите фамилию'
    isValid = false
  } else if (registerForm.surname.length < 2) {
    errors.surname = 'Фамилия должна содержать минимум 2 символа'
    isValid = false
  }

  // Имя
  if (!registerForm.name) {
    errors.name = 'Введите имя'
    isValid = false
  } else if (registerForm.name.length < 2) {
    errors.name = 'Имя должна содержать минимум 2 символа'
    isValid = false
  }

  // Отчество (опционально, но если есть - проверяем длину)
  if (registerForm.middleName && registerForm.middleName.length < 2) {
    errors.middleName = 'Отчество должно содержать минимум 2 символа'
    isValid = false
  }

  // Дата рождения
  if (!registerForm.birthDate) {
    errors.birthDate = 'Введите дату рождения'
    isValid = false
  } else {
    const birthDate = new Date(registerForm.birthDate)
    const today = new Date()
    const age = today.getFullYear() - birthDate.getFullYear()
    if (age < 18) {
      errors.birthDate = 'Возраст должен быть не менее 18 лет'
      isValid = false
    }
  }

  // Пол
  if (!registerForm.sex) {
    errors.sex = 'Выберите пол'
    isValid = false
  }

  // Роль
  if (!registerForm.role) {
    errors.role = 'Выберите роль'
    isValid = false
  }

  // Пароль
  if (!registerForm.password) {
    errors.password = 'Введите пароль'
    isValid = false
  } else if (registerForm.password.length < 8) {
    errors.password = 'Пароль должен быть не менее 8 символов'
    isValid = false
  } else if (!/(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*])/.test(registerForm.password)) {
    errors.password = 'Пароль должен содержать заглавные, строчные буквы, цифры и спецсимволы'
    isValid = false
  }

  // Подтверждение пароля
  if (!registerForm.confirmPassword) {
    errors.confirmPassword = 'Подтвердите пароль'
    isValid = false
  } else if (registerForm.password !== registerForm.confirmPassword) {
    errors.confirmPassword = 'Пароли не совпадают'
    isValid = false
  }

  return isValid
}

// Отправка формы
const handleRegister = async () => {
  if (!validateForm()) return

  loading.value = true
  successMessage.value = ''
  errors.form = ''

  try {
    // Формируем данные для отправки на бэкенд
    const userData = {
      surname: registerForm.surname,
      name: registerForm.name,
      middleName: registerForm.middleName || null,
      birthDate: registerForm.birthDate,
      sex: {
        id: registerForm.sex
      },
      role: {
        id: registerForm.role
      },
      email: registerForm.email,
      phone: registerForm.phone,
      address: registerForm.address || null,
      description: registerForm.description || null,
      password: registerForm.password,
      confirmPassword: registerForm.confirmPassword
    }

    console.log('📤 Отправляем данные на регистрацию:', userData)

    const response = await userService.register(userData)

    console.log('✅ Регистрация успешна!', response)
    successMessage.value = 'Регистрация прошла успешно! Сейчас вы будете перенаправлены на страницу входа.'

    // Через 2 секунды перенаправляем на страницу входа
    setTimeout(() => {
      router.push('/login')
    }, 2000)

  } catch (error) {
    console.error('❌ Ошибка регистрации:', error)

    if (error.response) {
      // Обработка разных кодов ошибок
      switch (error.response.status) {
        case 400:
          errors.form = error.response.data?.message || 'Некорректные данные'
          break
        case 409:
          errors.form = 'Пользователь с таким email или телефоном уже существует'
          break
        default:
          errors.form = 'Ошибка сервера. Попробуйте позже.'
      }
    } else if (error.request) {
      errors.form = 'Нет ответа от сервера. Проверьте подключение.'
    } else {
      errors.form = 'Ошибка при отправке запроса'
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
  width: 100%;
  display: flex;
  justify-content: center;
  padding: 2rem 1rem;
}

.register-card {
  width: 100%;
  max-width: 700px;
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

.error-banner {
  background-color: rgba(244, 67, 54, 0.1);
  color: var(--color-error, #f44336);
  padding: 12px 16px;
  border-radius: 8px;
  margin-bottom: 20px;
  border: 1px solid var(--color-error, #f44336);
  font-size: 14px;
}

.success-banner {
  background-color: rgba(76, 175, 80, 0.1);
  color: var(--color-success, #4caf50);
  padding: 12px 16px;
  border-radius: 8px;
  margin-bottom: 20px;
  border: 1px solid var(--color-success, #4caf50);
  font-size: 14px;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-bottom: 2rem;
}

.form-row {
  width: 100%;
}

.form-actions {
  margin-top: 1rem;
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

@media (max-width: 768px) {
  .register-card {
    padding: 1.5rem;
  }
}
</style>
