<template>
  <div class="help-page">
    <div class="help-card">
      <div class="help-header">
        <h1>🫂 Психологическая помощь</h1>
        <p class="subtitle">Поддержка всегда рядом. Все сервисы бесплатны и анонимны.</p>
      </div>

      <!-- Важное уведомление -->
      <div class="emergency-notice">
        <div class="emergency-icon">⚡</div>
        <div class="emergency-content">
          <h3>Если нужна срочная помощь</h3>
          <p>Звоните по номеру <strong>112</strong> - единый номер экстренных служб и экстренной психологической помощи. Работает круглосуточно во всех регионах России.</p>
        </div>
      </div>

      <!-- Горячие линии -->
      <section class="help-section">
        <h2>📞 Круглосуточные горячие линии</h2>
        <div class="phones-grid">
          <div class="phone-card">
            <div class="phone-number">112</div>
            <div class="phone-description">Единый номер экстренных служб и экстренной психологической помощи</div>
            <div class="phone-tag">круглосуточно • бесплатно • анонимно</div>
          </div>

          <div class="phone-card">
            <div class="phone-number">8-800-2000-122</div>
            <div class="phone-description">Единый общероссийский телефон доверия для детей, подростков и их родителей</div>
            <div class="phone-tag">круглосуточно • бесплатно • анонимно</div>
            <div class="phone-note">Также доступен по короткому номеру 124</div>
          </div>

          <div class="phone-card">
            <div class="phone-number">8-800-333-44-34</div>
            <div class="phone-description">Бесплатная кризисная линия доверия для взрослых в трудной жизненной ситуации</div>
            <div class="phone-tag">круглосуточно • бесплатно • анонимно</div>
          </div>
        </div>

        <!-- Важный совет -->
        <div class="help-tip">
          <div class="tip-icon">💡</div>
          <div class="tip-content">
            <h4>Совет по использованию телефона доверия</h4>
            <p>Если вам или близкому человеку трудно сконцентрироваться или держать в голове много информации во время разговора, не стесняйтесь сразу сказать об этом психологу. Просто начните разговор с фразы: <em>«У меня проблемы с памятью, поэтому мне трудно запоминать инструкции. Вы не могли бы говорить медленнее и повторять самое важное?»</em>. Специалисты обучены работе с разными людьми и обязательно подстроятся под ваш темп восприятия.</p>
          </div>
        </div>
      </section>

      <!-- Полезные книги -->
      <section class="help-section">
        <h2>📚 Полезные книги</h2>
        <div class="books-grid">
          <div class="book-card">
            <div class="book-icon">📖</div>
            <h3>«Прекрасная память в любом возрасте»</h3>
            <p class="book-author">В.Н. Амосов</p>
            <p class="book-description">
              Книга поможет разобраться, где проходит грань между обычной забывчивостью и началом серьезного заболевания.
              Автор дает практические советы по улучшению работы мозга. Особенно адресована тем, кто ухаживает за пожилыми людьми с болезнью Альцгеймера.
            </p>
          </div>

          <div class="book-card">
            <div class="book-icon">📖</div>
            <h3>«Миф об утраченных воспоминаниях»</h3>
            <p class="book-author">Элизабет Лофтус, Кэтрин Кетчем</p>
            <p class="book-description">
              Книга одного из самых влиятельных исследователей памяти. Объясняет, как работает наша память, почему воспоминания могут искажаться
              и как отличить реальные события от "ложных" воспоминаний. Полезна для глубокого понимания механизмов памяти.
            </p>
          </div>
        </div>
      </section>

      <!-- Полезные ресурсы -->
      <section class="help-section">
        <h2>🌐 Полезные ресурсы</h2>
        <div class="resources-grid">
          <!-- Деменция.net -->
          <a href="https://деменция.net" target="_blank" rel="noopener noreferrer" class="resource-card">
            <div class="resource-icon">🧠</div>
            <h3>Деменция.net</h3>
            <p class="resource-description">Проект благотворительного фонда «ПАМЯТЬ ПОКОЛЕНИЙ» для поддержки людей с когнитивными нарушениями и их семей</p>
            <div class="resource-features">
              <span class="feature">📋 Тест для самопроверки</span>
              <span class="feature">📚 Статьи и видео</span>
              <span class="feature">🎓 Онлайн-курс «Забота и Я»</span>
              <span class="feature">👨‍⚕️ Помощь в поиске врачей</span>
            </div>
          </a>

          <!-- Минздрав РФ -->
          <a href="https://minzdrav.gov.ru" target="_blank" rel="noopener noreferrer" class="resource-card">
            <div class="resource-icon">🏥</div>
            <h3>Минздрав России</h3>
            <p class="resource-description">Официальный портал Министерства здравоохранения РФ с информацией о медицинской помощи и реабилитации</p>
            <div class="resource-features">
              <span class="feature">📌 Адреса клиник</span>
              <span class="feature">📞 Контакты</span>
              <span class="feature">📋 Нормативные документы</span>
            </div>
          </a>

          <!-- ВОЗ о деменции -->
          <a href="https://www.who.int/ru/news-room/fact-sheets/detail/dementia" target="_blank" rel="noopener noreferrer" class="resource-card">
            <div class="resource-icon">🌍</div>
            <h3>ВОЗ: Деменция</h3>
            <p class="resource-description">Всемирная организация здравоохранения - информация о деменции, статистика, рекомендации</p>
            <div class="resource-features">
              <span class="feature">📊 Статистика</span>
              <span class="feature">📑 Научные данные</span>
              <span class="feature">🌐 Международный опыт</span>
            </div>
          </a>

          <!-- Мнемотехники -->
          <div class="resource-card">
            <div class="resource-icon">🧩</div>
            <h3>Упражнения для памяти</h3>
            <p class="resource-description">Простые и эффективные упражнения для тренировки памяти, которые можно выполнять самостоятельно</p>
            <div class="memory-exercises">
              <div class="exercise-item">
                <span class="exercise-dot">•</span>
                <span>Заучивание стихов</span>
              </div>
              <div class="exercise-item">
                <span class="exercise-dot">•</span>
                <span>Игры в слова</span>
              </div>
              <div class="exercise-item">
                <span class="exercise-dot">•</span>
                <span>Визуализация</span>
              </div>
              <div class="exercise-item">
                <span class="exercise-dot">•</span>
                <span>Ассоциативные ряды</span>
              </div>
              <div class="exercise-item">
                <span class="exercise-dot">•</span>
                <span>Кроссворды и судоку</span>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- Памятка для близких -->
      <section class="help-section">
        <h2>👪 Памятка для близких</h2>
        <div class="guide-card">
          <p class="guide-intro">Если ваш близкий столкнулся с проблемами памяти, эти простые правила помогут в общении:</p>
          <div class="guide-grid">
            <div class="guide-item">
              <span class="guide-number">1</span>
              <p>Говорите медленно и чётко, используйте короткие фразы</p>
            </div>
            <div class="guide-item">
              <span class="guide-number">2</span>
              <p>Сохраняйте зрительный контакт и улыбайтесь</p>
            </div>
            <div class="guide-item">
              <span class="guide-number">3</span>
              <p>Не спорьте и не поправляйте, если человек что-то забыл</p>
            </div>
            <div class="guide-item">
              <span class="guide-number">4</span>
              <p>Используйте фотографии и знакомые предметы для разговора</p>
            </div>
            <div class="guide-item">
              <span class="guide-number">5</span>
              <p>Хвалите за каждое успешно выполненное действие</p>
            </div>
            <div class="guide-item">
              <span class="guide-number">6</span>
              <p>Сохраняйте привычный распорядок дня</p>
            </div>
          </div>
        </div>
      </section>

      <!-- Важная информация -->
      <section class="help-section">
        <h2>⚠️ Важно знать</h2>
        <div class="info-grid">
          <div class="info-card">
            <h4>Анонимность</h4>
            <p>Все горячие линии гарантируют полную анонимность. Вам не нужно называть своё имя или какие-либо личные данные.</p>
          </div>
          <div class="info-card">
            <h4>Доступность</h4>
            <p>Звонки на все указанные номера бесплатны с любых телефонов, включая мобильные, на всей территории России.</p>
          </div>
          <div class="info-card">
            <h4>Профессионализм</h4>
            <p>На линиях работают опытные психологи, прошедшие специальную подготовку для работы с людьми в кризисных ситуациях.</p>
          </div>
        </div>
      </section>

      <div class="emergency-exit">
        <button @click="closeHelp" class="exit-button">
          <span>✕</span> Вернуться к модулям
        </button>
      </div>
    </div>

    <!-- Уведомления -->
    <Notification
      v-if="showNotification"
      :type="notificationType"
      :message="notificationMessage"
      :duration="3000"
      @close="showNotification = false"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import Notification from '@/components/UI/Notification.vue'

