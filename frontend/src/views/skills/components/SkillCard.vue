<template>
  <div class="skill-card" :style="{ borderLeftColor: getCategoryColor(skill.category) }">
    <div class="card-header">
      <div class="title-section">
        <span class="category-icon">{{ getCategoryIcon(skill.category) }}</span>
        <h3 class="skill-title">{{ skill.title }}</h3>
      </div>

      <div class="badges">
        <span class="category-badge" :style="{ backgroundColor: getCategoryColor(skill.category) }">
          {{ skill.categoryDisplayName }}
        </span>
      </div>
    </div>

    <!-- Описание навыка -->
    <p v-if="skill.description" class="skill-description">
      {{ skill.description }}
    </p>

    <!-- Мета-информация (сложность, важность) -->
    <div class="meta-info">
      <div v-if="skill.difficulty" class="meta-item" :title="'Сложность: ' + getDifficultyText(skill.difficulty)">
        <span class="meta-label">Сложность:</span>
        <div class="difficulty-stars">
          <span
            v-for="star in 5"
            :key="star"
            class="star"
            :class="{ active: star <= skill.difficulty }"
          >★</span>
        </div>
      </div>

      <div v-if="skill.importance" class="meta-item" :title="'Важность: ' + getImportanceText(skill.importance)">
        <span class="meta-label">Важность:</span>
        <div class="importance-dots">
          <span
            v-for="dot in 5"
            :key="dot"
            class="dot"
            :class="{ active: dot <= skill.importance }"
          >●</span>
        </div>
      </div>
    </div>

    <!-- Секция с чек-боксом и датой -->
    <div class="status-section">
      <label class="checkbox-label">
        <input
          type="checkbox"
          :checked="skill.canDoNow"
          @change="toggleStatus"
        >
        <span class="checkbox-text">Умею сейчас</span>
      </label>

      <div v-if="skill.lastPerformedAt" class="last-performed">
        <span class="performed-icon">✅</span>
        <span class="performed-date">Последний раз: {{ skill.formattedLastPerformedAt || formatDate(skill.lastPerformedAt) }}</span>
      </div>
      <div v-else-if="skill.canDoNow" class="last-performed">
        <span class="performed-icon">✨</span>
        <span class="performed-date">Только что освоено</span>
      </div>
    </div>

    <!-- Заметки -->
    <div v-if="skill.notes" class="notes-section">
      <span class="notes-icon">📝</span>
      <span class="notes-text">{{ skill.notes }}</span>
    </div>

    <div class="card-footer">
      <div class="skill-meta">
        <span class="meta-item">Добавлено: {{ skill.formattedCreatedAt || formatDate(skill.createdAt) }}</span>
        <span v-if="skill.formattedUpdatedAt" class="meta-item edited">
          • Изменено: {{ skill.formattedUpdatedAt }}
        </span>
        <span v-if="skill.isEdited" class="edited-indicator" title="Было отредактировано">
          📝
        </span>
      </div>

      <div class="card-actions">
        <button
          class="icon-button edit-btn"
          @click.stop="$emit('edit', skill)"
          title="Редактировать"
        >
          ✏️
        </button>
        <button
          class="icon-button delete-btn"
          @click.stop="$emit('delete', skill)"
          title="Удалить"
        >
          🗑️
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import skillService from '@/services/skillService'

const props = defineProps({
  skill: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['edit', 'delete', 'toggle'])

// Получить иконку категории
const getCategoryIcon = (category) => {
  return skillService.constructor.getCategoryIcon(category)
}

// Получить цвет категории
const getCategoryColor = (category) => {
  return skillService.constructor.getCategoryColor(category)
}

// Получить текст сложности
const getDifficultyText = (difficulty) => {
  return skillService.constructor.getDifficultyText(difficulty)
}

// Получить текст важности
const getImportanceText = (importance) => {
  return skillService.constructor.getImportanceText(importance)
}

// Переключение статуса
const toggleStatus = (event) => {
  emit('toggle', props.skill.id, event.target.checked)
}

// Форматирование даты
const formatDate = (dateString) => {
  if (!dateString) return 'Неизвестно'
  try {
    const date = new Date(dateString)
    return new Intl.DateTimeFormat('ru-RU', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    }).format(date)
  } catch (e) {
    return dateString
  }
}
</script>

<style scoped>
.skill-card {
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

.skill-card:hover {
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

.skill-title {
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

.skill-description {
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

/* Мета-информация */
.meta-info {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
  padding: 8px 0;
  border-top: 1px solid var(--color-light-gray, #e0e0e0);
  border-bottom: 1px solid var(--color-light-gray, #e0e0e0);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.meta-label {
  color: var(--color-gray, #9e9e9e);
  font-size: 13px;
}

.difficulty-stars {
  display: flex;
  gap: 2px;
}

.star {
  color: var(--color-light-gray, #e0e0e0);
  font-size: 16px;
}

.star.active {
  color: var(--color-warning, #ff9800);
}

.importance-dots {
  display: flex;
  gap: 4px;
}

.dot {
  color: var(--color-light-gray, #e0e0e0);
  font-size: 16px;
}

.dot.active {
  color: var(--color-primary, #88d424);
}

/* Статус и чек-бокс */
.status-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 14px;
  color: var(--color-dark-gray, #616161);
}

.checkbox-label input[type="checkbox"] {
  width: 18px;
  height: 18px;
  cursor: pointer;
  accent-color: var(--color-primary, #88d424);
}

.checkbox-text {
  font-weight: 500;
}

.last-performed {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--color-gray, #9e9e9e);
  font-size: 12px;
}

.performed-icon {
  font-size: 14px;
}

.performed-date {
  font-style: italic;
}

/* Заметки */
.notes-section {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  color: var(--color-gray, #9e9e9e);
  font-size: 13px;
  background-color: var(--color-white-smoke, #f5f5f5);
  padding: 8px 12px;
  border-radius: 8px;
}

.notes-icon {
  font-size: 14px;
  flex-shrink: 0;
}

.notes-text {
  line-height: 1.5;
  word-break: break-word;
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

.skill-meta {
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

  .meta-info {
    flex-direction: column;
    gap: 8px;
  }

  .status-section {
    flex-direction: column;
    align-items: flex-start;
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
