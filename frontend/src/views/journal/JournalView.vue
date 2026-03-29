<template>
  <div class="journal-page">
    <div class="journal-header">
      <div class="header-content">
        <h1>🌳 Личный дневник</h1>
        <p class="subtitle">Записывайте свои мысли, чувства и воспоминания</p>
      </div>

      <div class="header-actions">
        <Button
          variant="primary"
          @click="createNewEntry"
        >
          + Новая запись
        </Button>
        <Button
          variant="outline"
          @click="goBack"
        >
          ← Назад к модулям
        </Button>
      </div>
    </div>

    <!-- Статистика -->
    <div class="stats-section" v-if="stats">
      <div class="stat-card">
        <span class="stat-value">{{ stats.totalEntries }}</span>
        <span class="stat-label">Всего записей</span>
      </div>
      <div class="stat-card">
        <span class="stat-value">{{ stats.entriesWithMood }}</span>
        <span class="stat-label">С настроением</span>
      </div>
      <div class="stat-card">
        <span class="stat-value">{{ stats.editedEntries }}</span>
        <span class="stat-label">Отредактировано</span>
      </div>
    </div>

    <!-- Поиск/фильтры (опционально) -->
    <div class="filters-section">
      <div class="search-box">
        <input
          type="text"
          v-model="searchQuery"
          placeholder="🔍 Поиск по заголовку..."
          class="search-input"
        />
      </div>
    </div>

    <!-- Список записей -->
    <div class="entries-section">
      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <p>Загрузка записей...</p>
      </div>

      <div v-else-if="filteredEntries.length === 0" class="empty-state">
        <div class="empty-icon">📝</div>
        <h3>Здесь пока пусто</h3>
        <p>Создайте свою первую запись в дневнике</p>
        <Button
          variant="primary"
          @click="createNewEntry"
        >
          Создать запись
        </Button>
      </div>

      <div v-else class="entries-grid">
        <JournalEntryCard
          v-for="entry in filteredEntries"
          :key="entry.id"
          :entry="entry"
          @view="viewEntry"
          @edit="editEntry"
          @delete="deleteEntry"
        />
      </div>

      <!-- Пагинация -->
      <div v-if="totalPages > 1" class="pagination">
        <button
          class="page-btn"
          :disabled="currentPage === 0"
          @click="changePage(currentPage - 1)"
        >
          ←
        </button>
        <span class="page-info">
          Страница {{ currentPage + 1 }} из {{ totalPages }}
        </span>
        <button
          class="page-btn"
          :disabled="currentPage >= totalPages - 1"
          @click="changePage(currentPage + 1)"
        >
          →
        </button>
      </div>
    </div>

    <!-- Модальное окно для создания/редактирования -->
    <Teleport to="body">
      <div v-if="showForm" class="modal-overlay" @click.self="closeForm">
        <div class="modal-content">
          <JournalEntryForm
            :entry="editingEntry"
            :isEditing="!!editingEntry"
            @submit="saveEntry"
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
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import Button from '@/components/UI/Button.vue'
import Notification from '@/components/UI/Notification.vue'
import JournalEntryCard from './components/JournalEntryCard.vue'
import JournalEntryForm from './JournalEntryForm.vue'
import journalService from '@/services/journalService'

const router = useRouter()
const loading = ref(true)
const entries = ref([])
const stats = ref(null)
const currentPage = ref(0)
const totalPages = ref(0)
const searchQuery = ref('')
const showForm = ref(false)
const editingEntry = ref(null)
const showNotification = ref(false)
const notificationMessage = ref('')
const notificationType = ref('success')

// Фильтрация записей по поиску
const filteredEntries = computed(() => {
  if (!searchQuery.value) return entries.value
  const query = searchQuery.value.toLowerCase()
  return entries.value.filter(entry =>
    entry.title.toLowerCase().includes(query) ||
    entry.content.toLowerCase().includes(query)
  )
})

// Загрузка записей
const loadEntries = async () => {
  loading.value = true
  try {
    const response = await journalService.getEntries(currentPage.value, 10)
    entries.value = response.content
    totalPages.value = response.totalPages
  } catch (error) {
    console.error('❌ Ошибка загрузки записей:', error)
    showNotificationMessage('error', 'Ошибка загрузки записей')
  } finally {
    loading.value = false
  }
}

