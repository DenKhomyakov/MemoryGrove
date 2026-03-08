<template>
  <div class="poem-memorization">
    <div class="game-header">
      <button class="back-button" @click="$emit('back')">
        ← Назад к списку
      </button>
      <h2>📝 Заучивание стихов</h2>
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
        <div class="start-icon">📝</div>
        <p class="start-description">
          Сейчас вы увидите небольшое стихотворение.
          У вас будет время, чтобы его запомнить.
          Затем нужно будет воспроизвести его по памяти.
        </p>
        <Button
          variant="primary"
          @click="startGame"
          :loading="loading"
        >
          Начать упражнение
        </Button>
      </div>

      <!-- Заучивание -->
      <div v-else-if="currentPhase === 'memorizing'" class="memorizing-phase">
        <div class="timer-header">
          <div class="timer" v-if="timeRemaining > 0">
            ⏱️ Осталось времени: {{ timeRemaining }} сек
          </div>
          <Button
            variant="text"
            size="small"
            @click="finishMemorizing"
            class="skip-button"
          >
            Я запомнил(а), перейти к написанию →
          </Button>
        </div>

        <div class="poem-card">
          <div class="poem-author">{{ currentPoem.author }}</div>
          <h3 class="poem-title">{{ currentPoem.title }}</h3>
          <pre class="poem-text">{{ currentPoem.text }}</pre>
        </div>

        <div class="memorizing-hint">
          <span class="hint-icon">💡</span>
          <span class="hint-text">Прочитайте стихотворение несколько раз. Попробуйте представить его как картинку.</span>
        </div>
      </div>

      <!-- Написание -->
      <div v-else-if="currentPhase === 'writing'" class="writing-phase">
        <div class="timer">
          ⏱️ {{ formatTime(timeSeconds) }}
        </div>

        <div class="writing-card">
          <div class="poem-info">
            <span class="poem-author">{{ currentPoem.author }}</span>
            <span class="poem-title-small">«{{ currentPoem.title }}»</span>
          </div>

          <TextArea
            v-model="userText"
            label="Напишите стихотворение по памяти"
            placeholder="Введите текст стихотворения..."
            :error="errors.userText"
            rows="8"
          />

          <div class="writing-actions">
            <Button
              variant="outline"
              @click="showOriginal"
            >
              👀 Показать оригинал
            </Button>
            <Button
              variant="primary"
              @click="checkPoem"
              :disabled="!userText.trim()"
              :loading="checking"
            >
              Проверить
            </Button>
          </div>
        </div>

        <!-- Подсказка (оригинал) -->
        <div v-if="showOriginalPoem" class="original-poem">
          <div class="original-header">
            <span>Оригинал</span>
            <button class="close-original" @click="showOriginalPoem = false">✕</button>
          </div>
          <pre class="original-text">{{ currentPoem.text }}</pre>
        </div>
      </div>

      <!-- Результаты -->
      <div v-else-if="currentPhase === 'results'" class="results-phase">
        <div class="results-icon" :class="{
          'success': similarity >= 80,
          'warning': similarity >= 50 && similarity < 80,
          'error': similarity < 50
        }">
          {{ similarity >= 80 ? '🎉' : (similarity >= 50 ? '📝' : '😔') }}
        </div>

        <h3 class="results-title">
          {{ similarity >= 80 ? 'Отличный результат!' : (similarity >= 50 ? 'Неплохо, но можно точнее' : 'Попробуй ещё раз') }}
        </h3>

        <div class="results-stats">
          <div class="result-item">
            <span class="result-label">Совпадение с оригиналом:</span>
            <span class="result-value">{{ similarity }}%</span>
          </div>
          <div class="result-item">
            <span class="result-label">Время:</span>
            <span class="result-value">{{ formatTime(timeSeconds) }}</span>
          </div>
          <div class="result-item">
            <span class="result-label">Время на заучивание:</span>
            <span class="result-value">{{ formatTime(memorizingTime) }}</span>
          </div>
        </div>

        <!-- Сравнение текстов -->
        <div class="comparison">
          <div class="comparison-column">
            <h4>Ваш текст</h4>
            <pre class="comparison-text user-text">{{ userText }}</pre>
          </div>
          <div class="comparison-column">
            <h4>Оригинал</h4>
            <pre class="comparison-text original-text">{{ currentPoem.text }}</pre>
          </div>
        </div>

        <div class="results-actions">
          <Button
            variant="primary"
            @click="startGame"
          >
            Ещё стих
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
import TextArea from '@/components/UI/TextArea.vue'
import Notification from '@/components/UI/Notification.vue'
import brainTrainerService from '@/services/brainTrainerService'
import { getRandomPoem } from '../data/poems'

