<template>
  <div class="skill-form">
    <div class="form-header">
      <h2>{{ isEditing ? 'Редактирование навыка' : 'Новый навык' }}</h2>
      <p class="form-subtitle">
        {{ isEditing ? 'Измените информацию о навыке' : 'Добавьте навык, который вы умели выполнять ранее' }}
      </p>
    </div>

    <form @submit.prevent="handleSubmit" class="form-content">
      <!-- Категория -->
      <div class="form-row">
        <Select
          v-model="form.category"
          label="Категория"
          :options="categoryOptions"
          required
          :error="errors.category"
        />
      </div>

      <!-- Название навыка -->
      <div class="form-row">
        <TextField
          v-model="form.title"
          label="Название навыка"
          :placeholder="getTitlePlaceholder"
          required
          :error="errors.title"
          clearable
        />
      </div>

      <!-- Описание -->
      <div class="form-row">
        <TextArea
          v-model="form.description"
          label="Описание"
          :placeholder="getDescriptionPlaceholder"
          :error="errors.description"
          rows="3"
        />
      </div>

      <!-- Сложность и важность (в одной строке) -->
      <div class="form-row">
        <div class="row-fields">
          <div class="rating-field">
            <label class="rating-label">Сложность</label>
            <div class="rating-stars">
              <span
                v-for="star in 5"
                :key="star"
                class="star"
                :class="{ active: star <= (form.difficulty || 0) }"
                @click="form.difficulty = star"
              >
                ★
              </span>
            </div>
            <div v-if="errors.difficulty" class="error-message">{{ errors.difficulty }}</div>
          </div>

          <div class="rating-field">
            <label class="rating-label">Важность</label>
            <div class="importance-dots">
              <span
                v-for="dot in 5"
                :key="dot"
                class="dot"
                :class="{ active: dot <= (form.importance || 0) }"
                @click="form.importance = dot"
              >
                ●
              </span>
            </div>
            <div v-if="errors.importance" class="error-message">{{ errors.importance }}</div>
          </div>
        </div>
      </div>

      <!-- Статус выполнения -->
      <div class="form-row">
        <label class="checkbox-label">
          <input
            type="checkbox"
            v-model="form.canDoNow"
          >
          <span>Я могу выполнять этот навык сейчас</span>
        </label>
        <p class="checkbox-hint" v-if="form.canDoNow">
          При отметке будет автоматически установлена текущая дата выполнения
        </p>
      </div>

      <!-- Заметки -->
      <div class="form-row">
        <TextArea
          v-model="form.notes"
          label="Заметки (необязательно)"
          placeholder="Наблюдения, особенности, что помогает или мешает..."
          :error="errors.notes"
          rows="2"
        />
      </div>

      <!-- Информация о создании (для редактирования) -->
      <div v-if="isEditing && originalSkill" class="edit-info">
        <div class="info-item">
          <span class="info-label">Создано:</span>
          <span class="info-value">{{ originalSkill.formattedCreatedAt }}</span>
        </div>
        <div v-if="originalSkill.formattedLastPerformedAt" class="info-item">
          <span class="info-label">Последний раз:</span>
          <span class="info-value">{{ originalSkill.formattedLastPerformedAt }}</span>
        </div>
        <div v-if="originalSkill.updatedAt" class="info-item">
          <span class="info-label">Изменено:</span>
          <span class="info-value">{{ originalSkill.formattedUpdatedAt }}</span>
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
          {{ isEditing ? 'Сохранить изменения' : 'Добавить навык' }}
        </Button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import TextField from '@/components/UI/TextField.vue'
import TextArea from '@/components/UI/TextArea.vue'
import Select from '@/components/UI/Select.vue'
import Button from '@/components/UI/Button.vue'
import skillService from '@/services/skillService'

