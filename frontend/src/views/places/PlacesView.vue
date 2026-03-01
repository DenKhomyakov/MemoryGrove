<template>
  <div class="places-page">
    <div class="page-header">
      <div class="header-content">
        <h1>📍 Значимые места</h1>
        <p class="subtitle">Важные места, которые помогают ориентироваться в пространстве</p>
      </div>

      <Button
        variant="primary"
        @click="createNewPlace"
      >
        + Новое место
      </Button>
    </div>

    <!-- Сортировка и фильтры -->
    <div class="filters-section">
      <div class="sort-controls" v-if="places.length > 1">
        <button
          class="sort-button"
          :class="{ active: sortMode }"
          @click="toggleSortMode"
        >
          🔄 Режим сортировки
        </button>
        <span v-if="sortMode" class="sort-hint">
          Перетаскивайте карточки для изменения порядка
        </span>
      </div>

      <div class="search-box">
        <input
          type="text"
          v-model="searchQuery"
          placeholder="🔍 Поиск по названию или адресу..."
          class="search-input"
        />
      </div>
    </div>

    <!-- Список мест -->
    <div class="places-section">
      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <p>Загрузка мест...</p>
      </div>

      <div v-else-if="filteredPlaces.length === 0" class="empty-state">
        <div class="empty-icon">📍</div>
        <h3>Нет добавленных мест</h3>
        <p>Добавьте важные для вас места: дом, поликлинику, любимый парк</p>
        <Button
          variant="primary"
          @click="createNewPlace"
        >
          Добавить место
        </Button>
      </div>

      <!-- Сетка мест с поддержкой drag & drop в режиме сортировки -->
      <div v-else class="places-grid" :class="{ 'sort-mode': sortMode }">
        <draggable
          v-if="sortMode"
          v-model="filteredPlaces"
          item-key="id"
          handle=".drag-handle"
          @end="handleSortEnd"
        >
          <template #item="{ element: place }">
            <div class="place-wrapper">
              <div class="drag-handle">⋮⋮</div>
              <PlaceCard
                :place="place"
                @edit="editPlace"
                @delete="deletePlace"
              />
            </div>
          </template>
        </draggable>

        <template v-else>
          <PlaceCard
            v-for="place in filteredPlaces"
            :key="place.id"
            :place="place"
            @edit="editPlace"
            @delete="deletePlace"
          />
        </template>
      </div>
    </div>

    <!-- Модальное окно для создания/редактирования -->
    <Teleport to="body">
      <div v-if="showForm" class="modal-overlay" @click.self="closeForm">
        <div class="modal-content">
          <PlaceForm
            :place="editingPlace"
            :isEditing="!!editingPlace"
            @submit="savePlace"
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
import draggable from 'vuedraggable'
import Button from '@/components/UI/Button.vue'
import Notification from '@/components/UI/Notification.vue'
import PlaceCard from './components/PlaceCard.vue'
import PlaceForm from './PlaceForm.vue'
import placeService from '@/services/placeService'

const loading = ref(true)
const places = ref([])
const showForm = ref(false)
const editingPlace = ref(null)
const searchQuery = ref('')
const sortMode = ref(false)
const showNotification = ref(false)
const notificationMessage = ref('')
const notificationType = ref('success')

// Фильтрация мест по поиску
const filteredPlaces = computed(() => {
  if (!searchQuery.value) return places.value
  const query = searchQuery.value.toLowerCase()
  return places.value.filter(place =>
    place.title.toLowerCase().includes(query) ||
    place.address.toLowerCase().includes(query) ||
    (place.description && place.description.toLowerCase().includes(query))
  )
})

// Загрузка мест
const loadPlaces = async () => {
  loading.value = true
  try {
    const data = await placeService.getAllPlaces()
    console.log('📥 Получены места:', data)
    places.value = data || []
  } catch (error) {
    console.error('❌ Ошибка загрузки мест:', error)
    showNotificationMessage('error', 'Ошибка загрузки мест')
    places.value = []
  } finally {
    loading.value = false
  }
}

// Переключение режима сортировки
const toggleSortMode = () => {
  sortMode.value = !sortMode.value
}

