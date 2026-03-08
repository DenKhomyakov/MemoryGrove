<template>
  <div class="message-templates">
    <div class="templates-header">
      <h3>📋 Быстрые ответы</h3>
      <button
        class="toggle-templates"
        @click="showTemplates = !showTemplates"
      >
        {{ showTemplates ? '▲' : '▼' }}
      </button>
    </div>

    <div v-if="showTemplates" class="templates-list">
      <button
        v-for="template in templates"
        :key="template.id"
        class="template-button"
        @click="$emit('select', template)"
        :title="template.content"
      >
        {{ template.title }}
      </button>

      <!-- Кнопка управления шаблонами (будет позже) -->
      <button class="template-button manage-button" @click="$emit('manage')">
        ✏️ Управлять
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

defineProps({
  templates: {
    type: Array,
    default: () => []
  }
})

defineEmits(['select', 'manage'])

const showTemplates = ref(true)
</script>

<style scoped>
.message-templates {
  background-color: var(--color-white-smoke, #f5f5f5);
  border-radius: 12px;
  padding: 1rem;
  margin-bottom: 1rem;
}

.templates-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.templates-header h3 {
  font-size: 16px;
  color: var(--color-primary-dark, #6aa91c);
  margin: 0;
}

.toggle-templates {
  background: none;
  border: none;
  font-size: 16px;
  cursor: pointer;
  color: var(--color-gray, #9e9e9e);
  padding: 4px 8px;
  border-radius: 4px;
  transition: all 0.2s ease;
}

.toggle-templates:hover {
  color: var(--color-primary, #88d424);
  background-color: rgba(136, 212, 36, 0.1);
}

.templates-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 1rem;
}

.template-button {
  background-color: var(--color-white, #ffffff);
  border: 1px solid var(--color-light-gray, #e0e0e0);
  border-radius: 20px;
  padding: 6px 12px;
  font-size: 13px;
  color: var(--color-dark-gray, #616161);
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
  max-width: 150px;
  overflow: hidden;
  text-overflow: ellipsis;
}

.template-button:hover {
  border-color: var(--color-primary, #88d424);
  color: var(--color-primary-dark, #6aa91c);
  transform: translateY(-1px);
}

.manage-button {
  background-color: transparent;
  border-style: dashed;
  color: var(--color-gray, #9e9e9e);
}

.manage-button:hover {
  border-color: var(--color-primary, #88d424);
  color: var(--color-primary, #88d424);
}

@media (max-width: 768px) {
  .template-button {
    white-space: normal;
    max-width: none;
    flex: 1;
  }
}
</style>
