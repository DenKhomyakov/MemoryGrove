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
          <!-- Безопасный лес -->
          <div class="module-card">
            <div class="module-header">
              <span class="module-icon">🌳</span>
              <h3>Безопасный лес</h3>
            </div>
            <ul class="module-list">
              <li>Простые данные о пациенте (ФИО, возраст, город, имя лечащего врача, адрес проживания)</li>
              <li>Дневник восстановления (личные записи, недоступные другим пользователям)</li>
              <li>Система напоминаний (принять таблетки, приём у врача и т.п.)</li>
            </ul>
          </div>

          <!-- Восстановление связей -->
          <div class="module-card">
            <div class="module-header">
              <span class="module-icon">🤝</span>
              <h3>Восстановление связей</h3>
            </div>
            <ul class="module-list">
              <li>Контакты доверия (семья, друзья, коллеги, лечащие врачи)</li>
              <li>Контролируемое общение (переписка только с подтверждёнными контактами)</li>
            </ul>
          </div>

          <!-- Цифровая память -->
          <div class="module-card">
            <div class="module-header">
              <span class="module-icon">📸</span>
              <h3>Цифровая память</h3>
            </div>
            <ul class="module-list">
              <li>Фотографии с человеком</li>
              <li>Список значимых и любимых мест</li>
              <li>Коллекция интересов (фильмы, книги, музыка, игры)</li>
              <li>Анкета навыков (чек-лист действий, которые ранее пациент умел совершать)</li>
            </ul>
          </div>

          <!-- Психологическая помощь -->
          <div class="module-card">
            <div class="module-header">
              <span class="module-icon">🫂</span>
              <h3>Психологическая помощь</h3>
            </div>
            <p class="module-description">
              Вкладка будет перенаправлять на сайт службы психологической помощи
              или содержать контакты центров доверия.
            </p>
          </div>
        </div>
      </section>

      <!-- Технологический стек -->
      <section class="tech-section">
        <h2>Используемые технологии</h2>

        <div class="tech-grid">
          <div class="tech-item">
            <span class="tech-icon">🔙</span>
            <div class="tech-info">
              <h4>Backend</h4>
              <p>Java 24 (JDK Amazon Corretto 24.0.2.12) + Spring Boot 3.5.9</p>
            </div>
          </div>

          <div class="tech-item">
            <span class="tech-icon">🎨</span>
            <div class="tech-info">
              <h4>Frontend</h4>
              <p>JavaScript + Node.js (v24.13.1) + Vue 3</p>
            </div>
          </div>

          <div class="tech-item">
            <span class="tech-icon">🗄️</span>
            <div class="tech-info">
              <h4>База данных</h4>
              <p>PostgreSQL (18.0) + Liquibase + JPA/Hibernate</p>
            </div>
          </div>

          <div class="tech-item">
            <span class="tech-icon">🛠️</span>
            <div class="tech-info">
              <h4>Система сборки</h4>
              <p>Gradle</p>
            </div>
          </div>
        </div>
      </section>

      <!-- Среды разработки -->
      <section class="ide-section">
        <h2>Среды разработки</h2>

        <div class="ide-grid">
          <div class="ide-item">
            <span class="ide-icon">💡</span>
            <div class="ide-info">
              <h4>Backend</h4>
              <p>JetBrains IntelliJ IDEA Ultimate Edition 2025.2</p>
            </div>
          </div>

          <div class="ide-item">
            <span class="ide-icon">🌐</span>
            <div class="ide-info">
              <h4>Frontend</h4>
              <p>JetBrains WebStorm 2024.2</p>
            </div>
          </div>

          <div class="ide-item">
            <span class="ide-icon">📊</span>
            <div class="ide-info">
              <h4>База данных</h4>
              <p>DBeaver 25.2.4</p>
            </div>
          </div>

          <div class="ide-item">
            <span class="ide-icon">📝</span>
            <div class="ide-info">
              <h4>Система контроля версий</h4>
              <p>Git Extensions 6.0.2.18262</p>
            </div>
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
  max-width: 1000px;
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

/* Сетка причин */
.causes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
  margin-top: 1rem;
}

.cause-card {
  background-color: var(--color-white-smoke, #f5f5f5);
  border-radius: 12px;
  padding: 1.5rem;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.cause-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 12px var(--shadow-color, rgba(0,0,0,0.1));
}

.cause-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 1rem;
}

.cause-icon {
  font-size: 24px;
}

.cause-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-primary, #88d424);
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

/* Сетка модулей */
.modules-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
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

.module-description {
  color: var(--color-dark-gray, #616161);
  font-size: 14px;
  line-height: 1.6;
  margin: 0;
}

/* Технологический стек */
.tech-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
  margin-top: 1rem;
}

.tech-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 1rem;
  background-color: var(--color-white-smoke, #f5f5f5);
  border-radius: 10px;
  transition: background-color 0.3s ease;
}

.tech-item:hover {
  background-color: rgba(136, 212, 36, 0.1);
}

.tech-icon {
  font-size: 24px;
  min-width: 40px;
  text-align: center;
}

.tech-info h4 {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-primary-dark, #6aa91c);
  margin: 0 0 4px 0;
}

.tech-info p {
  font-size: 13px;
  color: var(--color-gray, #9e9e9e);
  margin: 0;
  line-height: 1.4;
}

/* Среды разработки */
.ide-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
  margin-top: 1rem;
}

.ide-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 1rem;
  background-color: var(--color-white-smoke, #f5f5f5);
  border-radius: 10px;
  transition: background-color 0.3s ease;
}

.ide-item:hover {
  background-color: rgba(136, 212, 36, 0.1);
}

.ide-icon {
  font-size: 24px;
  min-width: 40px;
  text-align: center;
}

.ide-info h4 {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-primary-dark, #6aa91c);
  margin: 0 0 4px 0;
}

.ide-info p {
  font-size: 13px;
  color: var(--color-gray, #9e9e9e);
  margin: 0;
  line-height: 1.4;
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
  .module-card {
    padding: 1rem;
  }

  .tech-item,
  .ide-item {
    flex-direction: column;
    text-align: center;
    gap: 8px;
  }

  .tech-icon,
  .ide-icon {
    min-width: auto;
  }
}
</style>
