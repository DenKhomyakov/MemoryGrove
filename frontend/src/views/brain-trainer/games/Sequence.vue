<template>
  <div class="sequence-game">
    <div class="game-header">
      <button class="back-button" @click="$emit('back')">
        ← Назад к списку
      </button>
      <h2>📊 Продолжи ряд</h2>
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
        <div class="stat-item" v-if="stats.bestScore">
          <span class="stat-value">{{ stats.bestScore }}</span>
          <span class="stat-label">Лучший счёт</span>
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
        <div class="start-icon">📊</div>
        <p class="start-description">
          Вам будет показана последовательность чисел, букв или символов.
          Нужно понять закономерность и выбрать правильное продолжение.
        </p>

        <div class="settings">
          <div class="setting-group">
            <label class="setting-label">Уровень сложности:</label>
            <div class="difficulty-buttons">
              <button
                v-for="diff in difficulties"
                :key="diff.value"
                class="difficulty-button"
                :class="{
                  active: selectedDifficulty === diff.value,
                  easy: diff.value === 'easy',
                  medium: diff.value === 'medium',
                  hard: diff.value === 'hard'
                }"
                @click="selectedDifficulty = diff.value"
              >
                <span class="difficulty-dot" :style="{ backgroundColor: diff.color }"></span>
                {{ diff.label }}
              </button>
            </div>
          </div>

          <div class="setting-group">
            <label class="setting-label">Количество вопросов:</label>
            <div class="count-buttons">
              <button
                v-for="count in [5, 10, 15]"
                :key="count"
                class="count-button"
                :class="{ active: questionsCount === count }"
                @click="questionsCount = count"
              >
                {{ count }}
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
            Вопрос {{ currentIndex + 1 }} из {{ totalQuestions }}
          </div>
          <div class="score">Счёт: {{ score }} / {{ totalQuestions }}</div>
          <div class="timer">⏱️ {{ formatTime(timeSeconds) }}</div>
        </div>

        <div class="question-card">
          <div class="difficulty-indicator" :style="{ backgroundColor: getDifficultyColorLocal(currentQuestion.difficulty) }">
            {{ getDifficultyDisplayLocal(currentQuestion.difficulty) }}
          </div>

          <div class="sequence-container">
            <div class="sequence-label">Последовательность:</div>
            <div class="sequence-items">
              <span
                v-for="(item, index) in currentQuestion.sequence"
                :key="index"
                class="sequence-item"
              >
                {{ item }}
                <span v-if="index < currentQuestion.sequence.length - 1" class="comma">,</span>
              </span>
              <span class="question-mark">?</span>
            </div>
          </div>

          <div class="options-grid">
            <button
              v-for="(option, index) in currentQuestion.options"
              :key="index"
              class="option-button"
              :class="{
                selected: selectedOption === index,
                correct: showResult && index === currentQuestion.correctIndex,
                incorrect: showResult && selectedOption === index && index !== currentQuestion.correctIndex
              }"
              @click="selectOption(index)"
              :disabled="showResult"
            >
              {{ option }}
            </button>
          </div>

          <div class="explanation" v-if="showResult">
            <div class="explanation-icon">💡</div>
            <div class="explanation-text">{{ currentQuestion.explanation }}</div>
          </div>

          <div class="question-actions">
            <Button
              variant="primary"
              @click="nextQuestion"
              :disabled="!canProceed"
            >
              {{ isLastQuestion ? 'Завершить' : 'Следующий вопрос' }}
            </Button>
          </div>
        </div>
      </div>

      <!-- Результаты -->
      <div v-else-if="currentPhase === 'results'" class="results-phase">
        <div class="results-icon" :class="{
          'success': scorePercentage >= 80,
          'warning': scorePercentage >= 50 && scorePercentage < 80,
          'error': scorePercentage < 50
        }">
          {{ scorePercentage >= 80 ? '🏆' : (scorePercentage >= 50 ? '👍' : '💪') }}
        </div>

        <h3 class="results-title">
          {{ scorePercentage >= 80 ? 'Отличное логическое мышление!' : (scorePercentage >= 50 ? 'Неплохо, тренируйтесь дальше' : 'Попробуйте ещё раз') }}
        </h3>

        <div class="results-stats">
          <div class="result-item">
            <span class="result-label">Правильных ответов:</span>
            <span class="result-value">{{ score }} из {{ totalQuestions }}</span>
          </div>
          <div class="result-item">
            <span class="result-label">Процент:</span>
            <span class="result-value">{{ scorePercentage }}%</span>
          </div>
          <div class="result-item">
            <span class="result-label">Время:</span>
            <span class="result-value">{{ formatTime(timeSeconds) }}</span>
          </div>
          <div class="result-item">
            <span class="result-label">Сложность:</span>
            <span class="result-value">{{ getDifficultyDisplayLocal(selectedDifficulty === 'all' ? 'mixed' : selectedDifficulty) }}</span>
          </div>
        </div>

        <!-- Статистика по типам -->
        <div class="type-stats" v-if="typeStats.length > 0">
          <h4>Статистика по типам</h4>
          <div class="type-stats-grid">
            <div v-for="stat in typeStats" :key="stat.type" class="type-stat-item">
              <div class="type-icon">{{ getTypeIcon(stat.type) }}</div>
              <div class="type-info">
                <div class="type-name">{{ getTypeName(stat.type) }}</div>
                <div class="type-score">{{ stat.correct }}/{{ stat.total }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- Детальный разбор -->
        <div class="details-section">
          <h4>Детальный разбор</h4>
          <div class="details-list">
            <div
              v-for="(result, index) in questionResults"
              :key="index"
              class="detail-item"
              :class="{ 'correct-detail': result.isCorrect, 'incorrect-detail': !result.isCorrect }"
            >
              <div class="detail-question">
                <span class="detail-number">{{ index + 1 }}.</span>
                <span class="detail-sequence">{{ formatSequence(result.sequence) }} → ?</span>
                <span class="detail-icon">{{ result.isCorrect ? '✓' : '✗' }}</span>
              </div>
              <div class="detail-answer" v-if="!result.isCorrect">
                Правильный ответ: {{ result.correctAnswer }}
              </div>
              <div class="detail-explanation">
                {{ result.explanation }}
              </div>
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
import {
  getRandomSequence,
  sequences
} from '../data/sequences'

const emit = defineEmits(['back'])

// Константы
const difficulties = [
  { value: 'all', label: 'Все уровни', color: 'var(--color-gray)' },
  { value: 'easy', label: 'Простой', color: 'var(--color-success)' },
  { value: 'medium', label: 'Средний', color: 'var(--color-warning)' }
]

// Состояния
const stats = ref(null)
const loading = ref(false)
const currentPhase = ref('start') // start, playing, results
const selectedDifficulty = ref('all')
const questionsCount = ref(10)
const questions = ref([])
const currentIndex = ref(0)
const selectedOption = ref(null)
const showResult = ref(false)
const score = ref(0)
const timeSeconds = ref(0)
const timerInterval = ref(null)
const questionResults = ref([])
const showNotification = ref(false)
const notificationMessage = ref('')
const notificationType = ref('success')

// Текущий вопрос
const currentQuestion = computed(() => {
  if (!questions.value.length) return null
  return questions.value[currentIndex.value]
})

// Общее количество вопросов
const totalQuestions = computed(() => questions.value.length)

// Последний ли вопрос
const isLastQuestion = computed(() => {
  return currentIndex.value === totalQuestions.value - 1
})

// Можно ли перейти к следующему вопросу
const canProceed = computed(() => {
  if (showResult.value) return true
  return selectedOption.value !== null
})

// Процент правильных ответов
const scorePercentage = computed(() => {
  return totalQuestions.value > 0
    ? Math.round((score.value / totalQuestions.value) * 100)
    : 0
})

// Статистика по типам
const typeStats = computed(() => {
  const stats = {}
  questionResults.value.forEach(result => {
    if (!stats[result.type]) {
      stats[result.type] = { total: 0, correct: 0 }
    }
    stats[result.type].total++
    if (result.isCorrect) stats[result.type].correct++
  })
  return Object.entries(stats).map(([type, data]) => ({
    type,
    ...data
  }))
})

// Загрузка статистики
const loadStats = async () => {
  try {
    stats.value = await brainTrainerService.getStatsByType('SEQUENCE')
    console.log('📊 Статистика загружена:', stats.value)
  } catch (error) {
    console.error('❌ Ошибка загрузки статистики:', error)
  }
}

// Форматирование времени
const formatTime = (seconds) => {
  return brainTrainerService.constructor.formatTime(seconds)
}

// Локальная функция для получения отображаемого названия сложности
const getDifficultyDisplayLocal = (diff) => {
  const map = {
    'easy': '🟢 Простой',
    'medium': '🟡 Средний',
    'mixed': '🔄 Смешанный'
  }
  return map[diff] || diff
}

// Локальная функция для получения цвета сложности
const getDifficultyColorLocal = (diff) => {
  const map = {
    'easy': 'var(--color-success, #4caf50)',
    'medium': 'var(--color-warning, #ff9800)',
    'mixed': 'var(--color-gray, #9e9e9e)'
  }
  return map[diff] || 'var(--color-gray, #9e9e9e)'
}

// Получение иконки типа
const getTypeIcon = (type) => {
  const map = {
    'number': '🔢',
    'letter': '📝',
    'logical': '🧩'
  }
  return map[type] || '📊'
}

// Получение названия типа
const getTypeName = (type) => {
  const map = {
    'number': 'Числовые',
    'letter': 'Буквенные',
    'logical': 'Логические'
  }
  return map[type] || type
}

// Форматирование последовательности для отображения
const formatSequence = (seq) => {
  return seq.join(', ')
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

// Генерация вопросов
const generateQuestions = () => {
  const newQuestions = []
  const availableSequences = selectedDifficulty.value === 'all'
    ? sequences
    : sequences.filter(s => s.difficulty === selectedDifficulty.value)

  if (availableSequences.length === 0) return []

  for (let i = 0; i < questionsCount.value; i++) {
    const randomIndex = Math.floor(Math.random() * availableSequences.length)
    newQuestions.push({ ...availableSequences[randomIndex] })
  }

  return newQuestions
}

// Начало игры
const startGame = () => {
  stopTimer()
  loading.value = false

  try {
    const generated = generateQuestions()
    if (generated.length === 0) {
      showNotificationMessage('error', 'Нет вопросов для выбранной сложности')
      return
    }

    questions.value = generated
    currentIndex.value = 0
    selectedOption.value = null
    showResult.value = false
    score.value = 0
    timeSeconds.value = 0
    questionResults.value = []
    currentPhase.value = 'playing'

    startTimer()
    console.log('🎮 Начало игры, вопросов:', questions.value.length)
  } catch (error) {
    console.error('❌ Ошибка начала игры:', error)
    showNotificationMessage('error', 'Ошибка при загрузке вопросов')
  }
}

// Выбор варианта
const selectOption = (index) => {
  if (showResult.value) return
  selectedOption.value = index
}

// Переход к следующему вопросу
const nextQuestion = async () => {
  if (!canProceed.value) return

  if (!showResult.value) {
    // Проверяем ответ
    const isCorrect = selectedOption.value === currentQuestion.value.correctIndex

    if (isCorrect) {
      score.value++
    }

    // Сохраняем результат вопроса
    questionResults.value.push({
      type: currentQuestion.value.type,
      sequence: currentQuestion.value.sequence,
      isCorrect,
      correctAnswer: currentQuestion.value.options[currentQuestion.value.correctIndex],
      explanation: currentQuestion.value.explanation
    })

    showResult.value = true

  } else {
    if (isLastQuestion.value) {
      // Завершаем игру
      stopTimer()

      // Отправляем статистику на бэкенд
      try {
        const isSuccess = scorePercentage.value >= 70
        await brainTrainerService.submitSequenceResult({
          correct: score.value,
          total: totalQuestions.value,
          percentage: scorePercentage.value,
          timeSeconds: timeSeconds.value,
          difficulty: selectedDifficulty.value
        })

        // Обновляем статистику
        await loadStats()

        showNotificationMessage(
          isSuccess ? 'success' : 'info',
          isSuccess ? '🎉 Отличный результат!' : '📝 Попробуй ещё раз!'
        )

      } catch (error) {
        console.error('❌ Ошибка сохранения статистики:', error)
        showNotificationMessage('error', 'Ошибка при сохранении результатов')
      }

      currentPhase.value = 'results'

    } else {
      // Переходим к следующему вопросу
      currentIndex.value++
      selectedOption.value = null
      showResult.value = false
    }
  }
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
.sequence-game {
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

.difficulty-buttons {
  display: flex;
  gap: 10px;
  justify-content: center;
  flex-wrap: wrap;
}

.difficulty-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border: 1px solid var(--color-light-gray, #e0e0e0);
  border-radius: 8px;
  background: none;
  color: var(--color-gray, #9e9e9e);
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.difficulty-button:hover {
  border-color: var(--color-primary, #88d424);
  color: var(--color-primary-dark, #6aa91c);
}

.difficulty-button.active {
  background-color: var(--color-primary, #88d424);
  border-color: var(--color-primary, #88d424);
  color: white;
}

.difficulty-button.active .difficulty-dot {
  background-color: white !important;
}

.difficulty-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  display: inline-block;
}

.count-buttons {
  display: flex;
  gap: 10px;
  justify-content: center;
  flex-wrap: wrap;
}

.count-button {
  width: 60px;
  height: 60px;
  border: 2px solid var(--color-light-gray, #e0e0e0);
  border-radius: 8px;
  background: none;
  color: var(--color-gray, #9e9e9e);
  font-size: 18px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.count-button:hover {
  border-color: var(--color-primary, #88d424);
  color: var(--color-primary, #88d424);
}

.count-button.active {
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

.score {
  color: var(--color-primary-dark, #6aa91c);
  font-weight: 600;
  font-size: 16px;
}

.timer {
  color: var(--color-primary, #88d424);
  font-weight: 500;
  font-size: 16px;
  font-family: monospace;
}

.question-card {
  background-color: var(--color-white-smoke, #f5f5f5);
  border-radius: 12px;
  padding: 2rem;
  position: relative;
}

.difficulty-indicator {
  position: absolute;
  top: -10px;
  left: 20px;
  padding: 4px 12px;
  border-radius: 20px;
  color: white;
  font-size: 12px;
  font-weight: 500;
  box-shadow: 0 2px 4px var(--shadow-color, rgba(0,0,0,0.1));
}

.sequence-container {
  margin-bottom: 2rem;
  text-align: center;
}

.sequence-label {
  color: var(--color-gray, #9e9e9e);
  font-size: 14px;
  margin-bottom: 1rem;
}

.sequence-items {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-wrap: wrap;
  gap: 5px;
  font-size: 24px;
  font-weight: 500;
}

.sequence-item {
  color: var(--color-primary-dark, #6aa91c);
}

.comma {
  color: var(--color-gray, #9e9e9e);
  margin-right: 5px;
}

.question-mark {
  font-size: 32px;
  font-weight: 600;
  color: var(--color-primary, #88d424);
  margin-left: 10px;
}

.options-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
  margin-bottom: 2rem;
}

.option-button {
  padding: 1rem;
  background-color: var(--color-white, #ffffff);
  border: 1px solid var(--color-light-gray, #e0e0e0);
  border-radius: 8px;
  font-size: 16px;
  color: var(--color-dark-gray, #616161);
  cursor: pointer;
  transition: all 0.2s ease;
  text-align: center;
}

.option-button:hover:not(:disabled):not(.selected):not(.correct):not(.incorrect) {
  border-color: var(--color-primary, #88d424);
  transform: translateY(-2px);
}

.option-button.selected {
  border-color: var(--color-primary, #88d424);
  background-color: rgba(136, 212, 36, 0.1);
  color: var(--color-primary-dark, #6aa91c);
}

.option-button.correct {
  border-color: var(--color-success, #4caf50);
  background-color: rgba(76, 175, 80, 0.1);
  color: var(--color-success, #4caf50);
}

.option-button.incorrect {
  border-color: var(--color-error, #f44336);
  background-color: rgba(244, 67, 54, 0.1);
  color: var(--color-error, #f44336);
}

.option-button:disabled {
  cursor: not-allowed;
  opacity: 0.8;
}

.explanation {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 2rem;
  padding: 1rem;
  background-color: rgba(33, 150, 243, 0.1);
  border-radius: 8px;
  border-left: 3px solid #2196f3;
}

.explanation-icon {
  font-size: 18px;
}

.explanation-text {
  flex: 1;
  color: var(--color-dark-gray, #616161);
  font-size: 14px;
  line-height: 1.6;
}

.question-actions {
  display: flex;
  justify-content: center;
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

/* Статистика по типам */
.type-stats {
  text-align: left;
}

.type-stats h4 {
  color: var(--color-primary-dark, #6aa91c);
  margin: 0 0 1rem 0;
  font-size: 16px;
}

.type-stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 10px;
}

.type-stat-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0.75rem;
  background-color: var(--color-white-smoke, #f5f5f5);
  border-radius: 8px;
}

.type-icon {
  font-size: 20px;
}

.type-info {
  flex: 1;
}

.type-name {
  color: var(--color-dark-gray, #616161);
  font-size: 13px;
  font-weight: 500;
  margin-bottom: 2px;
}

.type-score {
  color: var(--color-primary, #88d424);
  font-size: 12px;
}

/* Детальный разбор */
.details-section {
  text-align: left;
  max-height: 300px;
  overflow-y: auto;
  padding: 1rem;
  background-color: var(--color-white-smoke, #f5f5f5);
  border-radius: 8px;
}

.details-section h4 {
  color: var(--color-primary-dark, #6aa91c);
  margin: 0 0 1rem 0;
  font-size: 16px;
  text-align: center;
}

.details-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.detail-item {
  padding: 12px;
  border-radius: 6px;
  border-left: 3px solid transparent;
}

.detail-item.correct-detail {
  background-color: rgba(76, 175, 80, 0.1);
  border-left-color: var(--color-success, #4caf50);
}

.detail-item.incorrect-detail {
  background-color: rgba(244, 67, 54, 0.1);
  border-left-color: var(--color-error, #f44336);
}

.detail-question {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  margin-bottom: 5px;
}

.detail-number {
  color: var(--color-gray, #9e9e9e);
  font-size: 12px;
  min-width: 30px;
}

.detail-sequence {
  flex: 1;
  color: var(--color-dark-gray, #616161);
  font-weight: 500;
}

.detail-icon {
  font-size: 16px;
  min-width: 20px;
  text-align: center;
}

.correct-detail .detail-icon {
  color: var(--color-success, #4caf50);
}

.incorrect-detail .detail-icon {
  color: var(--color-error, #f44336);
}

.detail-answer {
  margin-top: 5px;
  margin-left: 40px;
  font-size: 13px;
  color: var(--color-primary, #88d424);
  margin-bottom: 4px;
}

.detail-explanation {
  margin-left: 40px;
  font-size: 12px;
  color: var(--color-gray, #9e9e9e);
  font-style: italic;
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

  .sequence-items {
    font-size: 18px;
  }

  .options-grid {
    grid-template-columns: 1fr;
  }

  .difficulty-buttons {
    flex-direction: column;
  }

  .difficulty-button {
    width: 100%;
    justify-content: center;
  }

  .type-stats-grid {
    grid-template-columns: 1fr;
  }

  .results-actions {
    flex-direction: column;
  }

  .results-actions button {
    width: 100%;
  }

  .detail-question {
    flex-direction: column;
    align-items: flex-start;
  }

  .detail-number {
    min-width: auto;
  }

  .detail-answer,
  .detail-explanation {
    margin-left: 20px;
  }
}
</style>
