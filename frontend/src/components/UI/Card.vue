<template>
  <div class="card" :class="{ 'card-padded': padded, 'card-hoverable': hoverable }">
    <slot />
  </div>
</template>

<script setup>
defineProps({
  padded: {
    type: Boolean,
    default: true
  },
  hoverable: {
    type: Boolean,
    default: false
  }
})
</script>

<style scoped>
.card {
  background-color: var(--color-white, #ffffff);
  border-radius: 12px;
  box-shadow: 0 2px 8px var(--shadow-color);
  transition: all 0.3s ease;
}

.card-padded {
  padding: 24px;
}

.card-hoverable:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 16px var(--shadow-color);
}
</style>
