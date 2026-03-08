<template>
  <div class="photo-gallery">
    <!-- Шапка галереи -->
    <div class="gallery-header">
      <div class="header-content">
        <h2>📸 Цифровой альбом</h2>
        <p class="subtitle">Ваши фотографии и воспоминания</p>
      </div>

      <div class="header-actions">
        <div class="view-toggle">
          <button
            class="toggle-button"
            :class="{ active: viewMode === 'grid' }"
            @click="viewMode = 'grid'"
            title="Сетка"
          >
            ⊞
          </button>
          <button
            class="toggle-button"
            :class="{ active: viewMode === 'list' }"
            @click="viewMode = 'list'"
            title="Список"
          >
            ☰
          </button>
        </div>

        <Button
          variant="primary"
          size="small"
          @click="$emit('upload')"
        >
          + Загрузить фото
        </Button>
      </div>
    </div>

    <!-- Статистика -->
    <div v-if="stats" class="gallery-stats">
      <div class="stat-item">
        <span class="stat-value">{{ stats.totalCount }}</span>
        <span class="stat-label">фотографий</span>
      </div>
      <div class="stat-item">
        <span class="stat-value">{{ stats.formattedTotalSize }}</span>
        <span class="stat-label">всего</span>
      </div>
      <div class="stat-item" v-if="stats.years && stats.years.length">
        <span class="stat-value">{{ stats.years.length }}</span>
        <span class="stat-label">лет в архиве</span>
      </div>
    </div>

    <!-- Фильтры и поиск -->
    <div class="filters-section">
      <div class="search-box">
        <input
          v-model="searchQuery"
          type="text"
          placeholder="🔍 Поиск по описанию, месту или людям..."
          class="search-input"
        />
      </div>

      <div class="filter-buttons">
        <select v-model="yearFilter" class="filter-select">
          <option value="all">Все годы</option>
          <option v-for="year in availableYears" :key="year" :value="year">
            {{ year }} год
          </option>
        </select>

        <select v-model="sortBy" class="filter-select">
          <option value="date-desc">Сначала новые</option>
          <option value="date-asc">Сначала старые</option>
          <option value="name-asc">По названию (А-Я)</option>
          <option value="name-desc">По названию (Я-А)</option>
          <option value="size-desc">По размеру (сначала большие)</option>
          <option value="size-asc">По размеру (сначала маленькие)</option>
        </select>
      </div>
    </div>

    <!-- Галерея -->
    <div class="gallery-content">
      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <p>Загрузка фотографий...</p>
      </div>

      <div v-else-if="filteredPhotos.length === 0" class="empty-state">
        <div class="empty-icon">📸</div>
        <h3>Нет фотографий</h3>
        <p v-if="searchQuery || yearFilter !== 'all'">
          Попробуйте изменить параметры поиска
        </p>
        <p v-else>
          Загрузите свои первые фотографии в цифровой альбом
        </p>
        <Button
          variant="primary"
          @click="$emit('upload')"
        >
          Загрузить фото
        </Button>
      </div>

      <!-- Сетка -->
      <div v-else-if="viewMode === 'grid'" class="photos-grid">
        <PhotoCard
          v-for="photo in filteredPhotos"
          :key="photo.id"
          :photo="photo"
          @view="$emit('view', photo)"
          @edit="$emit('edit', photo)"
          @delete="$emit('delete', photo)"
          @download="$emit('download', photo)"
        />
      </div>

      <!-- Список -->
      <div v-else class="photos-list">
        <div
          v-for="photo in filteredPhotos"
          :key="photo.id"
          class="list-item"
        >
          <div class="list-preview" @click="$emit('view', photo)">
            <img :src="photo.downloadUrl" :alt="photo.description">
          </div>

          <div class="list-info">
            <div class="list-header">
              <h4>{{ photo.title || 'Без названия' }}</h4>
              <span class="list-date">{{ photo.formattedUploadDate }}</span>
            </div>

            <p v-if="photo.description" class="list-description">
              {{ photo.description }}
            </p>

            <div class="list-metadata">
              <span v-if="photo.location" class="list-tag">
                📍 {{ photo.location }}
              </span>
              <span v-if="photo.year" class="list-tag">
                📅 {{ photo.year }}
              </span>
              <span v-if="photo.people" class="list-tag">
                👥 {{ photo.people }}
              </span>
              <span class="list-tag">
                📦 {{ formatFileSize(photo.fileSize) }}
              </span>
            </div>
          </div>

          <div class="list-actions">
            <button
              class="icon-button"
              @click="$emit('download', photo)"
              title="Скачать"
            >
              ⬇️
            </button>
            <button
              class="icon-button edit-btn"
              @click="$emit('edit', photo)"
              title="Редактировать"
            >
              ✏️
            </button>
            <button
              class="icon-button delete-btn"
              @click="$emit('delete', photo)"
              title="Удалить"
            >
              🗑️
            </button>
          </div>
        </div>
      </div>
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
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import Button from '@/components/UI/Button.vue'
import PhotoCard from './components/PhotoCard.vue'
import photoService from '@/services/photoService'

