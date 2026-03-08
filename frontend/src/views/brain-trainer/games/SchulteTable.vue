<template>
  <div class="schulte-table">
    <div class="game-header">
      <button class="back-button" @click="$emit('back')">
        ← Назад к списку
      </button>
      <h2>🔢 Таблица Шульте</h2>
    </div>

    <!-- Статистика -->
    <div v-if="stats" class="stats-card">
      <div class="stats-grid">
        <div class="stat-item">
          <span class="stat-value">{{ stats.attempts }}</span>
          <span class="stat-label">Всего попыток</span>
        </div>
        <div class="stat-item">
          <span class="stat-value">{{ stats.successes }}</span>
          <span class="stat-label">Успешно</span>
        </div>
        <div class="stat-item">
          <span class="stat-value">{{ stats.successRate }}%</span>
          <span class="stat-label">Успешность</span>
        </div>
        <div class="stat-item" v-if="stats.bestTimeSeconds">
          <span class="stat-value">{{ formatTime(stats.bestTimeSeconds) }}</span>
          <span class="stat-label">Лучшее время</span>
        </div>
      </div>
    </div>

    <!-- Основная игра -->
    <div class="game-area">
      <!-- Начало игры -->
      <div v-if="currentPhase === 'start'" class="start-screen">
        <div class="start-icon">🔢</div>
        <p class="start-description">
          Нажимайте на цифры по порядку от 1 до максимальной.
          Тренируйте внимание и скорость реакции!
        </p>

        <div class="settings">
          <div class="setting-group">
            <label class="setting-label">Размер таблицы:</label>
            <div class="size-buttons">
              <button
                v-for="size in tableSizes"
                :key="size.value"
                class="size-button"
                :class="{ active: tableSize === size.value }"
                @click="tableSize = size.value"
              >
                {{ size.label }}
              </button>
            </div>
          </div>
        </div>

        <Button
          variant="primary"
          @click="startGame"
          :loading="loading"
        >
          Начать упражнение
        </Button>
      </div>

      <!-- Игровой процесс -->
      <div v-else-if="currentPhase === 'playing'" class="playing-phase">
        <div class="game-header-info">
          <div class="progress">
            Найдено: {{ currentNumber - 1 }} из {{ totalNumbers }}
          </div>
          <div class="timer">⏱️ {{ formatTime(timeSeconds) }}</div>
          <div class="next-number">
            Следующее: <span class="next-value">{{ currentNumber }}</span>
          </div>
        </div>

        <div class="table-container">
          <div
            class="table-grid"
            :style="{
              gridTemplateColumns: `repeat(${tableSize}, 1fr)`
            }"
          >
            <button
              v-for="(cell, index) in table"
              :key="index"
              class="table-cell"
              :class="{
                'found': cell.found,
                'current': !cell.found && cell.value === currentNumber
              }"
              @click="onCellClick(cell)"
              :disabled="cell.found"
            >
              {{ cell.value }}
            </button>
          </div>
        </div>

        <div class="progress-bar">
          <div
            class="progress-fill"
            :style="{ width: `${(currentNumber - 1) / totalNumbers * 100}%` }"
          ></div>
        </div>

        <!-- Кнопка сброса (если запутался) -->
        <Button
          variant="outline"
          size="small"
          @click="resetGame"
          class="reset-button"
        >
          🔄 Начать заново
        </Button>
      </div>

      <!-- Результаты -->
      <div v-else-if="currentPhase === 'results'" class="results-phase">
        <div class="results-icon" :class="{
          'success': timeSeconds < getTimeThreshold(tableSize).good,
          'warning': timeSeconds >= getTimeThreshold(tableSize).good && timeSeconds < getTimeThreshold(tableSize).average,
          'error': timeSeconds >= getTimeThreshold(tableSize).average
        }">
          {{ timeSeconds < getTimeThreshold(tableSize).good ? '🏆' : (timeSeconds < getTimeThreshold(tableSize).average ? '👍' : '💪') }}
        </div>

        <h3 class="results-title">
          {{ timeSeconds < getTimeThreshold(tableSize).good ? 'Отличная скорость!' : (timeSeconds < getTimeThreshold(tableSize).average ? 'Хороший результат' : 'Попробуйте ещё раз') }}
        </h3>

        <div class="results-stats">
          <div class="result-item">
            <span class="result-label">Размер таблицы:</span>
            <span class="result-value">{{ tableSize }}×{{ tableSize }}</span>
          </div>
          <div class="result-item">
            <span class="result-label">Время:</span>
            <span class="result-value">{{ formatTime(timeSeconds) }}</span>
          </div>
          <div class="result-item">
            <span class="result-label">Оценка:</span>
            <span class="result-value" :style="{ color: getTimeColor(timeSeconds) }">
              {{ getTimeRating(timeSeconds) }}
            </span>
          </div>
        </div>

        <!-- Нормативы -->
        <div class="thresholds">
          <h4>Нормативы для {{ tableSize }}×{{ tableSize }}</h4>
          <div class="threshold-items">
            <div class="threshold-item good">
              <span class="threshold-label">Отлично:</span>
              <span class="threshold-value">< {{ formatTime(getTimeThreshold(tableSize).good) }}</span>
            </div>
            <div class="threshold-item average">
              <span class="threshold-label">Хорошо:</span>
              <span class="threshold-value">{{ formatTime(getTimeThreshold(tableSize).good) }} – {{ formatTime(getTimeThreshold(tableSize).average - 1) }}</span>
            </div>
            <div class="threshold-item poor">
              <span class="threshold-label">Нужно тренироваться:</span>
              <span class="threshold-value">≥ {{ formatTime(getTimeThreshold(tableSize).average) }}</span>
            </div>
          </div>
        </div>

        <div class="results-actions">
          <Button
            variant="primary"
            @click="startGame"
          >
            Ещё попытку
          </Button>
          <Button
            variant="outline"
            @click="goToMenu"
          >
            В меню упражнений
          </Button>
        </div>
      </div>
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
import { ref, computed, onMounted, onUnmounted } from 'vue'
import Button from '@/components/UI/Button.vue'
import Notification from '@/components/UI/Notification.vue'
import brainTrainerService from '@/services/brainTrainerService'

