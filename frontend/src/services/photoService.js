import apiClient from './api'

class PhotoService {

  /**
   * Получить все фотографии пользователя
   */
  async getAllPhotos() {
    try {
      const response = await apiClient.get('/memory/photos')
      console.log('✅ Фотографии получены:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка получения фотографий:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Получить фотографию по ID
   * @param {string} photoId - UUID фотографии
   */
  async getPhotoById(photoId) {
    try {
      const response = await apiClient.get(`/memory/photos/${photoId}`)
      console.log('✅ Фотография получена:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка получения фотографии:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Загрузить новую фотографию
   * @param {FormData} formData - данные формы с файлом и метаданными
   */
  async uploadPhoto(formData) {
    try {
      const response = await apiClient.post('/memory/photos/upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
      console.log('✅ Фотография загружена:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка загрузки фотографии:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Обновить метаданные фотографии
   * @param {string} photoId - UUID фотографии
   * @param {Object} metadata - { description, location, year, people }
   */
  async updatePhotoMetadata(photoId, metadata) {
    try {
      const response = await apiClient.patch(`/memory/photos/${photoId}`, metadata)
      console.log('✅ Метаданные обновлены:', response.data)
      return response.data
    } catch (error) {
      console.error('❌ Ошибка обновления метаданных:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Удалить фотографию
   * @param {string} photoId - UUID фотографии
   */
  async deletePhoto(photoId) {
    try {
      await apiClient.delete(`/memory/photos/${photoId}`)
      console.log('✅ Фотография удалена')
      return true
    } catch (error) {
      console.error('❌ Ошибка удаления фотографии:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Скачать фотографию
   * @param {string} photoId - UUID фотографии
   * @param {string} fileName - имя файла для сохранения
   */
  async downloadPhoto(photoId, fileName) {
    try {
      const response = await apiClient.get(`/memory/photos/${photoId}/download`, {
        responseType: 'blob'
      })

      // Создаём ссылку для скачивания
      const url = window.URL.createObjectURL(new Blob([response.data]))
      const link = document.createElement('a')
      link.href = url
      link.setAttribute('download', fileName)
      document.body.appendChild(link)
      link.click()
      link.remove()
      window.URL.revokeObjectURL(url)

      return true
    } catch (error) {
      console.error('❌ Ошибка скачивания фотографии:', error.response?.data || error.message)
      throw error
    }
  }

  /**
   * Форматировать размер файла в человекочитаемый вид
   * @param {number} bytes - размер в байтах
   * @returns {string} отформатированный размер
   */
  static formatFileSize(bytes) {
    if (bytes === 0) return '0 B'
    const k = 1024
    const sizes = ['B', 'KB', 'MB', 'GB']
    const i = Math.floor(Math.log(bytes) / Math.log(k))
    return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i]
  }
}

export default new PhotoService()
