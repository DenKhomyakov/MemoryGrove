<template>
  <div class="text-field-container" :class="{ 'has-error': error }">
    <label v-if="label" class="text-field-label" :for="id">
      {{ label }}
      <span v-if="required" class="required-star">*</span>
    </label>

    <div class="input-wrapper" :class="{ focused: isFocused, disabled: disabled }">
      <input
        :id="id"
        :type="type"
        :value="modelValue"
        :placeholder="placeholder"
        :disabled="disabled"
        :readonly="readonly"
        class="text-field-input"
        @input="onInput"
        @focus="onFocus"
        @blur="onBlur"
        v-bind="$attrs"
      />

      <!-- Иконка очистки (при наличии текста) -->
      <button
        v-if="clearable && modelValue && !disabled"
        class="clear-button"
        @click="clearValue"
        type="button"
      >
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
          <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
        </svg>
      </button>
    </div>

    <!-- Сообщение об ошибке -->
    <div v-if="error" class="error-message">
      {{ error }}
    </div>

    <!-- Вспомогательный текст -->
    <div v-if="hint && !error" class="hint-text">
      {{ hint }}
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  modelValue: [String, Number],
  label: String,
  type: {
    type: String,
    default: 'text'
  },
  placeholder: String,
  required: Boolean,
  disabled: Boolean,
  readonly: Boolean,
  error: String,
  hint: String,
  clearable: Boolean,
  id: {
    type: String,
    default: () => `field-${Math.random().toString(36).substr(2, 9)}`
  }
})

const emit = defineEmits(['update:modelValue', 'focus', 'blur', 'clear'])

const isFocused = ref(false)

const onInput = (event) => {
  emit('update:modelValue', event.target.value)
}

const onFocus = (event) => {
  isFocused.value = true
  emit('focus', event)
}

const onBlur = (event) => {
  isFocused.value = false
  emit('blur', event)
}

const clearValue = () => {
  emit('update:modelValue', '')
  emit('clear')
}
</script>

<style scoped>
.text-field-container {
  margin-bottom: 20px;
  width: 100%;
}

.text-field-label {
  display: block;
  margin-bottom: 6px;
  font-size: 14px;
  font-weight: 500;
  color: var(--color-field-label, #757575);
  transition: color 0.2s ease;
}

.required-star {
  color: var(--color-error, #f44336);
  margin-left: 4px;
}

.input-wrapper {
  position: relative;
  width: 100%;
  border: 1px solid var(--color-field-border, #d0d0d0);
  border-radius: 8px;
  background-color: var(--color-field-bg, #ffffff);
  transition: all 0.2s ease;
}

.input-wrapper:hover:not(.disabled) {
  border-color: var(--color-primary-light, #a8e454);
}

.input-wrapper.focused {
  border-color: var(--color-primary, #88d424);
  box-shadow: 0 0 0 3px rgba(136, 212, 36, 0.1);
}

.input-wrapper.disabled {
  background-color: var(--color-white-smoke, #f5f5f5);
  border-color: var(--color-light-gray, #e0e0e0);
  cursor: not-allowed;
  opacity: 0.7;
}

.input-wrapper.disabled .text-field-input {
  cursor: not-allowed;
}

.text-field-input {
  width: 100%;
  padding: 12px 16px;
  font-size: 16px;
  line-height: 1.5;
  color: var(--color-dark-gray, #616161);
  background: transparent;
  border: none;
  outline: none;
  font-family: inherit;
}

.text-field-input::placeholder {
  color: var(--color-gray, #9e9e9e);
  opacity: 0.7;
}

.text-field-input:disabled {
  cursor: not-allowed;
}

.clear-button {
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
}

.clear-button:hover {
  color: var(--color-primary, #88d424);
}

.error-message {
  margin-top: 4px;
  font-size: 12px;
  color: var(--color-error, #f44336);
  animation: slideIn 0.2s ease;
}

.hint-text {
  margin-top: 4px;
  font-size: 12px;
  color: var(--color-gray, #9e9e9e);
}

/* Состояние ошибки */
.text-field-container.has-error .input-wrapper {
  border-color: var(--color-error, #f44336);
}

.text-field-container.has-error .input-wrapper.focused {
  box-shadow: 0 0 0 3px rgba(244, 67, 54, 0.1);
}

.text-field-container.has-error .text-field-label {
  color: var(--color-error, #f44336);
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-5px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
