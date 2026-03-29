<template>
  <div class="interest-form">
    <div class="form-header">
      <h2>{{ isEditing ? 'Редактирование интереса' : 'Новый интерес' }}</h2>
      <p class="form-subtitle">
        {{ isEditing ? 'Измените информацию' : 'Добавьте то, что вам нравится' }}
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

      <!-- Название -->
      <div class="form-row">
        <TextField
          v-model="form.title"
          label="Название"
          :placeholder="getTitlePlaceholder"
          required
          :error="errors.title"
          clearable
        />
      </div>

      <!-- Автор/исполнитель (показывается для определённых категорий) -->
      <div v-if="showCreator" class="form-row">
        <TextField
          v-model="form.creator"
          :label="getCreatorLabel"
          :placeholder="getCreatorPlaceholder"
          :error="errors.creator"
          clearable
        />
      </div>

      <!-- Год и жанр (в одной строке) -->
      <div class="form-row">
        <div class="row-fields">
          <TextField
            v-model="form.year"
            label="Год"
            type="number"
            placeholder="2024"
            :error="errors.year"
            class="year-field"
          />
          <TextField
            v-model="form.genre"
            :label="getGenreLabel"
            :placeholder="getGenrePlaceholder"
            :error="errors.genre"
            class="genre-field"
          />
        </div>
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

      <!-- Оценка (лайк/дизлайк) -->
      <div class="form-row">
        <label class="section-label">Ваше отношение</label>
        <div class="like-buttons">
          <button
            type="button"
            class="like-option"
            :class="{ active: form.liked === true }"
            @click="form.liked = form.liked === true ? null : true"
          >
            <span class="like-emoji">❤️</span>
            <span>Нравится</span>
          </button>
          <button
            type="button"
            class="like-option dislike"
            :class="{ active: form.liked === false }"
            @click="form.liked = form.liked === false ? null : false"
          >
            <span class="like-emoji">💔</span>
            <span>Не нравится</span>
          </button>
        </div>
      </div>

      <!-- Рейтинг (звёзды) - показываем только если есть лайк -->
      <div v-if="form.liked !== null" class="form-row">
        <label class="section-label">Оценка</label>
        <div class="rating-stars">
          <span
            v-for="star in 5"
            :key="star"
            class="star"
            :class="{ active: star <= (form.rating || 0) }"
            @click="form.rating = star"
          >
            ★
          </span>
          <span class="rating-hint" v-if="form.rating">
            {{ getRatingText(form.rating) }}
          </span>
        </div>
        <div v-if="errors.rating" class="error-message">{{ errors.rating }}</div>
      </div>

      <!-- Флаг текущего интереса -->
      <div class="form-row">
        <label class="checkbox-label">
          <input
            type="checkbox"
            v-model="form.isCurrent"
          >
          <span>Это актуально для меня сейчас</span>
        </label>
        <p class="checkbox-hint" v-if="!form.isCurrent">
          Интерес будет отмечен как "прошлое"
        </p>
      </div>

      <!-- Информация о создании (для редактирования) -->
      <div v-if="isEditing && originalInterest" class="edit-info">
        <div class="info-item">
          <span class="info-label">Создано:</span>
          <span class="info-value">{{ originalInterest.formattedCreatedAt }}</span>
        </div>
        <div v-if="originalInterest.updatedAt" class="info-item">
          <span class="info-label">Изменено:</span>
          <span class="info-value">{{ originalInterest.formattedUpdatedAt }}</span>
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
          {{ isEditing ? 'Сохранить изменения' : 'Добавить интерес' }}
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
import interestService from '@/services/interestService'

