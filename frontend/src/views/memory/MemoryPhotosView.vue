<template>
  <div class="memory-photos-page">
    <div class="page-header">
      <div class="header-content">
        <h1>📸 Цифровая память</h1>
        <p class="subtitle">Сохраняйте важные моменты и воспоминания</p>
      </div>

      <!-- Кнопка возврата к модулям -->
      <Button
        variant="outline"
        @click="goBack"
      >
        ← Назад к модулям
      </Button>
    </div>

    <!-- Загрузчик фотографий (показывается при activeTab === 'upload') -->
    <div v-if="activeTab === 'upload'" class="upload-section">
      <PhotoUploader @upload-complete="handleUploadComplete" />
    </div>

    <!-- Галерея (показывается при activeTab === 'gallery') -->
    <div v-else class="gallery-section">
      <PhotoGallery
        :photos="photos"
        :stats="stats"
        :loading="loading"
        @upload="switchToUpload"
        @view="viewPhoto"
        @edit="editPhoto"
        @delete="deletePhoto"
        @download="downloadPhoto"
      />
    </div>

    <!-- Режим просмотра одной фотографии -->
    <PhotoViewer
      :show="showViewer"
      :current-photo="currentPhoto"
      :all-photos="photos"
      @close="showViewer = false"
      @prev="viewPhoto"
      @next="viewPhoto"
      @edit="editPhoto"
      @download="downloadPhoto"
    />

    <!-- Форма редактирования фотографии -->
    <Teleport to="body">
      <div v-if="showEditForm" class="modal-overlay" @click.self="closeEditForm">
        <div class="modal-content">
          <div class="edit-form">
            <div class="form-header">
              <h2>Редактирование фотографии</h2>
              <button class="close-button" @click="closeEditForm">✕</button>
            </div>

            <form @submit.prevent="savePhotoMetadata">
              <div class="form-preview">
                <img :src="editingPhoto?.downloadUrl" alt="Preview">
              </div>

              <div class="form-row">
                <TextField
                  v-model="editForm.description"
                  label="Описание"
                  placeholder="Что на этой фотографии?"
                  clearable
                />
              </div>

              <div class="form-row">
                <TextField
                  v-model="editForm.location"
                  label="Место съёмки"
                  placeholder="Где сделано фото?"
                  clearable
                />
              </div>

              <div class="form-row">
                <div class="row-fields">
                  <TextField
                    v-model="editForm.year"
                    label="Год"
                    type="number"
                    placeholder="2024"
                    class="year-field"
                  />
                  <TextField
                    v-model="editForm.people"
                    label="Люди"
                    placeholder="Кто на фото?"
                    class="people-field"
                  />
                </div>
              </div>

              <div class="form-actions">
                <Button
                  type="button"
                  variant="outline"
                  @click="closeEditForm"
                >
                  Отмена
                </Button>
                <Button
                  type="submit"
                  variant="primary"
                  :loading="saving"
                >
                  Сохранить
                </Button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </Teleport>

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
import { ref, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import Button from '@/components/UI/Button.vue'
import TextField from '@/components/UI/TextField.vue'
import Notification from '@/components/UI/Notification.vue'
import PhotoUploader from './PhotoUploader.vue'
import PhotoGallery from './PhotoGallery.vue'
import PhotoViewer from './PhotoViewer.vue'
import photoService from '@/services/photoService'

const router = useRouter()
const loading = ref(true)
const photos = ref([])
const stats = ref(null)
const activeTab = ref('gallery')
const showViewer = ref(false)
const currentPhoto = ref(null)
const showEditForm = ref(false)
const editingPhoto = ref(null)
const saving = ref(false)
const showNotification = ref(false)
const notificationMessage = ref('')
const notificationType = ref('success')

// Форма редактирования
const editForm = reactive({
  description: '',
  location: '',
  year: '',
  people: ''
})

// Загрузка данных
const loadPhotos = async () => {
  loading.value = true
  try {
    const response = await photoService.getAllPhotos()
    photos.value = response.photos || []
    stats.value = {
      totalCount: response.totalCount || 0,
      formattedTotalSize: response.formattedTotalSize || '0 B',
      years: response.years || []
    }
    console.log('📥 Загружено фотографий:', photos.value.length)
  } catch (error) {
    console.error('❌ Ошибка загрузки фотографий:', error)
    showNotificationMessage('error', 'Ошибка загрузки фотографий')
    photos.value = []
    stats.value = { totalCount: 0, formattedTotalSize: '0 B', years: [] }
  } finally {
    loading.value = false
  }
}

// Переключение на вкладку загрузки
const switchToUpload = () => {
  activeTab.value = 'upload'
}

// Обработка завершения загрузки
const handleUploadComplete = () => {
  activeTab.value = 'gallery'
  loadPhotos()
  showNotificationMessage('success', 'Фотографии успешно загружены')
}

// Просмотр фотографии
const viewPhoto = (photo) => {
  currentPhoto.value = photo
  showViewer.value = true
}

// Редактирование фотографии
const editPhoto = (photo) => {
  editingPhoto.value = photo
  editForm.description = photo.description || ''
  editForm.location = photo.location || ''
  editForm.year = photo.year || ''
  editForm.people = photo.people || ''
  showEditForm.value = true
}

// Сохранение метаданных
const savePhotoMetadata = async () => {
  if (!editingPhoto.value) return

  saving.value = true
  try {
    const metadata = {
      description: editForm.description || null,
      location: editForm.location || null,
      year: editForm.year ? parseInt(editForm.year) : null,
      people: editForm.people || null
    }

    const updated = await photoService.updatePhotoMetadata(editingPhoto.value.id, metadata)

    // Обновляем фото в списке
    const index = photos.value.findIndex(p => p.id === editingPhoto.value.id)
    if (index !== -1) {
      photos.value[index] = updated
    }

    showNotificationMessage('success', 'Метаданные обновлены')
    closeEditForm()
  } catch (error) {
    console.error('❌ Ошибка обновления:', error)
    showNotificationMessage('error', 'Ошибка при обновлении')
  } finally {
    saving.value = false
  }
}

// Закрытие формы редактирования
const closeEditForm = () => {
  showEditForm.value = false
  editingPhoto.value = null
  editForm.description = ''
  editForm.location = ''
  editForm.year = ''
  editForm.people = ''
}

// Удаление фотографии
const deletePhoto = async (photo) => {
  if (!confirm(`Удалить фотографию?`)) {
    return
  }

  try {
    await photoService.deletePhoto(photo.id)
    showNotificationMessage('success', 'Фотография удалена')
    await loadPhotos()

    // Если удалили текущее просматриваемое фото, закрываем просмотрщик
    if (showViewer.value && currentPhoto.value?.id === photo.id) {
      showViewer.value = false
    }
  } catch (error) {
    console.error('❌ Ошибка удаления:', error)
    showNotificationMessage('error', 'Ошибка при удалении')
  }
}

// Скачивание фотографии
const downloadPhoto = async (photo) => {
  try {
    await photoService.downloadPhoto(photo.id, photo.fileName)
    showNotificationMessage('success', 'Скачивание начато')
  } catch (error) {
    console.error('❌ Ошибка скачивания:', error)
    showNotificationMessage('error', 'Ошибка при скачивании')
  }
}

// Возврат на главную
const goBack = () => {
  router.push('/')
}

// Показать уведомление
const showNotificationMessage = (type, message) => {
  notificationType.value = type
  notificationMessage.value = message
  showNotification.value = true
}

onMounted(() => {
  loadPhotos()
})
</script>

<style scoped>
.memory-photos-page {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem 1rem;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  padding-bottom: 1rem;
  border-bottom: 2px solid var(--color-primary, #88d424);
}

.header-content h1 {
  font-size: 32px;
  color: var(--color-primary-dark, #6aa91c);
  margin: 0 0 4px 0;
}

.subtitle {
  color: var(--color-gray, #9e9e9e);
  margin: 0;
  font-size: 16px;
}

/* Секции */
.upload-section,
.gallery-section {
  min-height: 500px;
}

/* Модальное окно редактирования */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 1rem;
  overflow-y: auto;
}

.modal-content {
  width: 100%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
  animation: slideIn 0.3s ease;
}

@keyframes slideIn {
  from {
    transform: translateY(-30px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

/* Форма редактирования */
.edit-form {
  background: var(--color-white, #ffffff);
  border-radius: 16px;
  padding: 2rem;
  box-shadow: 0 4px 20px var(--shadow-color, rgba(0,0,0,0.1));
}

.form-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.form-header h2 {
  font-size: 24px;
  font-weight: 600;
  color: var(--color-primary-dark, #6aa91c);
  margin: 0;
}

.close-button {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: var(--color-gray, #9e9e9e);
  padding: 4px 8px;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.close-button:hover {
  background-color: var(--color-white-smoke, #f5f5f5);
  color: var(--color-error, #f44336);
}

.form-preview {
  width: 100%;
  height: 200px;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 1.5rem;
  background-color: var(--color-white-smoke, #f5f5f5);
}

.form-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.form-row {
  margin-bottom: 1.5rem;
}

.row-fields {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 12px;
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 2rem;
  padding-top: 1rem;
  border-top: 1px solid var(--color-light-gray, #e0e0e0);
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 1rem;
    text-align: center;
  }

  .page-header button {
    width: 100%;
  }

  .row-fields {
    grid-template-columns: 1fr;
  }

  .form-actions {
    flex-direction: column;
  }

  .form-actions button {
    width: 100%;
  }
}
</style>
