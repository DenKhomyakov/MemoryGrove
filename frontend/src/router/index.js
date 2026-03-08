import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import AboutView from '../views/AboutView.vue'
import ProfileView from '../views/ProfileView.vue'
import HelpView from '../views/HelpView.vue'
import JournalView from '../views/journal/JournalView.vue'
import JournalEntryView from '../views/journal/JournalEntryView.vue'
import RemindersView from '../views/reminders/RemindersView.vue'
import PlacesView from '../views/places/PlacesView.vue'
import InterestsView from '../views/interests/InterestsView.vue'
import SkillsView from '../views/skills/SkillsView.vue'
import MemoryPhotosView from '../views/memory/MemoryPhotosView.vue'
import BrainTrainerView from '../views/brain-trainer/BrainTrainerView.vue'
import ContactsView from '../views/contacts/ContactsView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
      meta: { requiresAuth: true }
    },
    {
      path: '/profile',
      name: 'profile',
      component: ProfileView,
      meta: { requiresAuth: true }
    },
    {
      path: '/journal',
      name: 'journal',
      component: JournalView,
      meta: { requiresAuth: true }
    },
    {
      path: '/journal/:id',
      name: 'journal-entry',
      component: JournalEntryView,
      meta: { requiresAuth: true }
    },
    {
      path: '/reminders',
      name: 'reminders',
      component: RemindersView,
      meta: { requiresAuth: true }
    },
    {
      path: '/places',
      name: 'places',
      component: PlacesView,
      meta: { requiresAuth: true }
    },
    {
      path: '/interests',
      name: 'interests',
      component: InterestsView,
      meta: { requiresAuth: true }
    },
    {
      path: '/skills',
      name: 'skills',
      component: SkillsView,
      meta: { requiresAuth: true }
    },
    {
      path: '/memory/photos',
      name: 'memory-photos',
      component: MemoryPhotosView,
      meta: { requiresAuth: true }
    },
    {
      path: '/brain-trainer',
      name: 'brain-trainer',
      component: BrainTrainerView,
      meta: { requiresAuth: true }
    },
    {
      path: '/contacts',
      name: 'contacts',
      component: ContactsView,
      meta: { requiresAuth: true }
    },
    {
      path: '/help',
      name: 'help',
      component: HelpView
    },
    {
      path: '/about',
      name: 'about',
      component: AboutView
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView
    },
    {
      path: '/register',
      name: 'register',
      component: RegisterView
    }
  ]
})

// Защита маршрутов
router.beforeEach((to, from, next) => {
  const isAuthenticated = localStorage.getItem('accessToken')

  if (to.meta.requiresAuth && !isAuthenticated) {
    next('/login')
  } else if ((to.path === '/login' || to.path === '/register') && isAuthenticated) {
    next('/')
  } else {
    next()
  }
})

export default router
