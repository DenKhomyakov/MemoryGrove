import apiClient from './api'

class ReminderService {

  /**
   * Получить все напоминания с пагинацией
   * @param {number} page - номер страницы (0-based)
   * @param {number} size - размер страницы
   */
  async getReminders(page = 0, size = 20) {
    try {
      const response = await apiClient.get(`/reminders?page=${page}&size=${size}`)
      console.log('✅ Напоминания получены:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка получения напоминаний:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Получить все напоминания (без пагинации)
   */
  async getAllReminders() {
    try {
      const response = await apiClient.get('/reminders/all')
      console.log('✅ Все напоминания получены:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка получения всех напоминаний:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Получить напоминание по ID
   * @param {string} reminderId - UUID напоминания
   */
  async getReminderById(reminderId) {
    try {
      const response = await apiClient.get(`/reminders/${reminderId}`)
      console.log('✅ Напоминание получено:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка получения напоминания:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Получить напоминания по статусу
   * @param {string} statusId - UUID статуса
   */
  async getRemindersByStatus(statusId) {
    try {
      const response = await apiClient.get(`/reminders/status/${statusId}`)
      console.log('✅ Напоминания по статусу получены:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка получения напоминаний по статусу:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Создать новое напоминание
   * @param {Object} reminderData - { title, description, remindAt }
   */
  async createReminder(reminderData) {
    try {
      const response = await apiClient.post('/reminders', reminderData)
      console.log('✅ Напоминание создано:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка создания напоминания:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Обновить напоминание
   * @param {string} reminderId - UUID напоминания
   * @param {Object} reminderData - { title, description, remindAt, statusId }
   */
  async updateReminder(reminderId, reminderData) {
    try {
      const response = await apiClient.put(`/reminders/${reminderId}`, reminderData)
      console.log('✅ Напоминание обновлено:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка обновления напоминания:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Обновить только статус напоминания
   * @param {string} reminderId - UUID напоминания
   * @param {string} statusId - UUID статуса
   */
  async updateReminderStatus(reminderId, statusId) {
    try {
      const response = await apiClient.patch(`/reminders/${reminderId}/status`, { statusId })
      console.log('✅ Статус напоминания обновлён:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка обновления статуса:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Удалить напоминание
   * @param {string} reminderId - UUID напоминания
   */
  async deleteReminder(reminderId) {
    try {
      await apiClient.delete(`/reminders/${reminderId}`)
      console.log('✅ Напоминание удалено')
      return true
    } catch (error) {
      console.error('❌ Ошибка удаления напоминания:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Константы статусов
   */
  static Status = {
    PENDING: '6cc6ee82-b293-453b-a5df-94428f5f84ed',
    SENT: '5f699cae-62e2-431b-a460-e9d594938424',
    COMPLETED: '7665777b-e1e9-4be7-a8c5-fb2e7d457b20',
    CANCELLED: 'e6ebcf79-82ac-4d92-bb35-0ec71a3845cd'
  }

  /**
   * Получить текст статуса по ID
   */
  static getStatusText(statusId) {
    const statusMap = {
      [this.Status.PENDING]: 'Ожидает',
      [this.Status.SENT]: 'Отправлено',
      [this.Status.COMPLETED]: 'Выполнено',
      [this.Status.CANCELLED]: 'Отменено'
    }
    return statusMap[statusId] || 'Неизвестно'
  }

  /**
   * Получить цвет статуса для отображения
   */
  static getStatusColor(statusId) {
    const colorMap = {
      [this.Status.PENDING]: 'var(--color-warning, #ff9800)',
      [this.Status.SENT]: 'var(--color-primary, #88d424)',
      [this.Status.COMPLETED]: 'var(--color-success, #4caf50)',
      [this.Status.CANCELLED]: 'var(--color-gray, #9e9e9e)'
    }
    return colorMap[statusId] || 'var(--color-gray, #9e9e9e)'
  }
}

export default new ReminderService()
