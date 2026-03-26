<template>
  <div class="chat-view">
    <div class="chat-header">
      <button class="back-button" @click="$emit('back')">
        ← Назад к контактам
      </button>

      <div class="contact-info" @click="goToContact">
        <Avatar
          :src="contact.photoUrl"
          :name="contact.displayName"
          size="small"
          :bg-color="getContactColor(contact.contactId)"
        />
        <div class="contact-details">
          <h2>{{ contact.displayName }}</h2>
          <p class="contact-subtitle">{{ contact.fullName }}</p>
        </div>
      </div>

      <button class="info-button" @click="showContactInfo = !showContactInfo" title="Информация о контакте">
        ℹ️
      </button>
    </div>

    <!-- Информация о контакте (раскрывающаяся) -->
    <div v-if="showContactInfo" class="contact-info-panel">
      <div class="info-row">
        <span class="info-label">📞 Телефон:</span>
        <span class="info-value">{{ contact.phone }}</span>
      </div>
      <div class="info-row">
        <span class="info-label">✉️ Email:</span>
        <span class="info-value">{{ contact.email }}</span>
      </div>
      <div class="info-row">
        <span class="info-label">📅 Добавлен:</span>
        <span class="info-value">{{ contact.formattedAddedAt }}</span>
      </div>
      <div class="info-row">
        <span class="info-label">⚙️ Статус:</span>
        <span class="info-value" :class="{ 'text-success': contact.isActive, 'text-error': !contact.isActive }">
          {{ contact.isActive ? 'Активен' : 'Неактивен' }}
        </span>
      </div>
      <div class="info-row" v-if="contact.nickname">
        <span class="info-label">✏️ Никнейм:</span>
        <span class="info-value">{{ contact.nickname }}</span>
      </div>
    </div>

    <!-- Панель шаблонов -->
    <MessageTemplates
      :templates="templates"
      @select="useTemplate"
      @manage="manageTemplates"
    />

    <!-- Область сообщений -->
    <div ref="messagesContainer" class="messages-container">
      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <p>Загрузка сообщений...</p>
      </div>

      <div v-else-if="messages.length === 0" class="empty-state">
        <div class="empty-icon">💬</div>
        <h3>Нет сообщений</h3>
        <p>Напишите первое сообщение</p>
      </div>

      <div v-else class="messages-list">
        <div
          v-for="message in messages"
          :key="message.id"
          class="message-wrapper"
        >
          <MessageBubble
            :message="message"
            @delete="deleteMessage"
          />
        </div>

        <!-- Индикатор загрузки предыдущих сообщений -->
        <div v-if="hasMore" class="load-more">
          <Button
            variant="text"
            size="small"
            @click="loadMoreMessages"
            :loading="loadingMore"
          >
            Загрузить ещё
          </Button>
        </div>
      </div>
    </div>

    <!-- Форма отправки сообщения -->
    <div class="message-form">
      <TextArea
        v-model="newMessage"
        placeholder="Напишите сообщение..."
        @keyup.enter.exact.prevent="sendMessage"
        @keyup.enter.shift.exact="addNewLine"
        :disabled="sending"
        :rows="1"
        class="message-input"
      />
      <Button
        variant="primary"
        @click="sendMessage"
        :disabled="!newMessage.trim() || sending"
        :loading="sending"
        class="send-button"
      >
        ➤
      </Button>
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
import { ref, onMounted, nextTick, watch } from 'vue'
import { useRouter } from 'vue-router'
import Button from '@/components/UI/Button.vue'
import TextArea from '@/components/UI/TextArea.vue'
import Notification from '@/components/UI/Notification.vue'
import Avatar from '@/components/UI/Avatar.vue'
import MessageBubble from './components/MessageBubble.vue'
import MessageTemplates from './components/MessageTemplates.vue'
import contactService from '@/services/contactService'

