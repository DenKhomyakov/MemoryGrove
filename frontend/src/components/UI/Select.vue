<template>
  <div class="select-container" :class="{ 'has-error': error }">
    <label v-if="label" class="select-label" :for="id">
      {{ label }}
      <span v-if="required" class="required-star">*</span>
    </label>

    <div class="select-wrapper" :class="{ focused: isFocused, disabled: disabled }">
      <select
        :id="id"
        :value="modelValue"
        :disabled="disabled"
        class="select-input"
        @change="onChange"
        @focus="onFocus"
        @blur="onBlur"
        v-bind="$attrs"
      >
        <option value="" disabled>Выберите {{ label?.toLowerCase() }}</option>
        <option
          v-for="option in options"
          :key="option.value"
          :value="option.value"
        >
          {{ option.label }}
        </option>
      </select>

      <div class="select-arrow">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
          <path d="M7 10L12 15L17 10" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
        </svg>
      </div>
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
import { ref } from 'vue'

const props = defineProps({
  modelValue: [String, Number],
  label: String,
  options: {
    type: Array,
    required: true,
    validator: (value) => value.every(opt => 'value' in opt && 'label' in opt)
  },
  required: Boolean,
  disabled: Boolean,
  error: String,
  hint: String,
  id: {
    type: String,
    default: () => `select-${Math.random().toString(36).substr(2, 9)}`
  }
})

const emit = defineEmits(['update:modelValue', 'change', 'focus', 'blur'])

const isFocused = ref(false)

const onChange = (event) => {
  emit('update:modelValue', event.target.value)
  emit('change', event.target.value)
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
.select-container {
  margin-bottom: 20px;
  width: 100%;
}

.select-label {
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

.select-wrapper {
  position: relative;
  width: 100%;
  border: 1px solid var(--color-field-border, #d0d0d0);
  border-radius: 8px;
  background-color: var(--color-field-bg, #ffffff);
  transition: all 0.2s ease;
  cursor: pointer;
}

.select-wrapper:hover:not(.disabled) {
  border-color: var(--color-primary-light, #a8e454);
}

.select-wrapper.focused {
  border-color: var(--color-primary, #88d424);
  box-shadow: 0 0 0 3px rgba(136, 212, 36, 0.1);
}

.select-wrapper.disabled {
  background-color: var(--color-white-smoke, #f5f5f5);
  border-color: var(--color-light-gray, #e0e0e0);
  cursor: not-allowed;
  opacity: 0.7;
}

.select-input {
  width: 100%;
  padding: 12px 40px 12px 16px;
  font-size: 16px;
  line-height: 1.5;
  color: var(--color-dark-gray, #616161);
  background: transparent;
  border: none;
  outline: none;
  font-family: inherit;
  appearance: none;
  cursor: pointer;
}

.select-input:disabled {
  cursor: not-allowed;
}

.select-arrow {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--color-gray, #9e9e9e);
  pointer-events: none;
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

.select-container.has-error .select-wrapper {
  border-color: var(--color-error, #f44336);
}

.select-container.has-error .select-wrapper.focused {
  box-shadow: 0 0 0 3px rgba(244, 67, 54, 0.1);
}

.select-container.has-error .select-label {
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
