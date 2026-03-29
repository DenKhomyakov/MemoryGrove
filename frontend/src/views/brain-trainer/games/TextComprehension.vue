<template>
  <div class="text-comprehension">
    <div class="game-header">
      <button class="back-button" @click="$emit('back')">
        ← Назад к списку
      </button>
      <h2>📖 Понимание текста</h2>
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
        <div class="start-icon">📖</div>
        <p class="start-description">
          Сейчас вы прочитаете небольшой рассказ.
          После этого нужно будет ответить на вопросы по тексту.
          Постарайтесь запомнить как можно больше деталей!
        </p>
        <Button
          variant="primary"
          @click="startGame"
          :loading="loading"
        >
          Начать упражнение
        </Button>
      </div>

      <!-- Чтение текста -->
      <div v-else-if="currentPhase === 'reading'" class="reading-phase">
        <div class="timer" v-if="timeRemaining > 0">
          ⏱️ Время на чтение: {{ timeRemaining }} сек
        </div>

        <div class="text-card">
          <h3 class="text-title">{{ currentText.title }}</h3>
          <p class="text-content">{{ currentText.text }}</p>
        </div>

        <Button
          variant="primary"
          @click="finishReading"
          :disabled="!canFinishReading"
        >
          Я прочитал(а), перейти к вопросам
        </Button>
        <p class="hint" v-if="!canFinishReading">
          Подождите немного, чтобы успеть прочитать
        </p>
      </div>

      <!-- Ответы на вопросы -->
      <div v-else-if="currentPhase === 'questions'" class="questions-phase">
        <div class="progress">
          Вопрос {{ currentQuestionIndex + 1 }} из {{ currentText.questions.length }}
        </div>

        <div class="question-card">
          <h3 class="question">{{ currentQuestion.question }}</h3>

          <div class="options">
            <div
              v-for="(option, index) in currentQuestion.options"
              :key="index"
              class="option"
              :class="{
                selected: selectedOption === index,
                correct: showResults && index === currentQuestion.correctIndex,
                incorrect: showResults && selectedOption === index && index !== currentQuestion.correctIndex
              }"
              @click="selectOption(index)"
            >
              <span class="option-marker">{{ String.fromCharCode(65 + index) }}.</span>
              <span class="option-text">{{ option }}</span>
              <span v-if="showResults && index === currentQuestion.correctIndex" class="correct-mark">✓</span>
              <span v-if="showResults && selectedOption === index && index !== currentQuestion.correctIndex" class="incorrect-mark">✗</span>
            </div>
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
          'success': score.percentage >= 70,
          'warning': score.percentage >= 40 && score.percentage < 70,
          'error': score.percentage < 40
        }">
          {{ score.percentage >= 70 ? '🎉' : (score.percentage >= 40 ? '📝' : '😔') }}
        </div>

        <h3 class="results-title">
          {{ score.percentage >= 70 ? 'Отличный результат!' : (score.percentage >= 40 ? 'Неплохо, но можно лучше' : 'Попробуй ещё раз') }}
        </h3>

        <div class="results-stats">
          <div class="result-item">
            <span class="result-label">Правильных ответов:</span>
            <span class="result-value">{{ score.correct }} из {{ score.total }}</span>
          </div>
          <div class="result-item">
            <span class="result-label">Процент:</span>
            <span class="result-value">{{ score.percentage }}%</span>
          </div>
          <div class="result-item">
            <span class="result-label">Время:</span>
            <span class="result-value">{{ formatTime(timeSeconds) }}</span>
          </div>
        </div>

        <div class="results-actions">
          <Button
            variant="primary"
            @click="startGame"
          >
            Ещё рассказ
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
import { getRandomText } from '../data/textComprehensionTexts'

const emit = defineEmits(['back'])

