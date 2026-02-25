<template>
  <div class="reminder-form">
    <div class="form-header">
      <h2>{{ isEditing ? 'Редактирование напоминания' : 'Новое напоминание' }}</h2>
      <p class="form-subtitle">
        {{ isEditing ? 'Измените детали напоминания' : 'Создайте напоминание о важном деле' }}
      </p>
    </div>

    <form @submit.prevent="handleSubmit" class="form-content">
      <!-- Заголовок -->
      <div class="form-row">
        <TextField
          v-model="form.title"
          label="Заголовок"
          placeholder="О чём нужно напомнить?"
          required
          :error="errors.title"
          clearable
        />
      </div>

      <!-- Описание -->
      <div class="form-row">
        <TextArea
          v-model="form.description"
          label="Описание (необязательно)"
          placeholder="Дополнительные детали, инструкции..."
          :error="errors.description"
          rows="3"
        />
      </div>

      <!-- Дата и время -->
      <div class="form-row">
        <div class="datetime-group">
          <DateInput
            v-model="form.date"
            label="Дата"
            required
            :error="errors.date"
            :min-date="today"
            max-date="2100-01-01"
          />
          <TimeInput
            v-model="form.time"
            label="Время"
            required
            :error="errors.time"
          />
        </div>
      </div>

      <!-- Статус (только для редактирования) -->
      <div v-if="isEditing" class="form-row">
        <Select
          v-model="form.statusId"
          label="Статус"
          :options="statusOptions"
          required
          :error="errors.statusId"
        />
      </div>

      <!-- Информация о создании (для редактирования) -->
      <div v-if="isEditing && originalReminder" class="edit-info">
        <div class="info-item">
          <span class="info-label">Создано:</span>
          <span class="info-value">{{ originalReminder.formattedCreatedAt }}</span>
        </div>
        <div v-if="originalReminder.updatedAt" class="info-item">
          <span class="info-label">Изменено:</span>
          <span class="info-value">{{ originalReminder.formattedUpdatedAt }}</span>
        </div>
      </div>

      <!-- Кнопки действий -->
      <div class="form-actions">
        <Button
          type="button"
          variant="outline"
          @click="handleCancel"
        >
          Отмена
        </Button>
        <Button
          type="submit"
          variant="primary"
          :loading="submitting"
        >
          {{ isEditing ? 'Сохранить изменения' : 'Создать напоминание' }}
        </Button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import TextField from '@/components/UI/TextField.vue'
import TextArea from '@/components/UI/TextArea.vue'
import DateInput from '@/components/UI/DateInput.vue'
import TimeInput from '@/components/UI/TimeInput.vue'
import Select from '@/components/UI/Select.vue'
import Button from '@/components/UI/Button.vue'
import reminderService from '@/services/reminderService'

const props = defineProps({
  reminder: {
    type: Object,
    default: null
  },
  isEditing: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['submit', 'cancel', 'close'])

const submitting = ref(false)
const originalReminder = ref(null)

// Получаем статусы из сервиса
const statuses = reminderService.constructor.Status || {
  PENDING: '6cc6ee82-b293-453b-a5df-94428f5f84ed',
  SENT: '5f699cae-62e2-431b-a460-e9d594938424',
  COMPLETED: '7665777b-e1e9-4be7-a8c5-fb2e7d457b20',
  CANCELLED: 'e6ebcf79-82ac-4d92-bb35-0ec71a3845cd'
}

// Сегодняшняя дата для ограничения выбора
const today = new Date().toISOString().split('T')[0]

// Опции статусов для выпадающего списка
const statusOptions = [
  { value: statuses.PENDING, label: 'Ожидает' },
  { value: statuses.SENT, label: 'Отправлено' },
  { value: statuses.COMPLETED, label: 'Выполнено' },
  { value: statuses.CANCELLED, label: 'Отменено' }
]

// Форма
const form = reactive({
  title: '',
  description: '',
  date: '',
  time: '',
  statusId: statuses.PENDING
})

// Ошибки
const errors = reactive({
  title: '',
  description: '',
  date: '',
  time: '',
  statusId: ''
})

// Комбинируем дату и время в один объект LocalDateTime
const combineDateTime = () => {
  if (!form.date || !form.time) return null
  // Добавляем секунды ":00" если их нет
  const timeWithSeconds = form.time.includes(':') ?
    (form.time.split(':').length === 2 ? form.time + ':00' : form.time) :
    form.time + ':00'
  return `${form.date}T${timeWithSeconds}`
}

// Разбираем LocalDateTime на дату и время
const parseDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return { date: '', time: '' }
  const [date, time] = dateTimeStr.split('T')
  return {
    date,
    time: time ? time.substring(0, 5) : '' // Берём только HH:MM
  }
}

