<template>
  <div class="entry-view">
    <div class="view-card">
      <!-- Шапка с кнопкой назад -->
      <div class="view-header">
        <button class="back-button" @click="goBack">
          ← Назад к списку
        </button>
        <div class="view-actions">
          <Button
            variant="outline"
            size="small"
            @click="showEditForm = true"
          >
            ✏️ Редактировать
          </Button>
          <Button
            variant="error"
            size="small"
            @click="handleDelete"
          >
            🗑️ Удалить
          </Button>
        </div>
      </div>

      <!-- Содержимое записи -->
      <article class="entry-content" v-if="entry">
        <div class="entry-meta">
          <div class="meta-item">
            <span class="meta-label">📅 Создано:</span>
            <span class="meta-value">{{ entry.formattedCreatedAt }}</span>
          </div>
          <div v-if="entry.edited" class="meta-item">
            <span class="meta-label">📝 Изменено:</span>
            <span class="meta-value">{{ entry.formattedUpdatedAt }}</span>
          </div>
          <div v-if="entry.mood" class="meta-item mood">
            <span class="meta-label">😊 Настроение:</span>
            <span class="meta-value">{{ entry.mood }}</span>
          </div>
        </div>

        <h1 class="entry-title">{{ entry.title }}</h1>

        <div class="entry-text">
          {{ entry.content }}
        </div>
      </article>

      <!-- Загрузка -->
      <div v-else-if="loading" class="loading-state">
        <div class="spinner"></div>
        <p>Загрузка записи...</p>
      </div>
    </div>

    <!-- Модальное окно редактирования -->
    <Teleport to="body">
      <div v-if="showEditForm" class="modal-overlay" @click.self="showEditForm = false">
        <div class="modal-content">
          <JournalEntryForm
            :entry="entry"
            :isEditing="true"
            @submit="updateEntry"
            @close="showEditForm = false"
          />
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
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Button from '@/components/UI/Button.vue'
import Notification from '@/components/UI/Notification.vue'
import JournalEntryForm from './JournalEntryForm.vue'
import journalService from '@/services/journalService'

const route = useRoute()
const router = useRouter()
const loading = ref(true)
const entry = ref(null)
const showEditForm = ref(false)
const showNotification = ref(false)
const notificationMessage = ref('')
const notificationType = ref('info')

// Загрузка записи
const loadEntry = async () => {
  const entryId = route.params.id
  if (!entryId) {
    showError('ID записи не указан')
    return
  }

  loading.value = true
  try {
    const data = await journalService.getEntryById(entryId)
    entry.value = data
  } catch (error) {
    console.error('❌ Ошибка загрузки записи:', error)
    if (error.response?.status === 403) {
      showError('У вас нет доступа к этой записи')
    } else if (error.response?.status === 404) {
      showError('Запись не найдена')
    } else {
      showError('Ошибка загрузки записи')
    }
  } finally {
    loading.value = false
  }
}

// Обновление записи
const updateEntry = async (entryData) => {
  try {
    const updated = await journalService.updateEntry(entry.value.id, entryData)
    entry.value = updated
    showEditForm.value = false
    showNotificationMessage('success', 'Запись успешно обновлена')
  } catch (error) {
    console.error('❌ Ошибка обновления:', error)
    showNotificationMessage('error', 'Ошибка при обновлении записи')
  }
}

// Показать ошибку
const showError = (message) => {
  notificationType.value = 'error'
  notificationMessage.value = message
  showNotification.value = true
  setTimeout(() => {
    router.push('/journal')
  }, 2000)
}

// Показать уведомление
const showNotificationMessage = (type, message) => {
  notificationType.value = type
  notificationMessage.value = message
  showNotification.value = true
}

// Переход назад
const goBack = () => {
  router.push('/journal')
}

// Удаление
const handleDelete = async () => {
  if (!confirm('Вы уверены, что хотите удалить эту запись?')) {
    return
  }

  try {
    await journalService.deleteEntry(entry.value.id)
    notificationType.value = 'success'
    notificationMessage.value = 'Запись успешно удалена'
    showNotification.value = true

    setTimeout(() => {
      router.push('/journal')
    }, 2000)
  } catch (error) {
    console.error('❌ Ошибка удаления:', error)
    notificationType.value = 'error'
    notificationMessage.value = 'Ошибка при удалении записи'
    showNotification.value = true
  }
}

onMounted(() => {
  loadEntry()
})
</script>

<style scoped>
.entry-view {
  width: 100%;
  display: flex;
  justify-content: center;
  padding: 2rem 1rem;
}

.view-card {
  width: 100%;
  max-width: 800px;
  background: var(--color-white, #ffffff);
  border-radius: 16px;
  padding: 2rem;
  box-shadow: 0 4px 20px var(--shadow-color, rgba(0,0,0,0.1));
}

.view-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid var(--color-light-gray, #e0e0e0);
}

.back-button {
  background: none;
  border: none;
  color: var(--color-primary, #88d424);
  font-size: 16px;
  cursor: pointer;
  padding: 8px 16px;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.back-button:hover {
  background-color: rgba(136, 212, 36, 0.1);
  transform: translateX(-3px);
}

.view-actions {
  display: flex;
  gap: 10px;
}

/* Мета-информация */
.entry-meta {
  background-color: var(--color-white-smoke, #f5f5f5);
  border-radius: 8px;
  padding: 1rem;
  margin-bottom: 2rem;
  display: flex;
  flex-wrap: wrap;
  gap: 1.5rem;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.meta-label {
  color: var(--color-gray, #9e9e9e);
  font-weight: 500;
}

.meta-value {
  color: var(--color-dark-gray, #616161);
}

.meta-item.mood .meta-value {
  background-color: rgba(136, 212, 36, 0.1);
  padding: 4px 12px;
  border-radius: 20px;
}

/* Заголовок */
.entry-title {
  font-size: 32px;
  font-weight: 600;
  color: var(--color-primary-dark, #6aa91c);
  margin: 0 0 1.5rem 0;
  line-height: 1.3;
}

/* Текст записи */
.entry-text {
  color: var(--color-dark-gray, #616161);
  font-size: 16px;
  line-height: 1.8;
  white-space: pre-wrap;
  word-break: break-word;
}

/* Загрузка */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 3rem;
  color: var(--color-gray, #9e9e9e);
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid var(--color-light-gray, #e0e0e0);
  border-top-color: var(--color-primary, #88d424);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 1rem;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* Модальное окно */
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
  max-width: 800px;
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

/* Адаптивность */
@media (max-width: 768px) {
  .view-card {
    padding: 1.5rem;
  }

  .view-header {
    flex-direction: column;
    gap: 1rem;
    align-items: flex-start;
  }

  .view-actions {
    width: 100%;
    flex-direction: column;
  }

  .view-actions button {
    width: 100%;
  }

  .entry-meta {
    flex-direction: column;
    gap: 0.5rem;
  }

  .entry-title {
    font-size: 24px;
  }

  .entry-text {
    font-size: 15px;
  }
}
</style>