const emit = defineEmits(['back'])

// Константы
const tableSizes = [
  { value: 3, label: '3×3 (простая)' },
  { value: 4, label: '4×4' },
  { value: 5, label: '5×5' },
  { value: 6, label: '6×6' },
  { value: 7, label: '7×7 (сложная)' }
]

// Нормативы времени (в секундах) для разных размеров
const timeThresholds = {
  3: { good: 15, average: 25 },
  4: { good: 30, average: 45 },
  5: { good: 50, average: 75 },
  6: { good: 80, average: 120 },
  7: { good: 120, average: 180 }
}

// Состояния
const stats = ref(null)
const loading = ref(false)
const currentPhase = ref('start') // start, playing, results
const tableSize = ref(5)
const table = ref([])
const currentNumber = ref(1)
const timeSeconds = ref(0)
const timerInterval = ref(null)
const showNotification = ref(false)
const notificationMessage = ref('')
const notificationType = ref('success')

// Общее количество чисел
const totalNumbers = computed(() => {
  return tableSize.value * tableSize.value
})

// Загрузка статистики
const loadStats = async () => {
  try {
    stats.value = await brainTrainerService.getStatsByType('SCHULTE_TABLE')
    console.log('📊 Статистика загружена:', stats.value)
  } catch (error) {
    console.error('❌ Ошибка загрузки статистики:', error)
  }
}

// Форматирование времени
const formatTime = (seconds) => {
  return brainTrainerService.constructor.formatTime(seconds)
}

// Получение норматива времени для текущего размера
const getTimeThreshold = (size) => {
  return timeThresholds[size] || timeThresholds[5]
}

// Получение цвета для оценки времени
const getTimeColor = (seconds) => {
  const threshold = getTimeThreshold(tableSize.value)
  if (seconds < threshold.good) return 'var(--color-success, #4caf50)'
  if (seconds < threshold.average) return 'var(--color-warning, #ff9800)'
  return 'var(--color-error, #f44336)'
}

// Получение текстовой оценки
const getTimeRating = (seconds) => {
  const threshold = getTimeThreshold(tableSize.value)
  if (seconds < threshold.good) return 'Отлично'
  if (seconds < threshold.average) return 'Хорошо'
  return 'Тренируйтесь дальше'
}

// Перемешивание массива (алгоритм Фишера-Йетса)
const shuffleArray = (array) => {
  for (let i = array.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [array[i], array[j]] = [array[j], array[i]];
  }
  return array
}

// Генерация таблицы
const generateTable = () => {
  const numbers = []
  for (let i = 1; i <= totalNumbers.value; i++) {
    numbers.push(i)
  }

  const shuffled = shuffleArray([...numbers])

  return shuffled.map(value => ({
    value,
    found: false
  }))
}

// Остановка таймера
const stopTimer = () => {
  if (timerInterval.value) {
    clearInterval(timerInterval.value)
    timerInterval.value = null
  }
}

// Запуск таймера
const startTimer = () => {
  timeSeconds.value = 0
  timerInterval.value = setInterval(() => {
    timeSeconds.value++
  }, 1000)
}

// Начало игры
const startGame = () => {
  stopTimer()
  loading.value = false

  try {
    table.value = generateTable()
    currentNumber.value = 1
    timeSeconds.value = 0
    currentPhase.value = 'playing'
    startTimer()
    console.log('🎮 Начало игры, размер:', tableSize.value)
  } catch (error) {
    console.error('❌ Ошибка начала игры:', error)
    showNotificationMessage('error', 'Ошибка при создании таблицы')
  }
}