const props = defineProps({
  skill: {
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
const originalSkill = ref(null)

// Опции категорий
const categoryOptions = [
  { value: 'SELF_CARE', label: '🚿 Самообслуживание' },
  { value: 'HOUSEHOLD', label: '🍳 Быт' },
  { value: 'COMMUNICATION', label: '📞 Коммуникация' },
  { value: 'MOBILITY', label: '🚶 Мобильность' },
  { value: 'HEALTH', label: '💊 Здоровье' },
  { value: 'COGNITIVE', label: '🧠 Когнитивные' },
  { value: 'OTHER', label: '📌 Другое' }
]

// Форма
const form = reactive({
  category: 'SELF_CARE',
  title: '',
  description: '',
  difficulty: null,
  importance: null,
  canDoNow: false,
  notes: ''
})

// Ошибки
const errors = reactive({
  category: '',
  title: '',
  description: '',
  difficulty: '',
  importance: '',
  notes: ''
})

// Вычисляемые поля для плейсхолдеров
const getTitlePlaceholder = computed(() => {
  const placeholders = {
    'SELF_CARE': 'Например: Чистить зубы, Одеваться',
    'HOUSEHOLD': 'Например: Приготовить чай, Помыть посуду',
    'COMMUNICATION': 'Например: Позвонить родственникам',
    'MOBILITY': 'Например: Сходить в магазин',
    'HEALTH': 'Например: Принять лекарства',
    'COGNITIVE': 'Например: Читать книгу',
    'OTHER': 'Название навыка'
  }
  return placeholders[form.category] || 'Название навыка'
})

const getDescriptionPlaceholder = computed(() => {
  const placeholders = {
    'SELF_CARE': 'Опишите, как выполняется этот навык ухода за собой...',
    'HOUSEHOLD': 'Опишите последовательность действий по дому...',
    'COMMUNICATION': 'Как проходит общение, с кем, в какой форме...',
    'MOBILITY': 'Куда нужно ходить, как далеко, нужен ли сопровождающий...',
    'HEALTH': 'Какие лекарства, в какое время, дозировка...',
    'COGNITIVE': 'Что именно читает/считает/пишет...'
  }
  return placeholders[form.category] || 'Описание навыка'
})

// Сброс полей при смене категории
watch(() => form.category, () => {
  if (!props.isEditing) {
    form.description = ''
  }
})

// Загружаем данные при редактировании
onMounted(() => {
  if (props.isEditing && props.skill) {
    originalSkill.value = props.skill
    form.category = props.skill.category || 'SELF_CARE'
    form.title = props.skill.title || ''
    form.description = props.skill.description || ''
    form.difficulty = props.skill.difficulty
    form.importance = props.skill.importance
    form.canDoNow = props.skill.canDoNow || false
    form.notes = props.skill.notes || ''
  }
})

// Валидация формы
const validateForm = () => {
  let isValid = true
  errors.category = ''
  errors.title = ''
  errors.description = ''
  errors.difficulty = ''
  errors.importance = ''
  errors.notes = ''

  if (!form.category) {
    errors.category = 'Выберите категорию'
    isValid = false
  }

  if (!form.title.trim()) {
    errors.title = 'Введите название навыка'
    isValid = false
  } else if (form.title.length < 3) {
    errors.title = 'Минимум 3 символа'
    isValid = false
  }

  if (form.difficulty && (form.difficulty < 1 || form.difficulty > 5)) {
    errors.difficulty = 'Сложность должна быть от 1 до 5'
    isValid = false
  }

  if (form.importance && (form.importance < 1 || form.importance > 5)) {
    errors.importance = 'Важность должна быть от 1 до 5'
    isValid = false
  }

  return isValid
}

// Отправка формы
const handleSubmit = () => {
  if (!validateForm()) return

  submitting.value = true

  const skillData = {
    category: form.category,
    title: form.title.trim(),
    description: form.description?.trim() || null,
    difficulty: form.difficulty,
    importance: form.importance,
    canDoNow: form.canDoNow,
    notes: form.notes?.trim() || null
  }

  emit('submit', skillData)

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
.skill-form {
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

.row-fields {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.rating-field {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.rating-label {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-field-label, #757575);
}

.rating-stars {
  display: flex;
  gap: 4px;
}

.star {
  font-size: 24px;
  color: var(--color-light-gray, #e0e0e0);
  cursor: pointer;
  transition: all 0.2s ease;
}

.star.active {
  color: var(--color-warning, #ff9800);
}

.star:hover {
  transform: scale(1.2);
}

.importance-dots {
  display: flex;
  gap: 8px;
}

.dot {
  font-size: 24px;
  color: var(--color-light-gray, #e0e0e0);
  cursor: pointer;
  transition: all 0.2s ease;
}

.dot.active {
  color: var(--color-primary, #88d424);
}

.dot:hover {
  transform: scale(1.2);
}

/* Чекбокс */
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

.checkbox-hint {
  color: var(--color-gray, #9e9e9e);
  font-size: 12px;
  margin: 4px 0 0 28px;
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

.error-message {
  margin-top: 4px;
  font-size: 12px;
  color: var(--color-error, #f44336);
}

@media (max-width: 768px) {
  .skill-form {
    padding: 1.5rem;
  }

  .form-header h2 {
    font-size: 24px;
  }

  .row-fields {
    grid-template-columns: 1fr;
    gap: 12px;
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
