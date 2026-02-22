<template>
  <Transition
    enter-active-class="notification-enter-active"
    leave-active-class="notification-leave-active"
    @after-leave="onAfterLeave"
  >
    <div
      v-if="visible"
      class="notification"
      :class="`notification-${type}`"
      role="alert"
    >
      <div class="notification-content">
        <span class="notification-icon" v-html="icon"></span>
        <span class="notification-message">{{ message }}</span>
      </div>
      <button
        v-if="closable"
        class="notification-close"
        @click="close"
        aria-label="Закрыть уведомление"
      >
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
          <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
        </svg>
      </button>
    </div>
  </Transition>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'

const props = defineProps({
  type: {
    type: String,
    default: 'info',
    validator: (value) => ['success', 'error', 'info'].includes(value)
  },
  message: {
    type: String,
    required: true
  },
  duration: {
    type: Number,
    default: 3000
  },
  closable: {
    type: Boolean,
    default: true
  },
  visible: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['close', 'update:visible'])

const icon = computed(() => {
  switch (props.type) {
    case 'success':
      return '<svg width="20" height="20" viewBox="0 0 24 24" fill="none"><path d="M20 6L9 17L4 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>'
    case 'error':
      return '<svg width="20" height="20" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/><path d="M12 8v5M12 16v0" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>'
    case 'info':
    default:
      return '<svg width="20" height="20" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/><path d="M12 8v5M12 16v0" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>'
  }
})

const close = () => {
  emit('update:visible', false)
  emit('close')
}

const onAfterLeave = () => {
  emit('close')
}

// Автоматическое закрытие через duration
let timeoutId = null

onMounted(() => {
  if (props.duration > 0) {
    timeoutId = setTimeout(() => {
      close()
    }, props.duration)
  }
})
</script>

<style scoped>
.notification {
  position: fixed;
  top: 80px;
  right: 20px;
  min-width: 320px;
  max-width: 480px;
  padding: 16px 20px;
  background-color: var(--color-white, #ffffff);
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  z-index: 9999;
  border-left: 4px solid transparent;
  animation: slideIn 0.3s ease;
}

.notification-content {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.notification-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.notification-message {
  font-size: 14px;
  line-height: 1.5;
  color: var(--color-dark-gray, #616161);
  word-break: break-word;
}

.notification-close {
  background: none;
  border: none;
  padding: 4px;
  cursor: pointer;
  color: var(--color-gray, #9e9e9e);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: color 0.2s ease;
  flex-shrink: 0;
}

.notification-close:hover {
  color: var(--color-dark-gray, #616161);
}

/* Типы уведомлений */
.notification-success {
  border-left-color: var(--color-success, #4caf50);
}

.notification-success .notification-icon {
  color: var(--color-success, #4caf50);
}

.notification-error {
  border-left-color: var(--color-error, #f44336);
}

.notification-error .notification-icon {
  color: var(--color-error, #f44336);
}

.notification-info {
  border-left-color: var(--color-primary, #88d424);
}

.notification-info .notification-icon {
  color: var(--color-primary, #88d424);
}

/* Анимации */
@keyframes slideIn {
  from {
    transform: translateX(100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

.notification-enter-active {
  animation: slideIn 0.3s ease;
}

.notification-leave-active {
  animation: slideIn 0.3s ease reverse;
}

/* Адаптивность */
@media (max-width: 768px) {
  .notification {
    top: 60px;
    right: 10px;
    left: 10px;
    min-width: auto;
    max-width: none;
  }
}
</style>
