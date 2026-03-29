<template>
  <div class="associations">
    <div class="game-header">
      <button class="back-button" @click="$emit('back')">
        ← Назад к списку
      </button>
      <h2>🔗 Ассоциации</h2>
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
        <div class="start-icon">🔗</div>
        <p class="start-description">
          Вам будет показано слово. Нужно выбрать из четырёх вариантов то,
          что с ним ассоциируется. Постарайтесь отвечать быстро и правильно!
        </p>
        <div class="difficulty-selector">
          <label class="difficulty-label">Количество вопросов:</label>
          <div class="difficulty-buttons">
            <button
              v-for="count in [5, 10, 15]"
              :key="count"
              class="difficulty-button"
              :class="{ active: questionsCount === count }"
              @click="questionsCount = count"
            >
              {{ count }}
            </button>
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
          <div class="word-container">
            <span class="word-label">Слово:</span>
            <span class="word">{{ currentQuestion.word }}</span>
          </div>

          <div v-if="showHint" class="hint">
            <span class="hint-icon">💡</span>
            <span class="hint-text">{{ currentQuestion.hint }}</span>
          </div>

          <div class="options-grid">
            <button
              v-for="(option, index) in currentQuestion.options"
              :key="index"
              class="option-button"
              :class="{
                selected: selectedOption === index,
                correct: showResult && option.isCorrect,
                incorrect: showResult && selectedOption === index && !option.isCorrect
              }"
              @click="selectOption(index)"
              :disabled="showResult"
            >
              {{ option.text }}
            </button>
          </div>

          <div class="question-actions">
            <Button
              variant="text"
              size="small"
              @click="toggleHint"
              class="hint-button"
            >
              {{ showHint ? 'Скрыть подсказку' : 'Показать подсказку' }}
            </Button>
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
          {{ scorePercentage >= 80 ? 'Отличный результат!' : (scorePercentage >= 50 ? 'Хорошо, но можно лучше' : 'Попробуй ещё раз') }}
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
                <span class="detail-word">{{ result.word }}</span>
                <span class="detail-icon">{{ result.isCorrect ? '✓' : '✗' }}</span>
              </div>
              <div class="detail-answer" v-if="!result.isCorrect">
                Правильный ответ: {{ result.correctAnswer }}
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
import { getRandomAssociation, shuffleOptions } from '../data/associations'

const emit = defineEmits(['back'])

// Состояния
const stats = ref(null)
const loading = ref(false)
const currentPhase = ref('start') // start, playing, results
const questionsCount = ref(10)
const questions = ref([])
const currentIndex = ref(0)
const selectedOption = ref(null)
const showResult = ref(false)
const showHint = ref(false)
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

// Загрузка статистики
const loadStats = async () => {
  try {
    stats.value = await brainTrainerService.getStatsByType('ASSOCIATION')
    console.log('📊 Статистика загружена:', stats.value)
  } catch (error) {
    console.error('❌ Ошибка загрузки статистики:', error)
  }
}

// Форматирование времени
const formatTime = (seconds) => {
  return brainTrainerService.constructor.formatTime(seconds)
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
  const usedIds = new Set()

  for (let i = 0; i < questionsCount.value; i++) {
    let question
    do {
      question = getRandomAssociation()
    } while (usedIds.has(question.id) && usedIds.size < 12)

    usedIds.add(question.id)
    newQuestions.push({
      ...question,
      options: shuffleOptions(question.options)
    })
  }

  return newQuestions
}

// Начало игры
const startGame = () => {
  stopTimer()
  loading.value = false

  try {
    questions.value = generateQuestions()
    currentIndex.value = 0
    selectedOption.value = null
    showResult.value = false
    showHint.value = false
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

// Переключение подсказки
const toggleHint = () => {
  showHint.value = !showHint.value
}

// Переход к следующему вопросу
const nextQuestion = async () => {
  if (!canProceed.value) return

  if (!showResult.value) {
    // Проверяем ответ
    const isCorrect = currentQuestion.value.options[selectedOption.value].isCorrect

    if (isCorrect) {
      score.value++
    }

    // Сохраняем результат вопроса
    questionResults.value.push({
      word: currentQuestion.value.word,
      isCorrect,
      correctAnswer: currentQuestion.value.options.find(opt => opt.isCorrect).text
    })

    showResult.value = true

  } else {
    if (isLastQuestion.value) {
      // Завершаем игру
      stopTimer()

      // Отправляем статистику на бэкенд
      try {
        const isSuccess = scorePercentage.value >= 70
        await brainTrainerService.submitAssociationResult({
          correct: score.value,
          total: totalQuestions.value,
          percentage: scorePercentage.value,
          timeSeconds: timeSeconds.value
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
      showHint.value = false
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
.associations {
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

.difficulty-selector {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  align-items: center;
}

.difficulty-label {
  color: var(--color-gray, #9e9e9e);
  font-size: 14px;
  font-weight: 500;
}

.difficulty-buttons {
  display: flex;
  gap: 10px;
  justify-content: center;
  flex-wrap: wrap;
}

.difficulty-button {
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

.difficulty-button:hover {
  border-color: var(--color-primary, #88d424);
  color: var(--color-primary, #88d424);
}

.difficulty-button.active {
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
}

.word-container {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 1.5rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid var(--color-light-gray, #e0e0e0);
}

.word-label {
  color: var(--color-gray, #9e9e9e);
  font-size: 14px;
  font-weight: 500;
}

.word {
  font-size: 32px;
  font-weight: 600;
  color: var(--color-primary-dark, #6aa91c);
}

.hint {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 1.5rem;
  padding: 1rem;
  background-color: rgba(136, 212, 36, 0.1);
  border-radius: 8px;
  color: var(--color-primary-dark, #6aa91c);
}

.hint-icon {
  font-size: 18px;
}

.hint-text {
  font-size: 14px;
  font-style: italic;
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

.question-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 15px;
}

.hint-button {
  color: var(--color-gray, #9e9e9e);
}

.hint-button:hover {
  color: var(--color-primary, #88d424);
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
  padding: 10px;
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
}

.detail-number {
  color: var(--color-gray, #9e9e9e);
  font-size: 12px;
  min-width: 30px;
}

.detail-word {
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

  .options-grid {
    grid-template-columns: 1fr;
  }

  .word {
    font-size: 24px;
  }

  .question-actions {
    flex-direction: column;
  }

  .question-actions button {
    width: 100%;
  }

  .results-actions {
    flex-direction: column;
  }

  .results-actions button {
    width: 100%;
  }
}
</style>
