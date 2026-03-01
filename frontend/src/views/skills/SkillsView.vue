<template>
  <div class="skills-page">
    <div class="page-header">
      <div class="header-content">
        <h1>📋 Анкета навыков</h1>
        <p class="subtitle">Отслеживайте, какие навыки вы уже освоили</p>
      </div>

      <Button
        variant="primary"
        @click="createNewSkill"
      >
        + Новый навык
      </Button>
    </div>

    <!-- Общая статистика -->
    <div v-if="totalStats" class="total-stats-card">
      <div class="stats-grid">
        <div class="stat-item">
          <span class="stat-value">{{ totalStats.total }}</span>
          <span class="stat-label">Всего навыков</span>
        </div>
        <div class="stat-item">
          <span class="stat-value">{{ totalStats.mastered }}</span>
          <span class="stat-label">Освоено</span>
        </div>
        <div class="stat-item">
          <div class="progress-circle">
            <svg viewBox="0 0 36 36" class="circular-chart">
              <path class="circle-bg"
                    d="M18 2.0845
                  a 15.9155 15.9155 0 0 1 0 31.831
                  a 15.9155 15.9155 0 0 1 0 -31.831"
              />
              <path class="circle"
                    :stroke-dasharray="`${totalStats.percentage}, 100`"
                    d="M18 2.0845
                  a 15.9155 15.9155 0 0 1 0 31.831
                  a 15.9155 15.9155 0 0 1 0 -31.831"
              />
              <text x="18" y="20.35" class="percentage">{{ totalStats.percentage }}%</text>
            </svg>
          </div>
          <span class="stat-label">Общий прогресс</span>
        </div>
      </div>
    </div>

    <!-- Статистика по категориям -->
    <div v-if="categoryStats.length > 0" class="categories-section">
      <h2>Прогресс по категориям</h2>
      <div class="categories-grid">
        <div
          v-for="stat in categoryStats"
          :key="stat.category"
          class="category-stat-card"
          :class="{ active: selectedCategory === stat.category }"
          @click="selectedCategory = selectedCategory === stat.category ? 'all' : stat.category"
        >
          <div class="category-header" :style="{ backgroundColor: getCategoryColor(stat.category) + '20' }">
            <!-- Оставляем только один значок здесь -->
            <span class="category-icon">{{ getCategoryIcon(stat.category) }}</span>
            <span class="category-name">{{ getCategoryDisplayName(stat.category) }}</span>
          </div>
          <div class="category-progress">
            <div class="progress-bar">
              <div
                class="progress-fill"
                :style="{ width: stat.percentage + '%', backgroundColor: getCategoryColor(stat.category) }"
              ></div>
            </div>
            <div class="progress-stats">
              <span>{{ stat.mastered }}/{{ stat.total }}</span>
              <span>{{ stat.percentage }}%</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Фильтры и поиск -->
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
          :class="{ active: statusFilter === 'all' }"
          @click="statusFilter = 'all'"
        >
          Все
        </button>
        <button
          class="filter-button mastered"
          :class="{ active: statusFilter === 'mastered' }"
          @click="statusFilter = 'mastered'"
        >
          ✅ Освоенные
        </button>
        <button
          class="filter-button learning"
          :class="{ active: statusFilter === 'learning' }"
          @click="statusFilter = 'learning'"
        >
          📝 В процессе
        </button>
      </div>
    </div>

    <!-- Список навыков, сгруппированный по категориям -->
    <div class="skills-section">
      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <p>Загрузка навыков...</p>
      </div>

      <div v-else-if="filteredSkills.length === 0" class="empty-state">
        <div class="empty-icon">📋</div>
        <h3>Нет навыков</h3>
        <p v-if="selectedCategory !== 'all' || statusFilter !== 'all' || searchQuery">
          Попробуйте изменить параметры фильтрации
        </p>
        <p v-else>
          Добавьте навыки, которые вы умели выполнять ранее
        </p>
        <Button
          variant="primary"
          @click="createNewSkill"
        >
          Добавить навык
        </Button>
      </div>

      <!-- Группировка по категориям -->
      <div v-else class="skills-by-category">
        <div
          v-for="category in groupedSkills"
          :key="category.name"
          class="category-group"
        >
          <div class="category-group-header" :style="{ borderLeftColor: getCategoryColor(category.name) }">
            <!-- Оставляем только один значок здесь, убираем дублирование в названии -->
            <span class="category-group-icon">{{ getCategoryIcon(category.name) }}</span>
            <h3 class="category-group-title">{{ getCategoryDisplayNameWithoutIcon(category.name) }}</h3>
            <span class="category-group-count">{{ category.skills.length }}</span>
          </div>

          <div class="skills-grid">
            <SkillCard
              v-for="skill in category.skills"
              :key="skill.id"
              :skill="skill"
              @edit="editSkill"
              @delete="deleteSkill"
              @toggle="toggleSkillStatus"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- Модальное окно для создания/редактирования -->
    <Teleport to="body">
      <div v-if="showForm" class="modal-overlay" @click.self="closeForm">
        <div class="modal-content">
          <SkillForm
            :skill="editingSkill"
            :isEditing="!!editingSkill"
            @submit="saveSkill"
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
import SkillCard from './components/SkillCard.vue'
import SkillForm from './SkillForm.vue'
import skillService from '@/services/skillService'

