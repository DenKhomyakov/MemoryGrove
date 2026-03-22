<template>
  <Teleport to="body">
    <div v-if="show" class="photo-viewer-overlay" @click.self="close">
      <div class="photo-viewer">
        <!-- Шапка просмотрщика -->
        <div class="viewer-header">
          <div class="photo-info">
            <h2>{{ currentPhoto.title || 'Без названия' }}</h2>
            <span class="photo-date">{{ currentPhoto.formattedUploadDate }}</span>
          </div>

          <div class="viewer-actions">
            <button class="action-button" @click="downloadPhoto" title="Скачать">
              ⬇️
            </button>
            <button class="action-button" @click="editPhoto" title="Редактировать">
              ✏️
            </button>
            <button class="action-button close-button" @click="close" title="Закрыть">
              ✕
            </button>
          </div>
        </div>

        <!-- Основное изображение -->
        <div class="image-container">
          <img
            :src="currentPhoto.downloadUrl"
            :alt="currentPhoto.description"
            @load="imageLoaded"
            :class="{ loaded: imageLoadedState }"
          >

          <!-- Навигация -->
          <button
            v-if="hasPrev"
            class="nav-button prev"
            @click="prevPhoto"
          >
            ←
          </button>
          <button
            v-if="hasNext"
            class="nav-button next"
            @click="nextPhoto"
          >
            →
          </button>
        </div>

        <!-- Нижняя панель с метаданными -->
        <div class="viewer-footer">
          <!-- Счётчик фотографий -->
          <div class="photo-counter">
            {{ currentIndex + 1 }} из {{ totalPhotos }}
          </div>

          <!-- Метаданные -->
          <div class="photo-metadata">
            <div v-if="currentPhoto.description" class="metadata-section">
              <h4>Описание</h4>
              <p>{{ currentPhoto.description }}</p>
            </div>

            <div class="metadata-grid">
              <div v-if="currentPhoto.location" class="metadata-item">
                <span class="metadata-icon">📍</span>
                <span class="metadata-text">{{ currentPhoto.location }}</span>
              </div>

              <div v-if="currentPhoto.year" class="metadata-item">
                <span class="metadata-icon">📅</span>
                <span class="metadata-text">{{ currentPhoto.year }} год</span>
              </div>

              <div v-if="currentPhoto.people" class="metadata-item">
                <span class="metadata-icon">👥</span>
                <span class="metadata-text">{{ currentPhoto.people }}</span>
              </div>

              <div class="metadata-item">
                <span class="metadata-icon">📦</span>
                <span class="metadata-text">{{ formatFileSize(currentPhoto.fileSize) }}</span>
              </div>

              <div class="metadata-item">
                <span class="metadata-icon">📁</span>
                <span class="metadata-text">{{ currentPhoto.fileName }}</span>
              </div>
            </div>
          </div>

          <!-- Миниатюры для навигации -->
          <div v-if="allPhotos.length > 1" class="thumbnails">
            <div
              v-for="(photo, index) in allPhotos"
              :key="photo.id"
              class="thumbnail"
              :class="{ active: index === currentIndex }"
              @click="goToPhoto(index)"
            >
              <img :src="photo.downloadUrl" :alt="photo.title">
            </div>
          </div>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import photoService from '@/services/photoService'

const props = defineProps({
  show: {
    type: Boolean,
    default: false
  },
  currentPhoto: {
    type: Object,
    default: null
  },
  allPhotos: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['close', 'prev', 'next', 'edit', 'download'])

const imageLoadedState = ref(false)

// Текущий индекс
const currentIndex = computed(() => {
  return props.allPhotos.findIndex(p => p.id === props.currentPhoto.id)
})

// Общее количество фотографий
const totalPhotos = computed(() => props.allPhotos.length)

// Есть ли предыдущая фотография
const hasPrev = computed(() => currentIndex.value > 0)

// Есть ли следующая фотография
const hasNext = computed(() => currentIndex.value < totalPhotos.value - 1)

// Сброс состояния загрузки при смене фото
watch(() => props.currentPhoto, () => {
  imageLoadedState.value = false
})

// Обработчик загрузки изображения
const imageLoaded = () => {
  imageLoadedState.value = true
}

// Переход к предыдущему фото
const prevPhoto = () => {
  if (hasPrev.value) {
    emit('prev', props.allPhotos[currentIndex.value - 1])
  }
}

// Переход к следующему фото
const nextPhoto = () => {
  if (hasNext.value) {
    emit('next', props.allPhotos[currentIndex.value + 1])
  }
}

// Переход к конкретному фото
const goToPhoto = (index) => {
  if (index !== currentIndex.value) {
    emit('next', props.allPhotos[index])
  }
}

// Редактирование фото
const editPhoto = () => {
  emit('edit', props.currentPhoto)
  close()
}

// Скачивание фото
const downloadPhoto = () => {
  emit('download', props.currentPhoto)
}

// Закрытие просмотрщика
const close = () => {
  emit('close')
}

// Обработка клавиш
const handleKeydown = (e) => {
  if (!props.show) return

  switch (e.key) {
    case 'Escape':
      close()
      break
    case 'ArrowLeft':
      prevPhoto()
      break
    case 'ArrowRight':
      nextPhoto()
      break
  }
}

// Добавляем и удаляем обработчик клавиш
watch(() => props.show, (newVal) => {
  if (newVal) {
    window.addEventListener('keydown', handleKeydown)
    document.body.style.overflow = 'hidden'
  } else {
    window.removeEventListener('keydown', handleKeydown)
    document.body.style.overflow = ''
  }
})

// Форматирование размера файла
const formatFileSize = (bytes) => {
  return photoService.constructor.formatFileSize(bytes)
}
</script>

<style scoped>
.photo-viewer-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.9);
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.photo-viewer {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(10px);
}

