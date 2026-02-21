<template>
  <div class="about">
    <h2>О проекте MemoryGrove</h2>
    <p class="lead">
      MemoryGrove - специализированный некоммерческий веб-сервис для людей, потерявших память.
    </p>

    <div class="features">
      <Card v-for="feature in features" :key="feature.title" class="feature-card">
        <h3>{{ feature.icon }} {{ feature.title }}</h3>
        <p>{{ feature.description }}</p>
      </Card>
    </div>

    <div class="info-section">
      <h3>Причины потери памяти</h3>
      <div class="causes-grid">
        <div v-for="category in causes" :key="category.title" class="cause-category">
          <h4>{{ category.title }}</h4>
          <ul>
            <li v-for="item in category.items" :key="item">{{ item }}</li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import Card from '@/components/UI/Card.vue'

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

// ToDo: Чисто для справки частично вынес описание описание проекта из README.md, чтобы страничка не выглядела пустой
const causes = [
  {
    title: 'Неврологические',
    items: ['Черепно-мозговые травмы', 'Инсульты', 'Нейродегенеративные заболевания']
  },
  {
    title: 'Сосудистые',
    items: ['Сосудистая деменция', 'Транзиторные ишемические атаки']
  },
  {
    title: 'Психиатрические',
    items: ['Диссоциативная амнезия', 'ПТСР', 'Депрессия']
  }
]
</script>

<style scoped>
.about {
  padding: 20px 0;
}

.about h2 {
  font-size: 32px;
  color: var(--color-primary-dark, #6aa91c);
  margin-bottom: 15px;
}

.lead {
  font-size: 18px;
  color: var(--color-gray, #9e9e9e);
  margin-bottom: 40px;
  line-height: 1.6;
}

.features {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 30px;
  margin-bottom: 50px;
}

.feature-card {
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
  line-height: 1.6;
  margin: 0;
}

.info-section {
  margin-top: 50px;
  padding-top: 30px;
  border-top: 1px solid var(--color-light-gray, #e0e0e0);
}

.info-section h3 {
  color: var(--color-primary-dark, #6aa91c);
  margin-bottom: 25px;
  font-size: 22px;
}

.causes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 25px;
}

.cause-category {
  background-color: var(--color-white, #ffffff);
  padding: 20px;
  border-radius: 10px;
  border: 1px solid var(--color-light-gray, #e0e0e0);
  transition: transform 0.3s ease;
}

.cause-category:hover {
  transform: translateY(-3px);
  box-shadow: 0 5px 15px var(--shadow-color);
}

.cause-category h4 {
  color: var(--color-primary, #88d424);
  margin: 0 0 15px 0;
  font-size: 18px;
}

.cause-category ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.cause-category li {
  color: var(--color-gray, #9e9e9e);
  margin-bottom: 8px;
  padding-left: 20px;
  position: relative;
}

.cause-category li::before {
  content: '•';
  color: var(--color-primary, #88d424);
  font-size: 18px;
  position: absolute;
  left: 4px;
  top: -2px;
}
</style>
