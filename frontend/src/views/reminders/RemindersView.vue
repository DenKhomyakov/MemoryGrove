<template>
  <div class="reminders-page">
    <div class="page-header">
      <div class="header-content">
        <h1>⏰ Напоминания</h1>
        <p class="subtitle">Добрые напоминания о важных делах</p>
      </div>

      <div class="header-actions">
        <Button
          variant="primary"
          @click="createNewReminder"
        >
          + Новое напоминание
        </Button>
        <Button
          variant="outline"
          @click="goBack"
        >
          ← Назад к модулям
        </Button>
      </div>
    </div>

    <!-- Фильтры по статусам -->
    <div class="filters-section">
      <button
        v-for="filter in statusFilters"
        :key="filter.id"
        class="filter-button"
        :class="{ active: currentFilter === filter.id }"
        @click="setFilter(filter.id)"
      >
        <span class="filter-dot" :style="{ backgroundColor: filter.color }"></span>
        {{ filter.label }}
      </button>
    </div>

    <!-- Список напоминаний -->
    <div class="reminders-section">
      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <p>Загрузка напоминаний...</p>
      </div>

      <div v-else-if="filteredReminders.length === 0" class="empty-state">
        <div class="empty-icon">⏰</div>
        <h3>Нет напоминаний</h3>
        <p v-if="currentFilter === 'all'">
          Создайте первое напоминание, чтобы не забывать о важном
        </p>
        <p v-else>
          Нет напоминаний с выбранным статусом
        </p>
        <Button
          variant="primary"
          @click="createNewReminder"
        >
          Создать напоминание
        </Button>
      </div>

      <div v-else class="reminders-grid">
        <ReminderCard
          v-for="reminder in filteredReminders"
          :key="reminder.id"
          :reminder="reminder"
          @complete="markAsCompleted"
          @cancel="markAsCancelled"
        @edit="editReminder"
        @delete="deleteReminder"
        />
      </div>
    </div>

    <!-- Модальное окно для создания/редактирования -->
    <Teleport to="body">
      <div v-if="showForm" class="modal-overlay" @click.self="closeForm">
        <div class="modal-content">
          <ReminderForm
            :reminder="editingReminder"
            :isEditing="!!editingReminder"
            @submit="saveReminder"
            @close="closeForm"
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
import { ref, onMounted, computed } from 'vue'
import Button from '@/components/UI/Button.vue'
import Notification from '@/components/UI/Notification.vue'
import ReminderCard from './components/ReminderCard.vue'
import ReminderForm from './ReminderForm.vue'
import reminderService from '@/services/reminderService'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(true)
const allReminders = ref([])
const showForm = ref(false)
const editingReminder = ref(null)
const currentFilter = ref('all')
const showNotification = ref(false)
const notificationMessage = ref('')
const notificationType = ref('success')

// Получаем статусы из сервиса
const statuses = reminderService.constructor.Status || {
  PENDING: '6cc6ee82-b293-453b-a5df-94428f5f84ed',
  SENT: '5f699cae-62e2-431b-a460-e9d594938424',
  COMPLETED: '7665777b-e1e9-4be7-a8c5-fb2e7d457b20',
  CANCELLED: 'e6ebcf79-82ac-4d92-bb35-0ec71a3845cd'
}

// Фильтры для статусов
const statusFilters = [
  { id: 'all', label: 'Все', color: 'var(--color-gray)' },
  { id: statuses.PENDING, label: 'Ожидают', color: 'var(--color-warning)' },
  { id: statuses.SENT, label: 'Отправлены', color: 'var(--color-primary)' },
  { id: statuses.COMPLETED, label: 'Выполнены', color: 'var(--color-success)' },
  { id: statuses.CANCELLED, label: 'Отменены', color: 'var(--color-gray)' }
]

// Отфильтрованные напоминания
const filteredReminders = computed(() => {
  if (!allReminders.value || allReminders.value.length === 0) {
    return []
  }

  if (currentFilter.value === 'all') {
    return allReminders.value
  }

  return allReminders.value.filter(r => r.status && r.status.id === currentFilter.value)
})

// Загрузка напоминаний
const loadReminders = async () => {
  loading.value = true
  try {
    const reminders = await reminderService.getAllReminders()
    console.log('📥 Получены напоминания:', reminders)

    if (Array.isArray(reminders)) {
      allReminders.value = reminders
    } else if (reminders && reminders.content) {
      allReminders.value = reminders.content
    } else {
      console.warn('⚠️ Неожиданный формат данных:', reminders)
      allReminders.value = []
    }

    console.log('✅ Загружено напоминаний:', allReminders.value.length)
  } catch (error) {
    console.error('❌ Ошибка загрузки напоминаний:', error)
    showNotificationMessage('error', 'Ошибка загрузки напоминаний')
    allReminders.value = []
  } finally {
    loading.value = false
  }
}

