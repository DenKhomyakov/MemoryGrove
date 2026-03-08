<template>
  <div class="contact-card" :class="{ 'inactive': !contact.isActive }" @click="$emit('click', contact)">
    <div class="contact-avatar" :style="{ backgroundColor: getContactColor(contact.contactId) }">
      <span class="avatar-text">{{ getContactIcon(contact) }}</span>
    </div>

    <div class="contact-info">
      <div class="contact-header">
        <h3 class="contact-name">{{ contact.displayName }}</h3>
        <div class="contact-badges">
          <span v-if="contact.nickname" class="badge nickname-badge" title="Никнейм">
            ✏️ {{ contact.nickname }}
          </span>
          <span v-if="!contact.isActive" class="badge inactive-badge">
            🔕 Неактивен
          </span>
        </div>
      </div>

      <div class="contact-details">
        <div class="detail-item">
          <span class="detail-icon">👤</span>
          <span class="detail-text">{{ contact.fullName }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-icon">📞</span>
          <span class="detail-text">{{ contact.phone }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-icon">✉️</span>
          <span class="detail-text">{{ contact.email }}</span>
        </div>
      </div>

      <div class="contact-footer">
        <div class="contact-meta">
          <span class="meta-item">Добавлен: {{ contact.formattedAddedAt }}</span>
        </div>

        <div class="contact-actions">
          <button
            class="icon-button chat-btn"
            @click.stop="$emit('chat', contact)"
            title="Написать сообщение"
          >
            💬
          </button>
          <button
            class="icon-button edit-btn"
            @click.stop="$emit('edit', contact)"
            title="Редактировать"
          >
            ✏️
          </button>
          <button
            class="icon-button delete-btn"
            @click.stop="$emit('delete', contact)"
            title="Удалить"
          >
            🗑️
          </button>
        </div>
      </div>
    </div>

    <!-- Индикатор непрочитанных сообщений -->
    <div v-if="unreadCount > 0" class="unread-badge">
      {{ unreadCount }}
    </div>
  </div>
</template>

<script setup>
import contactService from '@/services/contactService'

const props = defineProps({
  contact: {
    type: Object,
    required: true
  },
  unreadCount: {
    type: Number,
    default: 0
  }
})

defineEmits(['click', 'chat', 'edit', 'delete'])

const getContactIcon = (contact) => {
  return contactService.constructor.getContactIcon(contact)
}

const getContactColor = (contactId) => {
  return contactService.constructor.getContactColor(contactId)
}
</script>

<style scoped>
.contact-card {
  background: var(--color-white, #ffffff);
  border: 1px solid var(--color-light-gray, #e0e0e0);
  border-radius: 12px;
  padding: 1.5rem;
  transition: all 0.3s ease;
  display: flex;
  gap: 1.5rem;
  position: relative;
  cursor: pointer;
}

.contact-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 12px var(--shadow-color, rgba(0,0,0,0.1));
  border-color: var(--color-primary, #88d424);
}

.contact-card.inactive {
  opacity: 0.7;
  background-color: var(--color-white-smoke, #f5f5f5);
}

.contact-card.inactive:hover {
  opacity: 0.9;
}

/* Аватар */
.contact-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.avatar-text {
  color: white;
  font-size: 24px;
  font-weight: 600;
  text-transform: uppercase;
}

/* Информация о контакте */
.contact-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.contact-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 1rem;
  flex-wrap: wrap;
}

.contact-name {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-primary-dark, #6aa91c);
  margin: 0;
}

.contact-badges {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.badge {
  font-size: 11px;
  padding: 4px 8px;
  border-radius: 12px;
  white-space: nowrap;
}

.nickname-badge {
  background-color: rgba(136, 212, 36, 0.1);
  color: var(--color-primary-dark, #6aa91c);
  border: 1px solid var(--color-primary, #88d424);
}

.inactive-badge {
  background-color: rgba(244, 67, 54, 0.1);
  color: var(--color-error, #f44336);
  border: 1px solid var(--color-error, #f44336);
}

/* Детали */
.contact-details {
  display: flex;
  flex-direction: column;
  gap: 6px;
  background-color: var(--color-white-smoke, #f5f5f5);
  padding: 0.75rem;
  border-radius: 8px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: var(--color-dark-gray, #616161);
}

.detail-icon {
  font-size: 14px;
  min-width: 20px;
}

.detail-text {
  word-break: break-word;
}

/* Футер */
.contact-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
  flex-wrap: wrap;
  gap: 10px;
}

.contact-meta {
  color: var(--color-gray, #9e9e9e);
  font-size: 11px;
}

.contact-actions {
  display: flex;
  gap: 8px;
}

.icon-button {
  background: none;
  border: none;
  font-size: 18px;
  padding: 4px 8px;
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.2s ease;
  opacity: 0.6;
}

.icon-button:hover {
  opacity: 1;
  transform: scale(1.1);
}

.chat-btn:hover {
  background-color: rgba(136, 212, 36, 0.2);
  color: var(--color-primary, #88d424);
}

.edit-btn:hover {
  background-color: rgba(33, 150, 243, 0.2);
  color: #1976d2;
}

.delete-btn:hover {
  background-color: rgba(244, 67, 54, 0.2);
  color: var(--color-error, #f44336);
}

/* Индикатор непрочитанных */
.unread-badge {
  position: absolute;
  top: -5px;
  right: -5px;
  min-width: 20px;
  height: 20px;
  background-color: var(--color-error, #f44336);
  color: white;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 6px;
  box-shadow: 0 2px 4px var(--shadow-color, rgba(0,0,0,0.2));
}

@media (max-width: 768px) {
  .contact-card {
    flex-direction: column;
    padding: 1rem;
  }

  .contact-avatar {
    width: 50px;
    height: 50px;
  }

  .avatar-text {
    font-size: 20px;
  }

  .contact-header {
    flex-direction: column;
  }

  .contact-footer {
    flex-direction: column;
    align-items: flex-start;
  }

  .contact-actions {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>
