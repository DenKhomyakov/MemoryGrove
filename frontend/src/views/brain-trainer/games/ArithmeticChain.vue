<template>
  <div class="arithmetic-chain">
    <div class="game-header">
      <button class="back-button" @click="$emit('back')">
        ← Назад к списку
      </button>
      <h2>🧮 Арифметическая цепочка</h2>
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
          <span class="stat-label">Макс. длина</span>
        </div>
        <div class="stat-item" v-if="stats.bestTimeSeconds">
          <span class="stat-value">{{ formatTime(stats.bestTimeSeconds) }}</span>
          <span class="stat-label">Лучшее время</span>
        </div>
      </div>
    </div>

    <!-- Основная игра -->
    <div class="game-area">
      <!-- Настройки сложности -->
      <div class="difficulty-selector" v-if="!isPlaying">
        <label class="difficulty-label">Сложность (длина цепочки):</label>
        <div class="difficulty-buttons">
          <button
            v-for="len in [1, 2, 3, 4, 5]"
            :key="len"
            class="difficulty-button"
            :class="{ active: selectedLength === len }"
            @click="selectedLength = len"
          >
            {{ len }}
          </button>
        </div>
        <Button
          variant="primary"
          @click="startGame"
          :loading="generating"
        >
          Начать
        </Button>
      </div>

      <!-- Игровой процесс -->
      <div v-else class="game-play">
        <div class="expression-display">
          {{ currentChain.expression }} = ?
        </div>

        <div class="timer" v-if="timeSeconds > 0">
          ⏱️ {{ formatTime(timeSeconds) }}
        </div>

        <div class="answer-area">
          <TextField
            v-model="userAnswer"
            label="Ваш ответ"
            type="number"
            placeholder="Введите результат"
            @keyup.enter="submitAnswer"
            :disabled="answered"
            clearable
          />

          <Button
            variant="primary"
            @click="submitAnswer"
            :disabled="!userAnswer || answered"
            :loading="checking"
          >
            Проверить
          </Button>
        </div>

        <!-- Результат -->
        <div v-if="showResult" class="result" :class="{ success: isCorrect, error: !isCorrect }">
          <div class="result-icon">{{ isCorrect ? '✅' : '❌' }}</div>
          <div class="result-message">
            <span v-if="isCorrect">Правильно! Молодец!</span>
            <span v-else>Неправильно. Правильный ответ: {{ currentChain.correctResult }}</span>
          </div>
        </div>

        <!-- Кнопка "Ещё" -->
        <Button
          v-if="answered"
          variant="outline"
          @click="startGame"
          class="again-button"
        >
          Ещё цепочку
        </Button>
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
import { ref, onMounted, onUnmounted } from 'vue'
import TextField from '@/components/UI/TextField.vue'
import Button from '@/components/UI/Button.vue'
import Notification from '@/components/UI/Notification.vue'
import brainTrainerService from '@/services/brainTrainerService'

const emit = defineEmits(['back'])

// Состояния
const stats = ref(null)
const generating = ref(false)
const checking = ref(false)
const isPlaying = ref(false)
const currentChain = ref(null)
const userAnswer = ref('')
const answered = ref(false)
const isCorrect = ref(false)
const showResult = ref(false)
const selectedLength = ref(3)
const timeSeconds = ref(0)
const timerInterval = ref(null)
const showNotification = ref(false)
const notificationMessage = ref('')
const notificationType = ref('success')

// Загрузка статистики
const loadStats = async () => {
  try {
    stats.value = await brainTrainerService.getStatsByType('ARITHMETIC')
    console.log('📊 Статистика загружена:', stats.value)
  } catch (error) {
    console.error('❌ Ошибка загрузки статистики:', error)
  }
}

// Форматирование времени
const formatTime = (seconds) => {
  return brainTrainerService.constructor.formatTime(seconds)
}

// Запуск таймера
const startTimer = () => {
  timeSeconds.value = 0
  timerInterval.value = setInterval(() => {
    timeSeconds.value++
  }, 1000)
}

// Остановка таймера
const stopTimer = () => {
  if (timerInterval.value) {
    clearInterval(timerInterval.value)
    timerInterval.value = null
  }
}

// Начало игры
const startGame = async () => {
  generating.value = true
  answered.value = false
  showResult.value = false
  userAnswer.value = ''
  stopTimer()

  try {
    currentChain.value = await brainTrainerService.generateArithmeticChain(selectedLength.value)
    isPlaying.value = true
    startTimer()
    console.log('🎮 Новая цепочка:', currentChain.value)
  } catch (error) {
    showNotificationMessage('error', 'Ошибка при генерации цепочки')
  } finally {
    generating.value = false
  }
}

// Отправка ответа
const submitAnswer = async () => {
  if (!userAnswer.value || answered.value) return

  checking.value = true
  stopTimer()

  try {
    const result = await brainTrainerService.checkArithmeticAnswer(
      currentChain.value.correctResult,
      {
        result: parseInt(userAnswer.value),
        timeSeconds: timeSeconds.value,
        chainLength: currentChain.value.chainLength
      }
    )

    isCorrect.value = result
    answered.value = true
    showResult.value = true

    // Обновляем статистику
    await loadStats()

    if (result) {
      showNotificationMessage('success', '✅ Правильно! Так держать!')
    } else {
      showNotificationMessage('error', '❌ Попробуй ещё раз!')
    }

  } catch (error) {
    console.error('❌ Ошибка проверки:', error)
    showNotificationMessage('error', 'Ошибка при проверке ответа')
  } finally {
    checking.value = false
  }
}

// Показать уведомление
const showNotificationMessage = (type, message) => {
  notificationType.value = type
  notificationMessage.value = message
  showNotification.value = true
}

// Очистка таймера при размонтировании
onUnmounted(() => {
  stopTimer()
})

// Загрузка статистики при монтировании
onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.arithmetic-chain {
  width: 100%;
  max-width: 600px;
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

/* Выбор сложности */
.difficulty-selector {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
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
  width: 50px;
  height: 50px;
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
.game-play {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.expression-display {
  font-size: 48px;
  font-weight: 600;
  color: var(--color-primary-dark, #6aa91c);
  text-align: center;
  padding: 2rem;
  background-color: var(--color-white-smoke, #f5f5f5);
  border-radius: 12px;
  font-family: monospace;
  word-break: break-word;
}

.timer {
  text-align: center;
  font-size: 24px;
  color: var(--color-gray, #9e9e9e);
  font-family: monospace;
}

.answer-area {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.answer-area :deep(.text-field-container) {
  flex: 1;
  margin-bottom: 0;
}

/* Результат */
.result {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 1.5rem;
  border-radius: 8px;
  animation: slideIn 0.3s ease;
}

.result.success {
  background-color: rgba(76, 175, 80, 0.1);
  border: 1px solid var(--color-success, #4caf50);
}

.result.error {
  background-color: rgba(244, 67, 54, 0.1);
  border: 1px solid var(--color-error, #f44336);
}

.result-icon {
  font-size: 32px;
}

.result-message {
  font-size: 16px;
  color: var(--color-dark-gray, #616161);
}

.again-button {
  margin-top: 1rem;
  width: 100%;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 768px) {
  .game-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .expression-display {
    font-size: 32px;
    padding: 1rem;
  }

  .answer-area {
    flex-direction: column;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
