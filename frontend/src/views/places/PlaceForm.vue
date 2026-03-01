<template>
  <div class="place-form">
    <div class="form-header">
      <h2>{{ isEditing ? 'Редактирование места' : 'Новое место' }}</h2>
      <p class="form-subtitle">
        {{ isEditing ? 'Измените информацию о месте' : 'Добавьте важное для вас место' }}
      </p>
    </div>

    <form @submit.prevent="handleSubmit" class="form-content">
      <!-- Название -->
      <div class="form-row">
        <TextField
          v-model="form.title"
          label="Название места"
          placeholder="Например: Дом, Поликлиника, Любимый парк"
          required
          :error="errors.title"
          clearable
        />
      </div>

      <!-- Адрес -->
      <div class="form-row">
        <TextArea
          v-model="form.address"
          label="Адрес"
          placeholder="Введите полный адрес"
          required
          :error="errors.address"
          rows="2"
        />
      </div>

      <!-- Категория и специальные флаги -->
      <div class="form-row">
        <div class="category-section">
          <Select
            v-model="form.category"
            label="Категория"
            :options="categoryOptions"
            :error="errors.category"
          />
        </div>
      </div>

      <!-- Специальные типы мест -->
      <div class="form-row">
        <label class="checkbox-label">
          <input
            type="checkbox"
            v-model="form.isHome"
            @change="handleSpecialFlagChange('home')"
          >
          <span>🏠 Это мой домашний адрес</span>
        </label>
      </div>

      <div class="form-row">
        <label class="checkbox-label">
          <input
            type="checkbox"
            v-model="form.isWork"
            @change="handleSpecialFlagChange('work')"
          >
          <span>💼 Это моё место работы/учёбы</span>
        </label>
      </div>

      <div class="form-row">
        <label class="checkbox-label">
          <input
            type="checkbox"
            v-model="form.isMedical"
            @change="handleSpecialFlagChange('medical')"
          >
          <span>🏥 Это медицинское учреждение</span>
        </label>
      </div>

      <!-- Описание -->
      <div class="form-row">
        <TextArea
          v-model="form.description"
          label="Описание (необязательно)"
          placeholder="Дополнительная информация: часы работы, контактные данные, особенности..."
          :error="errors.description"
          rows="3"
        />
      </div>

      <!-- Информация о создании (для редактирования) -->
      <div v-if="isEditing && originalPlace" class="edit-info">
        <div class="info-item">
          <span class="info-label">Создано:</span>
          <span class="info-value">{{ originalPlace.formattedCreatedAt }}</span>
        </div>
        <div v-if="originalPlace.updatedAt" class="info-item">
          <span class="info-label">Изменено:</span>
          <span class="info-value">{{ originalPlace.formattedUpdatedAt }}</span>
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
          {{ isEditing ? 'Сохранить изменения' : 'Создать место' }}
        </Button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import TextField from '@/components/UI/TextField.vue'
import TextArea from '@/components/UI/TextArea.vue'
import Select from '@/components/UI/Select.vue'
import Button from '@/components/UI/Button.vue'

const props = defineProps({
  place: {
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
const originalPlace = ref(null)

// Опции категорий
const categoryOptions = [
  { value: '', label: 'Без категории' },
  { value: 'Дом', label: '🏠 Дом' },
  { value: 'Медицина', label: '🏥 Медицина' },
  { value: 'Работа', label: '💼 Работа' },
  { value: 'Отдых', label: '🌳 Отдых' },
  { value: 'Образование', label: '📚 Образование' },
  { value: 'Другое', label: '📍 Другое' }
]

// Форма
const form = reactive({
  title: '',
  address: '',
  description: '',
  category: '',
  isHome: false,
  isWork: false,
  isMedical: false
})

// Ошибки
const errors = reactive({
  title: '',
  address: '',
  description: '',
  category: ''
})

// Обработка специальных флагов (чтобы они не конфликтовали)
const handleSpecialFlagChange = (flag) => {
  if (flag === 'home' && form.isHome) {
    form.category = 'Дом'
  } else if (flag === 'work' && form.isWork) {
    form.category = 'Работа'
  } else if (flag === 'medical' && form.isMedical) {
    form.category = 'Медицина'
  }
}

// Загружаем данные при редактировании
onMounted(() => {
  if (props.isEditing && props.place) {
    originalPlace.value = props.place
    form.title = props.place.title || ''
    form.address = props.place.address || ''
    form.description = props.place.description || ''
    form.category = props.place.category || ''
    form.isHome = props.place.isHome || false
    form.isWork = props.place.isWork || false
    form.isMedical = props.place.isMedical || false
  }
})

// Валидация формы
const validateForm = () => {
  let isValid = true
  errors.title = ''
  errors.address = ''
  errors.description = ''
  errors.category = ''

  if (!form.title.trim()) {
    errors.title = 'Введите название места'
    isValid = false
  } else if (form.title.length < 2) {
    errors.title = 'Название должно содержать минимум 2 символа'
    isValid = false
  }

  if (!form.address.trim()) {
    errors.address = 'Введите адрес'
    isValid = false
  }

  return isValid
}

// Отправка формы
const handleSubmit = () => {
  if (!validateForm()) return

  submitting.value = true

  const placeData = {
    title: form.title.trim(),
    address: form.address.trim(),
    description: form.description.trim() || null,
    category: form.category || null,
    isHome: form.isHome,
    isWork: form.isWork,
    isMedical: form.isMedical
  }

  emit('submit', placeData)

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
.place-form {
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

.category-section {
  margin-bottom: 0.5rem;
}

/* Чекбоксы */
.checkbox-label {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  font-size: 14px;
  color: var(--color-dark-gray, #616161);
  padding: 8px 0;
}

.checkbox-label input[type="checkbox"] {
  width: 18px;
  height: 18px;
  cursor: pointer;
  accent-color: var(--color-primary, #88d424);
}

.checkbox-label span {
  user-select: none;
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
  .place-form {
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
