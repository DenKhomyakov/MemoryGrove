<template>
  <div class="profile-photo-section">
    <div class="photo-header">
      <span class="photo-icon">📸</span>
      <h3>Фото профиля</h3>
    </div>

    <div class="photo-content">
      <div class="current-photo">
        <Avatar
          :src="photoUrl"
          :name="userFullName"
          size="large"
          :bg-color="avatarColor"
          class="avatar-centered"
        />

        <div class="photo-actions">
          <input
            ref="fileInput"
            type="file"
            accept="image/jpeg,image/png,image/gif,image/webp"
            style="display: none"
            @change="handleFileSelect"
          />

          <Button
            variant="outline"
            size="small"
            @click="triggerFileInput"
            :disabled="uploading"
          >
            {{ uploading ? 'Загрузка...' : '📷 Загрузить фото' }}
          </Button>

          <Button
            v-if="hasPhoto"
            variant="text"
            size="small"
            @click="deletePhoto"
            :disabled="uploading"
            class="delete-photo-btn"
          >
            🗑️ Удалить
          </Button>
        </div>

        <p class="photo-hint">
          Поддерживаются JPEG, PNG, GIF, WebP (макс. 10 МБ)
        </p>
      </div>
    </div>

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
import { ref, computed } from 'vue'
import Button from '@/components/UI/Button.vue'
import Notification from '@/components/UI/Notification.vue'
import Avatar from '@/components/UI/Avatar.vue'
import userService from '@/services/userService'

const props = defineProps({
  userId: {
    type: String,
    required: true
  },
  userFullName: {
    type: String,
    default: ''
  },
  currentPhotoUrl: {
    type: String,
    default: null
  }
})

const emit = defineEmits(['photo-updated'])

const fileInput = ref(null)
const uploading = ref(false)
const showNotification = ref(false)
const notificationMessage = ref('')
const notificationType = ref('success')

const hasPhoto = computed(() => {
  return props.currentPhotoUrl && props.currentPhotoUrl.trim() !== ''
})

const photoUrl = computed(() => {
  if (hasPhoto.value) {
    return props.currentPhotoUrl
  }
  return null
})

const avatarColor = computed(() => {
  if (!props.userFullName) return 'var(--color-primary, #88d424)'

  let hash = 0
  for (let i = 0; i < props.userFullName.length; i++) {
    hash = ((hash << 5) - hash) + props.userFullName.charCodeAt(i)
    hash |= 0
  }

  const colors = [
    '#88d424', '#6aa91c', '#4caf50', '#ff9800', '#f44336', '#2196f3', '#9c27b0'
  ]
  return colors[Math.abs(hash) % colors.length]
})

const triggerFileInput = () => {
  fileInput.value.click()
}

const handleFileSelect = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  const validTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp']
  if (!validTypes.includes(file.type)) {
    showNotificationMessage('error', 'Неподдерживаемый тип файла')
    return
  }

  if (file.size > 10 * 1024 * 1024) {
    showNotificationMessage('error', 'Файл не должен превышать 10 МБ')
    return
  }

  uploading.value = true

  try {
    const updatedUser = await userService.uploadProfilePhoto(props.userId, file)
    emit('photo-updated', updatedUser)
    showNotificationMessage('success', 'Фото профиля обновлено')
  } catch (error) {
    console.error('❌ Ошибка загрузки фото:', error)
    showNotificationMessage('error', 'Ошибка при загрузке фото')
  } finally {
    uploading.value = false
    if (fileInput.value) fileInput.value.value = ''
  }
}

const deletePhoto = async () => {
  if (!confirm('Удалить фото профиля?')) return

  uploading.value = true

  try {
    await userService.deleteProfilePhoto(props.userId)
    emit('photo-updated', null)
    showNotificationMessage('success', 'Фото профиля удалено')
  } catch (error) {
    console.error('❌ Ошибка удаления фото:', error)
    showNotificationMessage('error', 'Ошибка при удалении фото')
  } finally {
    uploading.value = false
  }
}

const showNotificationMessage = (type, message) => {
  notificationType.value = type
  notificationMessage.value = message
  showNotification.value = true
}
</script>

<style scoped>
.profile-photo-section {
  background: var(--color-white, #ffffff);
  border-radius: 12px;
  padding: 1.5rem;
  border: 1px solid var(--color-light-gray, #e0e0e0);
}

.photo-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 1.5rem;
  padding-bottom: 0.75rem;
  border-bottom: 2px solid var(--color-primary, #88d424);
}

.photo-icon {
  font-size: 24px;
}

.photo-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-primary-dark, #6aa91c);
  margin: 0;
}

.photo-content {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
}

.current-photo {
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
}

.avatar-centered {
  margin: 0 auto;
  display: flex;
  justify-content: center;
}

.photo-actions {
  margin-top: 1.5rem;
  display: flex;
  gap: 12px;
  justify-content: center;
  flex-wrap: wrap;
}

.photo-hint {
  margin-top: 1rem;
  font-size: 12px;
  color: var(--color-gray, #9e9e9e);
  text-align: center;
}

.delete-photo-btn {
  color: var(--color-error, #f44336);
}

.delete-photo-btn:hover {
  background-color: rgba(244, 67, 54, 0.1);
  color: var(--color-error, #f44336);
}

@media (max-width: 768px) {
  .photo-actions {
    flex-direction: column;
    width: 100%;
    max-width: 250px;
  }

  .photo-actions button {
    width: 100%;
  }
}
</style>
