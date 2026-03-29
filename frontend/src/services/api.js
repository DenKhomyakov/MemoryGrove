import axios from 'axios'

/**
 * Базовый клиент для API
 */
const apiClient = axios.create({
  baseURL: '/api',  // через прокси Vite
  headers: {
    'Content-Type': 'application/json'
  }
})

/**
 * Перехватчик для добавления токена к каждому запросу
 */
apiClient.interceptors.request.use(
  config => {
    const token = localStorage.getItem('accessToken')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
      console.log('✅ Токен добавлен к запросу')
    } else {
      console.log('⚠️ Токен отсутствует')
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

/**
 * Перехватчик для обработки ошибок ответа
 */
apiClient.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      console.log('🔒 Ошибка 401 - неавторизован')
    }
    return Promise.reject(error)
  }
)

export default apiClient
