<template>
  <div class="about-page">
    <div class="about-card">
      <div class="about-header">
        <h1>🌳 MemoryGrove</h1>
        <p class="subtitle">Роща памяти - место, где воспоминания обретают новую жизнь</p>
      </div>

      <!-- О проекте -->
      <section class="about-section">
        <h2>О проекте</h2>
        <p class="about-description">
          MemoryGrove - это специализированный некоммерческий веб-сервис для людей, потерявших память вследствие различных причин.
          Веб-сервис является технологическим решением для помощи в реабилитации людям, столкнувшимся с потерей памяти.
        </p>
      </section>

      <!-- Причины потери памяти -->
      <section class="causes-section">
        <h2>Причины потери памяти</h2>
        <p class="section-intro">Эксперты выделяют 4 основные группы причин потери памяти:</p>

        <div class="causes-grid">
          <!-- Неврологические причины -->
          <div class="cause-card">
            <div class="cause-header">
              <span class="cause-icon">🧠</span>
              <h3>Неврологические причины</h3>
            </div>
            <ul class="cause-list">
              <li>Черепно-мозговые травмы (сотрясения мозга, контузии)</li>
              <li>Инсульты (ишемический, геморрагический)</li>
              <li>Нейродегенеративные заболевания:
                <ul class="sublist">
                  <li>Болезнь Альцгеймера</li>
                  <li>Деменция с тельцами Леви</li>
                  <li>Лобно-височная деменция</li>
                </ul>
              </li>
              <li>Эпилепсия и эпилептические приступы</li>
              <li>Опухоли головного мозга</li>
              <li>Рассеянный склероз</li>
            </ul>
          </div>

          <!-- Сосудистые нарушения -->
          <div class="cause-card">
            <div class="cause-header">
              <span class="cause-icon">❤️</span>
              <h3>Сосудистые нарушения</h3>
            </div>
            <ul class="cause-list">
              <li>Сосудистая деменция</li>
              <li>Транзиторные ишемические атаки (ТИА)</li>
              <li>Хроническая ишемия мозга</li>
            </ul>
          </div>

          <!-- Психиатрические причины -->
          <div class="cause-card">
            <div class="cause-header">
              <span class="cause-icon">💭</span>
              <h3>Психиатрические причины</h3>
            </div>
            <ul class="cause-list">
              <li>Диссоциативная амнезия (реакция на психологическую травму)</li>
              <li>Посттравматическое стрессовое расстройство (ПТСР)</li>
              <li>Депрессия (псевдодеменция)</li>
            </ul>
          </div>

          <!-- Другие медицинские причины -->
          <div class="cause-card">
            <div class="cause-header">
              <span class="cause-icon">⚕️</span>
              <h3>Другие медицинские причины</h3>
            </div>
            <ul class="cause-list">
              <li>Метаболические нарушения:
                <ul class="sublist">
                  <li>Гипотиреоз</li>
                  <li>Дефицит витамина B12</li>
                </ul>
              </li>
              <li>Инфекции:
                <ul class="sublist">
                  <li>Менингит</li>
                  <li>Энцефалит</li>
                  <li>Нейросифилис</li>
                </ul>
              </li>
              <li>Токсичное воздействие (алкоголь, наркотики, некоторые лекарства)</li>
              <li>Кислородное голодание (после остановки сердца, удушья)</li>
            </ul>
          </div>
        </div>
      </section>

      <!-- Модули веб-сервиса -->
      <section class="modules-section">
        <h2>Модули веб-сервиса</h2>

        <div class="modules-grid">
          <!-- Личный профиль -->
          <div class="module-card">
            <div class="module-header">
              <span class="module-icon">👤</span>
              <h3>Личный профиль</h3>
            </div>
            <ul class="module-list">
              <li>Управление данными (ФИО, дата рождения и т.д.)</li>
              <li>Смена пароля</li>
              <li>Загрузка фотографии профиля</li>
              <li>Привязка Telegram для уведомлений</li>
              <li>Деактивация аккаунта</li>
            </ul>
          </div>

          <!-- Безопасный лес -->
          <div class="module-card">
            <div class="module-header">
              <span class="module-icon">🌳</span>
              <h3>Безопасный лес</h3>
            </div>
            <ul class="module-list">
              <li>Личный дневник для записи мыслей и чувств</li>
              <li>Полная конфиденциальность записей</li>
              <li>Отметка настроения в каждой записи</li>
              <li>Статистика активности</li>
            </ul>
          </div>

          <!-- Напоминания -->
          <div class="module-card">
            <div class="module-header">
              <span class="module-icon">⏰</span>
              <h3>Напоминания</h3>
            </div>
            <ul class="module-list">
              <li>Создание напоминаний о важных делах</li>
              <li>Уведомления через Telegram</li>
              <li>Отслеживание статуса выполнения</li>
              <li>Категоризация по статусам выполненности</li>
            </ul>
          </div>

          <!-- Значимые места -->
          <div class="module-card">
            <div class="module-header">
              <span class="module-icon">📍</span>
              <h3>Значимые места</h3>
            </div>
            <ul class="module-list">
              <li>Домашний адрес</li>
              <li>Медицинские учреждения</li>
              <li>Место работы/учёбы</li>
              <li>Любимые места отдыха</li>
              <li>Категоризация по местам</li>
            </ul>
          </div>

          <!-- Коллекция интересов -->
          <div class="module-card">
            <div class="module-header">
              <span class="module-icon">🎨</span>
              <h3>Коллекция интересов</h3>
            </div>
            <ul class="module-list">
              <li>Любимые фильмы и сериалы</li>
              <li>Книги и авторы</li>
              <li>Музыка и исполнители</li>
              <li>Игры и хобби</li>
              <li>Оценка (нравится/не нравится)</li>
            </ul>
          </div>

          <!-- Анкета навыков -->
          <div class="module-card">
            <div class="module-header">
              <span class="module-icon">📋</span>
              <h3>Анкета навыков</h3>
            </div>
            <ul class="module-list">
              <li>Отслеживание освоенных навыков</li>
              <li>Категории: самообслуживание, быт, коммуникация и др.</li>
              <li>Оценка сложности и важности</li>
              <li>Дата последнего выполнения</li>
              <li>Статистика прогресса</li>
            </ul>
          </div>

          <!-- Восстановление связей -->
          <div class="module-card">
            <div class="module-header">
              <span class="module-icon">🤝</span>
              <h3>Восстановление связей</h3>
            </div>
            <ul class="module-list">
              <li>Доверенные контакты (семья, друзья, врачи)</li>
              <li>Безопасный чат</li>
              <li>Шаблоны сообщений для быстрых ответов</li>
            </ul>
          </div>

          <!-- Цифровая память -->
          <div class="module-card">
            <div class="module-header">
              <span class="module-icon">📸</span>
              <h3>Цифровая память</h3>
            </div>
            <ul class="module-list">
              <li>Загрузка и хранение фотографий</li>
              <li>Описания, места, даты</li>
              <li>Удобное отображение</li>
            </ul>
          </div>

          <!-- Тренажёр памяти -->
          <div class="module-card">
            <div class="module-header">
              <span class="module-icon">🧠</span>
              <h3>Тренажёр памяти</h3>
            </div>
            <ul class="module-list">
              <li>Арифметические цепочки</li>
              <li>Понимание текста с вопросами</li>
              <li>Заучивание стихов</li>
              <li>Ассоциации</li>
              <li>Логические ряды</li>
              <li>Таблица Шульте (с/без подсказок)</li>
              <li>Статистика прогресса</li>
            </ul>
          </div>

          <!-- Психологическая помощь -->
          <div class="module-card">
            <div class="module-header">
              <span class="module-icon">🫂</span>
              <h3>Психологическая помощь</h3>
            </div>
            <ul class="module-list">
              <li>Круглосуточные горячие линии</li>
              <li>Полезные книги и ресурсы</li>
              <li>Памятка для близких</li>
              <li>Экстренная помощь (112)</li>
            </ul>
          </div>
        </div>
      </section>

      <!-- Telegram бот -->
      <section class="telegram-section">
        <h2>Telegram бот</h2>
        <div class="telegram-card">
          <div class="telegram-icon">🤖</div>
          <div class="telegram-info">
            <h3>@MemoryGroveBot</h3>
            <p>Получайте уведомления о напоминаниях прямо в Telegram</p>
            <div class="telegram-features">
              <span class="feature">🔔 Уведомления о напоминаниях</span>
              <span class="feature">🔐 Безопасная привязка через код</span>
            </div>
          </div>
        </div>
      </section>

      <!-- Используемые технологии -->
      <section class="tech-section">
        <h2>Используемые технологии</h2>

        <div class="tech-grid">
          <div class="tech-card">
            <div class="tech-header">
              <span class="tech-icon">🔙</span>
              <h3>Backend</h3>
            </div>
            <ul class="tech-list">
              <li>Java 24 (JDK Amazon Corretto 24.0.2.12)</li>
              <li>Spring Boot 3.5.9</li>
              <li>Spring Security + JWT</li>
              <li>Spring Data JPA / Hibernate</li>
            </ul>
          </div>

          <div class="tech-card">
            <div class="tech-header">
              <span class="tech-icon">🎨</span>
              <h3>Frontend</h3>
            </div>
            <ul class="tech-list">
              <li>JavaScript + Node.js (v24.13.1)</li>
              <li>Vue 3 Composition API</li>
              <li>Vue Router + Pinia</li>
              <li>Axios для API запросов</li>
            </ul>
          </div>

          <div class="tech-card">
            <div class="tech-header">
              <span class="tech-icon">🗄️</span>
              <h3>База данных</h3>
            </div>
            <ul class="tech-list">
              <li>PostgreSQL 18.0</li>
              <li>Liquibase для миграций</li>
              <li>JPA/Hibernate ORM</li>
            </ul>
          </div>

          <div class="tech-card">
            <div class="tech-header">
              <span class="tech-icon">🤖</span>
              <h3>Telegram Bot</h3>
            </div>
            <ul class="tech-list">
              <li>Telegram Bot API</li>
              <li>Webhook / Long Polling</li>
              <li>Уведомления о напоминаниях</li>
            </ul>
          </div>

          <div class="tech-card">
            <div class="tech-header">
              <span class="tech-icon">📁</span>
              <h3>Файловое хранилище</h3>
            </div>
            <ul class="tech-list">
              <li>Локальное файловое хранилище</li>
              <li>Thumbnailator для миниатюр</li>
              <li>Поддержка JPEG, PNG, GIF, WebP</li>
            </ul>
          </div>

          <div class="tech-card">
            <div class="tech-header">
              <span class="tech-icon">🛠️</span>
              <h3>Система сборки</h3>
            </div>
            <ul class="tech-list">
              <li>Gradle</li>
              <li>Управление зависимостями</li>
              <li>Сборка и тестирование</li>
            </ul>
          </div>
        </div>
      </section>

      <!-- Среды разработки -->
      <section class="ide-section">
        <h2>Среды разработки</h2>

        <div class="ide-grid">
          <div class="ide-card">
            <div class="ide-header">
              <span class="ide-icon">💡</span>
              <h3>Backend</h3>
            </div>
            <ul class="ide-list">
              <li>JetBrains IntelliJ IDEA Ultimate Edition 2025.2</li>
              <li>Поддержка Spring Boot</li>
              <li>Встроенный HTTP клиент</li>
            </ul>
          </div>

          <div class="ide-card">
            <div class="ide-header">
              <span class="ide-icon">🌐</span>
              <h3>Frontend</h3>
            </div>
            <ul class="ide-list">
              <li>JetBrains WebStorm 2024.2</li>
              <li>Vue.js плагин</li>
              <li>Встроенный терминал</li>
            </ul>
          </div>

          <div class="ide-card">
            <div class="ide-header">
              <span class="ide-icon">📊</span>
              <h3>База данных</h3>
            </div>
            <ul class="ide-list">
              <li>DBeaver 25.2.4</li>
              <li>Визуализация схем</li>
              <li>SQL редактор</li>
            </ul>
          </div>

          <div class="ide-card">
            <div class="ide-header">
              <span class="ide-icon">📝</span>
              <h3>Система контроля версий</h3>
            </div>
            <ul class="ide-list">
              <li>Git Extensions 6.0.2.18262</li>
              <li>Визуализация веток</li>
            </ul>
          </div>
        </div>
      </section>

      <!-- Дополнительная информация -->
      <section class="footer-note">
        <p>MemoryGrove - некоммерческий проект, созданный с заботой о людях, столкнувшихся с потерей памяти.</p>
        <p class="version">Версия 1.0.0</p>
      </section>
    </div>
  </div>
