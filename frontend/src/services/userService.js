import apiClient from './api'

class UserService {

  /**
   * Регистрация нового пользователя
   */
  async register(userData) {
    try {
      const response = await apiClient.post('/users/register', userData)
      console.log('✅ Регистрация успешна:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка регистрации:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Получение пользователя по email
   */
  async getUserByEmail(email, token) {
    try {
      const response = await apiClient.get(`/users/email/${email}`)
      console.log('✅ Пользователь получен:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка получения пользователя:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Получение пользователя по ID
   */
  async getUserById(userId) {
    try {
      const response = await apiClient.get(`/users/${userId}`)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка получения пользователя по ID:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Обновление данных пользователя
   */
  async updateUser(userId, userData) {
    try {
      const response = await apiClient.put(`/users/${userId}`, userData)
      console.log('✅ Пользователь обновлен:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка обновления:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Деактивация пользователя
   */
  async deactivateUser(userId, reason = '') {
    try {
      const response = await apiClient.patch(`/users/${userId}/deactivate`, { reason })
      console.log('✅ Пользователь деактивирован')
      return response.data
    } catch (error) {
      console.error('❌ Ошибка деактивации:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Активация пользователя
   */
  async activateUser(userId) {
    try {
      const response = await apiClient.patch(`/users/${userId}/activate`)
      console.log('✅ Пользователь активирован')
      return response.data
    } catch (error) {
      console.error('❌ Ошибка активации:', error.response?.data || error.message)
      throw error
    }
  }
}

export default new UserService()
