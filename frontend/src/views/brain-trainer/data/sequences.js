/**
 * Данные для упражнения "Продолжи ряд"
 * Каждый элемент содержит последовательность и правильное продолжение
 */
export const sequences = [
  // Числовые последовательности (простые)
  {
    id: 1,
    type: 'number',
    difficulty: 'easy',
    sequence: [2, 4, 6, 8],
    options: [9, 10, 11, 12],
    correctIndex: 1,
    explanation: 'Каждое следующее число больше предыдущего на 2'
  },
  {
    id: 2,
    type: 'number',
    difficulty: 'easy',
    sequence: [5, 10, 15, 20],
    options: [22, 25, 30, 35],
    correctIndex: 1,
    explanation: 'Каждое следующее число больше предыдущего на 5'
  },
  {
    id: 3,
    type: 'number',
    difficulty: 'easy',
    sequence: [3, 6, 9, 12],
    options: [13, 14, 15, 16],
    correctIndex: 2,
    explanation: 'Каждое следующее число больше предыдущего на 3'
  },
  {
    id: 4,
    type: 'number',
    difficulty: 'easy',
    sequence: [10, 20, 30, 40],
    options: [45, 50, 55, 60],
    correctIndex: 1,
    explanation: 'Каждое следующее число больше предыдущего на 10'
  },

  // Числовые последовательности (средние)
  {
    id: 5,
    type: 'number',
    difficulty: 'medium',
    sequence: [2, 4, 8, 16],
    options: [18, 24, 32, 64],
    correctIndex: 2,
    explanation: 'Каждое следующее число умножается на 2'
  },
  {
    id: 6,
    type: 'number',
    difficulty: 'medium',
    sequence: [81, 27, 9, 3],
    options: [1, 2, 0, 6],
    correctIndex: 0,
    explanation: 'Каждое следующее число делится на 3'
  },
  {
    id: 7,
    type: 'number',
    difficulty: 'medium',
    sequence: [1, 4, 9, 16],
    options: [20, 24, 25, 36],
    correctIndex: 2,
    explanation: 'Квадраты чисел: 1², 2², 3², 4², следующее 5² = 25'
  },
  {
    id: 8,
    type: 'number',
    difficulty: 'medium',
    sequence: [2, 3, 5, 7],
    options: [8, 9, 10, 11],
    correctIndex: 3,
    explanation: 'Простые числа по порядку'
  },

  // Числовые последовательности (сложные)
  {
    id: 9,
    type: 'number',
    difficulty: 'hard',
    sequence: [1, 1, 2, 3, 5],
    options: [6, 7, 8, 9],
    correctIndex: 2,
    explanation: 'Числа Фибоначчи: каждое следующее равно сумме двух предыдущих (5 + 3 = 8)'
  },
  {
    id: 10,
    type: 'number',
    difficulty: 'hard',
    sequence: [2, 6, 12, 20],
    options: [24, 28, 30, 36],
    correctIndex: 2,
    explanation: '1×2, 2×3, 3×4, 4×5, следующее 5×6 = 30'
  },
  {
    id: 11,
    type: 'number',
    difficulty: 'hard',
    sequence: [3, 8, 15, 24],
    options: [30, 32, 35, 40],
    correctIndex: 2,
    explanation: '2²-1, 3²-1, 4²-1, 5²-1, следующее 6²-1 = 35'
  },

  // Буквенные последовательности
  {
    id: 12,
    type: 'letter',
    difficulty: 'easy',
    sequence: ['А', 'Б', 'В', 'Г'],
    options: ['Д', 'Е', 'Ё', 'Ж'],
    correctIndex: 0,
    explanation: 'Буквы алфавита по порядку'
  },
  {
    id: 13,
    type: 'letter',
    difficulty: 'easy',
    sequence: ['П', 'Н', 'В', 'С'],
    options: ['Ч', 'П', 'Ш', 'Щ'],
    correctIndex: 0,
    explanation: 'Дни недели: Понедельник, Вторник, Среда, Четверг, следующая Пятница (П)'
  },
  {
    id: 14,
    type: 'letter',
    difficulty: 'medium',
    sequence: ['Я', 'Ф', 'М', 'А'],
    options: ['М', 'И', 'А', 'Н'],
    correctIndex: 0,
    explanation: 'Месяцы: Январь, Февраль, Март, Апрель, следующий Май (М)'
  },
  {
    id: 15,
    type: 'letter',
    difficulty: 'medium',
    sequence: ['В', 'О', 'Л', 'Г'],
    options: ['А', 'Б', 'В', 'Г'],
    correctIndex: 0,
    explanation: 'Реки: Волга, Обь, Лена, Ганг, следующая Амазонка (А)'
  },

  // Логические последовательности
  {
    id: 16,
    type: 'logical',
    difficulty: 'easy',
    sequence: ['○', '○', '●', '○', '○', '●'],
    options: ['○', '●', '◑', '◐'],
    correctIndex: 1,
    explanation: 'Повторяющийся паттерн: два белых, один чёрный'
  },
  {
    id: 17,
    type: 'logical',
    difficulty: 'medium',
    sequence: ['▲', '■', '●', '▲', '■', '●'],
    options: ['▲', '■', '●', '◊'],
    correctIndex: 2,
    explanation: 'Повторяющийся паттерн из трёх фигур'
  },
  {
    id: 18,
    type: 'logical',
    difficulty: 'hard',
    sequence: ['↓', '↓', '↑', '↓', '↓', '↑'],
    options: ['↓', '↑', '←', '→'],
    correctIndex: 1,
    explanation: 'Паттерн: два вниз, один вверх'
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
    'medium': '🟡 Средний',
    'hard': '🔴 Сложный'
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
