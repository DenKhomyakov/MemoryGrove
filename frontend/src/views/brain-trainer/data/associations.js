/**
 * Данные для упражнения "Ассоциации"
 * Каждый элемент содержит слово и варианты ассоциаций
 */
export const associations = [
  {
    id: 1,
    word: 'Врач',
    options: [
      { text: 'Больница', isCorrect: true },
      { text: 'Книга', isCorrect: false },
      { text: 'Машина', isCorrect: false },
      { text: 'Дождь', isCorrect: false }
    ],
    hint: 'Где работает врач?'
  },
  {
    id: 2,
    word: 'Хлеб',
    options: [
      { text: 'Магазин', isCorrect: false },
      { text: 'Пекарня', isCorrect: true },
      { text: 'Школа', isCorrect: false },
      { text: 'Театр', isCorrect: false }
    ],
    hint: 'Где пекут хлеб?'
  },
  {
    id: 3,
    word: 'Книга',
    options: [
      { text: 'Читать', isCorrect: true },
      { text: 'Спать', isCorrect: false },
      { text: 'Бегать', isCorrect: false },
      { text: 'Плавать', isCorrect: false }
    ],
    hint: 'Что делают с книгой?'
  },
  {
    id: 4,
    word: 'Зима',
    options: [
      { text: 'Снег', isCorrect: true },
      { text: 'Цветы', isCorrect: false },
      { text: 'Листья', isCorrect: false },
      { text: 'Грибы', isCorrect: false }
    ],
    hint: 'Что идёт зимой?'
  },
  {
    id: 5,
    word: 'Собака',
    options: [
      { text: 'Конура', isCorrect: true },
      { text: 'Гнездо', isCorrect: false },
      { text: 'Дупло', isCorrect: false },
      { text: 'Нора', isCorrect: false }
    ],
    hint: 'Где живёт собака?'
  },
  {
    id: 6,
    word: 'Учитель',
    options: [
      { text: 'Школа', isCorrect: true },
      { text: 'Больница', isCorrect: false },
      { text: 'Магазин', isCorrect: false },
      { text: 'Завод', isCorrect: false }
    ],
    hint: 'Где работает учитель?'
  },
  {
    id: 7,
    word: 'Молоко',
    options: [
      { text: 'Корова', isCorrect: true },
      { text: 'Курица', isCorrect: false },
      { text: 'Овца', isCorrect: false },
      { text: 'Лошадь', isCorrect: false }
    ],
    hint: 'Кто даёт молоко?'
  },
  {
    id: 8,
    word: 'Самолёт',
    options: [
      { text: 'Небо', isCorrect: true },
      { text: 'Море', isCorrect: false },
      { text: 'Земля', isCorrect: false },
      { text: 'Река', isCorrect: false }
    ],
    hint: 'Где летает самолёт?'
  },
  {
    id: 9,
    word: 'Повар',
    options: [
      { text: 'Готовит', isCorrect: true },
      { text: 'Рисует', isCorrect: false },
      { text: 'Поёт', isCorrect: false },
      { text: 'Строит', isCorrect: false }
    ],
    hint: 'Что делает повар?'
  },
  {
    id: 10,
    word: 'Лето',
    options: [
      { text: 'Жара', isCorrect: true },
      { text: 'Холод', isCorrect: false },
      { text: 'Снег', isCorrect: false },
      { text: 'Лёд', isCorrect: false }
    ],
    hint: 'Какая погода летом?'
  },
  {
    id: 11,
    word: 'Рыба',
    options: [
      { text: 'Река', isCorrect: true },
      { text: 'Лес', isCorrect: false },
      { text: 'Поле', isCorrect: false },
      { text: 'Гора', isCorrect: false }
    ],
    hint: 'Где живёт рыба?'
  },
  {
    id: 12,
    word: 'Почтальон',
    options: [
      { text: 'Письма', isCorrect: true },
      { text: 'Лекарства', isCorrect: false },
      { text: 'Продукты', isCorrect: false },
      { text: 'Игрушки', isCorrect: false }
    ],
    hint: 'Что разносит почтальон?'
  }
]

/**
 * Получить случайную ассоциацию
 * @returns {Object} случайная ассоциация
 */
export const getRandomAssociation = () => {
  const randomIndex = Math.floor(Math.random() * associations.length)
  return associations[randomIndex]
}

/**
 * Получить ассоциацию по ID
 * @param {number} id - ID ассоциации
 * @returns {Object} ассоциация
 */
export const getAssociationById = (id) => {
  return associations.find(item => item.id === id)
}

/**
 * Перемешать варианты ответов
 * @param {Array} options - массив вариантов
 * @returns {Array} перемешанный массив
 */
export const shuffleOptions = (options) => {
  const shuffled = [...options]
  for (let i = shuffled.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1))
    ;[shuffled[i], shuffled[j]] = [shuffled[j], shuffled[i]]
  }
  return shuffled
}
