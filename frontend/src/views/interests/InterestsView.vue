<template>
  <div class="interests-page">
    <div class="page-header">
      <div class="header-content">
        <h1>🎨 Коллекция интересов</h1>
        <p class="subtitle">Фильмы, книги, музыка и всё, что вам нравится</p>
      </div>

      <div class="header-actions">
        <Button
          variant="primary"
          @click="createNewInterest"
        >
          + Новый интерес
        </Button>
        <Button
          variant="outline"
          @click="goBack"
        >
          ← Назад к модулям
        </Button>
      </div>
    </div>

    <!-- Статистика по категориям -->
    <div v-if="stats.length > 0" class="stats-section">
      <div
        v-for="stat in stats"
        :key="stat.category"
        class="stat-card"
        :class="{ active: selectedCategory === stat.category }"
        @click="selectedCategory = selectedCategory === stat.category ? 'all' : stat.category"
      >
        <div class="stat-icon">{{ getCategoryIcon(stat.category) }}</div>
        <div class="stat-info">
          <div class="stat-name">{{ stat.displayName }}</div>
          <div class="stat-numbers">
            <span class="stat-count">{{ stat.count }}</span>
            <span class="stat-likes">❤️ {{ stat.likesCount }}</span>
          </div>
        </div>
      </div>

      <!-- Кнопка "Все категории" -->
      <div
        class="stat-card all-categories"
        :class="{ active: selectedCategory === 'all' }"
        @click="selectedCategory = 'all'"
      >
        <div class="stat-icon">📋</div>
        <div class="stat-info">
          <div class="stat-name">Все</div>
          <div class="stat-numbers">
            <span class="stat-count">{{ totalCount }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Поиск и фильтры -->
    <div class="filters-section">
      <div class="search-box">
        <input
          type="text"
          v-model="searchQuery"
          placeholder="🔍 Поиск по названию или описанию..."
          class="search-input"
        />
      </div>

      <div class="filter-buttons">
        <button
          class="filter-button"
          :class="{ active: likeFilter === 'all' }"
          @click="likeFilter = 'all'"
        >
          Все
        </button>
        <button
          class="filter-button liked"
          :class="{ active: likeFilter === 'liked' }"
          @click="likeFilter = 'liked'"
        >
          ❤️ Нравится
        </button>
        <button
          class="filter-button disliked"
          :class="{ active: likeFilter === 'disliked' }"
          @click="likeFilter = 'disliked'"
        >
          💔 Не нравится
        </button>
        <button
          class="filter-button unrated"
          :class="{ active: likeFilter === 'unrated' }"
          @click="likeFilter = 'unrated'"
        >
          🤔 Не оценено
        </button>
      </div>
    </div>

    <!-- Список интересов -->
    <div class="interests-section">
      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <p>Загрузка интересов...</p>
      </div>

      <div v-else-if="filteredInterests.length === 0" class="empty-state">
        <div class="empty-icon">🎨</div>
        <h3>Нет интересов</h3>
        <p v-if="selectedCategory !== 'all' || likeFilter !== 'all' || searchQuery">
          Попробуйте изменить параметры фильтрации
        </p>
        <p v-else>
          Добавьте фильмы, книги, музыку и всё, что вам нравится
        </p>
        <Button
          variant="primary"
          @click="createNewInterest"
        >
          Добавить интерес
        </Button>
      </div>

      <!-- Сетка интересов -->
      <div v-else class="interests-grid">
        <InterestCard
          v-for="interest in filteredInterests"
          :key="interest.id"
          :interest="interest"
          @edit="editInterest"
          @delete="deleteInterest"
          @update-like="updateLike"
        />
      </div>
    </div>

    <!-- Модальное окно для создания/редактирования -->
    <Teleport to="body">
      <div v-if="showForm" class="modal-overlay" @click.self="closeForm">
        <div class="modal-content">
          <InterestForm
            :interest="editingInterest"
            :isEditing="!!editingInterest"
            @submit="saveInterest"
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
import InterestCard from './components/InterestCard.vue'
import InterestForm from './InterestForm.vue'
import interestService from '@/services/interestService'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(true)
const interests = ref([])
const stats = ref([])
const showForm = ref(false)
const editingInterest = ref(null)
const searchQuery = ref('')
const selectedCategory = ref('all')
const likeFilter = ref('all')
const showNotification = ref(false)
const notificationMessage = ref('')
const notificationType = ref('success')

// Общее количество интересов
const totalCount = computed(() => {
  return interests.value.length
})

// Фильтрация интересов
const filteredInterests = computed(() => {
  let filtered = interests.value

  // Фильтр по категории
  if (selectedCategory.value !== 'all') {
    filtered = filtered.filter(i => i.category === selectedCategory.value)
  }

  // Фильтр по оценке
  if (likeFilter.value === 'liked') {
    filtered = filtered.filter(i => i.liked === true)
  } else if (likeFilter.value === 'disliked') {
    filtered = filtered.filter(i => i.liked === false)
  } else if (likeFilter.value === 'unrated') {
    filtered = filtered.filter(i => i.liked === null)
  }

  // Поиск по тексту
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(i =>
      i.title.toLowerCase().includes(query) ||
      (i.creator && i.creator.toLowerCase().includes(query)) ||
      (i.description && i.description.toLowerCase().includes(query))
    )
  }

  return filtered
})