const props = defineProps({
  photos: {
    type: Array,
    default: () => []
  },
  stats: {
    type: Object,
    default: null
  },
  loading: {
    type: Boolean,
    default: false
  }
})

defineEmits(['view', 'edit', 'delete', 'download', 'upload'])

const viewMode = ref('grid')
const searchQuery = ref('')
const yearFilter = ref('all')
const sortBy = ref('date-desc')
const currentPage = ref(0)
const itemsPerPage = 12

// Доступные годы из статистики
const availableYears = computed(() => {
  return props.stats?.years || []
})

// Фильтрация и сортировка фотографий
const filteredPhotos = computed(() => {
  let filtered = [...props.photos]

  // Поиск по тексту
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(photo =>
      (photo.description && photo.description.toLowerCase().includes(query)) ||
      (photo.location && photo.location.toLowerCase().includes(query)) ||
      (photo.people && photo.people.toLowerCase().includes(query)) ||
      (photo.fileName && photo.fileName.toLowerCase().includes(query))
    )
  }

  // Фильтр по году
  if (yearFilter.value !== 'all') {
    filtered = filtered.filter(photo => photo.year === parseInt(yearFilter.value))
  }

  // Сортировка
  filtered.sort((a, b) => {
    switch (sortBy.value) {
      case 'date-desc':
        return new Date(b.uploadDate) - new Date(a.uploadDate)
      case 'date-asc':
        return new Date(a.uploadDate) - new Date(b.uploadDate)
      case 'name-asc':
        return (a.title || '').localeCompare(b.title || '')
      case 'name-desc':
        return (b.title || '').localeCompare(a.title || '')
      case 'size-desc':
        return (b.fileSize || 0) - (a.fileSize || 0)
      case 'size-asc':
        return (a.fileSize || 0) - (b.fileSize || 0)
      default:
        return 0
    }
  })

  return filtered
})

// Пагинация
const paginatedPhotos = computed(() => {
  const start = currentPage.value * itemsPerPage
  const end = start + itemsPerPage
  return filteredPhotos.value.slice(start, end)
})

const totalPages = computed(() => {
  return Math.ceil(filteredPhotos.value.length / itemsPerPage)
})

// Сброс страницы при изменении фильтров
watch([searchQuery, yearFilter, sortBy], () => {
  currentPage.value = 0
})

// Смена страницы
const changePage = (page) => {
  currentPage.value = page
}

// Форматирование размера файла
const formatFileSize = (bytes) => {
  return photoService.constructor.formatFileSize(bytes)
}
</script>

<style scoped>
.photo-gallery {
  width: 100%;
}

/* Шапка */
.gallery-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  flex-wrap: wrap;
  gap: 1rem;
}

