<template>
  <div class="user-info-card">
    <div class="card-header">
      <div class="header-left">
        <span class="header-icon">👤</span>
        <h3>Мои данные</h3>
      </div>
      <Button
        v-if="!isEditing"
        variant="text"
        size="small"
        @click="startEditing"
      >
        ✏️ Редактировать
      </Button>
    </div>

    <!-- Режим просмотра -->
    <div v-if="!isEditing" class="info-content">
      <div class="info-grid">
        <div class="info-row">
          <span class="info-label">ФИО:</span>
          <span class="info-value">{{ fullName }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">Возраст:</span>
          <span class="info-value">{{ userData?.age ? userData.age + ' лет' : '—' }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">Дата рождения:</span>
          <span class="info-value">{{ formatDate(userData?.birthDate) || '—' }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">Пол:</span>
          <span class="info-value">{{ getSexLabel(userData?.sex?.id) || '—' }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">Роль:</span>
          <span class="info-value">{{ userData?.role?.name || '—' }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">Email:</span>
          <span class="info-value">{{ userData?.email || '—' }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">Телефон:</span>
          <span class="info-value">{{ userData?.phone || '—' }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">Адрес:</span>
          <span class="info-value">{{ userData?.address || '—' }}</span>
        </div>
        <div v-if="userData?.description" class="info-row description-row">
          <span class="info-label">О себе:</span>
          <span class="info-value">{{ userData.description }}</span>
        </div>
      </div>
    </div>

    <!-- Режим редактирования -->
    <form v-else @submit.prevent="handleUpdate" class="edit-form">
      <div class="form-grid">
        <div class="form-row">
          <TextField
            v-model="editForm.surname"
            label="Фамилия"
            placeholder="Иванов"
            required
            :error="errors.surname"
            size="small"
          />
        </div>

        <div class="form-row">
          <TextField
            v-model="editForm.name"
            label="Имя"
            placeholder="Иван"
            required
            :error="errors.name"
            size="small"
          />
        </div>

        <div class="form-row">
          <TextField
            v-model="editForm.middleName"
            label="Отчество"
            placeholder="Иванович"
            :error="errors.middleName"
            size="small"
          />
        </div>

        <div class="form-row">
          <DateInput
            v-model="editForm.birthDate"
            label="Дата рождения"
            required
            :error="errors.birthDate"
          />
        </div>

        <div class="form-row">
          <Select
            v-model="editForm.sex"
            label="Пол"
            :options="sexOptions"
            required
            :error="errors.sex"
            size="small"
          />
        </div>

        <div class="form-row">
          <TextField
            v-model="editForm.address"
            label="Адрес"
            placeholder="г. Москва, ул. Ленина, д. 1"
            :error="errors.address"
            size="small"
          />
        </div>

        <div class="form-row full-width">
          <TextArea
            v-model="editForm.description"
            label="О себе"
            placeholder="Расскажите немного о себе..."
            :error="errors.description"
            rows="2"
          />
        </div>
      </div>

      <div class="form-actions">
        <Button
          type="button"
          variant="outline"
          size="small"
          @click="cancelEditing"
        >
          Отмена
        </Button>
        <Button
          type="submit"
          variant="primary"
          size="small"
          :loading="updating"
        >
          Сохранить
        </Button>
      </div>
    </form>

    <!-- Уведомления (встроенные) -->
    <div v-if="notification.show" class="notification-banner" :class="notification.type">
      {{ notification.message }}
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import TextField from '@/components/UI/TextField.vue'
import TextArea from '@/components/UI/TextArea.vue'
import DateInput from '@/components/UI/DateInput.vue'
import Select from '@/components/UI/Select.vue'
import Button from '@/components/UI/Button.vue'
import userService from '@/services/userService'

const props = defineProps({
  userData: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['update'])

const isEditing = ref(false)
const updating = ref(false)
const notification = reactive({
  show: false,
  type: 'success',
  message: ''
})

const errors = reactive({
  surname: '',
  name: '',
  middleName: '',
  birthDate: '',
  sex: '',
  address: '',
  description: ''
})

// Опции для выпадающих списков
const sexOptions = [
  { value: '6b426722-1c17-4900-ab99-f3374a8e3880', label: 'Мужской' },
  { value: '64b6d07b-6198-4c12-976b-9ebb507680cd', label: 'Женский' }
]

// Форма редактирования
const editForm = reactive({
  surname: '',
  name: '',
  middleName: '',
  birthDate: '',
  sex: '',
  address: '',
  description: ''
})

// Полное имя для отображения
const fullName = computed(() => {
  const parts = [
    props.userData?.surname,
    props.userData?.name,
    props.userData?.middleName
  ].filter(Boolean)
  return parts.join(' ') || '—'
})

// Следим за изменением userData и обновляем форму
watch(() => props.userData, (newData) => {
  if (newData) {
    editForm.surname = newData.surname || ''
    editForm.name = newData.name || ''
    editForm.middleName = newData.middleName || ''
    editForm.birthDate = newData.birthDate || ''
    editForm.sex = newData.sex?.id || ''
    editForm.address = newData.address || ''
    editForm.description = newData.description || ''
  }
}, { immediate: true })

// Форматирование даты
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return new Intl.DateTimeFormat('ru-RU', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  }).format(date)
}

// Получение названия пола по ID
const getSexLabel = (sexId) => {
  if (!sexId) return '—'
  const sex = sexOptions.find(option => option.value === sexId)
  return sex ? sex.label : '—'
}

// Начало редактирования
const startEditing = () => {
  isEditing.value = true
}

// Отмена редактирования
const cancelEditing = () => {
  isEditing.value = false
  // Сбрасываем ошибки
  Object.keys(errors).forEach(key => errors[key] = '')
}

// Показать уведомление
const showNotification = (type, message) => {
  notification.type = type
  notification.message = message
  notification.show = true

  setTimeout(() => {
    notification.show = false
  }, 3000)
}

// Валидация формы
const validateForm = () => {
  let isValid = true
  Object.keys(errors).forEach(key => errors[key] = '')

  if (!editForm.surname.trim()) {
    errors.surname = 'Введите фамилию'
    isValid = false
  } else if (editForm.surname.length < 2) {
    errors.surname = 'Минимум 2 символа'
    isValid = false
  }

  if (!editForm.name.trim()) {
    errors.name = 'Введите имя'
    isValid = false
  } else if (editForm.name.length < 2) {
    errors.name = 'Минимум 2 символа'
    isValid = false
  }

  if (editForm.middleName && editForm.middleName.length < 2) {
    errors.middleName = 'Минимум 2 символа'
    isValid = false
  }

  if (!editForm.birthDate) {
    errors.birthDate = 'Выберите дату'
    isValid = false
  } else {
    const birthDate = new Date(editForm.birthDate)
    const today = new Date()
    const age = today.getFullYear() - birthDate.getFullYear()
    if (age < 18) {
      errors.birthDate = 'Возраст должен быть не менее 18 лет'
      isValid = false
    }
  }

  if (!editForm.sex) {
    errors.sex = 'Выберите пол'
    isValid = false
  }

  return isValid
}

// Обновление данных
const handleUpdate = async () => {
  if (!validateForm()) return

  updating.value = true

  try {
    const updateData = {
      surname: editForm.surname.trim(),
      name: editForm.name.trim(),
      middleName: editForm.middleName.trim() || null,
      birthDate: editForm.birthDate,
      sex: {
        id: editForm.sex
      },
      phone: props.userData.phone, // Не меняется
      address: editForm.address.trim() || null,
      description: editForm.description.trim() || null
    }

    const updatedUser = await userService.updateUser(props.userData.id, updateData)

    showNotification('success', 'Данные успешно обновлены')
    isEditing.value = false
    emit('update', updatedUser)

  } catch (error) {
    console.error('❌ Ошибка обновления:', error)

    if (error.response?.status === 409) {
      showNotification('error', 'Данный номер телефона уже занят')
    } else {
      showNotification('error', error.response?.data?.message || 'Ошибка при обновлении')
    }
  } finally {
    updating.value = false
  }
}
</script>

<style scoped>
.user-info-card {
  background: var(--color-white, #ffffff);
  border-radius: 16px;
  padding: 1.5rem;
  box-shadow: 0 4px 12px var(--shadow-color, rgba(0,0,0,0.1));
  transition: all 0.3s ease;
  height: fit-content;
}

.user-info-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px var(--shadow-color, rgba(0,0,0,0.15));
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  padding-bottom: 0.75rem;
  border-bottom: 2px solid var(--color-primary, #88d424);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-icon {
  font-size: 24px;
}

.card-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-primary-dark, #6aa91c);
  margin: 0;
}

/* Режим просмотра */
.info-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-row {
  display: flex;
  padding: 8px 0;
  border-bottom: 1px solid var(--color-light-gray, #e0e0e0);
}

.info-row:last-child {
  border-bottom: none;
}

.info-label {
  min-width: 100px;
  font-size: 14px;
  font-weight: 500;
  color: var(--color-gray, #9e9e9e);
}

.info-value {
  flex: 1;
  font-size: 14px;
  color: var(--color-dark-gray, #616161);
  word-break: break-word;
}

.description-row .info-value {
  line-height: 1.6;
}

/* Режим редактирования */
.edit-form {
  margin-top: 1rem;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.form-row {
  width: 100%;
}

.form-row.full-width {
  grid-column: span 2;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 1rem;
  border-top: 1px solid var(--color-light-gray, #e0e0e0);
}

/* Уведомления */
.notification-banner {
  margin-top: 1rem;
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 14px;
  animation: slideDown 0.3s ease;
}

.notification-banner.success {
  background-color: rgba(76, 175, 80, 0.1);
  color: var(--color-success, #4caf50);
  border: 1px solid var(--color-success, #4caf50);
}

.notification-banner.error {
  background-color: rgba(244, 67, 54, 0.1);
  color: var(--color-error, #f44336);
  border: 1px solid var(--color-error, #f44336);
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Адаптивность */
@media (max-width: 768px) {
  .form-grid {
    grid-template-columns: 1fr;
  }

  .form-row.full-width {
    grid-column: span 1;
  }

  .info-row {
    flex-direction: column;
    gap: 4px;
  }

  .info-label {
    min-width: auto;
  }

  .form-actions {
    flex-direction: column;
  }

  .form-actions button {
    width: 100%;
  }
}
</style>
