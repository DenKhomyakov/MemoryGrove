<template>
  <div class="home">
    <section class="welcome-section">
      <h1>Добро пожаловать в MemoryGrove</h1>
      <p class="lead">Роща памяти - место, где воспоминания обретают новую жизнь</p>
    </section>

    <!-- Основные модули -->
    <section class="modules-section">
      <h2>Основные модули</h2>
      <div class="modules-grid">
        <router-link to="/journal" class="module-card">
          <div class="module-icon">🌳</div>
          <h3>Безопасный лес</h3>
          <p>Личный дневник, система напоминаний, данные о пациенте</p>
          <div class="module-status available">Доступно</div>
        </router-link>

        <router-link to="/reminders" class="module-card">
          <div class="module-icon">⏰</div>
          <h3>Напоминания</h3>
          <p>Добрые напоминания о важных делах</p>
          <div class="module-status available">Доступно</div>
        </router-link>

        <div class="module-card">
          <div class="module-icon">🤝</div>
          <h3>Восстановление связей</h3>
          <p>Контакты доверия, контролируемое общение с близкими</p>
          <div class="module-status">Скоро</div>
        </div>

        <div class="module-card">
          <div class="module-icon">📸</div>
          <h3>Цифровая память</h3>
          <p>Фотографии, значимые места, коллекция интересов</p>
          <div class="module-status">Скоро</div>
        </div>

        <router-link to="/help" class="module-card help-card">
          <div class="module-icon">🫂</div>
          <h3>Психологическая помощь</h3>
          <p>Горячие линии, полезные книги, форумы поддержки</p>
          <div class="module-status available">Доступно</div>
        </router-link>
      </div>
    </section>

    <!-- Быстрая помощь (важные номера) -->
    <section class="quick-help">
      <h2>📞 Круглосуточная помощь</h2>
      <div class="phones-grid">
        <div class="phone-card">
          <div class="phone-number">112</div>
          <div class="phone-description">Единый номер экстренных служб</div>
        </div>
        <div class="phone-card">
          <div class="phone-number">8-800-2000-122</div>
          <div class="phone-description">Телефон доверия (дети и родители)</div>
        </div>
        <div class="phone-card">
          <div class="phone-number">8-800-333-44-34</div>
          <div class="phone-description">Кризисная линия для взрослых</div>
        </div>
      </div>
      <router-link to="/help" class="more-link">Подробнее о помощи →</router-link>
    </section>
  </div>
</template>

<script setup>
// Компонент пока без логики
</script>

<style scoped>
.home {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem 1rem;
}

.welcome-section {
  text-align: center;
  margin-bottom: 3rem;
}

.welcome-section h1 {
  font-size: 36px;
  color: var(--color-primary-dark, #6aa91c);
  margin-bottom: 1rem;
}

.lead {
  font-size: 18px;
  color: var(--color-gray, #9e9e9e);
}

.modules-section {
  margin-bottom: 3rem;
}

.modules-section h2 {
  font-size: 24px;
  color: var(--color-primary-dark, #6aa91c);
  margin-bottom: 1.5rem;
  padding-bottom: 0.5rem;
  border-bottom: 2px solid var(--color-primary, #88d424);
}

.modules-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.module-card {
  background: var(--color-white, #ffffff);
  border-radius: 16px;
  padding: 1.5rem;
  text-decoration: none;
  color: inherit;
  box-shadow: 0 4px 12px var(--shadow-color, rgba(0,0,0,0.1));
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  position: relative;
  border: 1px solid transparent;
}

.module-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px var(--shadow-color, rgba(0,0,0,0.15));
}

.help-card {
  background: linear-gradient(135deg, var(--color-white, #ffffff) 0%, rgba(136, 212, 36, 0.05) 100%);
  border: 1px solid var(--color-primary, #88d424);
}

.module-icon {
  font-size: 48px;
  margin-bottom: 1rem;
}

.module-card h3 {
  font-size: 20px;
  color: var(--color-primary-dark, #6aa91c);
  margin: 0 0 0.5rem 0;
}

.module-card p {
  color: var(--color-gray, #9e9e9e);
  font-size: 14px;
  line-height: 1.5;
  margin: 0 0 1rem 0;
  flex: 1;
}

.module-status {
  font-size: 12px;
  padding: 4px 8px;
  border-radius: 4px;
  background-color: var(--color-light-gray, #e0e0e0);
  color: var(--color-gray, #9e9e9e);
  display: inline-block;
  align-self: flex-start;
}

.module-status.available {
  background-color: rgba(76, 175, 80, 0.1);
  color: var(--color-success, #4caf50);
}

/* Быстрая помощь */
.quick-help {
  background: linear-gradient(135deg, var(--color-white-smoke, #f5f5f5) 0%, var(--color-white, #ffffff) 100%);
  border-radius: 16px;
  padding: 2rem;
  margin-top: 2rem;
  border: 1px solid var(--color-light-gray, #e0e0e0);
}

.quick-help h2 {
  font-size: 24px;
  color: var(--color-primary-dark, #6aa91c);
  margin-bottom: 1.5rem;
  text-align: center;
}

.phones-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 1.5rem;
}

.phone-card {
  text-align: center;
  padding: 1rem;
  background: var(--color-white, #ffffff);
  border-radius: 12px;
  box-shadow: 0 2px 8px var(--shadow-color, rgba(0,0,0,0.05));
  transition: transform 0.3s ease;
}

.phone-card:hover {
  transform: scale(1.05);
}

.phone-number {
  font-size: 24px;
  font-weight: 600;
  color: var(--color-primary, #88d424);
  margin-bottom: 0.5rem;
}

.phone-description {
  font-size: 13px;
  color: var(--color-gray, #9e9e9e);
}

.more-link {
  display: block;
  text-align: center;
  color: var(--color-primary, #88d424);
  text-decoration: none;
  font-size: 16px;
  font-weight: 500;
  transition: color 0.3s ease;
}

.more-link:hover {
  color: var(--color-primary-dark, #6aa91c);
  text-decoration: underline;
}

@media (max-width: 768px) {
  .welcome-section h1 {
    font-size: 28px;
  }

  .lead {
    font-size: 16px;
  }

  .modules-grid {
    grid-template-columns: 1fr;
  }

  .phones-grid {
    grid-template-columns: 1fr;
  }
}
</style>
