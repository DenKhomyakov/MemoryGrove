<template>
  <div class="home">
    <div class="welcome-section">
      <h2>Добро пожаловать в MemoryGrove</h2>
      <p class="lead">Роща памяти - место, где воспоминания обретают новую жизнь</p>

      <div class="features-preview">
        <Card v-for="feature in features" :key="feature.title" class="feature-card">
          <h3>{{ feature.icon }} {{ feature.title }}</h3>
          <p>{{ feature.description }}</p>
        </Card>
      </div>

      <!-- Временно оставляем тестовый компонент -->
      <div class="test-section" v-if="!isAuthenticated">
        <h3>Тестирование связи с бэкендом</h3>
        <HelloWorld />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import Card from '@/components/UI/Card.vue'
import HelloWorld from '@/components/HelloWorld.vue'
import authService from '@/services/authService'

const isAuthenticated = ref(authService.isAuthenticated())

const features = [
  {
    icon: '🌳',
    title: 'Безопасный лес',
    description: 'Личный дневник, система напоминаний, простые данные о пациенте'
  },
  {
    icon: '🤝',
    title: 'Восстановление связей',
    description: 'Контакты доверия, контролируемое общение с близкими'
  },
  {
    icon: '📸',
    title: 'Цифровая память',
    description: 'Фотографии, значимые места, коллекция интересов'
  }
]
</script>

<style scoped>
.home {
  padding: 20px 0;
}

.welcome-section {
  text-align: center;
}

.welcome-section h2 {
  font-size: 32px;
  color: var(--color-primary-dark, #6aa91c);
  margin-bottom: 15px;
}

.lead {
  font-size: 18px;
  color: var(--color-gray, #9e9e9e);
  margin-bottom: 40px;
}

.features-preview {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 30px;
  margin: 40px 0;
}

.feature-card {
  text-align: left;
  transition: transform 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-5px);
}

.feature-card h3 {
  color: var(--color-primary, #88d424);
  margin: 0 0 15px 0;
  font-size: 20px;
}

.feature-card p {
  color: var(--color-gray, #9e9e9e);
  margin: 0;
  line-height: 1.6;
}

.test-section {
  margin-top: 60px;
  padding-top: 40px;
  border-top: 1px solid var(--color-light-gray, #e0e0e0);
}

.test-section h3 {
  color: var(--color-primary-dark, #6aa91c);
  margin-bottom: 30px;
}
</style>
