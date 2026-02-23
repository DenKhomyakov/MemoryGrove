import apiClient from './api'

class JournalService {

  /**
   * Получить все записи с пагинацией
   * @param {number} page - номер страницы (0-based)
   * @param {number} size - размер страницы
   */
  async getEntries(page = 0, size = 20) {
    try {
      const response = await apiClient.get(`/journal/entries?page=${page}&size=${size}`)
      console.log('✅ Записи получены:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка получения записей:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Получить все записи (без пагинации)
   */
  async getAllEntries() {
    try {
      const response = await apiClient.get('/journal/entries/all')
      console.log('✅ Все записи получены:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка получения всех записей:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Получить запись по ID
   * @param {string} entryId - UUID записи
   */
  async getEntryById(entryId) {
    try {
      const response = await apiClient.get(`/journal/entries/${entryId}`)
      console.log('✅ Запись получена:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка получения записи:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Создать новую запись
   * @param {Object} entryData - { title, content, mood }
   */
  async createEntry(entryData) {
    try {
      const response = await apiClient.post('/journal/entries', entryData)
      console.log('✅ Запись создана:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка создания записи:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Обновить запись
   * @param {string} entryId - UUID записи
   * @param {Object} entryData - { title, content, mood }
   */
  async updateEntry(entryId, entryData) {
    try {
      const response = await apiClient.put(`/journal/entries/${entryId}`, entryData)
      console.log('✅ Запись обновлена:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка обновления записи:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Удалить запись
   * @param {string} entryId - UUID записи
   */
  async deleteEntry(entryId) {
    try {
      await apiClient.delete(`/journal/entries/${entryId}`)
      console.log('✅ Запись удалена')
      return true
    } catch (error) {
      console.error('❌ Ошибка удаления записи:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Получить статистику дневника
   */
  async getStats() {
    try {
      const response = await apiClient.get('/journal/stats')
      console.log('✅ Статистика получена:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка получения статистики:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Поиск записей за период
   * @param {string} startDate - начало периода (ISO datetime)
   * @param {string} endDate - конец периода (ISO datetime)
   */
  async searchEntriesByDate(startDate, endDate) {
    try {
      const response = await apiClient.get(`/journal/entries/search?startDate=${startDate}&endDate=${endDate}`)
      console.log('✅ Записи за период получены:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка поиска записей:', error.response?.data || error.message)
      throw error
    }
  }
}

export default new JournalService()