</template>

<script setup>
// Компонент полностью статический, логика не требуется
</script>

<style scoped>
.about-page {
  width: 100%;
  display: flex;
  justify-content: center;
  padding: 2rem 1rem;
}

.about-card {
  width: 100%;
  max-width: 1200px;
  background: var(--color-white, #ffffff);
  border-radius: 16px;
  padding: 2.5rem;
  box-shadow: 0 4px 20px var(--shadow-color, rgba(0,0,0,0.1));
}

.about-header {
  text-align: center;
  margin-bottom: 2rem;
  padding-bottom: 1.5rem;
  border-bottom: 2px solid var(--color-primary, #88d424);
}

.about-header h1 {
  font-size: 36px;
  font-weight: 600;
  color: var(--color-primary-dark, #6aa91c);
  margin: 0 0 8px;
}

.about-header .subtitle {
  font-size: 18px;
  color: var(--color-gray, #9e9e9e);
  margin: 0;
  font-style: italic;
}

section {
  margin-bottom: 2.5rem;
}

section h2 {
  font-size: 24px;
  font-weight: 600;
  color: var(--color-primary-dark, #6aa91c);
  margin: 0 0 1rem 0;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid var(--color-light-gray, #e0e0e0);
}

.section-intro {
  color: var(--color-gray, #9e9e9e);
  margin-bottom: 1.5rem;
  font-size: 16px;
}

.about-description {
  font-size: 16px;
  line-height: 1.6;
  color: var(--color-dark-gray, #616161);
  margin: 0;
}

/* Сетка причин - единый стиль с модулями */
.causes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
  margin-top: 1rem;
}

.cause-card {
  background: linear-gradient(135deg, var(--color-white, #ffffff) 0%, var(--color-white-smoke, #f5f5f5) 100%);
  border: 1px solid var(--color-light-gray, #e0e0e0);
  border-radius: 12px;
  padding: 1.5rem;
  transition: transform 0.3s ease, border-color 0.3s ease;
}

.cause-card:hover {
  transform: translateY(-3px);
  border-color: var(--color-primary, #88d424);
  box-shadow: 0 6px 12px var(--shadow-color, rgba(0,0,0,0.1));
}

.cause-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 1rem;
}

.cause-icon {
  font-size: 28px;
}

.cause-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-primary-dark, #6aa91c);
  margin: 0;
}

.cause-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.cause-list li {
  color: var(--color-dark-gray, #616161);
  margin-bottom: 8px;
  padding-left: 20px;
  position: relative;
  font-size: 14px;
  line-height: 1.5;
}

.cause-list li::before {
  content: '•';
  color: var(--color-primary, #88d424);
  font-size: 16px;
  position: absolute;
  left: 4px;
  top: -2px;
}

.sublist {
  list-style: none;
  padding-left: 20px;
  margin: 4px 0 8px;
}

.sublist li {
  padding-left: 20px;
  font-size: 13px;
  color: var(--color-gray, #9e9e9e);
}

.sublist li::before {
  content: '◦';
  color: var(--color-primary-light, #a8e454);
}

/* Модули */
.modules-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 20px;
  margin-top: 1rem;
}

.module-card {
  background: linear-gradient(135deg, var(--color-white, #ffffff) 0%, var(--color-white-smoke, #f5f5f5) 100%);
  border: 1px solid var(--color-light-gray, #e0e0e0);
  border-radius: 12px;
  padding: 1.5rem;
  transition: transform 0.3s ease, border-color 0.3s ease;
}

.module-card:hover {
  transform: translateY(-3px);
  border-color: var(--color-primary, #88d424);
  box-shadow: 0 6px 12px var(--shadow-color, rgba(0,0,0,0.1));
}

.module-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 1rem;
}

.module-icon {
  font-size: 28px;
}

.module-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-primary-dark, #6aa91c);
  margin: 0;
}

.module-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.module-list li {
  color: var(--color-dark-gray, #616161);
  margin-bottom: 8px;
  padding-left: 20px;
  position: relative;
  font-size: 14px;
  line-height: 1.5;
}

.module-list li::before {
  content: '→';
  color: var(--color-primary, #88d424);
  position: absolute;
  left: 0;
  top: 0;
}

/* Технологии */
.tech-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  margin-top: 1rem;
}

.tech-card {
  background: linear-gradient(135deg, var(--color-white, #ffffff) 0%, var(--color-white-smoke, #f5f5f5) 100%);
  border: 1px solid var(--color-light-gray, #e0e0e0);
  border-radius: 12px;
  padding: 1.5rem;
  transition: transform 0.3s ease, border-color 0.3s ease;
}

.tech-card:hover {
  transform: translateY(-3px);
  border-color: var(--color-primary, #88d424);
  box-shadow: 0 6px 12px var(--shadow-color, rgba(0,0,0,0.1));
}

.tech-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 1rem;
}

.tech-icon {
  font-size: 28px;
}

.tech-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-primary-dark, #6aa91c);
  margin: 0;
}

.tech-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.tech-list li {
  color: var(--color-dark-gray, #616161);
  margin-bottom: 8px;
  padding-left: 20px;
  position: relative;
  font-size: 14px;
  line-height: 1.5;
}

.tech-list li::before {
  content: '⚙️';
  color: var(--color-primary, #88d424);
  position: absolute;
  left: 0;
  top: 0;
}

/* Среды разработки */
.ide-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
  margin-top: 1rem;
}

.ide-card {
  background: linear-gradient(135deg, var(--color-white, #ffffff) 0%, var(--color-white-smoke, #f5f5f5) 100%);
  border: 1px solid var(--color-light-gray, #e0e0e0);
  border-radius: 12px;
  padding: 1.5rem;
  transition: transform 0.3s ease, border-color 0.3s ease;
}

.ide-card:hover {
  transform: translateY(-3px);
  border-color: var(--color-primary, #88d424);
  box-shadow: 0 6px 12px var(--shadow-color, rgba(0,0,0,0.1));
}

.ide-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 1rem;
}

.ide-icon {
  font-size: 28px;
}

.ide-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-primary-dark, #6aa91c);
  margin: 0;
}

.ide-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.ide-list li {
  color: var(--color-dark-gray, #616161);
  margin-bottom: 8px;
  padding-left: 20px;
  position: relative;
  font-size: 14px;
  line-height: 1.5;
}

.ide-list li::before {
  content: '🛠️';
  color: var(--color-primary, #88d424);
  position: absolute;
  left: 0;
  top: 0;
}

/* Футер */
.footer-note {
  margin-top: 2rem;
  padding-top: 1.5rem;
  text-align: center;
  border-top: 1px solid var(--color-light-gray, #e0e0e0);
}

.footer-note p {
  color: var(--color-gray, #9e9e9e);
  font-size: 14px;
  margin: 0 0 8px 0;
}

.footer-note .version {
  font-size: 12px;
  color: var(--color-light-gray, #e0e0e0);
}

/* Telegram бот */
.telegram-section {
  margin-bottom: 2.5rem;
}

.telegram-card {
  display: flex;
  align-items: center;
  gap: 20px;
  background: linear-gradient(135deg, rgba(136, 212, 36, 0.05) 0%, var(--color-white, #ffffff) 100%);
  border: 2px solid var(--color-primary, #88d424);
  border-radius: 16px;
  padding: 1.5rem;
  transition: transform 0.3s ease;
}

.telegram-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 12px var(--shadow-color, rgba(0,0,0,0.1));
}

.telegram-icon {
  font-size: 48px;
}

.telegram-info h3 {
  font-size: 20px;
  color: var(--color-primary-dark, #6aa91c);
  margin: 0 0 8px 0;
}

.telegram-info p {
  color: var(--color-gray, #9e9e9e);
  font-size: 14px;
  margin: 0 0 12px 0;
}

.telegram-features {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.telegram-features .feature {
  background-color: var(--color-white-smoke, #f5f5f5);
  color: var(--color-dark-gray, #616161);
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
}

@media (max-width: 768px) {
  .telegram-card {
    flex-direction: column;
    text-align: center;
  }

  .telegram-features {
    justify-content: center;
  }
}

/* Адаптивность */
@media (max-width: 768px) {
  .about-card {
    padding: 1.5rem;
  }

  .about-header h1 {
    font-size: 28px;
  }

  .about-header .subtitle {
    font-size: 16px;
  }

  section h2 {
    font-size: 20px;
  }

  .causes-grid,
  .modules-grid,
  .tech-grid,
  .ide-grid {
    grid-template-columns: 1fr;
  }

  .cause-card,
  .module-card,
  .tech-card,
  .ide-card {
    padding: 1rem;
  }
}
</style>