.header-content h2 {
  font-size: 24px;
  color: var(--color-primary-dark, #6aa91c);
  margin: 0 0 4px 0;
}

.subtitle {
  color: var(--color-gray, #9e9e9e);
  margin: 0;
  font-size: 14px;
}

.header-actions {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.view-toggle {
  display: flex;
  gap: 4px;
  background: var(--color-white-smoke, #f5f5f5);
  padding: 4px;
  border-radius: 8px;
}

.toggle-button {
  background: none;
  border: none;
  padding: 6px 10px;
  font-size: 18px;
  cursor: pointer;
  border-radius: 6px;
  color: var(--color-gray, #9e9e9e);
  transition: all 0.2s ease;
}

.toggle-button:hover {
  background-color: var(--color-white, #ffffff);
  color: var(--color-primary, #88d424);
}

.toggle-button.active {
  background-color: var(--color-white, #ffffff);
  color: var(--color-primary, #88d424);
  box-shadow: 0 2px 4px var(--shadow-color, rgba(0,0,0,0.1));
}

/* Статистика */
.gallery-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 15px;
  margin-bottom: 2rem;
  padding: 1.5rem;
  background: linear-gradient(135deg, var(--color-white, #ffffff) 0%, var(--color-white-smoke, #f5f5f5) 100%);
  border-radius: 12px;
  box-shadow: 0 2px 8px var(--shadow-color, rgba(0,0,0,0.05));
}

.stat-item {
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: var(--color-primary, #88d424);
  line-height: 1.2;
}

.stat-label {
  color: var(--color-gray, #9e9e9e);
  font-size: 13px;
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
  gap: 10px;
}

.filter-select {
  padding: 8px 16px;
  border: 1px solid var(--color-light-gray, #e0e0e0);
  border-radius: 20px;
  font-size: 14px;
  color: var(--color-dark-gray, #616161);
  background-color: var(--color-white, #ffffff);
  cursor: pointer;
  transition: all 0.2s ease;
}

.filter-select:hover {
  border-color: var(--color-primary, #88d424);
}

.filter-select:focus {
  outline: none;
  border-color: var(--color-primary, #88d424);
  box-shadow: 0 0 0 3px rgba(136, 212, 36, 0.1);
}

/* Галерея */
.gallery-content {
  min-height: 400px;
}

.photos-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 2rem;
}

/* Список */
.photos-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin-bottom: 2rem;
}

.list-item {
  display: flex;
  gap: 20px;
  padding: 1rem;
  background: var(--color-white, #ffffff);
  border: 1px solid var(--color-light-gray, #e0e0e0);
  border-radius: 12px;
  transition: all 0.2s ease;
}

.list-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px var(--shadow-color, rgba(0,0,0,0.1));
}

.list-preview {
  width: 100px;
  height: 100px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  flex-shrink: 0;
}

.list-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.2s ease;
}

.list-preview:hover img {
  transform: scale(1.1);
}

.list-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.list-header h4 {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-primary-dark, #6aa91c);
  margin: 0;
}

.list-date {
  color: var(--color-gray, #9e9e9e);
  font-size: 12px;
}

.list-description {
  color: var(--color-gray, #9e9e9e);
  font-size: 13px;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.list-metadata {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.list-tag {
  background-color: var(--color-white-smoke, #f5f5f5);
  color: var(--color-dark-gray, #616161);
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 11px;
}

.list-actions {
  display: flex;
  gap: 4px;
  align-items: center;
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

@media (max-width: 768px) {
  .gallery-header {
    flex-direction: column;
    align-items: stretch;
    text-align: center;
  }

  .header-actions {
    justify-content: space-between;
  }

  .filters-section {
    flex-direction: column;
  }

  .filter-buttons {
    width: 100%;
  }

  .filter-select {
    flex: 1;
  }

  .list-item {
    flex-direction: column;
  }

  .list-preview {
    width: 100%;
    height: 150px;
  }

  .list-actions {
    justify-content: flex-end;
  }
}
</style>