// Сброс игры (начать заново)
const resetGame = () => {
  table.value = generateTable()
  currentNumber.value = 1
  timeSeconds.value = 0
  // Таймер не останавливаем, просто обнуляем
}

// Обработка клика по ячейке
const onCellClick = (cell) => {
  if (cell.found) return

  if (cell.value === currentNumber.value) {
    // Правильная цифра
    cell.found = true

    if (currentNumber.value === totalNumbers.value) {
      // Все цифры найдены!
      finishGame()
    } else {
      currentNumber.value++
    }
  } else {
    // Неправильная цифра - показываем подсказку
    showNotificationMessage('info', `Ищите цифру ${currentNumber.value}`)
  }
}

// Завершение игры
const finishGame = async () => {
  stopTimer()

  // Отправляем статистику на бэкенд
  try {
    // Считаем успехом, если время меньше среднего норматива
    const isSuccess = timeSeconds.value < getTimeThreshold(tableSize.value).average

    await brainTrainerService.submitSchulteResult({
      size: tableSize.value,
      timeSeconds: timeSeconds.value,
      isSuccess
    })

    // Обновляем статистику
    await loadStats()

    showNotificationMessage(
      isSuccess ? 'success' : 'info',
      isSuccess ? '🎉 Отличный результат!' : '📝 Попробуйте ещё раз!'
    )

  } catch (error) {
    console.error('❌ Ошибка сохранения статистики:', error)
    showNotificationMessage('error', 'Ошибка при сохранении результатов')
  }

  currentPhase.value = 'results'
}

// Вернуться в меню
const goToMenu = () => {
  stopTimer()
  emit('back')
}

// Показать уведомление
const showNotificationMessage = (type, message) => {
  notificationType.value = type
  notificationMessage.value = message
  showNotification.value = true
}

// Очистка при размонтировании
onUnmounted(() => {
  stopTimer()
})

// Загрузка статистики при монтировании
onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.schulte-table {
  width: 100%;
  max-width: 700px;
  margin: 0 auto;
}

.game-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 2rem;
  padding-bottom: 1rem;
  border-bottom: 2px solid var(--color-primary, #88d424);
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
  white-space: nowrap;
}

.back-button:hover {
  background-color: rgba(136, 212, 36, 0.1);
  transform: translateX(-3px);
}