// Загрузка статистики
const loadStats = async () => {
  try {
    stats.value = await journalService.getStats()
  } catch (error) {
    console.error('❌ Ошибка загрузки статистики:', error)
  }
}

// Смена страницы
const changePage = (newPage) => {
  currentPage.value = newPage
  loadEntries()
}

// Создание новой записи
const createNewEntry = () => {
  editingEntry.value = null
  showForm.value = true
}

// Редактирование записи
const editEntry = (entry) => {
  editingEntry.value = entry
  showForm.value = true
}

// Просмотр записи
const viewEntry = (entry) => {
  router.push(`/journal/${entry.id}`)
}

// Сохранение записи (создание или обновление)
const saveEntry = async (entryData) => {
  try {
    if (editingEntry.value) {
      // Обновление
      await journalService.updateEntry(editingEntry.value.id, entryData)
      showNotificationMessage('success', 'Запись успешно обновлена')
    } else {
      // Создание
      await journalService.createEntry(entryData)
      showNotificationMessage('success', 'Запись успешно создана')
    }

    closeForm()
    loadEntries()
    loadStats()
  } catch (error) {
    console.error('❌ Ошибка сохранения:', error)
    showNotificationMessage('error', 'Ошибка при сохранении записи')
  }
}

// Удаление записи
const deleteEntry = async (entry) => {
  if (!confirm(`Удалить запись "${entry.title}"?`)) {
    return
  }

  try {
    await journalService.deleteEntry(entry.id)
    showNotificationMessage('success', 'Запись успешно удалена')
    loadEntries()
    loadStats()
  } catch (error) {
    console.error('❌ Ошибка удаления:', error)
    showNotificationMessage('error', 'Ошибка при удалении записи')
  }
}

// Закрытие формы
const closeForm = () => {
  showForm.value = false
  editingEntry.value = null
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
  loadEntries()
  loadStats()
})
</script>

<style scoped>
.journal-page {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem 1rem;
}

/* Шапка */
.journal-header {
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

/* Статистика */
.stats-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 20px;
  margin-bottom: 2rem;
}

.stat-card {
  background: linear-gradient(135deg, var(--color-white, #ffffff) 0%, var(--color-white-smoke, #f5f5f5) 100%);
  border: 1px solid var(--color-light-gray, #e0e0e0);
  border-radius: 12px;
  padding: 1.5rem;
  text-align: center;
  transition: transform 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-3px);
  border-color: var(--color-primary, #88d424);
}

.stat-value {
  display: block;
  font-size: 36px;
  font-weight: 600;
  color: var(--color-primary, #88d424);
  line-height: 1.2;
}

.stat-label {
  color: var(--color-gray, #9e9e9e);
  font-size: 14px;
}

/* Фильтры */
.filters-section {
  margin-bottom: 2rem;
}

.search-box {
  max-width: 400px;
}

.search-input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid var(--color-light-gray, #e0e0e0);
  border-radius: 8px;
  font-size: 16px;
  transition: all 0.2s ease;
}

.search-input:focus {
  outline: none;
  border-color: var(--color-primary, #88d424);
  box-shadow: 0 0 0 3px rgba(136, 212, 36, 0.1);
}

/* Сетка записей */
.entries-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
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

/* Пагинация */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 2rem;
}

.page-btn {
  background: none;
  border: 1px solid var(--color-light-gray, #e0e0e0);
  border-radius: 8px;
  padding: 8px 16px;
  font-size: 18px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.page-btn:hover:not(:disabled) {
  background-color: var(--color-primary, #88d424);
  color: white;
  border-color: var(--color-primary, #88d424);
}

.page-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.page-info {
  color: var(--color-gray, #9e9e9e);
  font-size: 14px;
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

/* Адаптивность */
@media (max-width: 768px) {
  .journal-header {
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

  .journal-header button {
    width: 100%;
  }

  .stats-section {
    grid-template-columns: 1fr;
  }

  .entries-grid {
    grid-template-columns: 1fr;
  }

  .pagination {
    flex-wrap: wrap;
  }

  .modal-content {
    margin: 0;
  }
}
</style>
