// Цветовая палитра MemoryGrove 🌳
// Основной цвет: #88d424

export const colors = {
  // Основные цвета
  primary: '#88d424',      // Основной зеленый
  primaryLight: '#a8e454',  // Светло-зеленый (для ховеров)
  primaryDark: '#6aa91c',   // Темно-зеленый (для активных состояний)

  // Нейтральные цвета
  white: '#ffffff',
  whiteSmoke: '#f5f5f5',    // Фон страниц
  lightGray: '#e0e0e0',     // Границы, разделители
  gray: '#9e9e9e',          // Второстепенный текст
  darkGray: '#616161',      // Основной текст

  // Цвета состояний
  error: '#f44336',          // Ошибки
  success: '#4caf50',        // Успех
  warning: '#ff9800',        // Предупреждения

  // Поля ввода
  fieldBorder: '#d0d0d0',
  fieldFocus: '#88d424',
  fieldError: '#f44336',
  fieldLabel: '#757575',
  fieldBackground: '#ffffff',

  // Тени
  shadow: 'rgba(0, 0, 0, 0.1)'
}

// CSS переменные для глобального использования
export const cssVariables = {
  '--color-primary': colors.primary,
  '--color-primary-light': colors.primaryLight,
  '--color-primary-dark': colors.primaryDark,
  '--color-white': colors.white,
  '--color-white-smoke': colors.whiteSmoke,
  '--color-light-gray': colors.lightGray,
  '--color-gray': colors.gray,
  '--color-dark-gray': colors.darkGray,
  '--color-error': colors.error,
  '--color-success': colors.success,
  '--color-warning': colors.warning,
  '--color-field-border': colors.fieldBorder,
  '--color-field-focus': colors.fieldFocus,
  '--color-field-error': colors.fieldError,
  '--color-field-label': colors.fieldLabel,
  '--color-field-bg': colors.fieldBackground,
  '--shadow-color': colors.shadow
}