// Загрузка данных
const loadData = async () => {
  loading.value = true
  try {
    const [interestsData, statsData] = await Promise.all([
      interestService.getAllInterests(),
      interestService.getCategoryStats()
    ])

    interests.value = interestsData || []
    stats.value = statsData || []

    console.log('📥 Загружено интересов:', interests.value.length)
    console.log('📊 Статистика:', stats.value)
  } catch (error) {
    console.error('❌ Ошибка загрузки:', error)
    showNotificationMessage('error', 'Ошибка загрузки данных')
    interests.value = []
    stats.value = []
  } finally {
    loading.value = false
  }
}

// Получить иконку категории
const getCategoryIcon = (category) => {
  return interestService.constructor.getCategoryIcon(category)
}

// Создание нового интереса
const createNewInterest = () => {
  editingInterest.value = null
  showForm.value = true
}

// Редактирование интереса
const editInterest = (interest) => {
  editingInterest.value = interest
  showForm.value = true
}

// Сохранение интереса (создание или обновление)
const saveInterest = async (interestData) => {
  try {
    if (editingInterest.value) {
      // Обновление
      await interestService.updateInterest(editingInterest.value.id, {
        ...interestData,
        sortOrder: editingInterest.value.sortOrder
      })
      showNotificationMessage('success', 'Интерес обновлён')
    } else {
      // Создание
      await interestService.createInterest(interestData)
      showNotificationMessage('success', 'Интерес добавлен')
    }

    closeForm()
    await loadData()
  } catch (error) {
    console.error('❌ Ошибка сохранения:', error)
    showNotificationMessage('error', 'Ошибка при сохранении')
  }
}

// Обновление оценки (лайк/дизлайк)
const updateLike = async (interestId, likeData) => {
  try {
    const updated = await interestService.updateLike(interestId, likeData)

    // Обновляем интерес в списке
    const index = interests.value.findIndex(i => i.id === interestId)
    if (index !== -1) {
      interests.value[index] = updated
    }

    // Обновляем статистику
    const newStats = await interestService.getCategoryStats()
    stats.value = newStats

  } catch (error) {
    console.error('❌ Ошибка обновления оценки:', error)
    showNotificationMessage('error', 'Ошибка при обновлении оценки')
  }
}

// Удаление интереса
const deleteInterest = async (interest) => {
  if (!confirm(`Удалить "${interest.title}" из коллекции?`)) {
    return
  }

  try {
    await interestService.deleteInterest(interest.id)
    showNotificationMessage('success', 'Интерес удалён')
    await loadData()
  } catch (error) {
    console.error('❌ Ошибка удаления:', error)
    showNotificationMessage('error', 'Ошибка при удалении')
  }
}

// Закрытие формы
const closeForm = () => {
  showForm.value = false
  editingInterest.value = null
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
  loadData()
})
</script>

<style scoped>
.interests-page {
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

/* Статистика */
.stats-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 12px;
  margin-bottom: 2rem;
}

.stat-card {
  background: linear-gradient(135deg, var(--color-white, #ffffff) 0%, var(--color-white-smoke, #f5f5f5) 100%);
  border: 1px solid var(--color-light-gray, #e0e0e0);
  border-radius: 12px;
  padding: 1rem;
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px var(--shadow-color, rgba(0,0,0,0.1));
  border-color: var(--color-primary, #88d424);
}

.stat-card.active {
  border: 2px solid var(--color-primary, #88d424);
  background: linear-gradient(135deg, var(--color-white, #ffffff) 0%, rgba(136, 212, 36, 0.1) 100%);
}

.stat-card.all-categories {
  background: linear-gradient(135deg, var(--color-white, #ffffff) 0%, var(--color-white-smoke, #f5f5f5) 100%);
}

.stat-icon {
  font-size: 32px;
}

.stat-info {
  flex: 1;
}

.stat-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-primary-dark, #6aa91c);
  margin-bottom: 4px;
}

.stat-numbers {
  display: flex;
  gap: 8px;
  font-size: 12px;
}

.stat-count {
  color: var(--color-dark-gray, #616161);
  font-weight: 600;
}

.stat-likes {
  color: #ff4444;
}

/* Фильтры */
.filters-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 2rem;
  padding: 1rem;
  background-color: var(--color-white, #ffffff);
  border-radius: 12px;
  box-shadow: 0 2px 8px var(--shadow-color, rgba(0,0,0,0.05));
}

.search-box {
  flex: 1;
  min-width: 250px;
}

.search-input {
  width: 100%;
  padding: 10px 16px;
  border: 1px solid var(--color-light-gray, #e0e0e0);
  border-radius: 20px;
  font-size: 14px;
  transition: all 0.2s ease;
}

.search-input:focus {
  outline: none;
  border-color: var(--color-primary, #88d424);
  box-shadow: 0 0 0 3px rgba(136, 212, 36, 0.1);
}

.filter-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.filter-button {
  background: none;
  border: 1px solid var(--color-light-gray, #e0e0e0);
  border-radius: 20px;
  padding: 8px 16px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s ease;
  color: var(--color-gray, #9e9e9e);
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

.filter-button.liked.active {
  background-color: #ff4444;
  border-color: #ff4444;
}

.filter-button.disliked.active {
  background-color: #666;
  border-color: #666;
}

.filter-button.unrated.active {
  background-color: var(--color-gray, #9e9e9e);
  border-color: var(--color-gray, #9e9e9e);
}

/* Сетка интересов */
.interests-grid {
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

  .page-header button {
    width: 100%;
  }

  .stats-section {
    grid-template-columns: repeat(2, 1fr);
  }

  .filters-section {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-buttons {
    justify-content: center;
  }

  .interests-grid {
    grid-template-columns: 1fr;
  }
}
</style>
