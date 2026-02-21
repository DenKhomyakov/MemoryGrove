<template>
  <div class="date-input-container" :class="{ 'has-error': error }">
    <label v-if="label" class="date-input-label" :for="id">
      {{ label }}
      <span v-if="required" class="required-star">*</span>
    </label>

    <div class="date-input-wrapper" :class="{ focused: isFocused, disabled: disabled }">
      <input
        :id="id"
        type="date"
        :value="modelValue"
        :min="minDate"
        :max="maxDate"
        :disabled="disabled"
        :readonly="readonly"
        class="date-input"
        @input="onInput"
        @focus="onFocus"
        @blur="onBlur"
        v-bind="$attrs"
      />
    </div>

    <div v-if="error" class="error-message">
      {{ error }}
    </div>

    <div v-if="hint && !error" class="hint-text">
      {{ hint }}
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  modelValue: [String, Date],
  label: String,
  required: Boolean,
  disabled: Boolean,
  readonly: Boolean,
  error: String,
  hint: String,
  minDate: {
    type: String,
    default: '1900-01-01'
  },
  maxDate: {
    type: String,
    default: () => {
      const today = new Date()
      return today.toISOString().split('T')[0]
    }
  },
  id: {
    type: String,
    default: () => `date-${Math.random().toString(36).substr(2, 9)}`
  }
})

const emit = defineEmits(['update:modelValue', 'focus', 'blur'])

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
</script>

<style scoped>
.date-input-container {
  margin-bottom: 20px;
  width: 100%;
}

.date-input-label {
  display: block;
  margin-bottom: 6px;
  font-size: 14px;
  font-weight: 500;
  color: var(--color-field-label, #757575);
}

.required-star {
  color: var(--color-error, #f44336);
  margin-left: 4px;
}

.date-input-wrapper {
  position: relative;
  width: 100%;
  border: 1px solid var(--color-field-border, #d0d0d0);
  border-radius: 8px;
  background-color: var(--color-field-bg, #ffffff);
  transition: all 0.2s ease;
}

.date-input-wrapper:hover:not(.disabled) {
  border-color: var(--color-primary-light, #a8e454);
}

.date-input-wrapper.focused {
  border-color: var(--color-primary, #88d424);
  box-shadow: 0 0 0 3px rgba(136, 212, 36, 0.1);
}

.date-input-wrapper.disabled {
  background-color: var(--color-white-smoke, #f5f5f5);
  border-color: var(--color-light-gray, #e0e0e0);
  cursor: not-allowed;
  opacity: 0.7;
}

.date-input {
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

.date-input::-webkit-calendar-picker-indicator {
  cursor: pointer;
  opacity: 0.6;
  transition: opacity 0.2s ease;
}

.date-input::-webkit-calendar-picker-indicator:hover {
  opacity: 1;
}

.date-input:disabled {
  cursor: not-allowed;
}

.date-input:disabled::-webkit-calendar-picker-indicator {
  cursor: not-allowed;
  opacity: 0.3;
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

.date-input-container.has-error .date-input-wrapper {
  border-color: var(--color-error, #f44336);
}

.date-input-container.has-error .date-input-wrapper.focused {
  box-shadow: 0 0 0 3px rgba(244, 67, 54, 0.1);
}

.date-input-container.has-error .date-input-label {
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
