<template>
  <div class="entry-card" @click="$emit('click', entry)">
    <div class="entry-header">
      <h3 class="entry-title">{{ entry.title }}</h3>
      <div class="entry-mood" v-if="entry.mood" :title="'Настроение: ' + entry.mood">
        {{ entry.mood }}
      </div>
    </div>

    <p class="entry-preview">{{ getPreview(entry.content) }}</p>

    <div class="entry-footer">
      <div class="entry-date">
        <span class="date-icon">📅</span>
        <span>{{ entry.formattedCreatedAt }}</span>
        <span v-if="entry.edited" class="edited-indicator" title="Было редактирование">📝</span>
      </div>

      <div class="entry-actions">
        <button
          class="icon-button view-btn"
          @click.stop="$emit('view', entry)"
          title="Просмотр"
        >
          👁️
        </button>
        <button
          class="icon-button edit-btn"
          @click.stop="$emit('edit', entry)"
          title="Редактировать"
        >
          ✏️
        </button>
        <button
          class="icon-button delete-btn"
          @click.stop="$emit('delete', entry)"
          title="Удалить"
        >
          🗑️
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  entry: {
    type: Object,
    required: true
  }
})

defineEmits(['click', 'view', 'edit', 'delete'])

// Получить превью текста (первые 150 символов)
const getPreview = (content) => {
  if (!content) return ''
  const preview = content.length > 150 ? content.substring(0, 150) + '...' : content
  return preview
}
</script>

<style scoped>
.entry-card {
  background: var(--color-white, #ffffff);
  border: 1px solid var(--color-light-gray, #e0e0e0);
  border-radius: 12px;
  padding: 1.5rem;
  transition: all 0.3s ease;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.entry-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 12px var(--shadow-color, rgba(0,0,0,0.1));
  border-color: var(--color-primary, #88d424);
}

.entry-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 1rem;
}

.entry-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-primary-dark, #6aa91c);
  margin: 0;
  flex: 1;
}

.entry-mood {
  font-size: 20px;
  background-color: var(--color-white-smoke, #f5f5f5);
  padding: 4px 8px;
  border-radius: 20px;
  cursor: help;
}

.entry-preview {
  color: var(--color-gray, #9e9e9e);
  font-size: 14px;
  line-height: 1.6;
  margin: 0;
  word-break: break-word;
}

.entry-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
  padding-top: 0.5rem;
  border-top: 1px solid var(--color-light-gray, #e0e0e0);
}

.entry-date {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--color-gray, #9e9e9e);
  font-size: 12px;
}

.date-icon {
  font-size: 14px;
}

.edited-indicator {
  font-size: 14px;
  opacity: 0.7;
  margin-left: 4px;
  cursor: help;
}

.entry-actions {
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

.view-btn:hover {
  background-color: rgba(136, 212, 36, 0.2);
  color: var(--color-primary-dark, #6aa91c);
}

.edit-btn:hover {
  background-color: rgba(33, 150, 243, 0.2);
  color: #1976d2;
}

.delete-btn:hover {
  background-color: rgba(244, 67, 54, 0.2);
  color: var(--color-error, #f44336);
}

@media (max-width: 768px) {
  .entry-header {
    flex-direction: column;
    gap: 0.5rem;
  }

  .entry-footer {
    flex-direction: column;
    gap: 1rem;
    align-items: flex-start;
  }

  .entry-actions {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>