const loading = ref(true)
const skills = ref([])
const categoryStats = ref([])
const totalStats = ref(null)
const showForm = ref(false)
const editingSkill = ref(null)
const searchQuery = ref('')
const selectedCategory = ref('all')
const statusFilter = ref('all')
const showNotification = ref(false)
const notificationMessage = ref('')
const notificationType = ref('success')

// Получить отображаемое название категории с эмодзи
const getCategoryDisplayName = (category) => {
  return skillService.constructor.getCategoryDisplayName(category)
}

// Получить отображаемое название категории без эмодзи (для заголовков групп)
const getCategoryDisplayNameWithoutIcon = (category) => {
  const fullName = skillService.constructor.getCategoryDisplayName(category)
  // Убираем эмодзи и пробел в начале
  return fullName.replace(/^[^\s]+\s/, '')
}

// Получить иконку категории
const getCategoryIcon = (category) => {
  return skillService.constructor.getCategoryIcon(category)
}

// Получить цвет категории
const getCategoryColor = (category) => {
  return skillService.constructor.getCategoryColor(category)
}

// Фильтрация навыков
const filteredSkills = computed(() => {
  let filtered = skills.value

  // Фильтр по категории
  if (selectedCategory.value !== 'all') {
    filtered = filtered.filter(s => s.category === selectedCategory.value)
  }

  // Фильтр по статусу
  if (statusFilter.value === 'mastered') {
    filtered = filtered.filter(s => s.canDoNow)
  } else if (statusFilter.value === 'learning') {
    filtered = filtered.filter(s => !s.canDoNow)
  }

  // Поиск по тексту
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(s =>
      s.title.toLowerCase().includes(query) ||
      (s.description && s.description.toLowerCase().includes(query)) ||
      (s.notes && s.notes.toLowerCase().includes(query))
    )
  }

  return filtered
})

// Группировка навыков по категориям
const groupedSkills = computed(() => {
  const groups = {}

  filteredSkills.value.forEach(skill => {
    if (!groups[skill.category]) {
      groups[skill.category] = []
    }
    groups[skill.category].push(skill)
  })

  // Сортируем категории
  const categoryOrder = ['SELF_CARE', 'HOUSEHOLD', 'COMMUNICATION', 'MOBILITY', 'HEALTH', 'COGNITIVE', 'OTHER']

  return categoryOrder
    .filter(cat => groups[cat])
    .map(cat => ({
      name: cat,
      skills: groups[cat].sort((a, b) => (a.sortOrder || 0) - (b.sortOrder || 0))
    }))
})

// Загрузка данных
const loadData = async () => {
  loading.value = true
  try {
    const [skillsData, categoryStatsData, totalStatsData] = await Promise.all([
      skillService.getAllSkills(),
      skillService.getCategoryStats(),
      skillService.getTotalStats()
    ])

    skills.value = skillsData || []
    categoryStats.value = categoryStatsData || []
    totalStats.value = totalStatsData || { total: 0, mastered: 0, percentage: 0 }

    console.log('📥 Загружено навыков:', skills.value.length)
    console.log('📊 Статистика по категориям:', categoryStats.value)
    console.log('📈 Общая статистика:', totalStats.value)
  } catch (error) {
    console.error('❌ Ошибка загрузки:', error)
    showNotificationMessage('error', 'Ошибка загрузки данных')
    skills.value = []
    categoryStats.value = []
    totalStats.value = { total: 0, mastered: 0, percentage: 0 }
  } finally {
    loading.value = false
  }
}

// Создание нового навыка
const createNewSkill = () => {
  editingSkill.value = null
  showForm.value = true
}

// Редактирование навыка
const editSkill = (skill) => {
  editingSkill.value = skill
  showForm.value = true
}

// Сохранение навыка (создание или обновление)
const saveSkill = async (skillData) => {
  try {
    if (editingSkill.value) {
      // Обновление
      await skillService.updateSkill(editingSkill.value.id, {
        ...skillData,
        sortOrder: editingSkill.value.sortOrder
      })
      showNotificationMessage('success', 'Навык обновлён')
    } else {
      // Создание
      await skillService.createSkill(skillData)
      showNotificationMessage('success', 'Навык добавлен')
    }

    closeForm()
    await loadData()
  } catch (error) {
    console.error('❌ Ошибка сохранения:', error)
    showNotificationMessage('error', 'Ошибка при сохранении')
  }
}

