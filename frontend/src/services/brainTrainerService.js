import apiClient from './api'

/**
 * Сервис для работы с упражнениями для тренировки памяти
 */
class BrainTrainerService {

  /**
   * Сгенерировать арифметическую цепочку
   * @param {number} length - длина цепочки (количество операций)
   * @returns {Promise<Object>} { expression, correctResult, chainLength }
   */
  async generateArithmeticChain(length = 3) {
    try {
      const response = await apiClient.get(`/brain/arithmetic/generate?length=${length}`)
      console.log('✅ Арифметическая цепочка сгенерирована:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка генерации цепочки:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Проверить ответ на арифметическую цепочку
   * @param {number} correctResult - правильный результат
   * @param {Object} data - { result, timeSeconds, chainLength }
   * @returns {Promise<boolean>} true если ответ верный
   */
  async checkArithmeticAnswer(correctResult, data) {
    try {
      const response = await apiClient.post(`/brain/arithmetic/check/${correctResult}`, data)
      console.log('✅ Ответ проверен, результат:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка проверки ответа:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Отправить результаты понимания текста
   * @param {Object} data - { correctAnswers, totalQuestions, successRate, timeSeconds }
   * @returns {Promise<void>}
   */
  async submitTextComprehensionResult(data) {
    try {
      await apiClient.post('/brain/text-comprehension/result', data)
      console.log('✅ Результаты понимания текста отправлены:', data)
    } catch (error) {
      console.error('❌ Ошибка отправки результатов:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Отправить результаты заучивания стиха
   * @param {Object} data - { correct, similarity, timeSeconds, memorizingTime }
   * @returns {Promise<void>}
   */
  async submitPoemResult(data) {
    try {
      await apiClient.post('/brain/poem/result', data)
      console.log('✅ Результаты заучивания стиха отправлены:', data)
    } catch (error) {
      console.error('❌ Ошибка отправки результатов:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Получить всю статистику по упражнениям
   * @returns {Promise<Array>} массив со статистикой по каждому упражнению
   */
  async getAllStats() {
    try {
      const response = await apiClient.get('/brain/stats')
      console.log('✅ Статистика получена:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка получения статистики:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Получить статистику по конкретному типу упражнения
   * @param {string} exerciseType - тип упражнения (ARITHMETIC, TEXT_COMPREHENSION и т.д.)
   * @returns {Promise<Object>} статистика по упражнению
   */
  async getStatsByType(exerciseType) {
    try {
      const response = await apiClient.get(`/brain/stats/${exerciseType}`)
      console.log(`✅ Статистика по типу ${exerciseType} получена:`, response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка получения статистики по типу:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Получить отображаемое название упражнения по его типу
   * @param {string} type - тип упражнения
   * @returns {string} отображаемое название с эмодзи
   */
  static getDisplayName(type) {
    const names = {
      'ARITHMETIC': '🧮 Арифметическая цепочка',
      'TEXT_COMPREHENSION': '📖 Понимание текста',
      'POEM': '📝 Заучивание стихов',
      'ASSOCIATION': '🔗 Ассоциации',
      'SEQUENCE': '📊 Продолжи ряд',
      'REVERSE_WORD': '🔄 Слово наоборот',
      'ODD_ONE_OUT': '❓ Что лишнее?',
      'SCHULTE_TABLE': '🔢 Таблица Шульте'
    }
    return names[type] || type
  }

  /**
   * Получить иконку для упражнения по его типу
   * @param {string} type - тип упражнения
   * @returns {string} эмодзи-иконка
   */
  static getIcon(type) {
    const icons = {
      'ARITHMETIC': '🧮',
      'TEXT_COMPREHENSION': '📖',
      'POEM': '📝',
      'ASSOCIATION': '🔗',
      'SEQUENCE': '📊',
      'REVERSE_WORD': '🔄',
      'ODD_ONE_OUT': '❓',
      'SCHULTE_TABLE': '🔢'
    }
    return icons[type] || '🧠'
  }

  /**
   * Форматировать время (секунды -> MM:SS)
   * @param {number} seconds - время в секундах
   * @returns {string} отформатированное время
   */
  static formatTime(seconds) {
    if (!seconds && seconds !== 0) return '—'
    const mins = Math.floor(seconds / 60)
    const secs = seconds % 60
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
  }
}

export default new BrainTrainerService()
