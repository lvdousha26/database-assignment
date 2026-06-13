import { createRouter, createWebHashHistory } from 'vue-router'
import { useUserStore } from '@/stores'

const router = createRouter({
  history: createWebHashHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/admin',
      component: () => import('@/views/admin/Layout.vue'),
      redirect: '/admin/home',
      children: [
        {
          path: '/admin/home',
          component: () => import('@/views/admin/AdminHome.vue')
        },
        {
          path: '/admin/well',
          component: () => import('@/views/admin/WellManagement.vue')
        },
        {
          path: '/admin/operation',
          component: () => import('@/views/admin/OperationManagement.vue')
        },
        {
          path: '/admin/cost',
          component: () => import('@/views/admin/CostManagement.vue')
        },
        {
          path: '/admin/operation-type',
          component: () => import('@/views/admin/OperationTypeManagement.vue')
        },
        {
          path: '/admin/cost-category',
          component: () => import('@/views/admin/CostCategoryManagement.vue')
        },
        {
          path: '/admin/ai',
          component: () => import('@/views/admin/AIChat.vue')
        }
      ]
    },
    {
      path: '/',
      redirect: '/login'
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/public/Login.vue'),
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/public/Register.vue'),
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: () => import('@/views/DashboardStandalone.vue'),
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/admin'
    }
  ],
})

router.beforeEach((to, from, next) => {
  const useStore = useUserStore()
  const nextArr = ['/login', '/register']
  if (useStore.token || nextArr.includes(to.path)) {
    next()
  } else {
    next('/login')
  }
})

export default router