// Состояния
const stats = ref(null)
const loading = ref(false)
const currentPhase = ref('start') // start, reading, questions, results
const currentText = ref(null)
const currentQuestionIndex = ref(0)
const selectedOption = ref(null)
const showResults = ref(false)
const answers = ref([])
const timeSeconds = ref(0)
const timeRemaining = ref(30) // Время на чтение в секундах
const timerInterval = ref(null)
const readingTimer = ref(null)
const showNotification = ref(false)
const notificationMessage = ref('')
const notificationType = ref('success')

// Текущий вопрос
const currentQuestion = computed(() => {
  if (!currentText.value || !currentText.value.questions) return null
  return currentText.value.questions[currentQuestionIndex.value]
})

// Последний ли вопрос
const isLastQuestion = computed(() => {
  if (!currentText.value || !currentText.value.questions) return false
  return currentQuestionIndex.value === currentText.value.questions.length - 1
})

// Можно ли перейти к следующему вопросу
const canProceed = computed(() => {
  if (showResults.value) return true
  return selectedOption.value !== null
})

// Можно ли завершить чтение
const canFinishReading = computed(() => {
  return timeRemaining.value <= 0
})

// Результаты
const score = computed(() => {
  const total = currentText.value?.questions.length || 0
  const correct = answers.value.filter(a => a.isCorrect).length
  const percentage = total > 0 ? Math.round((correct / total) * 100) : 0
  return { total, correct, percentage }
})

// Загрузка статистики
const loadStats = async () => {
  try {
    stats.value = await brainTrainerService.getStatsByType('TEXT_COMPREHENSION')
    console.log('📊 Статистика загружена:', stats.value)
  } catch (error) {
    console.error('❌ Ошибка загрузки статистики:', error)
  }
}

// Форматирование времени
const formatTime = (seconds) => {
  return brainTrainerService.constructor.formatTime(seconds)
}

// Остановка всех таймеров
const stopAllTimers = () => {
  if (timerInterval.value) {
    clearInterval(timerInterval.value)
    timerInterval.value = null
  }
  if (readingTimer.value) {
    clearInterval(readingTimer.value)
    readingTimer.value = null
  }
}

// Запуск таймера игры
const startGameTimer = () => {
  timerInterval.value = setInterval(() => {
    timeSeconds.value++
  }, 1000)
}

// Запуск таймера на чтение
const startReadingTimer = () => {
  timeRemaining.value = 30
  readingTimer.value = setInterval(() => {
    if (timeRemaining.value > 0) {
      timeRemaining.value--
    } else {
      clearInterval(readingTimer.value)
      readingTimer.value = null
    }
  }, 1000)
}

// Начало игры
const startGame = () => {
  stopAllTimers()
  loading.value = false // Важно: устанавливаем loading в false сразу

  try {
    currentText.value = getRandomText()
    currentQuestionIndex.value = 0
    selectedOption.value = null
    showResults.value = false
    answers.value = []
    timeSeconds.value = 0
    currentPhase.value = 'reading'

    startReadingTimer()
    startGameTimer()
    console.log('📖 Новый текст:', currentText.value.title)
  } catch (error) {
    console.error('❌ Ошибка начала игры:', error)
    showNotificationMessage('error', 'Ошибка при загрузке текста')
  }
}

// Завершение чтения
const finishReading = () => {
  clearInterval(readingTimer.value)
  readingTimer.value = null
  currentPhase.value = 'questions'
}

// Выбор варианта
const selectOption = (index) => {
  if (showResults.value) return
  selectedOption.value = index
}

