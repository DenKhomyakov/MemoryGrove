<template>
  <div class="photo-uploader">
    <div class="uploader-header">
      <h2>Загрузка фотографий</h2>
      <p class="uploader-subtitle">Добавьте фотографии в свой цифровой альбом</p>
    </div>

    <!-- Зона загрузки -->
    <div
      class="drop-zone"
      :class="{ 'drag-over': isDragOver }"
      @dragover.prevent="handleDragOver"
      @dragleave.prevent="handleDragLeave"
      @drop.prevent="handleDrop"
      @click="triggerFileInput"
    >
      <input
        ref="fileInput"
        type="file"
        multiple
        accept="image/jpeg,image/png,image/gif,image/webp"
        @change="handleFileSelect"
        style="display: none"
      >

      <div class="drop-zone-content">
        <span class="upload-icon">📸</span>
        <p class="drop-text">Перетащите фотографии сюда или нажмите для выбора</p>
        <p class="drop-hint">Поддерживаются JPEG, PNG, GIF, WebP (макс. 10 МБ)</p>
      </div>
    </div>

    <!-- Предпросмотр перед загрузкой -->
    <div v-if="selectedFiles.length > 0" class="preview-section">
      <h3>Выбрано файлов: {{ selectedFiles.length }}</h3>

      <div class="preview-grid">
        <div
          v-for="(file, index) in selectedFiles"
          :key="index"
          class="preview-item"
        >
          <div class="preview-image">
            <img :src="file.preview" alt="preview">
            <button
              class="remove-file"
              @click="removeFile(index)"
              title="Удалить из списка"
            >
              ✕
            </button>
          </div>

          <div class="preview-info">
            <div class="file-name">{{ file.name }}</div>
            <div class="file-size">{{ formatFileSize(file.size) }}</div>
          </div>

          <!-- Метаданные для файла -->
          <div class="file-metadata">
            <input
              v-model="file.metadata.description"
              type="text"
              placeholder="Описание"
              class="metadata-input"
            >
            <input
              v-model="file.metadata.location"
              type="text"
              placeholder="Место"
              class="metadata-input"
            >
            <div class="metadata-row">
              <input
                v-model="file.metadata.year"
                type="number"
                placeholder="Год"
                class="metadata-input small"
              >
              <input
                v-model="file.metadata.people"
                type="text"
                placeholder="Люди"
                class="metadata-input"
              >
            </div>
          </div>
        </div>
      </div>

      <!-- Кнопки действий -->
      <div class="upload-actions">
        <Button
          variant="outline"
          @click="clearAll"
        >
          Очистить всё
        </Button>
        <Button
          variant="primary"
          :loading="uploading"
          @click="uploadAll"
        >
          Загрузить {{ selectedFiles.length }} {{ pluralize(selectedFiles.length) }}
        </Button>
      </div>
    </div>

    <!-- Уведомления -->
    <Notification
      v-if="showNotification"
      :type="notificationType"
      :message="notificationMessage"
      :duration="3000"
      @close="showNotification = false"
    />
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import Button from '@/components/UI/Button.vue'
import Notification from '@/components/UI/Notification.vue'
import photoService from '@/services/photoService'

const emit = defineEmits(['upload-complete'])

const fileInput = ref(null)
const isDragOver = ref(false)
const selectedFiles = ref([])
const uploading = ref(false)
const showNotification = ref(false)
const notificationMessage = ref('')
const notificationType = ref('success')

// Обработка перетаскивания
const handleDragOver = () => {
  isDragOver.value = true
}

const handleDragLeave = () => {
  isDragOver.value = false
}

const handleDrop = (event) => {
  isDragOver.value = false
  const files = Array.from(event.dataTransfer.files)
  processFiles(files)
}

// Выбор файлов через диалог
const triggerFileInput = () => {
  fileInput.value.click()
}

const handleFileSelect = (event) => {
  const files = Array.from(event.target.files)
  processFiles(files)
  // Очищаем input, чтобы можно было выбрать те же файлы снова
  event.target.value = ''
}

// Обработка файлов
const processFiles = (files) => {
  // Фильтруем по типу и размеру
  const validFiles = files.filter(file => {
    // Проверка типа
    const validTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp']
    if (!validTypes.includes(file.type)) {
      showNotificationMessage('error', `Файл ${file.name} имеет неподдерживаемый тип`)
      return false
    }

    // Проверка размера (10 MB)
    if (file.size > 10 * 1024 * 1024) {
      showNotificationMessage('error', `Файл ${file.name} превышает 10 МБ`)
      return false
    }

    return true
  })

  // Создаём предпросмотр для каждого файла
  validFiles.forEach(file => {
    const reader = new FileReader()
    reader.onload = (e) => {
      selectedFiles.value.push({
        file: file,
        name: file.name,
        size: file.size,
        preview: e.target.result,
        metadata: reactive({
          description: '',
          location: '',
          year: null,
          people: ''
        })
      })
    }
    reader.readAsDataURL(file)
  })
}

// Удаление файла из списка
const removeFile = (index) => {
  selectedFiles.value.splice(index, 1)
}

// Очистка всех файлов
const clearAll = () => {
  selectedFiles.value = []
}

