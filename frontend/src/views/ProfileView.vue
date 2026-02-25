<template>
  <div class="profile-page">
    <div class="profile-card">
      <div class="profile-header">
        <h2>Мой профиль</h2>
        <p>Управление личными данными</p>
      </div>

      <!-- Табы для переключения между разделами -->
      <div class="profile-tabs">
        <button
          class="tab-button"
          :class="{ active: activeTab === 'profile' }"
          @click="switchToProfileTab"
        >
          Личные данные
        </button>
        <button
          class="tab-button"
          :class="{ active: activeTab === 'password' }"
          @click="switchToPasswordTab"
        >
          Сменить пароль
        </button>
        <button
          class="tab-button"
          :class="{ active: activeTab === 'telegram' }"
          @click="switchToTelegramTab"
        >
          Telegram
        </button>
        <button
          class="tab-button"
          :class="{ active: activeTab === 'deactivate' }"
          @click="switchToDeactivateTab"
        >
          Деактивация
        </button>
      </div>

      <!-- Загрузка -->
      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <p>Загрузка данных...</p>
      </div>

      <!-- Вкладка с личными данными -->
      <div v-else-if="activeTab === 'profile'" class="tab-content">
        <!-- Режим просмотра -->
        <div v-if="!isEditing" class="profile-view">
          <div class="info-grid">
            <div class="info-item">
              <span class="info-label">Электронная почта:</span>
              <span class="info-value">{{ userData?.email || '—' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">Номер телефона:</span>
              <span class="info-value">{{ userData?.phone || '—' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">Фамилия:</span>
              <span class="info-value">{{ userData?.surname || '—' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">Имя:</span>
              <span class="info-value">{{ userData?.name || '—' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">Отчество:</span>
              <span class="info-value">{{ userData?.middleName || '—' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">Дата рождения:</span>
              <span class="info-value">{{ formatDate(userData?.birthDate) || '—' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">Пол:</span>
              <span class="info-value">{{ getSexLabel(userData?.sex?.id) || '—' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">Роль:</span>
              <span class="info-value">{{ userData?.role?.name || '—' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">Адрес проживания:</span>
              <span class="info-value">{{ userData?.address || '—' }}</span>
            </div>
            <div class="info-item description-item">
              <span class="info-label">О себе:</span>
              <span class="info-value">{{ userData?.description || '—' }}</span>
            </div>
          </div>

          <div class="form-actions">
            <Button
              variant="primary"
              @click="startEditing"
            >
              Изменить данные
            </Button>
          </div>
        </div>

        <!-- Режим редактирования -->
        <form v-else @submit.prevent="handleUpdateProfile" class="profile-form">
          <!-- Неизменяемые поля (только для чтения) -->
          <div class="form-row">
            <TextField
              v-model="profileForm.email"
              label="Электронная почта"
              type="email"
              :disabled="true"
              hint="Email нельзя изменить"
            />
          </div>

          <div class="form-row">
            <TextField
              v-model="profileForm.phone"
              label="Номер телефона"
              type="tel"
              :disabled="true"
              hint="Номер телефона нельзя изменить"
            />
          </div>

          <!-- Изменяемые поля -->
          <div class="form-row">
            <TextField
              v-model="profileForm.surname"
              label="Фамилия"
              placeholder="Иванов"
              required
              :error="errors.surname"
              clearable
            />
          </div>

          <div class="form-row">
            <TextField
              v-model="profileForm.name"
              label="Имя"
              placeholder="Иван"
              required
              :error="errors.name"
              clearable
            />
          </div>

          <div class="form-row">
            <TextField
              v-model="profileForm.middleName"
              label="Отчество"
              placeholder="Иванович"
              :error="errors.middleName"
              clearable
            />
          </div>

          <div class="form-row">
            <DateInput
              v-model="profileForm.birthDate"
              label="Дата рождения"
              required
              :error="errors.birthDate"
            />
          </div>

          <div class="form-row">
            <Select
              v-model="profileForm.sex"
              label="Пол"
              :options="sexOptions"
              required
              :error="errors.sex"
            />
          </div>

          <!-- Роль (только для чтения) -->
          <div class="form-row">
            <TextField
              v-model="profileForm.roleName"
              label="Роль"
              :disabled="true"
              hint="Роль определяется при регистрации"
            />
          </div>

          <div class="form-row">
            <TextField
              v-model="profileForm.address"
              label="Адрес проживания"
              placeholder="г. Москва, ул. Ленина, д. 1, кв. 1"
              :error="errors.address"
              clearable
            />
          </div>

          <div class="form-row">
            <TextArea
              v-model="profileForm.description"
              label="О себе"
              placeholder="Расскажите немного о себе..."
              :error="errors.description"
              rows="3"
            />
          </div>

          <div class="form-actions edit-actions">
            <Button
              type="button"
              variant="outline"
              @click="cancelEditing"
            >
              Отменить изменения
            </Button>
            <Button
              type="submit"
              variant="primary"
              :loading="updatingProfile"
            >
              Сохранить изменения
            </Button>
          </div>
        </form>
      </div>

      <!-- Вкладка со сменой пароля -->
      <div v-else-if="activeTab === 'password'" class="tab-content">
        <form @submit.prevent="handleChangePassword" class="profile-form">
          <div class="form-row">
            <PasswordField
              v-model="passwordForm.currentPassword"
              label="Текущий пароль"
              placeholder="Введите текущий пароль"
              required
              :error="errors.currentPassword"
            />
          </div>

          <div class="form-row">
            <PasswordField
              v-model="passwordForm.newPassword"
              label="Новый пароль"
              placeholder="Придумайте новый пароль"
              required
              :error="errors.newPassword"
            />
          </div>

          <div class="form-row">
            <PasswordField
              v-model="passwordForm.confirmNewPassword"
              label="Подтверждение нового пароля"
              placeholder="Повторите новый пароль"
              required
              :error="errors.confirmNewPassword"
            />
          </div>

          <div class="form-actions">
            <Button
              type="submit"
              variant="primary"
              :loading="updatingPassword"
            >
              Сменить пароль
            </Button>
          </div>
        </form>
      </div>

      <div v-else-if="activeTab === 'telegram'" class="tab-content">
        <TelegramBinding />
      </div>

      <!-- Вкладка деактивации -->
      <div v-else-if="activeTab === 'deactivate'" class="tab-content">
        <div class="deactivate-warning">
          <div class="warning-icon">⚠️</div>
          <h3>Внимание!</h3>
          <p>После деактивации аккаунта вход в систему станет невозможен.</p>
          <p class="note">Все ваши данные будут сохранены, а для повторной активации достаточно будет снова войти в аккаунт.</p>
        </div>

        <form @submit.prevent="handleDeactivate" class="profile-form">
          <div class="form-row">
            <TextArea
              v-model="deactivateForm.reason"
              label="Причина деактивации"
              placeholder="Укажите причину деактивации аккаунта (необязательно)"
              :error="errors.deactivateReason"
              rows="3"
            />
          </div>

          <div class="form-row">
            <PasswordField
              v-model="deactivateForm.password"
              label="Подтверждение паролем"
              placeholder="Введите ваш пароль для подтверждения"
              required
              :error="errors.deactivatePassword"
            />
          </div>

          <div class="form-actions deactivate-actions">
            <Button
              type="submit"
              variant="error"
              :loading="deactivating"
              block
            >
              Деактивировать аккаунт
            </Button>
          </div>
        </form>
      </div>

      <!-- Информация о статусе аккаунта -->
      <div class="account-status" v-if="userData">
        <div class="status-item">
          <span class="status-label">Дата регистрации:</span>
          <span class="status-value">{{ formatDateTime(userData.createdAt) }}</span>
        </div>
        <div class="status-item">
          <span class="status-label">Последний вход:</span>
          <span class="status-value">{{ userData.lastLoginAt ? formatDateTime(userData.lastLoginAt) : 'Не выполнялся' }}</span>
        </div>
        <div class="status-item" v-if="!userData.active">
          <span class="status-label">Дата деактивации:</span>
          <span class="status-value">{{ formatDateTime(userData.deactivatedAt) || '—' }}</span>
        </div>
        <div class="status-item" v-if="!userData.active && userData.deactivationReason">
          <span class="status-label">Причина деактивации:</span>
          <span class="status-value">{{ userData.deactivationReason }}</span>
        </div>
        <div class="status-item">
          <span class="status-label">Статус аккаунта:</span>
          <span class="status-value" :class="{ 'status-active': userData.active, 'status-inactive': !userData.active }">
            {{ userData.active ? 'Активен' : 'Деактивирован' }}
          </span>
        </div>
      </div>
    </div>

    <!-- Уведомления -->
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
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import TextField from '@/components/UI/TextField.vue'
import TextArea from '@/components/UI/TextArea.vue'
import DateInput from '@/components/UI/DateInput.vue'
import Select from '@/components/UI/Select.vue'
import PasswordField from '@/components/UI/PasswordField.vue'
import Button from '@/components/UI/Button.vue'
import Notification from '@/components/UI/Notification.vue'
import TelegramBinding from '@/components/TelegramBinding.vue'
import userService from '@/services/userService'
import authService from '@/services/authService'

const router = useRouter()
const activeTab = ref('profile')
const isEditing = ref(false)
const loading = ref(true)
const updatingProfile = ref(false)
const updatingPassword = ref(false)
const deactivating = ref(false)
const showNotification = ref(false)
const notificationMessage = ref('')
const notificationType = ref('success')
const userData = ref(null)

const errors = reactive({
  surname: '',
  name: '',
  middleName: '',
  birthDate: '',
  sex: '',
  address: '',
  description: '',
  currentPassword: '',
  newPassword: '',
  confirmNewPassword: '',
  deactivateReason: '',
  deactivatePassword: ''
})

// Опции для выпадающих списков
const sexOptions = [
  { value: '6b426722-1c17-4900-ab99-f3374a8e3880', label: 'Мужской' },
  { value: '64b6d07b-6198-4c12-976b-9ebb507680cd', label: 'Женский' }
]

// Форма профиля
const profileForm = reactive({
  email: '',
  phone: '',
  surname: '',
  name: '',
  middleName: '',
  birthDate: '',
  sex: '',
  roleName: '',
  address: '',
  description: ''
})

// Форма смены пароля
const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmNewPassword: ''
})

// Форма деактивации
const deactivateForm = reactive({
  reason: '',
  password: ''
})

// Показать уведомление
const showNotificationMessage = (type, message) => {
  notificationType.value = type
  notificationMessage.value = message
  showNotification.value = true
}

// Форматирование даты (для даты рождения)
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return new Intl.DateTimeFormat('ru-RU', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  }).format(date)
}

// Форматирование даты и времени (для created_at и last_login_at)
const formatDateTime = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return new Intl.DateTimeFormat('ru-RU', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  }).format(date)
}

// Получение названия пола по ID
const getSexLabel = (sexId) => {
  if (!sexId) return '—'
  const sex = sexOptions.find(option => option.value === sexId)
  return sex ? sex.label : '—'
}

// Переключение на вкладку профиля
const switchToProfileTab = () => {
  activeTab.value = 'profile'
  isEditing.value = false
}

// Переключение на вкладку смены пароля
const switchToPasswordTab = () => {
  activeTab.value = 'password'
  isEditing.value = false
  // Очищаем ошибки и форму пароля
  Object.keys(errors).forEach(key => {
    if (['currentPassword', 'newPassword', 'confirmNewPassword'].includes(key)) {
      errors[key] = ''
    }
  })
  passwordForm.currentPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmNewPassword = ''
}

// Переключение на вкладку Telegram
const switchToTelegramTab = () => {
  activeTab.value = 'telegram'
  isEditing.value = false
}

// Переключение на вкладку деактивации
const switchToDeactivateTab = () => {
  activeTab.value = 'deactivate'
  isEditing.value = false
  // Очищаем ошибки и форму деактивации
  errors.deactivateReason = ''
  errors.deactivatePassword = ''
  deactivateForm.reason = ''
  deactivateForm.password = ''
}

// Начало редактирования
const startEditing = () => {
  // Сохраняем текущие данные в форму
  profileForm.surname = userData.value?.surname || ''
  profileForm.name = userData.value?.name || ''
  profileForm.middleName = userData.value?.middleName || ''
  profileForm.birthDate = userData.value?.birthDate || ''
  profileForm.sex = userData.value?.sex?.id || ''
  profileForm.address = userData.value?.address || ''
  profileForm.description = userData.value?.description || ''

  isEditing.value = true
}

// Отмена редактирования
const cancelEditing = () => {
  isEditing.value = false
  // Очищаем ошибки
  Object.keys(errors).forEach(key => {
    if (!['currentPassword', 'newPassword', 'confirmNewPassword', 'deactivateReason', 'deactivatePassword'].includes(key)) {
      errors[key] = ''
    }
  })
}

// Загрузка данных пользователя
const loadUserData = async () => {
  loading.value = true
  try {
    const userEmail = localStorage.getItem('userEmail')

    if (!userEmail) {
      showNotificationMessage('error', 'Не удалось определить пользователя')
      router.push('/login')
      return
    }

    const response = await userService.getUserByEmail(userEmail)
    userData.value = response

    // Заполняем форму для редактирования
    profileForm.email = response.email || ''
    profileForm.phone = response.phone || ''
    profileForm.surname = response.surname || ''
    profileForm.name = response.name || ''
    profileForm.middleName = response.middleName || ''
    profileForm.birthDate = response.birthDate || ''
    profileForm.sex = response.sex?.id || ''
    profileForm.roleName = response.role?.name || ''
    profileForm.address = response.address || ''
    profileForm.description = response.description || ''

  } catch (error) {
    console.error('❌ Ошибка загрузки профиля:', error)
    if (error.response?.status === 404) {
      showNotificationMessage('error', 'Пользователь не найден')
      router.push('/login')
    } else {
      showNotificationMessage('error', 'Ошибка загрузки данных профиля')
    }
  } finally {
    loading.value = false
  }
}

// Валидация формы профиля
const validateProfileForm = () => {
  Object.keys(errors).forEach(key => {
    if (!['currentPassword', 'newPassword', 'confirmNewPassword', 'deactivateReason', 'deactivatePassword'].includes(key)) {
      errors[key] = ''
    }
  })

  let isValid = true

  if (!profileForm.surname) {
    errors.surname = 'Введите фамилию'
    isValid = false
  } else if (profileForm.surname.length < 2) {
    errors.surname = 'Фамилия должна содержать минимум 2 символа'
    isValid = false
  }

  if (!profileForm.name) {
    errors.name = 'Введите имя'
    isValid = false
  } else if (profileForm.name.length < 2) {
    errors.name = 'Имя должно содержать минимум 2 символа'
    isValid = false
  }

  if (profileForm.middleName && profileForm.middleName.length < 2) {
    errors.middleName = 'Отчество должно содержать минимум 2 символа'
    isValid = false
  }

  if (!profileForm.birthDate) {
    errors.birthDate = 'Введите дату рождения'
    isValid = false
  } else {
    const birthDate = new Date(profileForm.birthDate)
    const today = new Date()
    const age = today.getFullYear() - birthDate.getFullYear()
    if (age < 18) {
      errors.birthDate = 'Возраст должен быть не менее 18 лет'
      isValid = false
    }
  }

  if (!profileForm.sex) {
    errors.sex = 'Выберите пол'
    isValid = false
  }

  return isValid
}

// Валидация формы смены пароля
const validatePasswordForm = () => {
  errors.currentPassword = ''
  errors.newPassword = ''
  errors.confirmNewPassword = ''

  let isValid = true

  if (!passwordForm.currentPassword) {
    errors.currentPassword = 'Введите текущий пароль'
    isValid = false
  }

  if (!passwordForm.newPassword) {
    errors.newPassword = 'Введите новый пароль'
    isValid = false
  } else if (passwordForm.newPassword.length < 8) {
    errors.newPassword = 'Пароль должен быть не менее 8 символов'
    isValid = false
  } else if (!/(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*])/.test(passwordForm.newPassword)) {
    errors.newPassword = 'Пароль должен содержать заглавные, строчные буквы, цифры и спецсимволы'
    isValid = false
  }

  if (!passwordForm.confirmNewPassword) {
    errors.confirmNewPassword = 'Подтвердите новый пароль'
    isValid = false
  } else if (passwordForm.newPassword !== passwordForm.confirmNewPassword) {
    errors.confirmNewPassword = 'Пароли не совпадают'
    isValid = false
  }

  return isValid
}

// Валидация формы деактивации
const validateDeactivateForm = () => {
  errors.deactivatePassword = ''

  let isValid = true

  if (!deactivateForm.password) {
    errors.deactivatePassword = 'Введите пароль для подтверждения'
    isValid = false
  }

  return isValid
}

// Обновление профиля
const handleUpdateProfile = async () => {
  if (!validateProfileForm()) return

  updatingProfile.value = true

  try {
    const updateData = {
      surname: profileForm.surname,
      name: profileForm.name,
      middleName: profileForm.middleName || null,
      birthDate: profileForm.birthDate,
      sex: {
        id: profileForm.sex
      },
      phone: profileForm.phone,
      address: profileForm.address || null,
      description: profileForm.description || null
    }

    const response = await userService.updateUser(userData.value.id, updateData)
    console.log('✅ Профиль обновлён:', response)

    userData.value = response
    showNotificationMessage('success', 'Данные профиля успешно обновлены')
    isEditing.value = false

  } catch (error) {
    console.error('❌ Ошибка обновления профиля:', error)

    if (error.response?.status === 409) {
      showNotificationMessage('error', 'Данный номер телефона уже занят')
    } else {
      showNotificationMessage('error', error.response?.data?.message || 'Ошибка обновления профиля')
    }
  } finally {
    updatingProfile.value = false
  }
}

// Смена пароля
const handleChangePassword = async () => {
  if (!validatePasswordForm()) return

  updatingPassword.value = true

  try {
    const passwordData = {
      currentPassword: passwordForm.currentPassword,
      newPassword: passwordForm.newPassword,
      confirmNewPassword: passwordForm.confirmNewPassword
    }

    await userService.changePassword(userData.value.id, passwordData)
    console.log('✅ Пароль изменён')

    showNotificationMessage('success', 'Пароль успешно изменён')

    passwordForm.currentPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmNewPassword = ''

  } catch (error) {
    console.error('❌ Ошибка смены пароля:', error)

    if (error.response?.status === 400) {
      const message = error.response.data?.message || ''
      if (message.includes('текущий пароль')) {
        errors.currentPassword = 'Неверный текущий пароль'
      } else {
        showNotificationMessage('error', message || 'Ошибка смены пароля')
      }
    } else {
      showNotificationMessage('error', 'Ошибка смены пароля')
    }
  } finally {
    updatingPassword.value = false
  }
}

// Деактивация аккаунта
const handleDeactivate = async () => {
  if (!validateDeactivateForm()) return

  // Добавляем подтверждение
  if (!confirm('Вы уверены, что хотите деактивировать аккаунт? Это действие можно отменить повторным входом в систему.')) {
    return
  }

  deactivating.value = true

  try {
    // Сначала проверяем пароль через попытку входа
    try {
      await authService.login(userData.value.email, deactivateForm.password)
    } catch (error) {
      errors.deactivatePassword = 'Неверный пароль'
      deactivating.value = false
      return
    }

    // Если пароль верный, деактивируем аккаунт
    await userService.deactivateUser(userData.value.id, deactivateForm.reason)
    console.log('✅ Аккаунт деактивирован')

    showNotificationMessage('success', 'Аккаунт успешно деактивирован')

    // Выходим из системы
    authService.logout()

    // Перенаправляем на главную через 2 секунды
    setTimeout(() => {
      router.push('/')
    }, 2000)

  } catch (error) {
    console.error('❌ Ошибка деактивации:', error)
    showNotificationMessage('error', error.response?.data?.message || 'Ошибка деактивации аккаунта')
  } finally {
    deactivating.value = false
  }
}

// При монтировании загружаем данные
onMounted(() => {
  loadUserData()
})
</script>

<style scoped>
.profile-page {
  width: 100%;
  display: flex;
  justify-content: center;
  padding: 2rem 1rem;
}

.profile-card {
  width: 100%;
  max-width: 700px;
  background: var(--color-white, #ffffff);
  border-radius: 16px;
  padding: 2.5rem;
  box-shadow: 0 4px 20px var(--shadow-color, rgba(0,0,0,0.1));
}

.profile-header {
  text-align: center;
  margin-bottom: 2rem;
}

.profile-header h2 {
  font-size: 28px;
  font-weight: 600;
  color: var(--color-primary-dark, #6aa91c);
  margin: 0 0 8px;
}

.profile-header p {
  color: var(--color-gray, #9e9e9e);
  margin: 0;
}

.profile-tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 2rem;
  border-bottom: 1px solid var(--color-light-gray, #e0e0e0);
  padding-bottom: 10px;
  flex-wrap: wrap;
}

.tab-button {
  background: none;
  border: none;
  padding: 10px 20px;
  font-size: 16px;
  font-weight: 500;
  color: var(--color-gray, #9e9e9e);
  cursor: pointer;
  transition: all 0.2s ease;
  border-radius: 8px;
}

.tab-button:hover {
  color: var(--color-primary, #88d424);
  background-color: rgba(136, 212, 36, 0.1);
}

.tab-button.active {
  color: var(--color-primary, #88d424);
  background-color: rgba(136, 212, 36, 0.1);
}

.tab-content {
  margin-bottom: 2rem;
}

/* Стили для режима просмотра */
.profile-view {
  margin-bottom: 2rem;
}

.info-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 2rem;
}

.info-item {
  display: flex;
  padding: 8px 0;
  border-bottom: 1px solid var(--color-light-gray, #e0e0e0);
}

.info-item:last-child {
  border-bottom: none;
}

.info-label {
  min-width: 150px;
  font-weight: 500;
  color: var(--color-gray, #9e9e9e);
}

.info-value {
  flex: 1;
  color: var(--color-dark-gray, #616161);
}

.description-item .info-value {
  white-space: pre-wrap;
  line-height: 1.6;
}

/* Стили для формы */
.profile-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.form-row {
  width: 100%;
}

.form-actions {
  margin-top: 1rem;
  display: flex;
  justify-content: flex-end;
}

.edit-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.deactivate-actions {
  margin-top: 2rem;
}

/* Стили для предупреждения */
.deactivate-warning {
  background-color: rgba(244, 67, 54, 0.1);
  border: 1px solid var(--color-error, #f44336);
  border-radius: 12px;
  padding: 1.5rem;
  margin-bottom: 2rem;
  text-align: center;
}

.warning-icon {
  font-size: 48px;
  margin-bottom: 1rem;
}

.deactivate-warning h3 {
  color: var(--color-error, #f44336);
  font-size: 20px;
  margin-bottom: 0.5rem;
}

.deactivate-warning p {
  color: var(--color-dark-gray, #616161);
  margin-bottom: 0.5rem;
}

.deactivate-warning .note {
  color: var(--color-gray, #9e9e9e);
  font-size: 14px;
  font-style: italic;
}

/* Стили для загрузки */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 3rem;
  color: var(--color-gray, #9e9e9e);
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid var(--color-light-gray, #e0e0e0);
  border-top-color: var(--color-primary, #88d424);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 1rem;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* Стили для статуса аккаунта */
.account-status {
  margin-top: 2rem;
  padding-top: 1.5rem;
  border-top: 1px solid var(--color-light-gray, #e0e0e0);
}

.status-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  font-size: 14px;
}

.status-label {
  color: var(--color-gray, #9e9e9e);
}

.status-value {
  color: var(--color-dark-gray, #616161);
  font-weight: 500;
}

.status-active {
  color: var(--color-success, #4caf50);
}

.status-inactive {
  color: var(--color-error, #f44336);
}

@media (max-width: 768px) {
  .profile-card {
    padding: 1.5rem;
  }

  .profile-tabs {
    flex-direction: column;
  }

  .info-item {
    flex-direction: column;
    gap: 4px;
  }

  .info-label {
    min-width: auto;
  }

  .edit-actions {
    flex-direction: column;
  }

  .status-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
}
</style>
