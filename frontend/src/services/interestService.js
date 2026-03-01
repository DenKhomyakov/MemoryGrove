import apiClient from './api'

class InterestService {

  /**
   * Получить все интересы пользователя
   */
  async getAllInterests() {
    try {
      const response = await apiClient.get('/interests')
      console.log('✅ Интересы получены, количество:', response.data?.length || 0)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка получения интересов:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Получить интересы по категории
   * @param {string} category - категория (FILM, BOOK, MUSIC, GAME, SPORT, HOBBY, OTHER)
   */
  async getInterestsByCategory(category) {
    try {
      const response = await apiClient.get(`/interests/category/${category}`)
      console.log(`✅ Интересы категории ${category} получены:`, response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка получения интересов по категории:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Получить интерес по ID
   * @param {string} interestId - UUID интереса
   */
  async getInterestById(interestId) {
    try {
      const response = await apiClient.get(`/interests/${interestId}`)
      console.log('✅ Интерес получен:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка получения интереса:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Создать новый интерес
   * @param {Object} interestData - данные интереса
   */
  async createInterest(interestData) {
    try {
      const response = await apiClient.post('/interests', interestData)
      console.log('✅ Интерес создан:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка создания интереса:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Обновить интерес
   * @param {string} interestId - UUID интереса
   * @param {Object} interestData - новые данные
   */
  async updateInterest(interestId, interestData) {
    try {
      const response = await apiClient.put(`/interests/${interestId}`, interestData)
      console.log('✅ Интерес обновлён:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка обновления интереса:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Обновить только оценку (лайк/дизлайк)
   * @param {string} interestId - UUID интереса
   * @param {Object} likeData - { liked, rating }
   */
  async updateLike(interestId, likeData) {
    try {
      const response = await apiClient.patch(`/interests/${interestId}/like`, likeData)
      console.log('✅ Оценка интереса обновлена:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка обновления оценки:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Удалить интерес
   * @param {string} interestId - UUID интереса
   */
  async deleteInterest(interestId) {
    try {
      await apiClient.delete(`/interests/${interestId}`)
      console.log('✅ Интерес удалён')
      return true
    } catch (error) {
      console.error('❌ Ошибка удаления интереса:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Изменить порядок сортировки интересов
   * @param {string[]} interestIds - массив ID в нужном порядке
   */
  async reorderInterests(interestIds) {
    try {
      await apiClient.post('/interests/reorder', { interestIds })
      console.log('✅ Порядок интересов обновлён')
      return true
    } catch (error) {
      console.error('❌ Ошибка изменения порядка:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Получить статистику по категориям
   */
  async getCategoryStats() {
    try {
      const response = await apiClient.get('/interests/stats')
      console.log('✅ Статистика получена:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка получения статистики:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Получить отображаемое название категории
   * @param {string} category - кодовое название категории
   * @returns {string} отображаемое название
   */
  static getCategoryDisplayName(category) {
    const names = {
      'FILM': '🎬 Фильмы',
      'BOOK': '📚 Книги',
      'MUSIC': '🎵 Музыка',
      'GAME': '🎮 Игры',
      'SPORT': '⚽ Спорт',
      'HOBBY': '🧩 Хобби',
      'OTHER': '📌 Другое'
    }
    return names[category] || category
  }

  /**
   * Получить иконку для категории
   * @param {string} category - кодовое название категории
   * @returns {string} эмодзи-иконка
   */
  static getCategoryIcon(category) {
    const icons = {
      'FILM': '🎬',
      'BOOK': '📚',
      'MUSIC': '🎵',
      'GAME': '🎮',
      'SPORT': '⚽',
      'HOBBY': '🧩',
      'OTHER': '📌'
    }
    return icons[category] || '📌'
  }

  /**
   * Получить цвет для категории
   * @param {string} category - кодовое название категории
   * @returns {string} CSS цвет
   */
  static getCategoryColor(category) {
    const colors = {
      'FILM': 'var(--color-primary, #88d424)',
      'BOOK': 'var(--color-primary-dark, #6aa91c)',
      'MUSIC': 'var(--color-warning, #ff9800)',
      'GAME': 'var(--color-error, #f44336)',
      'SPORT': 'var(--color-success, #4caf50)',
      'HOBBY': 'var(--color-primary-light, #a8e454)',
      'OTHER': 'var(--color-gray, #9e9e9e)'
    }
    return colors[category] || 'var(--color-gray, #9e9e9e)'
  }

  /**
   * Получить текст оценки для отображения
   * @param {boolean|null} liked - значение liked
   * @returns {string} текст с эмодзи
   */
  static getLikeText(liked) {
    if (liked === true) return '❤️ Нравится'
    if (liked === false) return '💔 Не нравится'
    return '🤔 Не оценено'
  }

  /**
   * Получить эмодзи для оценки
   * @param {boolean|null} liked - значение liked
   * @returns {string} эмодзи
   */
  static getLikeEmoji(liked) {
    if (liked === true) return '❤️'
    if (liked === false) return '💔'
    return '🤔'
  }
}

export default new InterestService()