/* Шапка */
.viewer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem 2rem;
  color: white;
  background: linear-gradient(to bottom, rgba(0,0,0,0.5), transparent);
}

.photo-info h2 {
  font-size: 24px;
  margin: 0 0 4px 0;
  color: white;
}

.photo-date {
  font-size: 14px;
  opacity: 0.7;
}

.viewer-actions {
  display: flex;
  gap: 12px;
}

.action-button {
  background: none;
  border: none;
  color: white;
  font-size: 24px;
  cursor: pointer;
  padding: 8px;
  border-radius: 8px;
  transition: all 0.2s ease;
  opacity: 0.7;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.action-button:hover {
  opacity: 1;
  background-color: rgba(255, 255, 255, 0.2);
  transform: scale(1.1);
}

.close-button {
  font-size: 28px;
}

/* Контейнер изображения */
.image-container {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  padding: 2rem;
}

.image-container img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.image-container img.loaded {
  opacity: 1;
}

/* Кнопки навигации */
.nav-button {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: white;
  font-size: 48px;
  cursor: pointer;
  padding: 20px;
  opacity: 0.5;
  transition: all 0.2s ease;
  text-shadow: 0 2px 4px rgba(0,0,0,0.5);
}

.nav-button:hover {
  opacity: 1;
  transform: translateY(-50%) scale(1.2);
}

.nav-button.prev {
  left: 20px;
}

.nav-button.next {
  right: 20px;
}

/* Нижняя панель */
.viewer-footer {
  background: linear-gradient(to top, rgba(0,0,0,0.5), transparent);
  padding: 2rem;
  color: white;
}

.photo-counter {
  text-align: center;
  font-size: 14px;
  opacity: 0.7;
  margin-bottom: 1rem;
}

/* Метаданные */
.photo-metadata {
  max-width: 800px;
  margin: 0 auto 2rem;
}

.metadata-section {
  margin-bottom: 1.5rem;
  text-align: center;
}

.metadata-section h4 {
  font-size: 16px;
  font-weight: 500;
  opacity: 0.7;
  margin: 0 0 8px 0;
}

.metadata-section p {
  font-size: 18px;
  line-height: 1.6;
  margin: 0;
  word-break: break-word;
}

.metadata-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  justify-content: center;
}

.metadata-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  font-size: 14px;
}

.metadata-icon {
  font-size: 16px;
}

.metadata-text {
  opacity: 0.9;
}

/* Миниатюры */
.thumbnails {
  display: flex;
  gap: 10px;
  overflow-x: auto;
  padding: 10px 0;
  justify-content: center;
}

.thumbnail {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  opacity: 0.5;
  transition: all 0.2s ease;
  border: 2px solid transparent;
  flex-shrink: 0;
}

.thumbnail:hover {
  opacity: 0.8;
  transform: scale(1.1);
}

.thumbnail.active {
  opacity: 1;
  border-color: var(--color-primary, #88d424);
}

.thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

@media (max-width: 768px) {
  .viewer-header {
    padding: 1rem;
    flex-direction: column;
    gap: 1rem;
    text-align: center;
  }

  .photo-info h2 {
    font-size: 20px;
  }

  .nav-button {
    font-size: 32px;
    padding: 10px;
  }

  .viewer-footer {
    padding: 1rem;
  }

  .metadata-grid {
    gap: 8px;
  }

  .metadata-item {
    padding: 4px 12px;
    font-size: 12px;
  }

  .thumbnails {
    justify-content: flex-start;
  }
}
</style>
