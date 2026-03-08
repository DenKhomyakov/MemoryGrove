import apiClient from './api'

/**
 * Сервис для работы с доверенными контактами и сообщениями
 */
class ContactService {

  /**
   * Получить все активные контакты пользователя
   * @returns {Promise<Array>} список контактов
   */
  async getActiveContacts() {
    try {
      const response = await apiClient.get('/contacts')
      console.log('✅ Активные контакты получены:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка получения контактов:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Получить все контакты (включая неактивные)
   * @returns {Promise<Array>} список всех контактов
   */
  async getAllContacts() {
    try {
      const response = await apiClient.get('/contacts/all')
      console.log('✅ Все контакты получены:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка получения всех контактов:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Добавить новый контакт
   * @param {Object} data - { contactId, nickname }
   * @returns {Promise<Object>} добавленный контакт
   */
  async addContact(data) {
    try {
      const response = await apiClient.post('/contacts', data)
      console.log('✅ Контакт добавлен:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка добавления контакта:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Обновить настройки контакта
   * @param {string} contactId - ID контакта
   * @param {Object} data - { nickname, isActive, canSendMessages, canReceiveNotifications }
   * @returns {Promise<Object>} обновлённый контакт
   */
  async updateContact(contactId, data) {
    try {
      const response = await apiClient.patch(`/contacts/${contactId}`, data)
      console.log('✅ Контакт обновлён:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка обновления контакта:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Удалить контакт
   * @param {string} contactId - ID контакта
   * @returns {Promise<void>}
   */
  async deleteContact(contactId) {
    try {
      await apiClient.delete(`/contacts/${contactId}`)
      console.log('✅ Контакт удалён')
    } catch (error) {
      console.error('❌ Ошибка удаления контакта:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Получить переписку с контактом
   * @param {string} contactId - ID контакта
   * @param {number} page - номер страницы
   * @param {number} size - размер страницы
   * @returns {Promise<Object>} страница с сообщениями
   */
  async getConversation(contactId, page = 0, size = 20) {
    try {
      const response = await apiClient.get(`/messages/conversation/${contactId}?page=${page}&size=${size}`)
      console.log('✅ Переписка получена:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка получения переписки:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Отправить сообщение
   * @param {Object} data - { receiverId, content }
   * @returns {Promise<Object>} отправленное сообщение
   */
  async sendMessage(data) {
    try {
      const response = await apiClient.post('/messages', data)
      console.log('✅ Сообщение отправлено:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка отправки сообщения:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Получить последние сообщения
   * @returns {Promise<Array>} список последних сообщений
   */
  async getLastMessages() {
    try {
      const response = await apiClient.get('/messages/last')
      console.log('✅ Последние сообщения получены:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка получения последних сообщений:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Получить количество непрочитанных сообщений
   * @returns {Promise<Object>} { total }
   */
  async getUnreadCount() {
    try {
      const response = await apiClient.get('/messages/unread')
      console.log('✅ Количество непрочитанных:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка получения количества непрочитанных:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Отметить сообщения как прочитанные
   * @param {string} contactId - ID контакта
   * @returns {Promise<void>}
   */
  async markAsRead(contactId) {
    try {
      await apiClient.post(`/messages/read/${contactId}`)
      console.log('✅ Сообщения отмечены как прочитанные')
    } catch (error) {
      console.error('❌ Ошибка отметки сообщений:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Удалить сообщение
   * @param {string} messageId - ID сообщения
   * @returns {Promise<void>}
   */
  async deleteMessage(messageId) {
    try {
      await apiClient.delete(`/messages/${messageId}`)
      console.log('✅ Сообщение удалено')
    } catch (error) {
      console.error('❌ Ошибка удаления сообщения:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Получить все шаблоны сообщений
   * @returns {Promise<Array>} список шаблонов
   */
  async getTemplates() {
    try {
      const response = await apiClient.get('/messages/templates')
      console.log('✅ Шаблоны получены:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка получения шаблонов:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Создать новый шаблон
   * @param {Object} data - { title, content, sortOrder }
   * @returns {Promise<Object>} созданный шаблон
   */
  async createTemplate(data) {
    try {
      const response = await apiClient.post('/messages/templates', data)
      console.log('✅ Шаблон создан:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка создания шаблона:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Обновить шаблон
   * @param {string} templateId - ID шаблона
   * @param {Object} data - { title, content, sortOrder }
   * @returns {Promise<Object>} обновлённый шаблон
   */
  async updateTemplate(templateId, data) {
    try {
      const response = await apiClient.put(`/messages/templates/${templateId}`, data)
      console.log('✅ Шаблон обновлён:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка обновления шаблона:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Удалить шаблон
   * @param {string} templateId - ID шаблона
   * @returns {Promise<void>}
   */
  async deleteTemplate(templateId) {
    try {
      await apiClient.delete(`/messages/templates/${templateId}`)
      console.log('✅ Шаблон удалён')
    } catch (error) {
      console.error('❌ Ошибка удаления шаблона:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Получить иконку для контакта (по первой букве имени или эмодзи)
   * @param {Object} contact - объект контакта
   * @returns {string} иконка
   */
  static getContactIcon(contact) {
    if (!contact) return '👤'

    // Если есть никнейм, берём первую букву
    if (contact.nickname) {
      return contact.nickname.charAt(0).toUpperCase()
    }

    // Иначе первую букву фамилии или имени
    if (contact.fullName) {
      return contact.fullName.charAt(0).toUpperCase()
    }

    return '👤'
  }

  /**
   * Получить цвет для контакта (на основе ID для консистентности)
   * @param {string} contactId - ID контакта
   * @returns {string} CSS цвет
   */
  static getContactColor(contactId) {
    if (!contactId) return 'var(--color-primary, #88d424)'

    // Генерируем число из ID и выбираем цвет
    const hash = contactId.split('').reduce((acc, char) => {
      return acc + char.charCodeAt(0)
    }, 0)

    const colors = [
      'var(--color-primary, #88d424)',
      'var(--color-primary-dark, #6aa91c)',
      'var(--color-success, #4caf50)',
      'var(--color-warning, #ff9800)',
      'var(--color-error, #f44336)',
      '#2196f3', // синий
      '#9c27b0', // фиолетовый
      '#ff5722'  // оранжевый
    ]

    return colors[hash % colors.length]
  }

  /**
   * Форматировать время сообщения
   * @param {string} dateString - дата в ISO формате
   * @returns {string} отформатированное время
   */
  static formatMessageTime(dateString) {
    if (!dateString) return ''

    const date = new Date(dateString)
    const now = new Date()
    const yesterday = new Date(now)
    yesterday.setDate(yesterday.getDate() - 1)

    // Сегодня
    if (date.toDateString() === now.toDateString()) {
      return date.toLocaleTimeString('ru-RU', { hour: '2-digit', minute: '2-digit' })
    }

    // Вчера
    if (date.toDateString() === yesterday.toDateString()) {
      return 'вчера'
    }

    // Другая дата
    return date.toLocaleDateString('ru-RU', { day: '2-digit', month: '2-digit' })
  }
}

export default new ContactService()