const router = useRouter()
const showNotification = ref(false)
const notificationMessage = ref('')
const notificationType = ref('info')

// Функция быстрого выхода (перенаправляет на главную)
const closeHelp = () => {
  notificationType.value = 'info'
  notificationMessage.value = 'Страница закрыта. Если вам нужна помощь, вы всегда можете вернуться.'
  showNotification.value = true

  setTimeout(() => {
    router.push('/')
  }, 1500)
}
</script>

<style scoped>
.help-page {
  width: 100%;
  display: flex;
  justify-content: center;
  padding: 2rem 1rem;
}

.help-card {
  width: 100%;
  max-width: 1000px;
  background: var(--color-white, #ffffff);
  border-radius: 16px;
  padding: 2.5rem;
  box-shadow: 0 4px 20px var(--shadow-color, rgba(0,0,0,0.1));
}

.help-header {
  text-align: center;
  margin-bottom: 2rem;
  padding-bottom: 1.5rem;
  border-bottom: 2px solid var(--color-primary, #88d424);
}

.help-header h1 {
  font-size: 36px;
  font-weight: 600;
  color: var(--color-primary-dark, #6aa91c);
  margin: 0 0 8px;
}

.help-header .subtitle {
  font-size: 18px;
  color: var(--color-gray, #9e9e9e);
  margin: 0;
}

.help-section {
  margin-bottom: 2.5rem;
}

.help-section h2 {
  font-size: 24px;
  font-weight: 600;
  color: var(--color-primary-dark, #6aa91c);
  margin: 0 0 1.5rem 0;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid var(--color-light-gray, #e0e0e0);
}

/* Экстренное уведомление */
.emergency-notice {
  display: flex;
  gap: 20px;
  background-color: rgba(244, 67, 54, 0.1);
  border: 1px solid var(--color-error, #f44336);
  border-radius: 12px;
  padding: 1.5rem;
  margin-bottom: 2rem;
}

.emergency-icon {
  font-size: 36px;
}

.emergency-content h3 {
  color: var(--color-error, #f44336);
  font-size: 20px;
  margin: 0 0 0.5rem 0;
}

.emergency-content p {
  color: var(--color-dark-gray, #616161);
  margin: 0;
  line-height: 1.6;
}

.emergency-content strong {
  color: var(--color-error, #f44336);
  font-size: 18px;
}

/* Сетка телефонов */
.phones-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 2rem;
}

.phone-card {
  background: linear-gradient(135deg, var(--color-white-smoke, #f5f5f5) 0%, var(--color-white, #ffffff) 100%);
  border: 1px solid var(--color-light-gray, #e0e0e0);
  border-radius: 12px;
  padding: 1.5rem;
  transition: transform 0.3s ease, border-color 0.3s ease;
}

.phone-card:hover {
  transform: translateY(-3px);
  border-color: var(--color-primary, #88d424);
}

.phone-number {
  font-size: 28px;
  font-weight: 600;
  color: var(--color-primary, #88d424);
  margin-bottom: 0.5rem;
}

.phone-description {
  color: var(--color-dark-gray, #616161);
  font-size: 15px;
  line-height: 1.5;
  margin-bottom: 0.5rem;
}

.phone-tag {
  font-size: 12px;
  color: var(--color-success, #4caf50);
  background-color: rgba(76, 175, 80, 0.1);
  padding: 4px 8px;
  border-radius: 4px;
  display: inline-block;
}

.phone-note {
  font-size: 12px;
  color: var(--color-gray, #9e9e9e);
  margin-top: 0.5rem;
  font-style: italic;
}

/* Совет */
.help-tip {
  display: flex;
  gap: 15px;
  background-color: rgba(136, 212, 36, 0.1);
  border: 1px solid var(--color-primary, #88d424);
  border-radius: 12px;
  padding: 1.5rem;
  margin-top: 1rem;
}

.tip-icon {
  font-size: 24px;
}

.tip-content h4 {
  color: var(--color-primary-dark, #6aa91c);
  font-size: 18px;
  margin: 0 0 0.5rem 0;
}

.tip-content p {
  color: var(--color-dark-gray, #616161);
  margin: 0;
  line-height: 1.6;
}

.tip-content em {
  color: var(--color-primary, #88d424);
  font-style: italic;
}

/* Книги */
.books-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.book-card {
  background: var(--color-white-smoke, #f5f5f5);
  border-radius: 12px;
  padding: 1.5rem;
  transition: transform 0.3s ease;
}

.book-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 12px var(--shadow-color, rgba(0,0,0,0.1));
}

.book-icon {
  font-size: 32px;
  margin-bottom: 1rem;
}

.book-card h3 {
  color: var(--color-primary-dark, #6aa91c);
  font-size: 18px;
  margin: 0 0 0.5rem 0;
}

.book-author {
  color: var(--color-primary, #88d424);
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 0.5rem;
}

.book-description {
  color: var(--color-gray, #9e9e9e);
  font-size: 14px;
  line-height: 1.6;
  margin: 0;
}

/* Ресурсы */
.resources-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
}

.resource-card {
  background: linear-gradient(135deg, var(--color-white, #ffffff) 0%, var(--color-white-smoke, #f5f5f5) 100%);
  border: 1px solid var(--color-light-gray, #e0e0e0);
  border-radius: 12px;
  padding: 1.5rem;
  text-decoration: none;
  color: inherit;
  transition: all 0.3s ease;
}

.resource-card:hover {
  transform: translateY(-3px);
  border-color: var(--color-primary, #88d424);
  box-shadow: 0 6px 12px var(--shadow-color, rgba(0,0,0,0.1));
}

.resource-icon {
  font-size: 36px;
  margin-bottom: 1rem;
}

.resource-card h3 {
  color: var(--color-primary-dark, #6aa91c);
  font-size: 20px;
  margin: 0 0 0.5rem 0;
}

.resource-description {
  color: var(--color-gray, #9e9e9e);
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 1rem;
}

.resource-features {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.feature {
  font-size: 12px;
  color: var(--color-dark-gray, #616161);
  background-color: var(--color-white-smoke, #f5f5f5);
  padding: 4px 8px;
  border-radius: 4px;
}

/* Упражнения для памяти */
.memory-exercises {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.exercise-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--color-dark-gray, #616161);
  font-size: 14px;
}

.exercise-dot {
  color: var(--color-primary, #88d424);
  font-size: 18px;
}

/* Памятка для близких */
.guide-card {
  background: linear-gradient(135deg, rgba(136, 212, 36, 0.05) 0%, var(--color-white, #ffffff) 100%);
  border: 1px solid var(--color-primary, #88d424);
  border-radius: 12px;
  padding: 1.5rem;
}

.guide-intro {
  color: var(--color-dark-gray, #616161);
  font-size: 16px;
  margin-bottom: 1.5rem;
  text-align: center;
}

.guide-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 15px;
}

.guide-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0.5rem;
}

.guide-number {
  width: 24px;
  height: 24px;
  background-color: var(--color-primary, #88d424);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
  flex-shrink: 0;
}

.guide-item p {
  color: var(--color-dark-gray, #616161);
  font-size: 14px;
  line-height: 1.5;
  margin: 0;
}

/* Информационная сетка */
.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.info-card {
  background: var(--color-white-smoke, #f5f5f5);
  border-radius: 12px;
  padding: 1.5rem;
  text-align: center;
}

.info-card h4 {
  color: var(--color-primary-dark, #6aa91c);
  font-size: 18px;
  margin: 0 0 0.5rem 0;
}

.info-card p {
  color: var(--color-gray, #9e9e9e);
  font-size: 14px;
  line-height: 1.6;
  margin: 0;
}

/* Экстренный выход */
.emergency-exit {
  margin-top: 2rem;
  padding-top: 1.5rem;
  border-top: 1px solid var(--color-light-gray, #e0e0e0);
  text-align: center;
}

.exit-button {
  background-color: var(--color-error, #f44336);
  color: white;
  border: none;
  border-radius: 8px;
  padding: 12px 24px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.exit-button:hover {
  background-color: #d32f2f;
  transform: scale(1.05);
}

.exit-note {
  color: var(--color-gray, #9e9e9e);
  font-size: 12px;
  margin-top: 0.5rem;
}

/* Адаптивность */
@media (max-width: 768px) {
  .help-card {
    padding: 1.5rem;
  }

  .help-header h1 {
    font-size: 28px;
  }

  .emergency-notice {
    flex-direction: column;
    text-align: center;
  }

  .help-tip {
    flex-direction: column;
    text-align: center;
  }

  .phones-grid,
  .books-grid,
  .resources-grid,
  .info-grid,
  .guide-grid {
    grid-template-columns: 1fr;
  }

  .guide-item {
    justify-content: center;
  }

  .exit-button {
    width: 100%;
    justify-content: center;
  }
}
</style>