const emit = defineEmits(['back'])

// Состояния
const stats = ref(null)
const loading = ref(false)
const checking = ref(false)
const currentPhase = ref('start') // start, memorizing, writing, results
const currentPoem = ref(null)
const userText = ref('')
const timeSeconds = ref(0)
const memorizingTime = ref(0)
const timeRemaining = ref(60) // 60 секунд на заучивание
const memorizingTimer = ref(null)
const gameTimer = ref(null)
const showOriginalPoem = ref(false)
const similarity = ref(0)
const showNotification = ref(false)
const notificationMessage = ref('')
const notificationType = ref('success')
const errors = ref({
  userText: ''
})

// Загрузка статистики
const loadStats = async () => {
  try {
    stats.value = await brainTrainerService.getStatsByType('POEM')
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
  if (memorizingTimer.value) {
    clearInterval(memorizingTimer.value)
    memorizingTimer.value = null
  }
  if (gameTimer.value) {
    clearInterval(gameTimer.value)
    gameTimer.value = null
  }
}

// Запуск таймера на заучивание
const startMemorizingTimer = () => {
  timeRemaining.value = 60
  memorizingTime.value = 0
  memorizingTimer.value = setInterval(() => {
    if (timeRemaining.value > 0) {
      timeRemaining.value--
      memorizingTime.value++
    } else {
      // Время вышло, автоматически переходим к написанию
      clearInterval(memorizingTimer.value)
      memorizingTimer.value = null
      finishMemorizing()
    }
  }, 1000)
}

// Запуск таймера игры
const startGameTimer = () => {
  gameTimer.value = setInterval(() => {
    timeSeconds.value++
  }, 1000)
}

// Начало игры
const startGame = () => {
  stopAllTimers()
  loading.value = false

  try {
    currentPoem.value = getRandomPoem()
    userText.value = ''
    timeSeconds.value = 0
    memorizingTime.value = 0
    timeRemaining.value = 60
    showOriginalPoem.value = false
    similarity.value = 0
    currentPhase.value = 'memorizing'

    startMemorizingTimer()
    startGameTimer()
    console.log('📖 Новый стих:', currentPoem.value.title)
  } catch (error) {
    console.error('❌ Ошибка начала игры:', error)
    showNotificationMessage('error', 'Ошибка при загрузке стиха')
  }
}

// Завершение заучивания (раньше времени или по таймеру)
const finishMemorizing = () => {
  clearInterval(memorizingTimer.value)
  memorizingTimer.value = null
  currentPhase.value = 'writing'
}

// Показать оригинал
const showOriginal = () => {
  showOriginalPoem.value = true
}

// Вычисление схожести текстов (простое сравнение)
const calculateSimilarity = (text1, text2) => {
  const normalize = (str) => {
    return str
      .toLowerCase()
      .replace(/[^\wа-яё\s]/gi, '') // убираем знаки препинания
      .replace(/\s+/g, ' ') // множественные пробелы
      .trim()
  }

  const normalized1 = normalize(text1)
  const normalized2 = normalize(text2)

  if (normalized1 === normalized2) return 100

  // Разбиваем на слова
  const words1 = normalized1.split(' ')
  const words2 = normalized2.split(' ')

  // Считаем совпадающие слова
  let matches = 0
  const maxLength = Math.max(words1.length, words2.length)

  words1.forEach((word, index) => {
    if (words2[index] === word) {
      matches++
    }
  })

  return Math.round((matches / maxLength) * 100)
}

// Проверка стиха
const checkPoem = async () => {
  if (!userText.value.trim()) {
    errors.value.userText = 'Введите текст стихотворения'
    return
  }

  checking.value = true
  stopAllTimers()

  try {
    // Вычисляем схожесть
    const sim = calculateSimilarity(userText.value, currentPoem.value.text)
    similarity.value = sim

    // Считаем упражнение успешным при схожести 80% и выше
    const isSuccess = sim >= 80

    // Отправляем статистику на бэкенд
    await brainTrainerService.submitPoemResult({
      correct: isSuccess ? 1 : 0,
      similarity: sim,
      timeSeconds: timeSeconds.value,
      memorizingTime: memorizingTime.value
    })

    // Обновляем статистику
    await loadStats()

    showNotificationMessage(
      isSuccess ? 'success' : 'info',
      isSuccess ? '🎉 Отлично! Стих запомнился!' : '📝 Попробуй ещё раз, обращая внимание на слова'
    )

    currentPhase.value = 'results'

  } catch (error) {
    console.error('❌ Ошибка проверки:', error)
    showNotificationMessage('error', 'Ошибка при проверке')
  } finally {
    checking.value = false
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
.poem-memorization {
  width: 100%;
  max-width: 800px;
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

/* Фаза заучивания */
.memorizing-phase {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.timer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 15px;
}

.timer {
  font-size: 18px;
  color: var(--color-primary, #88d424);
  font-weight: 500;
}

.skip-button {
  color: var(--color-gray, #9e9e9e);
}

.skip-button:hover {
  color: var(--color-primary, #88d424);
}

.poem-card {
  background-color: var(--color-white-smoke, #f5f5f5);
  border-radius: 12px;
  padding: 2rem;
  border-left: 4px solid var(--color-primary, #88d424);
}

.poem-author {
  color: var(--color-primary, #88d424);
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 8px;
}

.poem-title {
  font-size: 20px;
  color: var(--color-primary-dark, #6aa91c);
  margin: 0 0 1rem 0;
  text-align: center;
}

.poem-text {
  font-family: 'Georgia', serif;
  font-size: 16px;
  line-height: 1.8;
  color: var(--color-dark-gray, #616161);
  white-space: pre-wrap;
  text-align: center;
  margin: 0;
}

.memorizing-hint {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 1rem;
  background-color: rgba(136, 212, 36, 0.1);
  border-radius: 8px;
  color: var(--color-primary-dark, #6aa91c);
}

.hint-icon {
  font-size: 20px;
}

.hint-text {
  font-size: 14px;
  font-style: italic;
}

/* Фаза написания */
.writing-phase {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.writing-card {
  background-color: var(--color-white-smoke, #f5f5f5);
  border-radius: 12px;
  padding: 2rem;
}

.poem-info {
  display: flex;
  gap: 15px;
  align-items: center;
  margin-bottom: 1.5rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid var(--color-light-gray, #e0e0e0);
}

.poem-author {
  color: var(--color-primary, #88d424);
  font-weight: 500;
  font-size: 14px;
  background-color: rgba(136, 212, 36, 0.1);
  padding: 4px 12px;
  border-radius: 20px;
}

.poem-title-small {
  color: var(--color-dark-gray, #616161);
  font-size: 16px;
}

.writing-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 1.5rem;
}

/* Оригинал (подсказка) */
.original-poem {
  background-color: var(--color-white, #ffffff);
  border: 2px solid var(--color-primary, #88d424);
  border-radius: 12px;
  padding: 1.5rem;
  animation: slideUp 0.3s ease;
}

.original-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  color: var(--color-primary-dark, #6aa91c);
  font-weight: 500;
}

.close-original {
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
  color: var(--color-gray, #9e9e9e);
  padding: 4px 8px;
  border-radius: 4px;
  transition: all 0.2s ease;
}

.close-original:hover {
  color: var(--color-error, #f44336);
  background-color: rgba(244, 67, 54, 0.1);
}

.original-text {
  font-family: 'Georgia', serif;
  font-size: 15px;
  line-height: 1.8;
  color: var(--color-dark-gray, #616161);
  white-space: pre-wrap;
  margin: 0;
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
  max-width: 350px;
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

/* Сравнение текстов */
.comparison {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  text-align: left;
}

.comparison-column h4 {
  color: var(--color-primary-dark, #6aa91c);
  font-size: 16px;
  margin: 0 0 1rem 0;
  text-align: center;
}

.comparison-text {
  background-color: var(--color-white-smoke, #f5f5f5);
  border-radius: 8px;
  padding: 1rem;
  font-family: 'Georgia', serif;
  font-size: 14px;
  line-height: 1.8;
  white-space: pre-wrap;
  max-height: 200px;
  overflow-y: auto;
  margin: 0;
}

.comparison-text.user-text {
  border-left: 3px solid var(--color-primary, #88d424);
}

.comparison-text.original-text {
  border-left: 3px solid var(--color-gray, #9e9e9e);
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

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
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

  .timer-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .poem-card {
    padding: 1rem;
  }

  .poem-title {
    font-size: 18px;
  }

  .poem-text {
    font-size: 14px;
  }

  .writing-actions {
    flex-direction: column;
  }

  .writing-actions button {
    width: 100%;
  }

  .comparison {
    grid-template-columns: 1fr;
    gap: 10px;
  }

  .results-actions {
    flex-direction: column;
  }

  .results-actions button {
    width: 100%;
  }
}
</style>