// Загружаем данные при редактировании
onMounted(() => {
  if (props.isEditing && props.reminder) {
    originalReminder.value = props.reminder
    form.title = props.reminder.title || ''
    form.description = props.reminder.description || ''
    form.statusId = props.reminder.status?.id || statuses.PENDING

    const { date, time } = parseDateTime(props.reminder.remindAt)
    form.date = date
    form.time = time
  }
})

// Валидация формы
const validateForm = () => {
  let isValid = true
  errors.title = ''
  errors.description = ''
  errors.date = ''
  errors.time = ''
  errors.statusId = ''

  if (!form.title.trim()) {
    errors.title = 'Введите заголовок'
    isValid = false
  }

  if (!form.date) {
    errors.date = 'Выберите дату'
    isValid = false
  }

  if (!form.time) {
    errors.time = 'Выберите время'
    isValid = false
  }

  if (props.isEditing && !form.statusId) {
    errors.statusId = 'Выберите статус'
    isValid = false
  }

  return isValid
}

// Отправка формы
const handleSubmit = () => {
  if (!validateForm()) return

  submitting.value = true

  const reminderData = {
    title: form.title.trim(),
    description: form.description.trim() || null,
    remindAt: combineDateTime()
  }

  if (props.isEditing) {
    reminderData.statusId = form.statusId
  }

  emit('submit', reminderData)

  // Сбрасываем submitting через небольшую задержку
  setTimeout(() => {
    submitting.value = false
  }, 500)
}

// Отмена
const handleCancel = () => {
  emit('cancel')
  emit('close')
}
</script>

<style scoped>
.reminder-form {
  width: 100%;
  max-width: 600px;
  margin: 0 auto;
  background: var(--color-white, #ffffff);
  border-radius: 16px;
  padding: 2rem;
  box-shadow: 0 4px 20px var(--shadow-color, rgba(0,0,0,0.1));
}

.form-header {
  text-align: center;
  margin-bottom: 2rem;
  padding-bottom: 1rem;
  border-bottom: 2px solid var(--color-primary, #88d424);
}

.form-header h2 {
  font-size: 28px;
  font-weight: 600;
  color: var(--color-primary-dark, #6aa91c);
  margin: 0 0 8px;
}

.form-subtitle {
  color: var(--color-gray, #9e9e9e);
  margin: 0;
  font-size: 16px;
}

.form-content {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.form-row {
  width: 100%;
}

.datetime-group {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

/* Информация о редактировании */
.edit-info {
  background-color: var(--color-white-smoke, #f5f5f5);
  border-radius: 8px;
  padding: 1rem;
  display: flex;
  flex-wrap: wrap;
  gap: 1.5rem;
  font-size: 14px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.info-label {
  color: var(--color-gray, #9e9e9e);
  font-weight: 500;
}

.info-value {
  color: var(--color-dark-gray, #616161);
}

/* Кнопки */
.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 1rem;
  padding-top: 1rem;
  border-top: 1px solid var(--color-light-gray, #e0e0e0);
}

@media (max-width: 768px) {
  .reminder-form {
    padding: 1.5rem;
  }

  .form-header h2 {
    font-size: 24px;
  }

  .datetime-group {
    grid-template-columns: 1fr;
    gap: 0.5rem;
  }

  .edit-info {
    flex-direction: column;
    gap: 0.5rem;
  }

  .form-actions {
    flex-direction: column;
  }

  .form-actions button {
    width: 100%;
  }
}
</style>
