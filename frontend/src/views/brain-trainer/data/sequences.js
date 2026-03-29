/**
 * Данные для упражнения "Продолжи ряд"
 * Каждый элемент содержит последовательность и правильное продолжение
 * Все последовательности простые и понятные для людей с нарушениями памяти
 */

export const sequences = [
  {
    id: 1,
    type: 'number',
    difficulty: 'easy',
    sequence: [1, 2, 3, 4],
    options: [5, 6, 7, 8],
    correctIndex: 0,
    explanation: 'Каждое следующее число больше предыдущего на 1'
  },
  {
    id: 2,
    type: 'number',
    difficulty: 'easy',
    sequence: [5, 6, 7, 8],
    options: [9, 10, 11, 12],
    correctIndex: 0,
    explanation: 'Каждое следующее число больше предыдущего на 1'
  },
  {
    id: 3,
    type: 'number',
    difficulty: 'easy',
    sequence: [2, 4, 6, 8],
    options: [9, 10, 11, 12],
    correctIndex: 1,
    explanation: 'Каждое следующее число больше предыдущего на 2'
  },
  {
    id: 4,
    type: 'number',
    difficulty: 'easy',
    sequence: [3, 5, 7, 9],
    options: [10, 11, 12, 13],
    correctIndex: 1,
    explanation: 'Каждое следующее число больше предыдущего на 2'
  },
  {
    id: 5,
    type: 'number',
    difficulty: 'easy',
    sequence: [3, 6, 9, 12],
    options: [13, 14, 15, 16],
    correctIndex: 2,
    explanation: 'Каждое следующее число больше предыдущего на 3'
  },
  {
    id: 6,
    type: 'number',
    difficulty: 'easy',
    sequence: [4, 7, 10, 13],
    options: [14, 15, 16, 17],
    correctIndex: 2,
    explanation: 'Каждое следующее число больше предыдущего на 3'
  },
  {
    id: 7,
    type: 'number',
    difficulty: 'easy',
    sequence: [5, 10, 15, 20],
    options: [22, 23, 24, 25],
    correctIndex: 3,
    explanation: 'Каждое следующее число больше предыдущего на 5'
  },
  {
    id: 8,
    type: 'number',
    difficulty: 'easy',
    sequence: [10, 15, 20, 25],
    options: [28, 30, 32, 35],
    correctIndex: 1,
    explanation: 'Каждое следующее число больше предыдущего на 5'
  },
  {
    id: 9,
    type: 'number',
    difficulty: 'easy',
    sequence: [10, 20, 30, 40],
    options: [45, 50, 55, 60],
    correctIndex: 1,
    explanation: 'Каждое следующее число больше предыдущего на 10'
  },
  {
    id: 10,
    type: 'number',
    difficulty: 'easy',
    sequence: [15, 25, 35, 45],
    options: [50, 55, 60, 65],
    correctIndex: 1,
    explanation: 'Каждое следующее число больше предыдущего на 10'
  },
  {
    id: 11,
    type: 'number',
    difficulty: 'medium',
    sequence: [1, 2, 4, 8],
    options: [10, 12, 14, 16],
    correctIndex: 3,
    explanation: 'Каждое следующее число умножается на 2'
  },
  {
    id: 12,
    type: 'number',
    difficulty: 'medium',
    sequence: [2, 4, 8, 16],
    options: [18, 20, 24, 32],
    correctIndex: 3,
    explanation: 'Каждое следующее число умножается на 2'
  },
  {
    id: 13,
    type: 'number',
    difficulty: 'medium',
    sequence: [1, 3, 9, 27],
    options: [30, 36, 54, 81],
    correctIndex: 3,
    explanation: 'Каждое следующее число умножается на 3'
  },
  {
    id: 14,
    type: 'number',
    difficulty: 'medium',
    sequence: [1, 3, 5, 7],
    options: [8, 9, 10, 11],
    correctIndex: 1,
    explanation: 'Нечётные числа по порядку: 1, 3, 5, 7, следующее 9'
  },
  {
    id: 15,
    type: 'number',
    difficulty: 'medium',
    sequence: [2, 4, 6, 8],
    options: [9, 10, 11, 12],
    correctIndex: 1,
    explanation: 'Чётные числа по порядку: 2, 4, 6, 8, следующее 10'
  },
  {
    id: 16,
    type: 'letter',
    difficulty: 'easy',
    sequence: ['А', 'Б', 'В', 'Г'],
    options: ['Д', 'Е', 'Ё', 'Ж'],
    correctIndex: 0,
    explanation: 'Буквы алфавита по порядку'
  },
  {
    id: 17,
    type: 'letter',
    difficulty: 'easy',
    sequence: ['О', 'П', 'Р', 'С'],
    options: ['Т', 'У', 'Ф', 'Х'],
    correctIndex: 0,
    explanation: 'Буквы алфавита по порядку'
  },
  {
    id: 18,
    type: 'letter',
    difficulty: 'easy',
    sequence: ['К', 'Л', 'М', 'Н'],
    options: ['О', 'П', 'Р', 'С'],
    correctIndex: 0,
    explanation: 'Буквы алфавита по порядку'
  },
  {
    id: 19,
    type: 'letter',
    difficulty: 'easy',
    sequence: ['ПН', 'ВТ', 'СР', 'ЧТ'],
    options: ['ПТ', 'СБ', 'ВС', 'ПН'],
    correctIndex: 0,
    explanation: 'Дни недели: ПН, ВТ, СР, ЧТ, следующая ПТ (пятница)'
  },
  {
    id: 20,
    type: 'letter',
    difficulty: 'easy',
    sequence: ['ВТ', 'СР', 'ЧТ', 'ПТ'],
    options: ['СБ', 'ВС', 'ПН', 'ВТ'],
    correctIndex: 0,
    explanation: 'Дни недели: ВТ, СР, ЧТ, ПТ, следующая СБ (суббота)'
  },
  {
    id: 21,
    type: 'letter',
    difficulty: 'medium',
    sequence: ['Янв', 'Фев', 'Мар', 'Апр'],
    options: ['Май', 'Июн', 'Июл', 'Авг'],
    correctIndex: 0,
    explanation: 'Месяцы по порядку: январь, февраль, март, апрель, следующий май'
  },
  {
    id: 22,
    type: 'letter',
    difficulty: 'medium',
    sequence: ['Сен', 'Окт', 'Ноя', 'Дек'],
    options: ['Янв', 'Фев', 'Мар', 'Апр'],
    correctIndex: 0,
    explanation: 'Месяцы по порядку: сентябрь, октябрь, ноябрь, декабрь, следующий январь'
  },
  {
    id: 23,
    type: 'logical',
    difficulty: 'easy',
    sequence: ['○', '○', '●', '○', '○', '●'],
    options: ['○', '●', '◑', '◐'],
    correctIndex: 0,
    explanation: 'Повторяющиеся символы: два белых кружка, один чёрный'
  },
  {
    id: 24,
    type: 'logical',
    difficulty: 'easy',
    sequence: ['▲', '▲', '■', '▲', '▲', '■'],
    options: ['▲', '■', '●', '◆'],
    correctIndex: 0,
    explanation: 'Повторяющиеся символы: два треугольника, один квадрат'
  },
  {
    id: 25,
    type: 'logical',
    difficulty: 'medium',
    sequence: ['↑', '↑', '↓', '↑', '↑', '↓'],
    options: ['↑', '↓', '←', '→'],
    correctIndex: 0,
    explanation: 'Повторяющиеся символы: две стрелки вверх, одна вниз'
  }
]