const props = defineProps({
  interest: {
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
const originalInterest = ref(null)

// Опции категорий
const categoryOptions = [
  { value: 'FILM', label: '🎬 Фильмы' },
  { value: 'BOOK', label: '📚 Книги' },
  { value: 'MUSIC', label: '🎵 Музыка' },
  { value: 'GAME', label: '🎮 Игры' },
  { value: 'SPORT', label: '⚽ Спорт' },
  { value: 'HOBBY', label: '🧩 Хобби' },
  { value: 'OTHER', label: '📌 Другое' }
]

// Форма
const form = reactive({
  category: 'FILM',
  title: '',
  creator: '',
  year: '',
  genre: '',
  description: '',
  liked: null,
  rating: null,
  isCurrent: true
})

// Ошибки
const errors = reactive({
  category: '',
  title: '',
  creator: '',
  year: '',
  genre: '',
  description: '',
  rating: ''
})

// Вычисляемые поля для плейсхолдеров
const showCreator = computed(() => {
  return ['FILM', 'BOOK', 'MUSIC', 'GAME'].includes(form.category)
})

const getTitlePlaceholder = computed(() => {
  const placeholders = {
    'FILM': 'Например: Зелёная миля',
    'BOOK': 'Например: Мастер и Маргарита',
    'MUSIC': 'Например: The Dark Side of the Moon',
    'GAME': 'Например: Ведьмак 3',
    'SPORT': 'Например: Футбол',
    'HOBBY': 'Например: Собирание пазлов',
    'OTHER': 'Название'
  }
  return placeholders[form.category] || 'Название'
})

const getCreatorLabel = computed(() => {
  const labels = {
    'FILM': 'Режиссёр',
    'BOOK': 'Автор',
    'MUSIC': 'Исполнитель',
    'GAME': 'Разработчик'
  }
  return labels[form.category] || 'Автор/исполнитель'
})

const getCreatorPlaceholder = computed(() => {
  const placeholders = {
    'FILM': 'Например: Фрэнк Дарабонт',
    'BOOK': 'Например: Михаил Булгаков',
    'MUSIC': 'Например: Pink Floyd',
    'GAME': 'Например: CD Projekt Red'
  }
  return placeholders[form.category] || 'Имя автора'
})

const getGenreLabel = computed(() => {
  const labels = {
    'FILM': 'Жанр фильма',
    'BOOK': 'Жанр книги',
    'MUSIC': 'Жанр музыки',
    'GAME': 'Жанр игры',
    'SPORT': 'Вид спорта',
    'HOBBY': 'Тип хобби'
  }
  return labels[form.category] || 'Жанр'
})

const getGenrePlaceholder = computed(() => {
  const placeholders = {
    'FILM': 'Например: Драма, Фантастика',
    'BOOK': 'Например: Роман, Фэнтези',
    'MUSIC': 'Например: Рок, Классика',
    'GAME': 'Например: RPG, Стратегия',
    'SPORT': 'Например: Футбол, Баскетбол',
    'HOBBY': 'Например: Пазлы, Вышивание'
  }
  return placeholders[form.category] || 'Жанр'
})

const getDescriptionPlaceholder = computed(() => {
  const placeholders = {
    'FILM': 'О чём фильм? Ваши впечатления...',
    'BOOK': 'О чём книга? Что понравилось?...',
    'MUSIC': 'Какая это музыка? Ваши ощущения...',
    'GAME': 'О чём игра? Что нравится?...',
    'SPORT': 'Как часто занимаетесь? Достижения?...',
    'HOBBY': 'Что именно нравится делать?...'
  }
  return placeholders[form.category] || 'Описание'
})

// Получить текстовое описание рейтинга
const getRatingText = (rating, liked) => {
  if (liked === true) {
    const texts = {
      1: 'Еле нравится',
      2: 'Немного нравится',
      3: 'Нормально',
      4: 'Очень нравится',
      5: 'Обожаю!'
    }
    return texts[rating] || ''
  } else {
    const texts = {
      1: 'Не очень нравится',
      2: 'Так себе',
      3: 'Неплохо',
      4: 'В целом пойдет',
      5: 'Отлично'
    }
    return texts[rating] || ''
  }
}

// Сброс полей при смене категории
watch(() => form.category, () => {
  if (!props.isEditing) {
    form.creator = ''
    form.genre = ''
    form.description = ''
  }
})

// Загружаем данные при редактировании
onMounted(() => {
  if (props.isEditing && props.interest) {
    originalInterest.value = props.interest
    form.category = props.interest.category || 'FILM'
    form.title = props.interest.title || ''
    form.creator = props.interest.creator || ''
    form.year = props.interest.year || ''
    form.genre = props.interest.genre || ''
    form.description = props.interest.description || ''
    form.liked = props.interest.liked
    form.rating = props.interest.rating
    form.isCurrent = props.interest.isCurrent !== false
  }
})

// Валидация формы
const validateForm = () => {
  let isValid = true
  errors.category = ''
  errors.title = ''
  errors.creator = ''
  errors.year = ''
  errors.genre = ''
  errors.description = ''
  errors.rating = ''

  if (!form.category) {
    errors.category = 'Выберите категорию'
    isValid = false
  }

  if (!form.title.trim()) {
    errors.title = 'Введите название'
    isValid = false
  } else if (form.title.length < 2) {
    errors.title = 'Минимум 2 символа'
    isValid = false
  }

  if (form.year && (form.year < 1800 || form.year > 2100)) {
    errors.year = 'Введите корректный год'
    isValid = false
  }

  if (form.liked === true && (!form.rating || form.rating < 1 || form.rating > 5)) {
    errors.rating = 'Укажите оценку от 1 до 5'
    isValid = false
  }

  return isValid
}

// Отправка формы
const handleSubmit = () => {
  if (!validateForm()) return

  submitting.value = true

  const interestData = {
    category: form.category,
    title: form.title.trim(),
    creator: form.creator?.trim() || null,
    year: form.year ? parseInt(form.year) : null,
    genre: form.genre?.trim() || null,
    description: form.description?.trim() || null,
    liked: form.liked,
    rating: form.rating,
    isCurrent: form.isCurrent
  }

  emit('submit', interestData)

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
.interest-form {
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
  grid-template-columns: 1fr 2fr;
  gap: 12px;
}

.section-label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: var(--color-field-label, #757575);
}

/* Кнопки лайков */
.like-buttons {
  display: flex;
  gap: 12px;
}

.like-option {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 10px;
  border: 1px solid var(--color-light-gray, #e0e0e0);
  border-radius: 8px;
  background: none;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 14px;
  color: var(--color-gray, #9e9e9e);
}

.like-option.active {
  border-width: 2px;
}

.like-option.active:first-child {
  background-color: rgba(255, 0, 0, 0.05);
  border-color: #ff4444;
  color: #ff4444;
}

.like-option.dislike.active {
  background-color: rgba(0, 0, 0, 0.05);
  border-color: #666;
  color: #666;
}

.like-emoji {
  font-size: 20px;
}

/* Звёздный рейтинг */
.rating-stars {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.star {
  font-size: 32px;
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

.rating-hint {
  color: var(--color-gray, #9e9e9e);
  font-size: 14px;
  margin-left: 8px;
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
  .interest-form {
    padding: 1.5rem;
  }

  .form-header h2 {
    font-size: 24px;
  }

  .row-fields {
    grid-template-columns: 1fr;
    gap: 8px;
  }

  .like-buttons {
    flex-direction: column;
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
