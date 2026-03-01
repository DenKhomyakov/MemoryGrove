<template>
  <div class="interest-card" :style="{ borderLeftColor: getCategoryColor(interest.category) }">
    <div class="card-header">
      <div class="title-section">
        <span class="category-icon">{{ getCategoryIcon(interest.category) }}</span>
        <h3 class="interest-title">{{ interest.title }}</h3>
      </div>

      <div class="badges">
        <span class="category-badge" :style="{ backgroundColor: getCategoryColor(interest.category) }">
          {{ getCategoryDisplayName(interest.category) }}
        </span>
        <span v-if="!interest.isCurrent" class="badge past-badge" title="Раньше нравилось, сейчас не актуально">
          ⏳ Прошлое
        </span>
      </div>
    </div>

    <!-- Информация об авторе/исполнителе -->
    <div v-if="interest.creator" class="info-row">
      <span class="info-label">Автор:</span>
      <span class="info-value">{{ interest.creator }}</span>
    </div>

    <!-- Год и жанр -->
    <div v-if="interest.year || interest.genre" class="info-row">
      <span v-if="interest.year" class="year-badge">{{ interest.year }}</span>
      <span v-if="interest.genre" class="genre-badge">{{ interest.genre }}</span>
    </div>

    <!-- Описание -->
    <p v-if="interest.description" class="interest-description">
      {{ interest.description }}
    </p>

    <!-- Оценка -->
    <div class="like-section">
      <button
        class="like-button"
        :class="{ active: interest.liked === true }"
        @click.stop="toggleLike(true)"
        title="Нравится"
      >
        ❤️
      </button>
      <button
        class="like-button dislike"
        :class="{ active: interest.liked === false }"
        @click.stop="toggleLike(false)"
        title="Не нравится"
      >
        💔
      </button>

      <!-- Звёздный рейтинг -->
      <div class="rating-stars" v-if="interest.liked !== null">
        <span
          v-for="star in 5"
          :key="star"
          class="star"
          :class="{ active: star <= (interest.rating || 0) }"
          @click.stop="setRating(star)"
        >
          ★
        </span>
      </div>
    </div>

    <div class="card-footer">
      <div class="interest-meta">
        <span class="meta-item">Добавлено: {{ interest.formattedCreatedAt || formatDate(interest.createdAt) }}</span>
        <span v-if="interest.formattedUpdatedAt" class="meta-item edited">
          • Изменено: {{ interest.formattedUpdatedAt }}
        </span>
        <span v-if="interest.isEdited" class="edited-indicator" title="Было отредактировано">
          📝
        </span>
      </div>

      <div class="card-actions">
        <button
          class="icon-button edit-btn"
          @click.stop="$emit('edit', interest)"
          title="Редактировать"
        >
          ✏️
        </button>
        <button
          class="icon-button delete-btn"
          @click.stop="$emit('delete', interest)"
          title="Удалить"
        >
          🗑️
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import interestService from '@/services/interestService'

const props = defineProps({
  interest: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['edit', 'delete', 'update-like'])

// Получить отображаемое название категории
const getCategoryDisplayName = (category) => {
  return interestService.constructor.getCategoryDisplayName(category)
}

// Получить иконку категории
const getCategoryIcon = (category) => {
  return interestService.constructor.getCategoryIcon(category)
}

// Получить цвет категории
const getCategoryColor = (category) => {
  return interestService.constructor.getCategoryColor(category)
}

// Переключение лайка
const toggleLike = (liked) => {
  const newLiked = props.interest.liked === liked ? null : liked
  emit('update-like', props.interest.id, {
    liked: newLiked,
    rating: newLiked ? props.interest.rating || 3 : null
  })
}

// Установка рейтинга
const setRating = (rating) => {
  if (props.interest.liked) {
    emit('update-like', props.interest.id, {
      liked: true,
      rating
    })
  }
}

// Форматирование даты
const formatDate = (dateString) => {
  if (!dateString) return 'Неизвестно'
  try {
    const date = new Date(dateString)
    return new Intl.DateTimeFormat('ru-RU', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric'
    }).format(date)
  } catch (e) {
    return dateString
  }
}
</script>

