<template>
  <div class="photo-card">
    <div class="photo-preview" @click="$emit('view', photo)">
      <img
        :src="photo.downloadUrl"
        :alt="photo.description || photo.fileName"
        loading="lazy"
      >

      <!-- Кнопка скачивания -->
      <button
        class="download-button"
        @click.stop="$emit('download', photo)"
        title="Скачать фото"
      >
        ⬇️
      </button>
    </div>

    <div class="photo-info">
      <div class="photo-header">
        <span class="photo-date">{{ photo.formattedUploadDate || formatDate(photo.uploadDate) }}</span>
      </div>

      <!-- Описание -->
      <p v-if="photo.description" class="photo-description">
        {{ photo.description }}
      </p>

      <!-- Метаданные -->
      <div class="photo-metadata">
        <div v-if="photo.location" class="metadata-item">
          <span class="metadata-icon">📍</span>
          <span class="metadata-text">{{ photo.location }}</span>
        </div>

        <div v-if="photo.year" class="metadata-item">
          <span class="metadata-icon">📅</span>
          <span class="metadata-text">{{ photo.year }} год</span>
        </div>

        <div v-if="photo.people" class="metadata-item">
          <span class="metadata-icon">👥</span>
          <span class="metadata-text">{{ photo.people }}</span>
        </div>

        <div class="metadata-item">
          <span class="metadata-icon">📦</span>
          <span class="metadata-text">{{ formatFileSize(photo.fileSize) }}</span>
        </div>
      </div>

      <!-- Футер с действиями -->
      <div class="photo-footer">
        <div class="photo-meta">
          <span v-if="photo.isEdited" class="edited-indicator" title="Было отредактировано">
            📝
          </span>
        </div>

        <div class="card-actions">
          <button
            class="icon-button edit-btn"
            @click.stop="$emit('edit', photo)"
            title="Редактировать"
          >
            ✏️
          </button>
          <button
            class="icon-button delete-btn"
            @click.stop="$emit('delete', photo)"
            title="Удалить"
          >
            🗑️
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import photoService from '@/services/photoService'

const props = defineProps({
  photo: {
    type: Object,
    required: true
  }
})

defineEmits(['view', 'edit', 'delete', 'download'])

// Короткий ID для отображения (первые 8 символов)
const shortId = computed(() => {
  return props.photo.id ? props.photo.id.substring(0, 8) + '...' : ''
})

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

// Форматирование размера файла
const formatFileSize = (bytes) => {
  return photoService.constructor.formatFileSize(bytes)
}
</script>

<style scoped>
.photo-card {
  background: var(--color-white, #ffffff);
  border: 1px solid var(--color-light-gray, #e0e0e0);
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.photo-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 12px var(--shadow-color, rgba(0,0,0,0.1));
}

/* Превью фотографии */
.photo-preview {
  position: relative;
  width: 100%;
  height: 200px;
  overflow: hidden;
  cursor: pointer;
  background-color: var(--color-white-smoke, #f5f5f5);
}

.photo-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.photo-preview:hover img {
  transform: scale(1.05);
}

.download-button {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(255, 255, 255, 0.9);
  border: none;
  border-radius: 50%;
  width: 36px;
  height: 36px;
  font-size: 18px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.2s ease;
  box-shadow: 0 2px 8px var(--shadow-color, rgba(0,0,0,0.1));
}

.photo-preview:hover .download-button {
  opacity: 1;
}

.download-button:hover {
  background: var(--color-white, #ffffff);
  transform: scale(1.1);
}

/* Информация о фото */
.photo-info {
  padding: 1rem;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  flex: 1;
}

.photo-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 0.5rem;
}

.photo-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-primary-dark, #6aa91c);
  margin: 0;
  word-break: break-word;
  flex: 1;
}

.photo-date {
  font-size: 12px;
  color: var(--color-gray, #9e9e9e);
  white-space: nowrap;
}

.photo-description {
  color: var(--color-gray, #9e9e9e);
  font-size: 13px;
  line-height: 1.5;
  margin: 0;
  word-break: break-word;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* Метаданные */
.photo-metadata {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  padding: 0.5rem 0;
  border-top: 1px solid var(--color-light-gray, #e0e0e0);
  border-bottom: 1px solid var(--color-light-gray, #e0e0e0);
}

.metadata-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: var(--color-dark-gray, #616161);
}

.metadata-icon {
  font-size: 14px;
}

.metadata-text {
  white-space: nowrap;
}

/* Футер */
.photo-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
  padding-top: 0.5rem;
}

.photo-meta {
  color: var(--color-gray, #9e9e9e);
  font-size: 11px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.edited-indicator {
  font-size: 14px;
  opacity: 0.7;
  cursor: help;
  transition: opacity 0.2s ease;
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
  font-size: 16px;
  padding: 4px 6px;
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
  .photo-header {
    flex-direction: column;
  }

  .photo-date {
    white-space: normal;
  }

  .photo-metadata {
    flex-direction: column;
    gap: 8px;
  }

  .photo-footer {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }

  .card-actions {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>