// Переход к следующему вопросу
const nextQuestion = async () => {
  if (!canProceed.value) return

  if (!showResults.value) {
    // Показываем правильный ответ
    showResults.value = true

    // Сохраняем ответ
    answers.value.push({
      questionIndex: currentQuestionIndex.value,
      selected: selectedOption.value,
      isCorrect: selectedOption.value === currentQuestion.value.correctIndex
    })

  } else {
    // Переходим к следующему вопросу или завершаем
    if (isLastQuestion.value) {
      // Завершаем игру
      stopAllTimers()

      // Отправляем статистику на бэкенд
      try {
        await brainTrainerService.submitTextComprehensionResult({
          correctAnswers: score.value.correct,
          totalQuestions: score.value.total,
          successRate: score.value.percentage,
          timeSeconds: timeSeconds.value
        })

        // Обновляем статистику
        await loadStats()

        showNotificationMessage(
          score.value.percentage >= 70 ? 'success' : 'info',
          score.value.percentage >= 70 ? '🎉 Отличный результат!' : '📝 Попробуй ещё раз!'
        )

      } catch (error) {
        console.error('❌ Ошибка сохранения статистики:', error)
        showNotificationMessage('error', 'Ошибка при сохранении результатов')
      }

      // Показываем результаты
      currentPhase.value = 'results'

    } else {
      // Переходим к следующему вопросу
      currentQuestionIndex.value++
      selectedOption.value = null
      showResults.value = false
    }
  }
}

// Вернуться в меню
const goToMenu = () => {
  stopAllTimers()
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
  stopAllTimers()
})

// Загрузка статистики при монтировании
onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.text-comprehension {
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

/* Фаза чтения */
.reading-phase {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.timer {
  text-align: center;
  font-size: 18px;
  color: var(--color-primary, #88d424);
  font-weight: 500;
}

.text-card {
  background-color: var(--color-white-smoke, #f5f5f5);
  border-radius: 12px;
  padding: 2rem;
  border-left: 4px solid var(--color-primary, #88d424);
}

.text-title {
  font-size: 20px;
  color: var(--color-primary-dark, #6aa91c);
  margin: 0 0 1rem 0;
  text-align: center;
}

.text-content {
  color: var(--color-dark-gray, #616161);
  font-size: 16px;
  line-height: 1.8;
  margin: 0;
  text-align: justify;
}

.hint {
  color: var(--color-gray, #9e9e9e);
  font-size: 14px;
  text-align: center;
  font-style: italic;
}

/* Фаза вопросов */
.questions-phase {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.progress {
  text-align: center;
  color: var(--color-gray, #9e9e9e);
  font-size: 14px;
}

.question-card {
  background-color: var(--color-white-smoke, #f5f5f5);
  border-radius: 12px;
  padding: 2rem;
}

.question {
  font-size: 18px;
  color: var(--color-primary-dark, #6aa91c);
  margin: 0 0 1.5rem 0;
  line-height: 1.6;
}

.options {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 2rem;
}

.option {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background-color: var(--color-white, #ffffff);
  border: 1px solid var(--color-light-gray, #e0e0e0);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.option:hover:not(.correct):not(.incorrect) {
  border-color: var(--color-primary, #88d424);
  transform: translateX(5px);
}

.option.selected {
  border-color: var(--color-primary, #88d424);
  background-color: rgba(136, 212, 36, 0.05);
}

.option.correct {
  border-color: var(--color-success, #4caf50);
  background-color: rgba(76, 175, 80, 0.05);
}

.option.incorrect {
  border-color: var(--color-error, #f44336);
  background-color: rgba(244, 67, 54, 0.05);
}

.option-marker {
  font-weight: 600;
  color: var(--color-gray, #9e9e9e);
  min-width: 30px;
}

.option-text {
  flex: 1;
  color: var(--color-dark-gray, #616161);
}

.correct-mark {
  color: var(--color-success, #4caf50);
  font-size: 20px;
  font-weight: 600;
  margin-left: auto;
}

.incorrect-mark {
  color: var(--color-error, #f44336);
  font-size: 20px;
  font-weight: 600;
  margin-left: auto;
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

  .text-card {
    padding: 1rem;
  }

  .text-title {
    font-size: 18px;
  }

  .text-content {
    font-size: 14px;
  }

  .question-card {
    padding: 1rem;
  }

  .option {
    padding: 8px 12px;
  }

  .results-actions {
    flex-direction: column;
  }

  .results-actions button {
    width: 100%;
  }
}
</style>
