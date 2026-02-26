<template>
  <div class="telegram-binding">
    <div class="binding-header">
      <span class="binding-icon">📱</span>
      <h3>Telegram уведомления</h3>
    </div>

    <!-- Загрузка -->
    <div v-if="loading" class="loading-small">
      <div class="spinner-small"></div>
      <span>Загрузка...</span>
    </div>

    <!-- Привязан -->
    <div v-else-if="isBound" class="bound-info">
      <div class="success-badge">
        ✅ Привязан
      </div>

      <div class="telegram-details">
        <div v-if="bindingInfo.telegramUsername" class="detail-item">
          <span class="detail-label">Username:</span>
          <span class="detail-value">@{{ bindingInfo.telegramUsername }}</span>
        </div>

        <div class="detail-item">
          <span class="detail-label">Имя:</span>
          <span class="detail-value">{{ bindingInfo.firstName }} {{ bindingInfo.lastName }}</span>
        </div>

        <div class="detail-item">
          <span class="detail-label">Привязан:</span>
          <span class="detail-value">{{ formatDate(bindingInfo.boundAt) }}</span>
        </div>

        <div class="detail-item toggle-item">
          <span class="detail-label">Уведомления:</span>
          <label class="toggle-switch">
            <input
              type="checkbox"
              v-model="notificationsEnabled"
              @change="toggleNotifications"
            >
            <span class="toggle-slider"></span>
          </label>
        </div>
      </div>

      <Button
        variant="error"
        size="small"
        @click="unbindTelegram"
        :loading="unbinding"
      >
        Отключить Telegram
      </Button>
    </div>

    <!-- Не привязан -->
    <div v-else class="unbound-info">
      <p class="description">
        Привяжите Telegram, чтобы получать уведомления о напоминаниях прямо в мессенджер
      </p>

      <!-- Состояние генерации кода -->
      <div v-if="generatingCode" class="code-display">
        <div class="code-box">
          <span class="code">{{ bindingCode }}</span>
          <button
            class="copy-button"
            @click="copyCode"
            title="Копировать код"
          >
            📋
          </button>
        </div>

        <div class="code-instructions">
          <p>1️⃣ Скопируйте этот код</p>
          <p>2️⃣ Откройте Telegram бота <strong>@MemoryGroveBot</strong></p>
          <p>3️⃣ Отправьте код боту</p>
          <p class="code-hint">Код действителен 10 минут</p>
        </div>

        <div class="code-actions">
          <Button
            variant="outline"
            size="small"
            @click="generateNewCode"
            :disabled="generating"
          >
            Сгенерировать новый код
          </Button>
        </div>
      </div>

      <!-- Кнопка для генерации кода -->
      <div v-else>
        <Button
          variant="primary"
          @click="generateCode"
          :loading="generating"
        >
          Привязать Telegram
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
import { ref, onMounted } from 'vue'
import Button from '@/components/UI/Button.vue'
import Notification from '@/components/UI/Notification.vue'
import apiClient from '@/services/api'

// Состояния
const loading = ref(true)
const generating = ref(false)
const unbinding = ref(false)
const isBound = ref(false)
const bindingInfo = ref({})
const bindingCode = ref('')
const generatingCode = ref(false) // Показываем ли код
const notificationsEnabled = ref(false)
const showNotification = ref(false)
const notificationMessage = ref('')
const notificationType = ref('success')

// Загрузка статуса привязки
const loadBindingStatus = async () => {
  loading.value = true
  try {
    const response = await apiClient.get('/telegram/binding-status')
    console.log('📥 Статус привязки:', response.data)
    bindingInfo.value = response.data
    isBound.value = response.data.bound
    notificationsEnabled.value = response.data.notificationsEnabled || false
  } catch (error) {
    console.error('❌ Ошибка загрузки статуса:', error)
    showNotificationMessage('error', 'Ошибка загрузки статуса привязки')
  } finally {
    loading.value = false
  }
}

// Генерация кода привязки
const generateCode = async () => {
  generating.value = true
  try {
    const response = await apiClient.post('/telegram/generate-code')
    console.log('✅ Ответ от сервера:', response.data)

    if (response.data && response.data.code) {
      bindingCode.value = response.data.code
      generatingCode.value = true
      showNotificationMessage('success', 'Код сгенерирован! Скопируйте его и отправьте боту.')
    } else {
      throw new Error('Неверный формат ответа от сервера')
    }
  } catch (error) {
    console.error('❌ Ошибка генерации кода:', error)
    showNotificationMessage('error', 'Ошибка при генерации кода')
  } finally {
    generating.value = false
  }
}

// Генерация нового кода
const generateNewCode = async () => {
  bindingCode.value = ''
  generatingCode.value = false
  await generateCode()
}

// Копирование кода в буфер обмена
const copyCode = () => {
  navigator.clipboard.writeText(bindingCode.value)
  showNotificationMessage('success', 'Код скопирован!')
}

