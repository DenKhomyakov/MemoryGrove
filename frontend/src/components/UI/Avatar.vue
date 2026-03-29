<template>
  <div class="avatar" :class="[size, { 'has-image': hasImage }]" :style="avatarStyle">
    <img
      v-if="hasImage && !imageError"
      :src="src"
      :alt="alt"
      @error="handleImageError"
      class="avatar-img"
    />
    <div v-else class="avatar-placeholder">
      <span class="placeholder-text">{{ initials }}</span>
    </div>
    <slot></slot>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  src: {
    type: String,
    default: null
  },
  alt: {
    type: String,
    default: 'Avatar'
  },
  name: {
    type: String,
    default: ''
  },
  size: {
    type: String,
    default: 'medium',
    validator: (value) => ['small', 'medium', 'large'].includes(value)
  },
  bgColor: {
    type: String,
    default: null
  }
})

const imageError = ref(false)

const hasImage = computed(() => {
  return props.src && props.src.trim() !== '' && !imageError.value
})

const initials = computed(() => {
  if (!props.name) return '?'
  const parts = props.name.trim().split(/\s+/)
  if (parts.length >= 2) {
    return (parts[0][0] + parts[1][0]).toUpperCase()
  }
  return parts[0][0].toUpperCase()
})

const avatarStyle = computed(() => {
  if (props.bgColor && !hasImage.value) {
    return { backgroundColor: props.bgColor }
  }
  return {}
})

const handleImageError = () => {
  imageError.value = true
}
</script>

<style scoped>
.avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
  background-color: var(--color-primary-light, #a8e454);
}

.avatar.small {
  width: 32px;
  height: 32px;
  font-size: 12px;
}

.avatar.medium {
  width: 48px;
  height: 48px;
  font-size: 18px;
}

.avatar.large {
  width: 80px;
  height: 80px;
  font-size: 28px;
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  background-color: inherit;
}

.placeholder-text {
  font-weight: 600;
  color: var(--color-white, #ffffff);
  text-transform: uppercase;
}
</style>
