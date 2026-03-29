<template>
  <div class="exercise-card" @click="$emit('click')">
    <div class="card-header">
      <span class="exercise-icon">{{ icon }}</span>
      <h3 class="exercise-title">{{ title }}</h3>
    </div>

    <p class="exercise-description">{{ description }}</p>

    <div v-if="stats" class="stats-preview">
      <div class="stat-item">
        <span class="stat-label">Попыток:</span>
        <span class="stat-value">{{ stats.attempts }}</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">Успешно:</span>
        <span class="stat-value">{{ stats.successes }} ({{ stats.successRate }}%)</span>
      </div>
      <div v-if="stats.bestTimeSeconds" class="stat-item">
        <span class="stat-label">Лучшее время:</span>
        <span class="stat-value">{{ formatTime(stats.bestTimeSeconds) }}</span>
      </div>
    </div>

    <div class="card-footer">
      <span class="play-button">Начать тренировку →</span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import brainTrainerService from '@/services/brainTrainerService'

const props = defineProps({
  type: {
    type: String,
    required: true
  },
  title: {
    type: String,
    required: true
  },
  description: {
    type: String,
    required: true
  },
  stats: {
    type: Object,
    default: null
  }
})

defineEmits(['click'])

const icon = computed(() => {
  return brainTrainerService.constructor.getIcon(props.type)
})

const formatTime = (seconds) => {
  return brainTrainerService.constructor.formatTime(seconds)
}
</script>

<style scoped>
.exercise-card {
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

.exercise-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 12px var(--shadow-color, rgba(0,0,0,0.1));
  border-color: var(--color-primary, #88d424);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
}

.exercise-icon {
  font-size: 32px;
}

.exercise-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-primary-dark, #6aa91c);
  margin: 0;
}

.exercise-description {
  color: var(--color-gray, #9e9e9e);
  font-size: 14px;
  line-height: 1.6;
  margin: 0;
}

.stats-preview {
  background-color: var(--color-white-smoke, #f5f5f5);
  border-radius: 8px;
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
}

.stat-label {
  color: var(--color-gray, #9e9e9e);
}

.stat-value {
  color: var(--color-primary-dark, #6aa91c);
  font-weight: 500;
}

.card-footer {
  margin-top: auto;
  text-align: right;
}

.play-button {
  color: var(--color-primary, #88d424);
  font-size: 14px;
  font-weight: 500;
  transition: transform 0.2s ease;
  display: inline-block;
}

.exercise-card:hover .play-button {
  transform: translateX(5px);
}

@media (max-width: 768px) {
  .exercise-card {
    padding: 1rem;
  }

  .stats-preview {
    padding: 8px;
  }
}
</style>
