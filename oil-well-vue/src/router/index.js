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
          path: '/admin/ai',
          component: () => import('@/views/admin/AIChat.vue')
        },
        {
          path: '/admin/profile',
          component: () => import('@/views/admin/Profile.vue')
        },
        {
          path: '/admin/message',
          component: () => import('@/views/admin/MessageManagement.vue')
        },
        {
          path: '/admin/users',
          component: () => import('@/views/admin/UserManagement.vue')
        },
        {
          path: '/admin/authority',
          component: () => import('@/views/admin/AdminAuthority.vue')
        }
      ]
    },
    {
      path: '/user',
      component: () => import('@/views/user/Layout.vue'),
      children: [
        {
          path: 'authority',
          component: () => import('@/views/user/PermissionRequest.vue')
        },
        {
          path: 'history',
          component: () => import('@/views/user/AuthorityRequestHistory.vue')
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