// Обработка окончания сортировки
const handleSortEnd = async () => {
  const placeIds = filteredPlaces.value.map(p => p.id)

  try {
    await placeService.reorderPlaces(placeIds)
    showNotificationMessage('success', 'Порядок мест сохранён')
    // Обновляем порядок в основном массиве
    places.value = [...filteredPlaces.value]
  } catch (error) {
    console.error('❌ Ошибка сохранения порядка:', error)
    showNotificationMessage('error', 'Ошибка при сохранении порядка')
    // Перезагружаем исходный порядок
    await loadPlaces()
  } finally {
    sortMode.value = false
  }
}

// Создание нового места
const createNewPlace = () => {
  editingPlace.value = null
  showForm.value = true
}

// Редактирование места
const editPlace = (place) => {
  editingPlace.value = place
  showForm.value = true
}

// Сохранение места (создание или обновление)
const savePlace = async (placeData) => {
  try {
    if (editingPlace.value) {
      // Обновление
      await placeService.updatePlace(editingPlace.value.id, {
        ...placeData,
        sortOrder: editingPlace.value.sortOrder
      })
      showNotificationMessage('success', 'Место обновлено')
    } else {
      // Создание
      await placeService.createPlace(placeData)
      showNotificationMessage('success', 'Место добавлено')
    }

    closeForm()
    await loadPlaces()
  } catch (error) {
    console.error('❌ Ошибка сохранения:', error)
    showNotificationMessage('error', 'Ошибка при сохранении')
  }
}

// Удаление места
const deletePlace = async (place) => {
  if (!confirm(`Удалить место "${place.title}"?`)) {
    return
  }

  try {
    await placeService.deletePlace(place.id)
    showNotificationMessage('success', 'Место удалено')
    await loadPlaces()
  } catch (error) {
    console.error('❌ Ошибка удаления:', error)
    showNotificationMessage('error', 'Ошибка при удалении')
  }
}

// Закрытие формы
const closeForm = () => {
  showForm.value = false
  editingPlace.value = null
}

// Показать уведомление
const showNotificationMessage = (type, message) => {
  notificationType.value = type
  notificationMessage.value = message
  showNotification.value = true
}

onMounted(() => {
  loadPlaces()
})
</script>

<style scoped>
.places-page {
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

/* Фильтры и сортировка */
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

.sort-controls {
  display: flex;
  align-items: center;
  gap: 15px;
  flex-wrap: wrap;
}

.sort-button {
  background: none;
  border: 1px solid var(--color-light-gray, #e0e0e0);
  border-radius: 20px;
  padding: 8px 16px;
  color: var(--color-gray, #9e9e9e);
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 14px;
}

.sort-button:hover {
  border-color: var(--color-primary, #88d424);
  color: var(--color-primary-dark, #6aa91c);
}

.sort-button.active {
  background-color: var(--color-primary, #88d424);
  border-color: var(--color-primary, #88d424);
  color: white;
}

.sort-hint {
  color: var(--color-primary, #88d424);
  font-size: 13px;
  font-style: italic;
}

.search-box {
  flex: 1;
  max-width: 300px;
}

.search-input {
  width: 100%;
  padding: 8px 16px;
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

/* Сетка мест */
.places-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
  margin-bottom: 2rem;
}

.places-grid.sort-mode {
  gap: 15px;
}

.place-wrapper {
  position: relative;
  cursor: move;
}

.drag-handle {
  position: absolute;
  top: 10px;
  left: 10px;
  font-size: 20px;
  color: var(--color-gray, #9e9e9e);
  cursor: grab;
  z-index: 10;
  opacity: 0;
  transition: opacity 0.2s ease;
}

.place-wrapper:hover .drag-handle {
  opacity: 1;
}

.drag-handle:active {
  cursor: grabbing;
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

  .page-header button {
    width: 100%;
  }

  .filters-section {
    flex-direction: column;
    align-items: stretch;
  }

  .search-box {
    max-width: none;
  }

  .places-grid {
    grid-template-columns: 1fr;
  }
}
</style>