// Установка фильтра
const setFilter = (filterId) => {
  currentFilter.value = filterId
}

// Создание нового напоминания
const createNewReminder = () => {
  editingReminder.value = null
  showForm.value = true
}

// Редактирование напоминания
const editReminder = (reminder) => {
  editingReminder.value = reminder
  showForm.value = true
}

// Отметка как выполненное
const markAsCompleted = async (reminder) => {
  try {
    await reminderService.updateReminderStatus(reminder.id, statuses.COMPLETED)
    showNotificationMessage('success', 'Напоминание отмечено как выполненное')
    await loadReminders()
  } catch (error) {
    console.error('❌ Ошибка:', error)
    showNotificationMessage('error', 'Ошибка при обновлении статуса')
  }
}

// Отметка как отменённое
const markAsCancelled = async (reminder) => {
  if (!confirm(`Отменить напоминание "${reminder.title}"?`)) {
    return
  }

  try {
    await reminderService.updateReminderStatus(reminder.id, statuses.CANCELLED)
    showNotificationMessage('success', 'Напоминание отменено')
    await loadReminders()
  } catch (error) {
    console.error('❌ Ошибка:', error)
    showNotificationMessage('error', 'Ошибка при отмене напоминания')
  }
}

// Сохранение напоминания (создание или обновление)
const saveReminder = async (reminderData) => {
  try {
    let response

    if (editingReminder.value) {
      // Обновление
      response = await reminderService.updateReminder(editingReminder.value.id, {
        ...reminderData,
        statusId: editingReminder.value.status.id
      })
      showNotificationMessage('success', 'Напоминание обновлено')
    } else {
      // Создание
      response = await reminderService.createReminder(reminderData)
      showNotificationMessage('success', 'Напоминание создано')
    }

    closeForm()
    await loadReminders()

    // Логируем ответ для отладки (можно удалить потом)
    console.log('Ответ от сервера:', response)

  } catch (error) {
    console.error('❌ Ошибка сохранения:', error)
    showNotificationMessage('error', 'Ошибка при сохранении')
  }
}

// Удаление напоминания
const deleteReminder = async (reminder) => {
  if (!confirm(`Удалить напоминание "${reminder.title}"?`)) {
    return
  }

  try {
    await reminderService.deleteReminder(reminder.id)
    showNotificationMessage('success', 'Напоминание удалено')
    await loadReminders()
  } catch (error) {
    console.error('❌ Ошибка удаления:', error)
    showNotificationMessage('error', 'Ошибка при удалении')
  }
}

// Закрытие формы
const closeForm = () => {
  showForm.value = false
  editingReminder.value = null
}

// Показать уведомление
const showNotificationMessage = (type, message) => {
  notificationType.value = type
  notificationMessage.value = message
  showNotification.value = true
}

// Возврат на главную
const goBack = () => {
  router.push('/')
}

onMounted(() => {
  loadReminders()
})
</script>

<style scoped>
.reminders-page {
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

.search-input:focus {
  outline: none;
  border-color: var(--color-primary, #88d424);
  box-shadow: 0 0 0 3px rgba(136, 212, 36, 0.1);
}

.header-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.subtitle {
  color: var(--color-gray, #9e9e9e);
  margin: 0;
  font-size: 16px;
}

/* Фильтры */
.filters-section {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 2rem;
  padding: 1rem;
  background-color: var(--color-white, #ffffff);
  border-radius: 12px;
  box-shadow: 0 2px 8px var(--shadow-color, rgba(0,0,0,0.05));
}

.filter-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border: 1px solid var(--color-light-gray, #e0e0e0);
  border-radius: 20px;
  background: none;
  color: var(--color-gray, #9e9e9e);
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.filter-button:hover {
  border-color: var(--color-primary, #88d424);
  color: var(--color-primary-dark, #6aa91c);
}

.filter-button.active {
  background-color: var(--color-primary, #88d424);
  border-color: var(--color-primary, #88d424);
  color: white;
}

.filter-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  display: inline-block;
}

/* Сетка напоминаний */
.reminders-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
  margin-bottom: 2rem;
}

/* Пустое состояние */
.empty-state {
  text-align: center;
  padding: 3rem;
  background: var(--color-white-smoke, #f5f5f5);
  border-radius: 12px;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 1rem;
  opacity: 0.5;
}

.empty-state h3 {
  color: var(--color-dark-gray, #616161);
  font-size: 20px;
  margin-bottom: 0.5rem;
}

.empty-state p {
  color: var(--color-gray, #9e9e9e);
  margin-bottom: 1.5rem;
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

/* Адаптивность */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 1rem;
    text-align: center;
  }

  .header-actions {
    width: 100%;
  }

  .header-actions button {
    flex: 1;
  }

  .page-header button {
    width: 100%;
  }

  .filters-section {
    justify-content: center;
  }

  .reminders-grid {
    grid-template-columns: 1fr;
  }
}
</style>