// Отключение Telegram
const unbindTelegram = async () => {
  if (!confirm('Вы уверены, что хотите отключить Telegram?')) {
    return
  }

  unbinding.value = true
  try {
    await apiClient.delete('/telegram/unbind')
    showNotificationMessage('success', 'Telegram отключён')

    // Сбрасываем состояния
    bindingCode.value = ''
    generatingCode.value = false
    await loadBindingStatus()
  } catch (error) {
    console.error('❌ Ошибка отключения:', error)
    showNotificationMessage('error', 'Ошибка при отключении')
  } finally {
    unbinding.value = false
  }
}

// Переключение уведомлений
const toggleNotifications = async () => {
  // TODO: добавить эндпоинт для переключения уведомлений
  showNotificationMessage('info', 'Функция в разработке')
  notificationsEnabled.value = !notificationsEnabled.value
}

// Форматирование даты
const formatDate = (dateString) => {
  if (!dateString) return ''
  try {
    const date = new Date(dateString)
    return new Intl.DateTimeFormat('ru-RU', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    }).format(date)
  } catch (e) {
    return dateString
  }
}

// Показать уведомление
const showNotificationMessage = (type, message) => {
  notificationType.value = type
  notificationMessage.value = message
  showNotification.value = true
}

// При монтировании загружаем статус
onMounted(() => {
  loadBindingStatus()
})
</script>

<style scoped>
.telegram-binding {
  background: var(--color-white, #ffffff);
  border-radius: 12px;
  padding: 1.5rem;
  border: 1px solid var(--color-light-gray, #e0e0e0);
}

.binding-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 1.5rem;
}

.binding-icon {
  font-size: 24px;
}

.binding-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-primary-dark, #6aa91c);
  margin: 0;
}

/* Загрузка */
.loading-small {
  display: flex;
  align-items: center;
  gap: 10px;
  color: var(--color-gray, #9e9e9e);
  font-size: 14px;
  padding: 1rem 0;
}

.spinner-small {
  width: 20px;
  height: 20px;
  border: 2px solid var(--color-light-gray, #e0e0e0);
  border-top-color: var(--color-primary, #88d424);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* Привязан */
.bound-info {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.success-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background-color: rgba(76, 175, 80, 0.1);
  color: var(--color-success, #4caf50);
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  width: fit-content;
}

.telegram-details {
  background-color: var(--color-white-smoke, #f5f5f5);
  border-radius: 8px;
  padding: 1rem;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.detail-label {
  min-width: 100px;
  color: var(--color-gray, #9e9e9e);
  font-size: 14px;
}

.detail-value {
  color: var(--color-dark-gray, #616161);
  font-size: 14px;
  font-weight: 500;
}

.toggle-item {
  align-items: center;
}

/* Toggle Switch */
.toggle-switch {
  position: relative;
  display: inline-block;
  width: 50px;
  height: 24px;
}

.toggle-switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.toggle-slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: var(--color-light-gray, #e0e0e0);
  transition: 0.3s;
  border-radius: 24px;
}

.toggle-slider:before {
  position: absolute;
  content: "";
  height: 20px;
  width: 20px;
  left: 2px;
  bottom: 2px;
  background-color: white;
  transition: 0.3s;
  border-radius: 50%;
}

input:checked + .toggle-slider {
  background-color: var(--color-primary, #88d424);
}

input:checked + .toggle-slider:before {
  transform: translateX(26px);
}

/* Не привязан */
.unbound-info {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.description {
  color: var(--color-gray, #9e9e9e);
  font-size: 14px;
  line-height: 1.6;
  margin: 0;
}

/* Код привязки */
.code-display {
  background-color: var(--color-white-smoke, #f5f5f5);
  border-radius: 12px;
  padding: 1.5rem;
}

.code-box {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 1.5rem;
}

.code {
  font-family: monospace;
  font-size: 32px;
  font-weight: 600;
  letter-spacing: 4px;
  color: var(--color-primary, #88d424);
  background-color: var(--color-white, #ffffff);
  padding: 12px 24px;
  border-radius: 8px;
  border: 2px solid var(--color-primary, #88d424);
}

.copy-button {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  padding: 8px;
  border-radius: 8px;
  transition: all 0.2s ease;
  opacity: 0.6;
}

.copy-button:hover {
  opacity: 1;
  background-color: var(--color-white-smoke, #f5f5f5);
  transform: scale(1.1);
}

.code-instructions {
  margin-bottom: 1.5rem;
}

.code-instructions p {
  color: var(--color-dark-gray, #616161);
  font-size: 14px;
  margin: 8px 0;
  padding-left: 20px;
  position: relative;
}

.code-instructions p::before {
  content: "•";
  color: var(--color-primary, #88d424);
  position: absolute;
  left: 4px;
}

.code-hint {
  color: var(--color-gray, #9e9e9e) !important;
  font-style: italic;
  margin-top: 12px !important;
}

.code-actions {
  display: flex;
  justify-content: center;
}
</style>
