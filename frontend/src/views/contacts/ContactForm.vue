<template>
  <div class="contact-form">
    <div class="form-header">
      <h2>{{ isEditing ? 'Редактирование контакта' : 'Новый контакт' }}</h2>
      <p class="form-subtitle">
        {{ isEditing ? 'Измените настройки контакта' : 'Добавьте доверенное лицо' }}
      </p>
    </div>

    <form @submit.prevent="handleSubmit" class="form-content">
      <!-- ID пользователя (только для создания) -->
      <div v-if="!isEditing" class="form-row">
        <TextField
          v-model="form.contactId"
          label="ID пользователя"
          placeholder="Введите ID пользователя (можно найти в профиле)"
          required
          :error="errors.contactId"
          clearable
        />
        <p class="field-hint">
          ID можно скопировать из профиля пользователя или попросить его сообщить вам
        </p>
      </div>

      <!-- Никнейм -->
      <div class="form-row">
        <TextField
          v-model="form.nickname"
          label="Никнейм (как вы называете этого человека)"
          placeholder="Например: Сын Петя, Доктор Иванова"
          :error="errors.nickname"
          clearable
        />
      </div>

      <!-- Настройки для существующего контакта -->
      <div v-if="isEditing" class="settings-section">
        <h3>Настройки общения</h3>

        <div class="form-row">
          <label class="checkbox-label">
            <input
              type="checkbox"
              v-model="form.isActive"
            >
            <span>Контакт активен</span>
          </label>
          <p class="field-hint" v-if="!form.isActive">
            Если снять галочку, контакт не сможет писать вам
          </p>
        </div>

        <div class="form-row">
          <label class="checkbox-label">
            <input
              type="checkbox"
              v-model="form.canSendMessages"
            >
            <span>Может отправлять сообщения</span>
          </label>
        </div>

        <div class="form-row">
          <label class="checkbox-label">
            <input
              type="checkbox"
              v-model="form.canReceiveNotifications"
            >
            <span>Получать уведомления от этого контакта</span>
          </label>
        </div>
      </div>

      <!-- Информация о контакте (только для редактирования) -->
      <div v-if="isEditing && originalContact" class="edit-info">
        <div class="info-item">
          <span class="info-label">ID контакта:</span>
          <span class="info-value">{{ originalContact.contactId }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">ФИО:</span>
          <span class="info-value">{{ originalContact.fullName }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">Телефон:</span>
          <span class="info-value">{{ originalContact.phone }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">Email:</span>
          <span class="info-value">{{ originalContact.email }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">Добавлен:</span>
          <span class="info-value">{{ originalContact.formattedAddedAt }}</span>
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
          {{ isEditing ? 'Сохранить изменения' : 'Добавить контакт' }}
        </Button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import TextField from '@/components/UI/TextField.vue'
import Button from '@/components/UI/Button.vue'

const props = defineProps({
  contact: {
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
const originalContact = ref(null)

// Форма
const form = reactive({
  contactId: '',
  nickname: '',
  isActive: true,
  canSendMessages: true,
  canReceiveNotifications: true
})

// Ошибки
const errors = reactive({
  contactId: '',
  nickname: ''
})

// Загружаем данные при редактировании
onMounted(() => {
  if (props.isEditing && props.contact) {
    originalContact.value = props.contact
    form.contactId = props.contact.contactId
    form.nickname = props.contact.nickname || ''
    form.isActive = props.contact.isActive
    form.canSendMessages = props.contact.canSendMessages
    form.canReceiveNotifications = props.contact.canReceiveNotifications
  }
})

// Валидация формы
const validateForm = () => {
  let isValid = true
  errors.contactId = ''
  errors.nickname = ''

  if (!props.isEditing && !form.contactId.trim()) {
    errors.contactId = 'Введите ID пользователя'
    isValid = false
  }

  if (form.nickname && form.nickname.length > 100) {
    errors.nickname = 'Никнейм не должен превышать 100 символов'
    isValid = false
  }

  return isValid
}

// Отправка формы
const handleSubmit = () => {
  if (!validateForm()) return

  submitting.value = true

  const formData = {
    nickname: form.nickname.trim() || null
  }

  if (!props.isEditing) {
    formData.contactId = form.contactId.trim()
  } else {
    // Для редактирования добавляем настройки
    if (form.isActive !== props.contact.isActive) {
      formData.isActive = form.isActive
    }
    if (form.canSendMessages !== props.contact.canSendMessages) {
      formData.canSendMessages = form.canSendMessages
    }
    if (form.canReceiveNotifications !== props.contact.canReceiveNotifications) {
      formData.canReceiveNotifications = form.canReceiveNotifications
    }
  }

  emit('submit', formData)

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
.contact-form {
  width: 100%;
  max-width: 500px;
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

.field-hint {
  color: var(--color-gray, #9e9e9e);
  font-size: 12px;
  margin: 4px 0 0 0;
  font-style: italic;
}

/* Настройки */
.settings-section {
  background-color: var(--color-white-smoke, #f5f5f5);
  border-radius: 8px;
  padding: 1.5rem;
}

.settings-section h3 {
  color: var(--color-primary-dark, #6aa91c);
  font-size: 16px;
  margin: 0 0 1rem 0;
}

/* Чекбоксы */
.checkbox-label {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  font-size: 14px;
  color: var(--color-dark-gray, #616161);
  padding: 4px 0;
}

.checkbox-label input[type="checkbox"] {
  width: 18px;
  height: 18px;
  cursor: pointer;
  accent-color: var(--color-primary, #88d424);
}

/* Информация о контакте */
.edit-info {
  background-color: var(--color-white-smoke, #f5f5f5);
  border-radius: 8px;
  padding: 1rem;
  display: flex;
  flex-direction: column;
  gap: 8px;
  font-size: 14px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.info-label {
  color: var(--color-gray, #9e9e9e);
  font-weight: 500;
  min-width: 100px;
}

.info-value {
  color: var(--color-dark-gray, #616161);
  word-break: break-word;
  flex: 1;
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
  .contact-form {
    padding: 1.5rem;
  }

  .form-header h2 {
    font-size: 24px;
  }

  .info-item {
    flex-direction: column;
    align-items: flex-start;
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