// Загрузка всех файлов
const uploadAll = async () => {
  if (selectedFiles.value.length === 0) return

  uploading.value = true
  let successCount = 0
  let errorCount = 0

  for (const fileData of selectedFiles.value) {
    try {
      const formData = new FormData()
      formData.append('file', fileData.file)
      formData.append('description', fileData.metadata.description || '')
      formData.append('location', fileData.metadata.location || '')
      formData.append('year', fileData.metadata.year || '')
      formData.append('people', fileData.metadata.people || '')

      await photoService.uploadPhoto(formData)
      successCount++
    } catch (error) {
      console.error(`❌ Ошибка загрузки ${fileData.name}:`, error)
      errorCount++
    }
  }

  // Показываем результат
  if (errorCount === 0) {
    showNotificationMessage('success', `✅ Все ${successCount} ${pluralize(successCount)} успешно загружены`)
    selectedFiles.value = []
    emit('upload-complete')
  } else {
    showNotificationMessage('warning', `⚠️ Загружено: ${successCount}, ошибок: ${errorCount}`)
    // Удаляем успешно загруженные файлы из списка
    // В реальном проекте здесь лучше реализовать более сложную логику
  }

  uploading.value = false
}

// Форматирование размера файла
const formatFileSize = (bytes) => {
  return photoService.constructor.formatFileSize(bytes)
}

// Плюрализация
const pluralize = (count) => {
  const words = ['фотография', 'фотографии', 'фотографий']
  const cases = [2, 0, 1, 1, 1, 2]
  return words[(count % 100 > 4 && count % 100 < 20) ? 2 : cases[Math.min(count % 10, 5)]]
}

// Показать уведомление
const showNotificationMessage = (type, message) => {
  notificationType.value = type
  notificationMessage.value = message
  showNotification.value = true
}
</script>

<style scoped>
.photo-uploader {
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
  background: var(--color-white, #ffffff);
  border-radius: 16px;
  padding: 2rem;
  box-shadow: 0 4px 20px var(--shadow-color, rgba(0,0,0,0.1));
}

.uploader-header {
  text-align: center;
  margin-bottom: 2rem;
}

.uploader-header h2 {
  font-size: 28px;
  font-weight: 600;
  color: var(--color-primary-dark, #6aa91c);
  margin: 0 0 8px;
}

.uploader-subtitle {
  color: var(--color-gray, #9e9e9e);
  margin: 0;
  font-size: 16px;
}

/* Зона загрузки */
.drop-zone {
  border: 2px dashed var(--color-light-gray, #e0e0e0);
  border-radius: 12px;
  padding: 3rem 2rem;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background-color: var(--color-white-smoke, #f5f5f5);
  margin-bottom: 2rem;
}

.drop-zone:hover {
  border-color: var(--color-primary, #88d424);
  background-color: rgba(136, 212, 36, 0.05);
}

.drop-zone.drag-over {
  border-color: var(--color-primary, #88d424);
  background-color: rgba(136, 212, 36, 0.1);
  transform: scale(1.02);
}

.drop-zone-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
}

.upload-icon {
  font-size: 48px;
  opacity: 0.5;
}

.drop-text {
  color: var(--color-dark-gray, #616161);
  font-size: 16px;
  margin: 0;
}

.drop-hint {
  color: var(--color-gray, #9e9e9e);
  font-size: 14px;
  margin: 0;
}

/* Предпросмотр */
.preview-section {
  margin-top: 2rem;
}

.preview-section h3 {
  font-size: 18px;
  color: var(--color-primary-dark, #6aa91c);
  margin-bottom: 1rem;
}

.preview-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 2rem;
}

.preview-item {
  background: var(--color-white-smoke, #f5f5f5);
  border-radius: 12px;
  padding: 1rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.preview-image {
  position: relative;
  width: 100%;
  height: 150px;
  border-radius: 8px;
  overflow: hidden;
}

.preview-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.remove-file {
  position: absolute;
  top: 5px;
  right: 5px;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.5);
  color: white;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  transition: all 0.2s ease;
}

.remove-file:hover {
  background: rgba(244, 67, 54, 0.8);
  transform: scale(1.1);
}

.preview-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
}

.file-name {
  color: var(--color-dark-gray, #616161);
  font-weight: 500;
  word-break: break-word;
  flex: 1;
  margin-right: 8px;
}

.file-size {
  color: var(--color-gray, #9e9e9e);
  white-space: nowrap;
}

/* Метаданные файла */
.file-metadata {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.metadata-input {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid var(--color-light-gray, #e0e0e0);
  border-radius: 6px;
  font-size: 13px;
  transition: all 0.2s ease;
}

.metadata-input:focus {
  outline: none;
  border-color: var(--color-primary, #88d424);
  box-shadow: 0 0 0 3px rgba(136, 212, 36, 0.1);
}

.metadata-input.small {
  width: 80px;
}

.metadata-row {
  display: flex;
  gap: 8px;
}

/* Кнопки действий */
.upload-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

@media (max-width: 768px) {
  .photo-uploader {
    padding: 1.5rem;
  }

  .uploader-header h2 {
    font-size: 24px;
  }

  .preview-grid {
    grid-template-columns: 1fr;
  }

  .upload-actions {
    flex-direction: column;
  }

  .upload-actions button {
    width: 100%;
  }
}
</style>
