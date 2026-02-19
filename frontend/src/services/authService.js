import apiClient from './api'

class AuthService {

  /**
   * Вход в систему
   */
  async login(identifier, password) {
    try {
      const response = await apiClient.post('/auth/login', {
        identifier,
        password
      })

      const { accessToken, refreshToken } = response.data

      localStorage.setItem('accessToken', accessToken)
      localStorage.setItem('refreshToken', refreshToken)

      console.log('✅ Вход выполнен успешно')
      return response.data
    } catch (error) {
      console.error('❌ Ошибка входа:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Выход из системы
   */
  logout() {
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    console.log('👋 Выход выполнен')
  }

  /**
   * Обновление токена
   */
  async refreshToken() {
    try {
      const refreshToken = localStorage.getItem('refreshToken')
      if (!refreshToken) {
        throw new Error('Нет refresh токена')
      }

      const response = await apiClient.post('/auth/refresh', {
        refreshToken
      })

      const { accessToken, refreshToken: newRefreshToken } = response.data
      localStorage.setItem('accessToken', accessToken)
      localStorage.setItem('refreshToken', newRefreshToken)

      console.log('✅ Токен обновлен')
      return response.data
    } catch (error) {
      console.error('❌ Ошибка обновления токена:', error.response?.data || error.message)
      this.logout()
      throw error
    }
  }

  /**
   * Проверка, авторизован ли пользователь
   */
  isAuthenticated() {
    return !!localStorage.getItem('accessToken')
  }

  /**
   * Получение текущего токена
   */
  getToken() {
    return localStorage.getItem('accessToken')
  }
}

export default new AuthService()
