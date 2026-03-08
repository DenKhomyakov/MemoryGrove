<template>
  <div class="brain-trainer-page">
    <div class="page-header">
      <div class="header-content">
        <h1>🧠 Тренажёр памяти</h1>
        <p class="subtitle">Упражнения для тренировки внимания и памяти</p>
      </div>

      <Button
        variant="outline"
        @click="goBack"
      >
        ← Назад к модулям
      </Button>
    </div>

    <!-- Общая статистика -->
    <div v-if="allStats.length > 0" class="total-stats">
      <div class="stat-card">
        <span class="stat-value">{{ totalAttempts }}</span>
        <span class="stat-label">Всего тренировок</span>
      </div>
      <div class="stat-card">
        <span class="stat-value">{{ totalSuccesses }}</span>
        <span class="stat-label">Успешных</span>
      </div>
      <div class="stat-card">
        <span class="stat-value">{{ Math.round((totalSuccesses / totalAttempts) * 100) || 0 }}%</span>
        <span class="stat-label">Общий прогресс</span>
      </div>
    </div>

    <!-- Список упражнений -->
    <div class="exercises-section">
      <h2>Доступные упражнения</h2>

      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <p>Загрузка упражнений...</p>
      </div>

      <div v-else class="exercises-grid">
        <!-- Арифметическая цепочка -->
        <ExerciseCard
          type="ARITHMETIC"
          title="🧮 Арифметическая цепочка"
          description="Решайте последовательность примеров и тренируйте устный счёт"
          :stats="getStatsForType('ARITHMETIC')"
          @click="openGame('arithmetic')"
        />

        <!-- Понимание текста -->
        <ExerciseCard
          type="TEXT_COMPREHENSION"
          title="📖 Понимание текста"
          description="Читайте рассказы и отвечайте на вопросы по содержанию"
          :stats="getStatsForType('TEXT_COMPREHENSION')"
          @click="openGame('text')"
        />

        <!-- Заучивание стихов -->
        <ExerciseCard
          type="POEM"
          title="📝 Заучивание стихов"
          description="Запоминайте четверостишия и воспроизводите их по памяти"
          :stats="getStatsForType('POEM')"
          @click="openGame('poem')"
        />
      </div>

      <!-- Ассоциации -->
      <ExerciseCard
        type="ASSOCIATION"
        title="🔗 Ассоциации"
        description="Подбирайте ассоциации к словам из предложенных вариантов"
        :stats="getStatsForType('ASSOCIATION')"
        @click="openGame('association')"
      />
    </div>

    <!-- Компоненты игр -->
    <div v-if="activeGame === 'arithmetic'" class="game-container">
      <ArithmeticChain @back="closeGame" />
    </div>

    <div v-if="activeGame === 'text'" class="game-container">
      <TextComprehension @back="closeGame" />
    </div>

    <div v-if="activeGame === 'poem'" class="game-container">
      <PoemMemorization @back="closeGame" />
    </div>

    <div v-if="activeGame === 'association'" class="game-container">
      <Associations @back="closeGame" />
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
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import Button from '@/components/UI/Button.vue'
import Notification from '@/components/UI/Notification.vue'
import ExerciseCard from './components/ExerciseCard.vue'
import ArithmeticChain from './games/ArithmeticChain.vue'
import TextComprehension from './games/TextComprehension.vue'
import PoemMemorization from './games/PoemMemorization.vue'
import Associations from './games/Associations.vue'
import brainTrainerService from '@/services/brainTrainerService'

const router = useRouter()
const loading = ref(true)
const allStats = ref([])
const activeGame = ref(null)
const showNotification = ref(false)
const notificationMessage = ref('')
const notificationType = ref('info')

// Загрузка статистики
const loadStats = async () => {
  loading.value = true
  try {
    allStats.value = await brainTrainerService.getAllStats()
    console.log('📊 Загружена статистика:', allStats.value)
  } catch (error) {
    console.error('❌ Ошибка загрузки статистики:', error)
    showNotificationMessage('error', 'Ошибка загрузки статистики')
    allStats.value = []
  } finally {
    loading.value = false
  }
}

// Получить статистику для конкретного типа
const getStatsForType = (type) => {
  return allStats.value.find(s => s.exerciseType === type) || null
}

// Общая статистика
const totalAttempts = computed(() => {
  return allStats.value.reduce((sum, stat) => sum + stat.attempts, 0)
})

const totalSuccesses = computed(() => {
  return allStats.value.reduce((sum, stat) => sum + stat.successes, 0)
})

// Открыть игру
const openGame = (game) => {
  activeGame.value = game
}

// Закрыть игру
const closeGame = () => {
  activeGame.value = null
  // Обновляем статистику после игры
  loadStats()
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
  loadStats()
})
</script>

<style scoped>
.brain-trainer-page {
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
.total-stats {
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

/* Список упражнений */
.exercises-section h2 {
  font-size: 24px;
  color: var(--color-primary-dark, #6aa91c);
  margin-bottom: 1.5rem;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid var(--color-light-gray, #e0e0e0);
}

.exercises-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 2rem;
}

/* Контейнер игры */
.game-container {
  margin-top: 2rem;
  padding-top: 2rem;
  border-top: 1px solid var(--color-light-gray, #e0e0e0);
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

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 1rem;
    text-align: center;
  }

  .page-header button {
    width: 100%;
  }

  .exercises-grid {
    grid-template-columns: 1fr;
  }
}
</style>