// Переключение статуса навыка
const toggleSkillStatus = async (skillId, canDoNow) => {
  try {
    await skillService.toggleSkillStatus(skillId, canDoNow)
    await loadData() // Перезагружаем для обновления статистики
  } catch (error) {
    console.error('❌ Ошибка переключения статуса:', error)
    showNotificationMessage('error', 'Ошибка при обновлении статуса')
  }
}

// Удаление навыка
const deleteSkill = async (skill) => {
  if (!confirm(`Удалить навык "${skill.title}" из анкеты?`)) {
    return
  }

  try {
    await skillService.deleteSkill(skill.id)
    showNotificationMessage('success', 'Навык удалён')
    await loadData()
  } catch (error) {
    console.error('❌ Ошибка удаления:', error)
    showNotificationMessage('error', 'Ошибка при удалении')
  }
}

// Закрытие формы
const closeForm = () => {
  showForm.value = false
  editingSkill.value = null
}

// Показать уведомление
const showNotificationMessage = (type, message) => {
  notificationType.value = type
  notificationMessage.value = message
  showNotification.value = true
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.skills-page {
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

/* Общая статистика */
.total-stats-card {
  background: linear-gradient(135deg, var(--color-white, #ffffff) 0%, var(--color-white-smoke, #f5f5f5) 100%);
  border-radius: 16px;
  padding: 1.5rem;
  margin-bottom: 2rem;
  box-shadow: 0 4px 12px var(--shadow-color, rgba(0,0,0,0.05));
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.stat-item {
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.stat-value {
  font-size: 36px;
  font-weight: 600;
  color: var(--color-primary, #88d424);
  line-height: 1.2;
}

.stat-label {
  color: var(--color-gray, #9e9e9e);
  font-size: 14px;
}

/* Прогресс круг */
.progress-circle {
  width: 80px;
  height: 80px;
  margin-bottom: 8px;
}

.circular-chart {
  width: 100%;
  height: 100%;
}

.circle-bg {
  stroke: var(--color-light-gray, #e0e0e0);
  stroke-width: 2;
  fill: none;
}

.circle {
  stroke: var(--color-primary, #88d424);
  stroke-width: 2;
  fill: none;
  stroke-linecap: round;
  transition: stroke-dasharray 0.3s ease;
}

.percentage {
  fill: var(--color-dark-gray, #616161);
  font-size: 0.5rem;
  text-anchor: middle;
  font-weight: 500;
}

/* Статистика по категориям */
.categories-section {
  margin-bottom: 2rem;
}

.categories-section h2 {
  font-size: 20px;
  color: var(--color-primary-dark, #6aa91c);
  margin-bottom: 1rem;
}

.categories-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 15px;
}

.category-stat-card {
  background: var(--color-white, #ffffff);
  border: 1px solid var(--color-light-gray, #e0e0e0);
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
}

.category-stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px var(--shadow-color, rgba(0,0,0,0.1));
}

.category-stat-card.active {
  border: 2px solid var(--color-primary, #88d424);
}

.category-header {
  padding: 12px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.category-icon {
  font-size: 20px;
  flex-shrink: 0;
}

.category-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-dark-gray, #616161);
}

.category-progress {
  padding: 12px;
}

.progress-bar {
  height: 6px;
  background-color: var(--color-light-gray, #e0e0e0);
  border-radius: 3px;
  overflow: hidden;
  margin-bottom: 6px;
}

.progress-fill {
  height: 100%;
  transition: width 0.3s ease;
}

.progress-stats {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: var(--color-gray, #9e9e9e);
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

.filter-button.mastered.active {
  background-color: var(--color-success, #4caf50);
  border-color: var(--color-success, #4caf50);
}

.filter-button.learning.active {
  background-color: var(--color-warning, #ff9800);
  border-color: var(--color-warning, #ff9800);
}

/* Группировка по категориям */
.skills-by-category {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.category-group {
  background: var(--color-white-smoke, #f5f5f5);
  border-radius: 16px;
  padding: 1.5rem;
}

.category-group-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 1.5rem;
  padding-bottom: 0.75rem;
  border-left: 4px solid;
  padding-left: 1rem;
}

.category-group-icon {
  font-size: 24px;
  flex-shrink: 0;
}

.category-group-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--color-primary-dark, #6aa91c);
  margin: 0;
  flex: 1;
}

.category-group-count {
  background-color: var(--color-white, #ffffff);
  color: var(--color-gray, #9e9e9e);
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
}

.skills-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
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

  .stats-grid {
    grid-template-columns: 1fr;
    gap: 15px;
  }

  .filters-section {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-buttons {
    justify-content: center;
  }

  .skills-grid {
    grid-template-columns: 1fr;
  }

  .category-group-header {
    flex-wrap: wrap;
  }
}
</style>
