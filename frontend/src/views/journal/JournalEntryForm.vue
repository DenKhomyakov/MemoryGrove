<template>
  <div class="entry-form">
    <div class="form-header">
      <h2>{{ isEditing ? 'Редактирование записи' : 'Новая запись' }}</h2>
      <p class="form-subtitle">{{ isEditing ? 'Измените содержание записи' : 'Поделитесь своими мыслями' }}</p>
    </div>

    <form @submit.prevent="handleSubmit" class="form-content">
      <!-- Заголовок -->
      <div class="form-row">
        <TextField
          v-model="form.title"
          label="Заголовок"
          placeholder="О чём вы думаете?"
          required
          :error="errors.title"
          clearable
        />
      </div>

      <!-- Содержание -->
      <div class="form-row">
        <TextArea
          v-model="form.content"
          label="Содержание"
          placeholder="Запишите свои мысли, чувства, воспоминания..."
          required
          :error="errors.content"
          rows="6"
        />
      </div>

      <!-- Настроение -->
      <div class="form-row">
        <TextField
          v-model="form.mood"
          label="Настроение (необязательно)"
          placeholder="😊 например: спокойное, радостное, задумчивое..."
          :error="errors.mood"
          clearable
        />
      </div>

      <!-- Дополнительная информация для редактирования -->
      <div v-if="isEditing && originalEntry" class="edit-info">
        <div class="info-item">
          <span class="info-label">Создано:</span>
          <span class="info-value">{{ originalEntry.formattedCreatedAt }}</span>
        </div>
        <div v-if="originalEntry.edited" class="info-item">
          <span class="info-label">Изменено:</span>
          <span class="info-value">{{ originalEntry.formattedUpdatedAt }}</span>
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
          {{ isEditing ? 'Сохранить изменения' : 'Создать запись' }}
        </Button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import TextField from '@/components/UI/TextField.vue'
import TextArea from '@/components/UI/TextArea.vue'
import Button from '@/components/UI/Button.vue'

const props = defineProps({
  entry: {
    type: Object,
    default: null
  },
  isEditing: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['submit', 'cancel', 'close'])

const router = useRouter()
const submitting = ref(false)
const originalEntry = ref(null)

const form = reactive({
  title: '',
  content: '',
  mood: ''
})

const errors = reactive({
  title: '',
  content: '',
  mood: ''
})

// Загружаем данные при редактировании
onMounted(() => {
  if (props.isEditing && props.entry) {
    originalEntry.value = props.entry
    form.title = props.entry.title || ''
    form.content = props.entry.content || ''
    form.mood = props.entry.mood || ''
  }
})

// Валидация формы
const validateForm = () => {
  let isValid = true
  errors.title = ''
  errors.content = ''
  errors.mood = ''

  if (!form.title.trim()) {
    errors.title = 'Введите заголовок'
    isValid = false
  } else if (form.title.length < 2) {
    errors.title = 'Заголовок должен содержать минимум 2 символа'
    isValid = false
  }

  if (!form.content.trim()) {
    errors.content = 'Введите содержание'
    isValid = false
  }

  return isValid
}

// Отправка формы
const handleSubmit = () => {
  if (!validateForm()) return

  submitting.value = true

  const entryData = {
    title: form.title.trim(),
    content: form.content.trim(),
    mood: form.mood.trim() || null
  }

  emit('submit', entryData)
}

// Отмена
const handleCancel = () => {
  emit('cancel')
  emit('close')
}
</script>

<style scoped>
.entry-form {
  width: 100%;
  max-width: 800px;
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
  .entry-form {
    padding: 1.5rem;
  }

  .form-header h2 {
    font-size: 24px;
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
