import apiClient from './api'

class PlaceService {

  /**
   * Получить все значимые места пользователя
   */
  async getAllPlaces() {
    try {
      const response = await apiClient.get('/places')
      console.log('✅ Места получены, количество:', response.data?.length || 0)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка получения мест:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Получить место по ID
   * @param {string} placeId - UUID места
   */
  async getPlaceById(placeId) {
    try {
      const response = await apiClient.get(`/places/${placeId}`)
      console.log('✅ Место получено:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка получения места:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Создать новое место
   * @param {Object} placeData - { title, address, description, category, isHome, isWork, isMedical }
   */
  async createPlace(placeData) {
    try {
      const response = await apiClient.post('/places', placeData)
      console.log('✅ Место создано:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка создания места:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Обновить место
   * @param {string} placeId - UUID места
   * @param {Object} placeData - { title, address, description, category, isHome, isWork, isMedical, sortOrder }
   */
  async updatePlace(placeId, placeData) {
    try {
      const response = await apiClient.put(`/places/${placeId}`, placeData)
      console.log('✅ Место обновлено:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка обновления места:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Удалить место
   * @param {string} placeId - UUID места
   */
  async deletePlace(placeId) {
    try {
      await apiClient.delete(`/places/${placeId}`)
      console.log('✅ Место удалено')
      return true
    } catch (error) {
      console.error('❌ Ошибка удаления места:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Изменить порядок сортировки мест
   * @param {string[]} placeIds - массив ID мест в нужном порядке
   */
  async reorderPlaces(placeIds) {
    try {
      await apiClient.post('/places/reorder', { placeIds })
      console.log('✅ Порядок мест обновлён')
      return true
    } catch (error) {
      console.error('❌ Ошибка изменения порядка:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Проверить, было ли место отредактировано
   * @param {Object} place - объект места
   * @returns {boolean}
   */
  static isEdited(place) {
    return place.updatedAt && place.updatedAt !== place.createdAt
  }

  /**
   * Получить иконку для категории
   * @param {string} category - категория места
   * @returns {string} эмодзи-иконка
   */
  static getCategoryIcon(category) {
    const icons = {
      'Дом': '🏠',
      'Медицина': '🏥',
      'Работа': '💼',
      'Отдых': '🌳',
      'Образование': '📚',
      'Другое': '📍'
    }
    return icons[category] || '📍'
  }

  /**
   * Получить цвет для категории
   * @param {string} category - категория места
   * @returns {string} CSS цвет
   */
  static getCategoryColor(category) {
    const colors = {
      'Дом': 'var(--color-primary, #88d424)',
      'Медицина': 'var(--color-error, #f44336)',
      'Работа': 'var(--color-warning, #ff9800)',
      'Отдых': 'var(--color-success, #4caf50)',
      'Образование': 'var(--color-primary-dark, #6aa91c)',
      'Другое': 'var(--color-gray, #9e9e9e)'
    }
    return colors[category] || 'var(--color-gray, #9e9e9e)'
  }
}

export default new PlaceService()
