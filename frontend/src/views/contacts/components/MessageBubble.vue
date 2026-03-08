<template>
  <div class="message-bubble" :class="{ 'outgoing': message.outgoing }">
    <div class="message-content">
      <div class="message-text">{{ message.content }}</div>
      <div class="message-meta">
        <span class="message-time">{{ formatTime(message.sentAt) }}</span>
        <span v-if="message.outgoing && message.readAt" class="read-status" title="Прочитано">
          ✓✓
        </span>
        <span v-else-if="message.outgoing && !message.readAt" class="read-status" title="Доставлено">
          ✓
        </span>
        <button
          v-if="message.outgoing"
          class="delete-message"
          @click.stop="$emit('delete', message)"
          title="Удалить"
        >
          🗑️
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import contactService from '@/services/contactService'

const props = defineProps({
  message: {
    type: Object,
    required: true
  }
})

defineEmits(['delete'])

const formatTime = (dateString) => {
  return contactService.constructor.formatMessageTime(dateString)
}
</script>

<style scoped>
.message-bubble {
  display: flex;
  margin-bottom: 12px;
}

.message-bubble.outgoing {
  justify-content: flex-end;
}

.message-content {
  max-width: 70%;
  min-width: 120px;
  background-color: var(--color-white, #ffffff);
  border: 1px solid var(--color-light-gray, #e0e0e0);
  border-radius: 18px;
  padding: 12px 16px;
  position: relative;
  box-shadow: 0 2px 4px var(--shadow-color, rgba(0,0,0,0.05));
}

.message-bubble.outgoing .message-content {
  background-color: var(--color-primary, #88d424);
  border-color: var(--color-primary-dark, #6aa91c);
  color: white;
}

.message-text {
  font-size: 15px;
  line-height: 1.5;
  word-break: break-word;
  margin-bottom: 6px;
}

.message-meta {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 6px;
  font-size: 11px;
  color: var(--color-gray, #9e9e9e);
}

.message-bubble.outgoing .message-meta {
  color: rgba(255, 255, 255, 0.8);
}

.message-time {
  white-space: nowrap;
}

.read-status {
  font-size: 14px;
  line-height: 1;
}

.delete-message {
  background: none;
  border: none;
  font-size: 14px;
  cursor: pointer;
  opacity: 0;
  transition: opacity 0.2s ease;
  padding: 2px 4px;
  border-radius: 4px;
  color: inherit;
}

.message-content:hover .delete-message {
  opacity: 0.6;
}

.delete-message:hover {
  opacity: 1 !important;
  background-color: rgba(244, 67, 54, 0.2);
}

.message-bubble.outgoing .delete-message:hover {
  background-color: rgba(0, 0, 0, 0.2);
}

@media (max-width: 768px) {
  .message-content {
    max-width: 85%;
  }

  .delete-message {
    opacity: 0.6;
  }
}
</style>