<style scoped>
.interest-card {
  background: var(--color-white, #ffffff);
  border: 1px solid var(--color-light-gray, #e0e0e0);
  border-left-width: 4px;
  border-radius: 12px;
  padding: 1.5rem;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.interest-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 12px var(--shadow-color, rgba(0,0,0,0.1));
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 1rem;
  flex-wrap: wrap;
}

.title-section {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.category-icon {
  font-size: 24px;
}

.interest-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-primary-dark, #6aa91c);
  margin: 0;
  word-break: break-word;
}

.badges {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  align-items: center;
}

.category-badge {
  font-size: 11px;
  padding: 4px 8px;
  border-radius: 12px;
  color: white;
  white-space: nowrap;
}

.past-badge {
  background-color: var(--color-gray, #9e9e9e);
  color: white;
  font-size: 11px;
  padding: 4px 8px;
  border-radius: 12px;
}

.info-row {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.info-label {
  color: var(--color-gray, #9e9e9e);
  font-size: 13px;
  font-weight: 500;
}

.info-value {
  color: var(--color-dark-gray, #616161);
  font-size: 14px;
  font-weight: 500;
}

.year-badge {
  background-color: var(--color-primary-light, #a8e454);
  color: white;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
}

.genre-badge {
  background-color: var(--color-white-smoke, #f5f5f5);
  color: var(--color-gray, #9e9e9e);
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
}

.interest-description {
  color: var(--color-gray, #9e9e9e);
  font-size: 14px;
  line-height: 1.6;
  margin: 0;
  word-break: break-word;
  background-color: var(--color-white-smoke, #f5f5f5);
  padding: 12px;
  border-radius: 8px;
  border-left: 2px solid var(--color-primary-light, #a8e454);
}

/* Секция лайков */
.like-section {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
  border-top: 1px solid var(--color-light-gray, #e0e0e0);
  border-bottom: 1px solid var(--color-light-gray, #e0e0e0);
}

.like-button {
  background: none;
  border: 1px solid var(--color-light-gray, #e0e0e0);
  border-radius: 20px;
  padding: 4px 12px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.2s ease;
  opacity: 0.5;
}

.like-button.active {
  opacity: 1;
  border-color: currentColor;
}

.like-button:hover:not(.active) {
  opacity: 0.8;
  transform: scale(1.05);
}

.like-button.active:first-child {
  background-color: rgba(255, 0, 0, 0.1);
  border-color: #ff4444;
  color: #ff4444;
}

.like-button.dislike.active {
  background-color: rgba(0, 0, 0, 0.1);
  border-color: #666;
  color: #666;
}

.rating-stars {
  display: flex;
  gap: 4px;
  margin-left: auto;
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

/* Футер */
.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
  padding-top: 0.5rem;
  border-top: 1px solid var(--color-light-gray, #e0e0e0);
  flex-wrap: wrap;
  gap: 10px;
}

.interest-meta {
  color: var(--color-gray, #9e9e9e);
  font-size: 11px;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
}

.meta-item.edited {
  color: var(--color-primary, #88d424);
}

.edited-indicator {
  font-size: 14px;
  opacity: 0.7;
  cursor: help;
  transition: opacity 0.2s ease;
  display: inline-flex;
  align-items: center;
}

.edited-indicator:hover {
  opacity: 1;
}

.card-actions {
  display: flex;
  gap: 8px;
}

.icon-button {
  background: none;
  border: none;
  font-size: 18px;
  padding: 4px 8px;
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.2s ease;
  opacity: 0.6;
}

.icon-button:hover {
  opacity: 1;
  transform: scale(1.1);
}

.edit-btn:hover {
  background-color: rgba(33, 150, 243, 0.2);
  color: #1976d2;
}

.delete-btn:hover {
  background-color: rgba(244, 67, 54, 0.2);
  color: var(--color-error, #f44336);
}

@media (max-width: 768px) {
  .card-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .badges {
    width: 100%;
  }

  .like-section {
    flex-wrap: wrap;
  }

  .rating-stars {
    margin-left: 0;
    width: 100%;
    justify-content: center;
  }

  .card-footer {
    flex-direction: column;
    align-items: flex-start;
  }

  .card-actions {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>
