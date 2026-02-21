<template>
  <button
    class="btn"
    :class="[
      variant,
      size,
      { 'btn-block': block, 'btn-loading': loading, 'btn-disabled': disabled }
    ]"
    :disabled="disabled || loading"
    :type="type"
    v-bind="$attrs"
    @click="$emit('click', $event)"
  >
    <span v-if="loading" class="spinner">
      <svg viewBox="0 0 24 24" width="20" height="20">
        <circle
          class="spinner-circle"
          cx="12"
          cy="12"
          r="10"
          fill="none"
          stroke="currentColor"
          stroke-width="3"
        />
      </svg>
    </span>
    <span class="btn-content">
      <slot />
    </span>
  </button>
</template>

<script setup>
defineProps({
  variant: {
    type: String,
    default: 'primary',
    validator: (value) => ['primary', 'secondary', 'outline', 'text'].includes(value)
  },
  size: {
    type: String,
    default: 'medium',
    validator: (value) => ['small', 'medium', 'large'].includes(value)
  },
  block: Boolean,
  loading: Boolean,
  disabled: Boolean,
  type: {
    type: String,
    default: 'button'
  }
})

defineEmits(['click'])
</script>

<style scoped>
.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border: none;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
  font-family: inherit;
}

/* Размеры */
.btn.small {
  padding: 8px 16px;
  font-size: 14px;
}

.btn.medium {
  padding: 12px 24px;
  font-size: 16px;
}

.btn.large {
  padding: 16px 32px;
  font-size: 18px;
}

/* Варианты */
.btn.primary {
  background-color: var(--color-primary, #88d424);
  color: var(--color-white, #ffffff);
}

.btn.primary:hover:not(:disabled) {
  background-color: var(--color-primary-dark, #6aa91c);
  transform: translateY(-1px);
  box-shadow: 0 4px 8px var(--shadow-color);
}

.btn.secondary {
  background-color: var(--color-white-smoke, #f5f5f5);
  color: var(--color-dark-gray, #616161);
}

.btn.secondary:hover:not(:disabled) {
  background-color: var(--color-light-gray, #e0e0e0);
}

.btn.outline {
  background-color: transparent;
  color: var(--color-primary, #88d424);
  border: 1px solid var(--color-primary, #88d424);
}

.btn.outline:hover:not(:disabled) {
  background-color: var(--color-primary, #88d424);
  color: var(--color-white, #ffffff);
}

.btn.text {
  background-color: transparent;
  color: var(--color-primary, #88d424);
  padding-left: 8px;
  padding-right: 8px;
}

.btn.text:hover:not(:disabled) {
  background-color: rgba(136, 212, 36, 0.1);
}

/* Блок */
.btn.btn-block {
  width: 100%;
}

/* Загрузка */
.btn-loading {
  cursor: wait;
  opacity: 0.8;
}

.spinner {
  animation: spin 1s linear infinite;
  display: inline-flex;
}

.spinner-circle {
  stroke-dasharray: 75;
  stroke-dashoffset: 75;
  animation: dash 1.5s ease-in-out infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@keyframes dash {
  0% {
    stroke-dashoffset: 75;
  }
  50% {
    stroke-dashoffset: 37.5;
  }
  100% {
    stroke-dashoffset: 75;
  }
}

/* Отключено */
.btn-disabled,
.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  pointer-events: none;
}
</style>
