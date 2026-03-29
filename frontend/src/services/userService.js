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
  async getUserByEmail(email) {
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
   * Смена пароля пользователя
   * @param {string} userId - UUID пользователя
   * @param {Object} passwordData - объект с полями currentPassword, newPassword, confirmNewPassword
   */
  async changePassword(userId, passwordData) {
    try {
      const response = await apiClient.patch(`/users/${userId}/password`, passwordData)
      console.log('✅ Пароль успешно изменён')
      return response.data
    } catch (error) {
      console.error('❌ Ошибка смены пароля:', error.response?.data || error.message)
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

  /**
   * Загрузка фотографии профиля
   * @param {string} userId - UUID пользователя
   * @param {File} file - файл изображения
   */
  async uploadProfilePhoto(userId, file) {
    try {
      const formData = new FormData()
      formData.append('file', file)

      const response = await apiClient.post(`/users/${userId}/photo`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
      console.log('✅ Фото профиля загружено:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка загрузки фото:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Получение URL фотографии профиля
   * @param {string} userId - UUID пользователя
   * @returns {string} URL для доступа к фото
   */
  getProfilePhotoUrl(userId) {
    return `/api/users/${userId}/photo`
  }

  /**
   * Удаление фотографии профиля
   * @param {string} userId - UUID пользователя
   */
  async deleteProfilePhoto(userId) {
    try {
      await apiClient.delete(`/users/${userId}/photo`)
      console.log('✅ Фото профиля удалено')
      return true
    } catch (error) {
      console.error('❌ Ошибка удаления фото:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Форматировать размер файла
   */
  static formatFileSize(bytes) {
    if (bytes === 0) return '0 B'
    const k = 1024
    const sizes = ['B', 'KB', 'MB', 'GB']
    const i = Math.floor(Math.log(bytes) / Math.log(k))
    return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i]
  }
}

export default new UserService()