const props = defineProps({
  contact: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['back', 'update'])

const router = useRouter()
const messagesContainer = ref(null)
const messages = ref([])
const templates = ref([])
const loading = ref(true)
const loadingMore = ref(false)
const sending = ref(false)
const newMessage = ref('')
const currentPage = ref(0)
const hasMore = ref(false)
const showContactInfo = ref(false)
const showNotification = ref(false)
const notificationMessage = ref('')
const notificationType = ref('success')

// Загрузка сообщений
const loadMessages = async (page = 0) => {
  try {
    const response = await contactService.getConversation(props.contact.contactId, page, 20)

    if (page === 0) {
      messages.value = response.content
    } else {
      messages.value = [...messages.value, ...response.content]
    }

    hasMore.value = !response.last
    currentPage.value = page

    // Отмечаем как прочитанные
    if (page === 0) {
      await contactService.markAsRead(props.contact.contactId)
    }

    // Скроллим вниз
    await nextTick()
    scrollToBottom()
  } catch (error) {
    console.error('❌ Ошибка загрузки сообщений:', error)
    showNotificationMessage('error', 'Ошибка загрузки сообщений')
  } finally {
    loading.value = false
    loadingMore.value = false
  }
}

// Загрузка шаблонов
const loadTemplates = async () => {
  try {
    templates.value = await contactService.getTemplates()
  } catch (error) {
    console.error('❌ Ошибка загрузки шаблонов:', error)
  }
}

// Загрузка следующих сообщений
const loadMoreMessages = async () => {
  loadingMore.value = true
  await loadMessages(currentPage.value + 1)
}

// Отправка сообщения
const sendMessage = async () => {
  if (!newMessage.value.trim() || sending.value) return

  sending.value = true
  const messageText = newMessage.value.trim()

  try {
    const sentMessage = await contactService.sendMessage({
      receiverId: props.contact.contactId,
      content: messageText
    })

    messages.value.unshift(sentMessage)
    newMessage.value = ''

    await nextTick()
    scrollToBottom()

  } catch (error) {
    console.error('❌ Ошибка отправки сообщения:', error)
    showNotificationMessage('error', 'Ошибка отправки сообщения')
  } finally {
    sending.value = false
  }
}

// Использование шаблона
const useTemplate = (template) => {
  newMessage.value = template.content
}

// Управление шаблонами
const manageTemplates = () => {
  showNotificationMessage('info', 'Управление шаблонами будет доступно позже')
}

// Удаление сообщения
const deleteMessage = async (message) => {
  if (!confirm('Удалить это сообщение?')) return

  try {
    await contactService.deleteMessage(message.id)
    messages.value = messages.value.filter(m => m.id !== message.id)
    showNotificationMessage('success', 'Сообщение удалено')
  } catch (error) {
    console.error('❌ Ошибка удаления сообщения:', error)
    showNotificationMessage('error', 'Ошибка удаления сообщения')
  }
}

// Переход на страницу контакта
const goToContact = () => {
  showContactInfo.value = !showContactInfo.value
}

// Добавление новой строки (Shift+Enter)
const addNewLine = () => {
  newMessage.value += '\n'
}

// Скролл вниз
const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

// Получение иконки контакта
const getContactIcon = (contact) => {
  return contactService.constructor.getContactIcon(contact)
}

// Получение цвета контакта
const getContactColor = (contactId) => {
  return contactService.constructor.getContactColor(contactId)
}

// Показать уведомление
const showNotificationMessage = (type, message) => {
  notificationType.value = type
  notificationMessage.value = message
  showNotification.value = true
}

// Обновление при смене контакта
watch(() => props.contact, () => {
  loading.value = true
  messages.value = []
  currentPage.value = 0
  loadMessages()
})

onMounted(() => {
  loadMessages()
  loadTemplates()
})
</script>

<style scoped>
.chat-view {
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
  background: var(--color-white, #ffffff);
  border-radius: 16px;
  box-shadow: 0 4px 20px var(--shadow-color, rgba(0,0,0,0.1));
  display: flex;
  flex-direction: column;
  height: 80vh;
  max-height: 800px;
}

/* Шапка */
.chat-header {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 1rem 1.5rem;
  border-bottom: 2px solid var(--color-primary, #88d424);
  flex-shrink: 0;
}

.back-button {
  background: none;
  border: none;
  color: var(--color-primary, #88d424);
  font-size: 16px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 8px;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.back-button:hover {
  background-color: rgba(136, 212, 36, 0.1);
  transform: translateX(-3px);
}

.contact-info {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 8px;
  transition: background-color 0.2s ease;
}

.contact-info:hover {
  background-color: var(--color-white-smoke, #f5f5f5);
}

.contact-details h2 {
  font-size: 18px;
  color: var(--color-primary-dark, #6aa91c);
  margin: 0;
}

.contact-subtitle {
  font-size: 13px;
  color: var(--color-gray, #9e9e9e);
  margin: 0;
}

.info-button {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  padding: 8px;
  border-radius: 50%;
  transition: all 0.2s ease;
  opacity: 0.6;
}

.info-button:hover {
  opacity: 1;
  background-color: var(--color-white-smoke, #f5f5f5);
}

/* Информационная панель */
.contact-info-panel {
  background-color: var(--color-white-smoke, #f5f5f5);
  padding: 1rem 1.5rem;
  border-bottom: 1px solid var(--color-light-gray, #e0e0e0);
  font-size: 14px;
}

.info-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 4px 0;
}

.info-label {
  min-width: 100px;
  color: var(--color-gray, #9e9e9e);
}

.info-value {
  color: var(--color-dark-gray, #616161);
  word-break: break-word;
}

.text-success {
  color: var(--color-success, #4caf50);
}

.text-error {
  color: var(--color-error, #f44336);
}

/* Область сообщений */
.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 1.5rem;
  background-color: var(--color-white-smoke, #f5f5f5);
}

.messages-list {
  display: flex;
  flex-direction: column-reverse;
  gap: 4px;
}

.message-wrapper {
  width: 100%;
}

.load-more {
  display: flex;
  justify-content: center;
  padding: 10px;
}

/* Пустое состояние */
.empty-state {
  text-align: center;
  padding: 3rem;
  color: var(--color-gray, #9e9e9e);
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 1rem;
  opacity: 0.5;
}

.empty-state h3 {
  font-size: 18px;
  margin-bottom: 0.5rem;
}

.empty-state p {
  font-size: 14px;
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

/* Форма отправки */
.message-form {
  display: flex;
  gap: 12px;
  padding: 1rem 1.5rem;
  border-top: 1px solid var(--color-light-gray, #e0e0e0);
  background-color: var(--color-white, #ffffff);
  flex-shrink: 0;
}

.message-input {
  flex: 1;
  margin-bottom: 0;
}

.message-input :deep(textarea) {
  resize: none;
  min-height: 40px;
  max-height: 120px;
}

.send-button {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  padding: 0;
  font-size: 20px;
  flex-shrink: 0;
}

@media (max-width: 768px) {
  .chat-view {
    height: 100vh;
    max-height: none;
    border-radius: 0;
  }

  .chat-header {
    padding: 0.75rem 1rem;
  }

  .back-button {
    padding: 4px 8px;
  }

  .contact-details h2 {
    font-size: 16px;
  }

  .info-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }

  .info-label {
    min-width: auto;
  }

  .message-form {
    padding: 0.75rem 1rem;
  }

  .send-button {
    width: 44px;
    height: 44px;
  }
}
</style>