.game-header h2 {
  font-size: 24px;
  color: var(--color-primary-dark, #6aa91c);
  margin: 0;
}

/* Статистика */
.stats-card {
  background: linear-gradient(135deg, var(--color-white, #ffffff) 0%, var(--color-white-smoke, #f5f5f5) 100%);
  border: 1px solid var(--color-light-gray, #e0e0e0);
  border-radius: 12px;
  padding: 1.5rem;
  margin-bottom: 2rem;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(100px, 1fr));
  gap: 15px;
}

.stat-item {
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: var(--color-primary, #88d424);
  line-height: 1.2;
}

.stat-label {
  color: var(--color-gray, #9e9e9e);
  font-size: 12px;
}

/* Игровая область */
.game-area {
  background: var(--color-white, #ffffff);
  border: 1px solid var(--color-light-gray, #e0e0e0);
  border-radius: 12px;
  padding: 2rem;
  box-shadow: 0 4px 12px var(--shadow-color, rgba(0,0,0,0.05));
}

/* Стартовый экран */
.start-screen {
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.start-icon {
  font-size: 64px;
  opacity: 0.7;
}

.start-description {
  color: var(--color-gray, #9e9e9e);
  font-size: 16px;
  line-height: 1.8;
  max-width: 500px;
  margin: 0 auto;
}

.settings {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  background-color: var(--color-white-smoke, #f5f5f5);
  border-radius: 12px;
  padding: 1.5rem;
}

.setting-group {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.setting-label {
  color: var(--color-gray, #9e9e9e);
  font-size: 14px;
  font-weight: 500;
  text-align: left;
}

.size-buttons {
  display: flex;
  gap: 10px;
  justify-content: center;
  flex-wrap: wrap;
}

.size-button {
  padding: 10px 20px;
  border: 1px solid var(--color-light-gray, #e0e0e0);
  border-radius: 8px;
  background: none;
  color: var(--color-gray, #9e9e9e);
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.size-button:hover {
  border-color: var(--color-primary, #88d424);
  color: var(--color-primary-dark, #6aa91c);
}

.size-button.active {
  background-color: var(--color-primary, #88d424);
  border-color: var(--color-primary, #88d424);
  color: white;
}

/* Игровой процесс */
.playing-phase {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.game-header-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 15px;
  padding: 1rem;
  background-color: var(--color-white-smoke, #f5f5f5);
  border-radius: 8px;
}

.progress {
  color: var(--color-gray, #9e9e9e);
  font-size: 14px;
}

.timer {
  color: var(--color-primary, #88d424);
  font-weight: 500;
  font-size: 16px;
  font-family: monospace;
}

.next-number {
  color: var(--color-primary-dark, #6aa91c);
  font-size: 16px;
  font-weight: 500;
}

.next-value {
  font-size: 24px;
  font-weight: 600;
  color: var(--color-primary, #88d424);
  margin-left: 5px;
}

.table-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 1rem;
  background-color: var(--color-white-smoke, #f5f5f5);
  border-radius: 12px;
}

.table-grid {
  display: grid;
  gap: 8px;
  width: 100%;
  max-width: 500px;
}

.table-cell {
  aspect-ratio: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--color-white, #ffffff);
  border: 2px solid var(--color-light-gray, #e0e0e0);
  border-radius: 8px;
  font-size: 20px;
  font-weight: 500;
  color: var(--color-dark-gray, #616161);
  cursor: pointer;
  transition: all 0.2s ease;
  width: 100%;
  padding: 0;
}

.table-cell:hover:not(:disabled) {
  border-color: var(--color-primary, #88d424);
  transform: scale(1.05);
}

.table-cell.found {
  background-color: rgba(76, 175, 80, 0.1);
  border-color: var(--color-success, #4caf50);
  color: var(--color-success, #4caf50);
  cursor: default;
  opacity: 0.7;
}

.table-cell.current {
  border-color: var(--color-primary, #88d424);
  background-color: rgba(136, 212, 36, 0.1);
  color: var(--color-primary-dark, #6aa91c);
  font-weight: 600;
  animation: pulse 1s ease infinite;
}

.table-cell:disabled {
  cursor: not-allowed;
}

.progress-bar {
  height: 8px;
  background-color: var(--color-light-gray, #e0e0e0);
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, var(--color-primary, #88d424) 0%, var(--color-primary-dark, #6aa91c) 100%);
  transition: width 0.3s ease;
}

.reset-button {
  align-self: center;
}

/* Фаза результатов */
.results-phase {
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.results-icon {
  font-size: 64px;
}

.results-icon.success {
  animation: bounce 1s ease infinite;
}

.results-icon.warning {
  animation: pulse 1s ease infinite;
}

.results-icon.error {
  opacity: 0.7;
}

.results-title {
  font-size: 24px;
  color: var(--color-primary-dark, #6aa91c);
  margin: 0;
}

.results-stats {
  background-color: var(--color-white-smoke, #f5f5f5);
  border-radius: 12px;
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-width: 300px;
  margin: 0 auto;
}

.result-item {
  display: flex;
  justify-content: space-between;
  font-size: 16px;
}

.result-label {
  color: var(--color-gray, #9e9e9e);
}

.result-value {
  color: var(--color-primary-dark, #6aa91c);
  font-weight: 600;
}

/* Нормативы */
.thresholds {
  text-align: left;
  background-color: var(--color-white-smoke, #f5f5f5);
  border-radius: 12px;
  padding: 1.5rem;
}

.thresholds h4 {
  color: var(--color-primary-dark, #6aa91c);
  margin: 0 0 1rem 0;
  font-size: 16px;
  text-align: center;
}

.threshold-items {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.threshold-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  border-radius: 6px;
}

.threshold-item.good {
  background-color: rgba(76, 175, 80, 0.1);
  border-left: 3px solid var(--color-success, #4caf50);
}

.threshold-item.average {
  background-color: rgba(255, 152, 0, 0.1);
  border-left: 3px solid var(--color-warning, #ff9800);
}

.threshold-item.poor {
  background-color: rgba(244, 67, 54, 0.1);
  border-left: 3px solid var(--color-error, #f44336);
}

.threshold-label {
  color: var(--color-gray, #9e9e9e);
  font-size: 13px;
}

.threshold-value {
  font-weight: 600;
  font-size: 14px;
  font-family: monospace;
}

.threshold-item.good .threshold-value {
  color: var(--color-success, #4caf50);
}

.threshold-item.average .threshold-value {
  color: var(--color-warning, #ff9800);
}

.threshold-item.poor .threshold-value {
  color: var(--color-error, #f44336);
}

.results-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

@media (max-width: 768px) {
  .game-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .game-header-info {
    flex-direction: column;
    align-items: stretch;
    text-align: center;
  }

  .table-cell {
    font-size: 16px;
  }

  .size-buttons {
    flex-direction: column;
  }

  .size-button {
    width: 100%;
  }

  .results-actions {
    flex-direction: column;
  }

  .results-actions button {
    width: 100%;
  }

  .threshold-item {
    flex-direction: column;
    gap: 5px;
    text-align: center;
  }
}
</style>
