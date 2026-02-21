<template>
  <div class="textarea-container" :class="{ 'has-error': error }">
    <label v-if="label" class="textarea-label" :for="id">
      {{ label }}
      <span v-if="required" class="required-star">*</span>
    </label>

    <div class="textarea-wrapper" :class="{ focused: isFocused, disabled: disabled }">
      <textarea
        :id="id"
        :value="modelValue"
        :placeholder="placeholder"
        :disabled="disabled"
        :readonly="readonly"
        :rows="rows"
        class="textarea-input"
        @input="onInput"
        @focus="onFocus"
        @blur="onBlur"
        v-bind="$attrs"
      ></textarea>
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
  placeholder: String,
  required: Boolean,
  disabled: Boolean,
  readonly: Boolean,
  error: String,
  hint: String,
  rows: {
    type: Number,
    default: 3
  },
  id: {
    type: String,
    default: () => `textarea-${Math.random().toString(36).substr(2, 9)}`
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
.textarea-container {
  margin-bottom: 20px;
  width: 100%;
}

.textarea-label {
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

.textarea-wrapper {
  position: relative;
  width: 100%;
  border: 1px solid var(--color-field-border, #d0d0d0);
  border-radius: 8px;
  background-color: var(--color-field-bg, #ffffff);
  transition: all 0.2s ease;
}

.textarea-wrapper:hover:not(.disabled) {
  border-color: var(--color-primary-light, #a8e454);
}

.textarea-wrapper.focused {
  border-color: var(--color-primary, #88d424);
  box-shadow: 0 0 0 3px rgba(136, 212, 36, 0.1);
}

.textarea-wrapper.disabled {
  background-color: var(--color-white-smoke, #f5f5f5);
  border-color: var(--color-light-gray, #e0e0e0);
  cursor: not-allowed;
  opacity: 0.7;
}

.textarea-input {
  width: 100%;
  padding: 12px 16px;
  font-size: 16px;
  line-height: 1.5;
  color: var(--color-dark-gray, #616161);
  background: transparent;
  border: none;
  outline: none;
  font-family: inherit;
  resize: vertical;
}

.textarea-input::placeholder {
  color: var(--color-gray, #9e9e9e);
  opacity: 0.7;
}

.textarea-input:disabled {
  cursor: not-allowed;
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

.textarea-container.has-error .textarea-wrapper {
  border-color: var(--color-error, #f44336);
}

.textarea-container.has-error .textarea-wrapper.focused {
  box-shadow: 0 0 0 3px rgba(244, 67, 54, 0.1);
}

.textarea-container.has-error .textarea-label {
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
