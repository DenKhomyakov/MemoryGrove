<template>
  <div class="reminder-card" :class="{ 'overdue': reminder.overdue }">
    <div class="card-header">
      <h3 class="reminder-title">{{ reminder.title }}</h3>
      <div class="status-badge" :style="{ backgroundColor: getStatusColor(reminder.status.id) }">
        {{ getStatusText(reminder.status.id) }}
      </div>
    </div>

    <p v-if="reminder.description" class="reminder-description">
      {{ reminder.description }}
    </p>

    <div class="reminder-time">
      <span class="time-icon">⏰</span>
      <span class="time-text">{{ reminder.formattedRemindAt }}</span>
      <span v-if="reminder.overdue" class="overdue-badge">Просрочено!</span>
    </div>

    <div class="card-footer">
      <div class="reminder-meta">
        <span class="meta-item">Создано: {{ reminder.formattedCreatedAt }}</span>
      </div>

      <div class="card-actions">
        <button
          v-if="reminder.status.id === ReminderService.Status.PENDING"
          class="icon-button complete-btn"
          @click.stop="$emit('complete', reminder)"
          title="Отметить выполненным"
        >
          ✅
        </button>
        <button
          class="icon-button edit-btn"
          @click.stop="$emit('edit', reminder)"
          title="Редактировать"
        >
          ✏️
        </button>
        <button
          class="icon-button delete-btn"
          @click.stop="$emit('delete', reminder)"
          title="Удалить"
        >
          🗑️
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import ReminderService from '@/services/reminderService'

const props = defineProps({
  reminder: {
    type: Object,
    required: true
  }
})

defineEmits(['complete', 'edit', 'delete'])

const getStatusText = (statusId) => {
  return ReminderService.getStatusText(statusId)
}

const getStatusColor = (statusId) => {
  return ReminderService.getStatusColor(statusId)
}
</script>

<style scoped>
.reminder-card {
  background: var(--color-white, #ffffff);
  border: 1px solid var(--color-light-gray, #e0e0e0);
  border-radius: 12px;
  padding: 1.5rem;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.reminder-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 12px var(--shadow-color, rgba(0,0,0,0.1));
}

.reminder-card.overdue {
  border-left: 4px solid var(--color-error, #f44336);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 1rem;
}

.reminder-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-primary-dark, #6aa91c);
  margin: 0;
  flex: 1;
}

.status-badge {
  font-size: 12px;
  color: white;
  padding: 4px 8px;
  border-radius: 20px;
  white-space: nowrap;
}

.reminder-description {
  color: var(--color-gray, #9e9e9e);
  font-size: 14px;
  line-height: 1.6;
  margin: 0;
  word-break: break-word;
}

.reminder-time {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--color-dark-gray, #616161);
  font-size: 14px;
  background-color: var(--color-white-smoke, #f5f5f5);
  padding: 8px 12px;
  border-radius: 8px;
}

.time-icon {
  font-size: 16px;
}

.time-text {
  font-weight: 500;
}

.overdue-badge {
  color: var(--color-error, #f44336);
  font-size: 12px;
  font-weight: 600;
  margin-left: auto;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
  padding-top: 0.5rem;
  border-top: 1px solid var(--color-light-gray, #e0e0e0);
}

.reminder-meta {
  color: var(--color-gray, #9e9e9e);
  font-size: 11px;
}

.card-actions {
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

.complete-btn:hover {
  background-color: rgba(76, 175, 80, 0.2);
}

.edit-btn:hover {
  background-color: rgba(33, 150, 243, 0.2);
}

.delete-btn:hover {
  background-color: rgba(244, 67, 54, 0.2);
}

@media (max-width: 768px) {
  .card-header {
    flex-direction: column;
  }

  .reminder-time {
    flex-wrap: wrap;
  }

  .card-footer {
    flex-direction: column;
    gap: 1rem;
    align-items: flex-start;
  }

  .card-actions {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>
