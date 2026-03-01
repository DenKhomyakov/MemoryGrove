import apiClient from './api'

class SkillService {

  /**
   * Получить все навыки пользователя
   */
  async getAllSkills() {
    try {
      const response = await apiClient.get('/skills')
      console.log('✅ Навыки получены, количество:', response.data?.length || 0)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка получения навыков:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Получить навыки по категории
   * @param {string} category - категория (SELF_CARE, HOUSEHOLD, COMMUNICATION, MOBILITY, HEALTH, COGNITIVE, OTHER)
   */
  async getSkillsByCategory(category) {
    try {
      const response = await apiClient.get(`/skills/category/${category}`)
      console.log(`✅ Навыки категории ${category} получены:`, response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка получения навыков по категории:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Получить навык по ID
   * @param {string} skillId - UUID навыка
   */
  async getSkillById(skillId) {
    try {
      const response = await apiClient.get(`/skills/${skillId}`)
      console.log('✅ Навык получен:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка получения навыка:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Создать новый навык
   * @param {Object} skillData - данные навыка
   */
  async createSkill(skillData) {
    try {
      const response = await apiClient.post('/skills', skillData)
      console.log('✅ Навык создан:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка создания навыка:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Обновить навык
   * @param {string} skillId - UUID навыка
   * @param {Object} skillData - новые данные
   */
  async updateSkill(skillId, skillData) {
    try {
      const response = await apiClient.put(`/skills/${skillId}`, skillData)
      console.log('✅ Навык обновлён:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка обновления навыка:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Быстрое переключение статуса навыка (для чек-бокса)
   * @param {string} skillId - UUID навыка
   * @param {boolean} canDoNow - новый статус
   */
  async toggleSkillStatus(skillId, canDoNow) {
    try {
      const response = await apiClient.patch(`/skills/${skillId}/toggle`, { canDoNow })
      console.log('✅ Статус навыка обновлён:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка переключения статуса:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Удалить навык
   * @param {string} skillId - UUID навыка
   */
  async deleteSkill(skillId) {
    try {
      await apiClient.delete(`/skills/${skillId}`)
      console.log('✅ Навык удалён')
      return true
    } catch (error) {
      console.error('❌ Ошибка удаления навыка:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Изменить порядок сортировки навыков
   * @param {string[]} skillIds - массив ID в нужном порядке
   */
  async reorderSkills(skillIds) {
    try {
      await apiClient.post('/skills/reorder', { skillIds })
      console.log('✅ Порядок навыков обновлён')
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
      const response = await apiClient.get('/skills/stats/categories')
      console.log('✅ Статистика по категориям получена:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка получения статистики по категориям:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Получить общую статистику
   */
  async getTotalStats() {
    try {
      const response = await apiClient.get('/skills/stats/total')
      console.log('✅ Общая статистика получена:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка получения общей статистики:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Получить отображаемое название категории
   * @param {string} category - кодовое название категории
   * @returns {string} отображаемое название с эмодзи
   */
  static getCategoryDisplayName(category) {
    const names = {
      'SELF_CARE': '🚿 Самообслуживание',
      'HOUSEHOLD': '🍳 Быт',
      'COMMUNICATION': '📞 Коммуникация',
      'MOBILITY': '🚶 Мобильность',
      'HEALTH': '💊 Здоровье',
      'COGNITIVE': '🧠 Когнитивные',
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
      'SELF_CARE': '🚿',
      'HOUSEHOLD': '🍳',
      'COMMUNICATION': '📞',
      'MOBILITY': '🚶',
      'HEALTH': '💊',
      'COGNITIVE': '🧠',
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
      'SELF_CARE': 'var(--color-primary, #88d424)',
      'HOUSEHOLD': 'var(--color-warning, #ff9800)',
      'COMMUNICATION': 'var(--color-primary-dark, #6aa91c)',
      'MOBILITY': 'var(--color-success, #4caf50)',
      'HEALTH': 'var(--color-error, #f44336)',
      'COGNITIVE': 'var(--color-primary-light, #a8e454)',
      'OTHER': 'var(--color-gray, #9e9e9e)'
    }
    return colors[category] || 'var(--color-gray, #9e9e9e)'
  }

  /**
   * Получить текст сложности
   * @param {number} difficulty - сложность (1-5)
   * @returns {string} текстовое описание
   */
  static getDifficultyText(difficulty) {
    const texts = {
      1: 'Очень легко',
      2: 'Легко',
      3: 'Средне',
      4: 'Сложно',
      5: 'Очень сложно'
    }
    return texts[difficulty] || ''
  }

  /**
   * Получить текст важности
   * @param {number} importance - важность (1-5)
   * @returns {string} текстовое описание
   */
  static getImportanceText(importance) {
    const texts = {
      1: 'Не важно',
      2: 'Мало важно',
      3: 'Средне',
      4: 'Важно',
      5: 'Очень важно'
    }
    return texts[importance] || ''
  }
}

export default new SkillService()
