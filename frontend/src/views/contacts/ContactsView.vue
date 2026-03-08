<template>
  <div class="contacts-page">
    <div class="page-header">
      <div class="header-content">
        <h1>🤝 Восстановление связей</h1>
        <p class="subtitle">Доверенные люди, с которыми вы можете общаться</p>
      </div>

      <div class="header-actions">
        <Button
          variant="primary"
          @click="createNewContact"
        >
          + Добавить контакт
        </Button>
        <Button
          variant="outline"
          @click="goBack"
        >
          ← Назад к модулям
        </Button>
      </div>
    </div>

    <!-- Статистика -->
    <div v-if="stats.totalContacts > 0" class="stats-section">
      <div class="stat-card">
        <span class="stat-value">{{ stats.totalContacts }}</span>
        <span class="stat-label">Всего контактов</span>
      </div>
      <div class="stat-card">
        <span class="stat-value">{{ stats.activeContacts }}</span>
        <span class="stat-label">Активных</span>
      </div>
      <div class="stat-card" @click="showOnlyUnread = !showOnlyUnread" :class="{ clickable: true, active: showOnlyUnread }">
        <span class="stat-value">{{ stats.unreadTotal }}</span>
        <span class="stat-label">Непрочитанных</span>
      </div>
    </div>

    <!-- Поиск и фильтры -->
    <div class="filters-section">
      <div class="search-box">
        <input
          type="text"
          v-model="searchQuery"
          placeholder="🔍 Поиск по имени, телефону или email..."
          class="search-input"
        />
      </div>

      <div class="filter-buttons">
        <button
          class="filter-button"
          :class="{ active: contactFilter === 'all' }"
          @click="contactFilter = 'all'"
        >
          Все
        </button>
        <button
          class="filter-button active-filter"
          :class="{ active: contactFilter === 'active' }"
          @click="contactFilter = 'active'"
        >
          ✅ Активные
        </button>
        <button
          class="filter-button inactive-filter"
          :class="{ active: contactFilter === 'inactive' }"
          @click="contactFilter = 'inactive'"
        >
          🔕 Неактивные
        </button>
      </div>
    </div>

    <!-- Список контактов -->
    <div class="contacts-section">
      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <p>Загрузка контактов...</p>
      </div>

      <div v-else-if="filteredContacts.length === 0" class="empty-state">
        <div class="empty-icon">🤝</div>
        <h3>Нет контактов</h3>
        <p v-if="searchQuery || contactFilter !== 'all'">
          Попробуйте изменить параметры поиска
        </p>
        <p v-else>
          Добавьте доверенных людей, чтобы начать общение
        </p>
        <Button
          variant="primary"
          @click="createNewContact"
        >
          Добавить контакт
        </Button>
      </div>

      <div v-else class="contacts-list">
        <ContactCard
          v-for="contact in filteredContacts"
          :key="contact.id"
          :contact="contact"
          :unreadCount="unreadCounts[contact.contactId] || 0"
          @click="openChat(contact)"
          @chat="openChat(contact)"
          @edit="editContact(contact)"
          @delete="deleteContact(contact)"
        />
      </div>
    </div>

    <!-- Модальное окно для создания/редактирования контакта -->
    <Teleport to="body">
      <div v-if="showForm" class="modal-overlay" @click.self="closeForm">
        <div class="modal-content">
          <ContactForm
            :contact="editingContact"
            :isEditing="!!editingContact"
            @submit="saveContact"
            @close="closeForm"
          />
        </div>
      </div>
    </Teleport>

    <!-- Модальное окно чата -->
    <Teleport to="body">
      <div v-if="showChat" class="modal-overlay chat-overlay" @click.self="closeChat">
        <div class="modal-content chat-content">
          <ChatView
            :contact="selectedContact"
            @back="closeChat"
            @update="loadData"
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
import { ref, onMounted, onUnmounted, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import Button from '@/components/UI/Button.vue'
import Notification from '@/components/UI/Notification.vue'
import ContactCard from './components/ContactCard.vue'
import ContactForm from './ContactForm.vue'
import ChatView from './ChatView.vue'
import contactService from '@/services/contactService'

const router = useRouter()
const loading = ref(true)
const contacts = ref([])
const unreadCounts = ref({})
const showForm = ref(false)
const showChat = ref(false)
const editingContact = ref(null)
const selectedContact = ref(null)
const searchQuery = ref('')
const contactFilter = ref('all')
const showOnlyUnread = ref(false)
const showNotification = ref(false)
const notificationMessage = ref('')
const notificationType = ref('success')

// Статистика
const stats = computed(() => {
  const total = contacts.value.length
  const active = contacts.value.filter(c => c.isActive).length
  const unreadTotal = Object.values(unreadCounts.value).reduce((sum, count) => sum + count, 0)

  return {
    totalContacts: total,
    activeContacts: active,
    unreadTotal
  }
})

// Отфильтрованные контакты
const filteredContacts = computed(() => {
  let filtered = [...contacts.value]

  // Фильтр по статусу
  if (contactFilter.value === 'active') {
    filtered = filtered.filter(c => c.isActive)
  } else if (contactFilter.value === 'inactive') {
    filtered = filtered.filter(c => !c.isActive)
  }

  // Фильтр по непрочитанным
  if (showOnlyUnread.value) {
    filtered = filtered.filter(c => (unreadCounts.value[c.contactId] || 0) > 0)
  }

  // Поиск по тексту
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(c =>
      c.displayName.toLowerCase().includes(query) ||
      c.fullName.toLowerCase().includes(query) ||
      c.phone.includes(query) ||
      c.email.toLowerCase().includes(query) ||
      (c.nickname && c.nickname.toLowerCase().includes(query))
    )
  }

  // Сортируем: сначала с непрочитанными, потом по имени
  return filtered.sort((a, b) => {
    const aUnread = unreadCounts.value[a.contactId] || 0
    const bUnread = unreadCounts.value[b.contactId] || 0

    if (aUnread > 0 && bUnread === 0) return -1
    if (aUnread === 0 && bUnread > 0) return 1

    return a.displayName.localeCompare(b.displayName)
  })
})

