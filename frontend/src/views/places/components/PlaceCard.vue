<template>
  <div class="place-card" :class="{ 'home-place': place.home, 'medical-place': place.medical }">
    <div class="card-header">
      <div class="title-section">
        <span class="category-icon">{{ getCategoryIcon(place.category) }}</span>
        <h3 class="place-title">{{ place.title }}</h3>
      </div>

      <div class="badges">
        <span v-if="place.home" class="badge home-badge" title="Домашний адрес">🏠 Дом</span>
        <span v-if="place.work" class="badge work-badge" title="Место работы">💼 Работа</span>
        <span v-if="place.medical" class="badge medical-badge" title="Медицинское учреждение">🏥 Медицина</span>
        <span v-if="place.category && !place.home && !place.work && !place.medical"
              class="badge category-badge"
              :style="{ backgroundColor: getCategoryColor(place.category) }">
          {{ place.category }}
        </span>
      </div>
    </div>

    <div class="place-address">
      <span class="address-icon">📍</span>
      <span class="address-text">{{ place.address }}</span>
    </div>

    <p v-if="place.description" class="place-description">
      {{ place.description }}
    </p>

    <div class="card-footer">
      <div class="place-meta">
        <span class="meta-item">Добавлено: {{ place.formattedCreatedAt || formatDate(place.createdAt) }}</span>
      </div>

      <div class="card-actions">
        <button
          class="icon-button edit-btn"
          @click.stop="$emit('edit', place)"
          title="Редактировать"
        >
          ✏️
        </button>
        <button
          class="icon-button delete-btn"
          @click.stop="$emit('delete', place)"
          title="Удалить"
        >
          🗑️
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import placeService from '@/services/placeService'

const props = defineProps({
  place: {
    type: Object,
    required: true
  }
})

defineEmits(['edit', 'delete'])

// Получить иконку категории
const getCategoryIcon = (category) => {
  return placeService.constructor.getCategoryIcon(category)
}

// Получить цвет категории
const getCategoryColor = (category) => {
  return placeService.constructor.getCategoryColor(category)
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
.place-card {
  background: var(--color-white, #ffffff);
  border: 1px solid var(--color-light-gray, #e0e0e0);
  border-radius: 12px;
  padding: 1.5rem;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.place-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 12px var(--shadow-color, rgba(0,0,0,0.1));
}

.place-card.home-place {
  border-left: 4px solid var(--color-primary, #88d424);
}

.place-card.medical-place {
  border-left: 4px solid var(--color-error, #f44336);
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

.place-title {
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
}

.badge {
  font-size: 11px;
  padding: 4px 8px;
  border-radius: 12px;
  color: white;
  white-space: nowrap;
}

.home-badge {
  background-color: var(--color-primary, #88d424);
}

.work-badge {
  background-color: var(--color-warning, #ff9800);
}

.medical-badge {
  background-color: var(--color-error, #f44336);
}

.category-badge {
  background-color: var(--color-gray, #9e9e9e);
}

.place-address {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  color: var(--color-dark-gray, #616161);
  font-size: 14px;
  background-color: var(--color-white-smoke, #f5f5f5);
  padding: 8px 12px;
  border-radius: 8px;
}

.address-icon {
  font-size: 16px;
  flex-shrink: 0;
}

.address-text {
  line-height: 1.5;
  word-break: break-word;
}

.place-description {
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

.place-meta {
  color: var(--color-gray, #9e9e9e);
  font-size: 11px;
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