/**
 * Получить случайную последовательность
 * @param {string} difficulty - уровень сложности (easy, medium, hard, all)
 * @returns {Object} случайная последовательность
 */
export const getRandomSequence = (difficulty = 'all') => {
  let filtered = sequences
  if (difficulty !== 'all') {
    filtered = sequences.filter(s => s.difficulty === difficulty)
  }
  const randomIndex = Math.floor(Math.random() * filtered.length)
  return { ...filtered[randomIndex] }
}

/**
 * Получить последовательность по ID
 * @param {number} id - ID последовательности
 * @returns {Object} последовательность
 */
export const getSequenceById = (id) => {
  return sequences.find(s => s.id === id)
}

/**
 * Получить отображаемое название сложности
 * @param {string} difficulty - уровень сложности
 * @returns {string} отображаемое название
 */
export const getDifficultyDisplay = (difficulty) => {
  const map = {
    'easy': '🟢 Простой',
    'medium': '🟡 Средний'
  }
  return map[difficulty] || difficulty
}

/**
 * Получить цвет сложности
 * @param {string} difficulty - уровень сложности
 * @returns {string} CSS цвет
 */
export const getDifficultyColor = (difficulty) => {
  const map = {
    'easy': 'var(--color-success, #4caf50)',
    'medium': 'var(--color-warning, #ff9800)',
    'hard': 'var(--color-error, #f44336)'
  }
  return map[difficulty] || 'var(--color-gray, #9e9e9e)'
}