// Загрузка данных
const loadData = async () => {
  loading.value = true
  try {
    const [contactsData, unreadData] = await Promise.all([
      contactService.getActiveContacts(),
      contactService.getUnreadCount()
    ])

    contacts.value = contactsData || []

    // Здесь нужно будет получить количество непрочитанных по каждому контакту
    // Пока просто общее
    console.log('📥 Загружено контактов:', contacts.value.length)

    // Загружаем последние сообщения для получения непрочитанных
    await loadUnreadCounts()

  } catch (error) {
    console.error('❌ Ошибка загрузки данных:', error)
    showNotificationMessage('error', 'Ошибка загрузки контактов')
    contacts.value = []
  } finally {
    loading.value = false
  }
}

// Загрузка количества непрочитанных
const loadUnreadCounts = async () => {
  try {
    // TODO: получить количество непрочитанных по каждому контакту
    // Пока заглушка
    unreadCounts.value = {}
  } catch (error) {
    console.error('❌ Ошибка загрузки непрочитанных:', error)
  }
}

// Создание нового контакта
const createNewContact = () => {
  editingContact.value = null
  showForm.value = true
}

// Редактирование контакта
const editContact = (contact) => {
  editingContact.value = contact
  showForm.value = true
}

// Сохранение контакта
const saveContact = async (contactData) => {
  try {
    if (editingContact.value) {
      // Обновление
      await contactService.updateContact(editingContact.value.contactId, contactData)
      showNotificationMessage('success', 'Контакт обновлён')
    } else {
      // Создание
      await contactService.addContact(contactData)
      showNotificationMessage('success', 'Контакт добавлен')
    }

    closeForm()
    await loadData()
  } catch (error) {
    console.error('❌ Ошибка сохранения:', error)

    if (error.response?.status === 409) {
      showNotificationMessage('error', 'Этот пользователь уже добавлен в контакты')
    } else if (error.response?.status === 404) {
      showNotificationMessage('error', 'Пользователь с таким ID не найден')
    } else {
      showNotificationMessage('error', 'Ошибка при сохранении')
    }
  }
}

// Удаление контакта
const deleteContact = async (contact) => {
  if (!confirm(`Удалить контакт "${contact.displayName}"?`)) {
    return
  }

  try {
    await contactService.deleteContact(contact.contactId)
    showNotificationMessage('success', 'Контакт удалён')
    await loadData()

    // Если удалили текущий открытый чат, закрываем его
    if (showChat.value && selectedContact.value?.contactId === contact.contactId) {
      closeChat()
    }
  } catch (error) {
    console.error('❌ Ошибка удаления:', error)
    showNotificationMessage('error', 'Ошибка при удалении')
  }
}

// Открыть чат
const openChat = (contact) => {
  selectedContact.value = contact
  showChat.value = true
}

// Закрыть чат
const closeChat = () => {
  showChat.value = false
  selectedContact.value = null
  // Обновляем данные (непрочитанные могли измениться)
  loadData()
}

// Закрыть форму
const closeForm = () => {
  showForm.value = false
  editingContact.value = null
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

// Сброс фильтра при поиске
watch(searchQuery, () => {
  contactFilter.value = 'all'
  showOnlyUnread.value = false
})

onMounted(() => {
  loadData()

  // Периодическое обновление непрочитанных (каждые 30 секунд)
  const interval = setInterval(() => {
    if (!showChat.value) { // Не обновляем, если открыт чат
      loadUnreadCounts()
    }
  }, 30000)

  // Очистка при размонтировании
  onUnmounted(() => {
    clearInterval(interval)
  })
})
</script>

<style scoped>
.contacts-page {
  width: 100%;
  max-width: 1000px;
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
  flex-wrap: wrap;
  gap: 1rem;
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

.header-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

/* Статистика */
.stats-section {
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
  transition: all 0.3s ease;
}

.stat-card.clickable {
  cursor: pointer;
}

.stat-card.clickable:hover {
  transform: translateY(-3px);
  border-color: var(--color-primary, #88d424);
}

.stat-card.clickable.active {
  border: 2px solid var(--color-primary, #88d424);
  background: linear-gradient(135deg, var(--color-white, #ffffff) 0%, rgba(136, 212, 36, 0.1) 100%);
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

.filter-button.active-filter.active {
  background-color: var(--color-success, #4caf50);
  border-color: var(--color-success, #4caf50);
}

.filter-button.inactive-filter.active {
  background-color: var(--color-gray, #9e9e9e);
  border-color: var(--color-gray, #9e9e9e);
}

/* Список контактов */
.contacts-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin-bottom: 2rem;
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

/* Модальные окна */
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

.chat-overlay {
  background-color: rgba(0, 0, 0, 0.8);
}

.modal-content {
  width: 100%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  animation: slideIn 0.3s ease;
}

.chat-content {
  max-width: 800px;
  padding: 0;
  background: none;
  box-shadow: none;
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

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    text-align: center;
  }

  .header-actions {
    width: 100%;
  }

  .header-actions button {
    flex: 1;
  }

  .filters-section {
    flex-direction: column;
  }

  .filter-buttons {
    width: 100%;
  }

  .filter-button {
    flex: 1;
    text-align: center;
  }

  .modal-content {
    margin: 0;
    max-height: 100vh;
    border-radius: 0;
  }

  .chat-content {
    height: 100vh;
  }
}
</style>
