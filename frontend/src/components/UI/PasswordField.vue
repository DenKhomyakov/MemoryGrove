<template>
  <TextField
    :type="showPassword ? 'text' : 'password'"
    v-bind="$props"
    v-on="$listeners"
  >
    <template #append>
      <button
        type="button"
        class="password-toggle"
        @click="togglePassword"
        :title="showPassword ? 'Скрыть пароль' : 'Показать пароль'"
      >
        <svg
          v-if="showPassword"
          width="20"
          height="20"
          viewBox="0 0 24 24"
          fill="none"
        >
          <path
            d="M1 12C1 12 5 4 12 4C19 4 23 12 23 12C23 12 19 20 12 20C5 20 1 12 1 12Z"
            stroke="currentColor"
            stroke-width="2"
          />
          <circle cx="12" cy="12" r="3" fill="currentColor" />
        </svg>
        <svg
          v-else
          width="20"
          height="20"
          viewBox="0 0 24 24"
          fill="none"
        >
          <path
            d="M17.94 17.94C16.2306 19.243 14.1491 19.9649 12 20C5 20 1 12 1 12C2.24389 9.6819 3.96914 7.65663 6.06 6.06M9.9 4.24C10.5883 4.0789 11.2931 3.99834 12 4C19 4 23 12 23 12C22.393 13.1356 21.6691 14.2047 20.84 15.19M14.12 14.12C13.8454 14.4148 13.5141 14.6512 13.1462 14.8151C12.7782 14.9791 12.3809 15.0673 11.9781 15.0744C11.5753 15.0815 11.1752 15.0074 10.8016 14.8565C10.4281 14.7056 10.0887 14.4811 9.80385 14.1962C9.51897 13.9113 9.29439 13.5719 9.14351 13.1984C8.99262 12.8248 8.91853 12.4247 8.92563 12.0219C8.93274 11.6191 9.02091 11.2218 9.18488 10.8538C9.34884 10.4858 9.58525 10.1546 9.88 9.88"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
          />
          <line
            x1="1"
            y1="1"
            x2="23"
            y2="23"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
          />
        </svg>
      </button>
    </template>
  </TextField>
</template>

<script setup>
import { ref } from 'vue'
import TextField from './TextField.vue'

const props = defineProps({
  // Все пропсы TextField
})

const showPassword = ref(false)

const togglePassword = () => {
  showPassword.value = !showPassword.value
}
</script>

<style scoped>
.password-toggle {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  padding: 4px;
  cursor: pointer;
  color: var(--color-gray, #9e9e9e);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: color 0.2s ease;
  z-index: 1;
}

.password-toggle:hover {
  color: var(--color-primary, #88d424);
}

/* Увеличиваем правый отступ для поля с паролем */
:deep(.text-field-input) {
  padding-right: 44px;
}
</style>
