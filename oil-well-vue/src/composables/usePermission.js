import { ref, computed } from 'vue'
import { getMyPermissions } from '@/api/authority'
import { useUserStore } from '@/stores'

export function usePermission() {
  const userStore = useUserStore()
  const permissions = ref(null)
  const loading = ref(false)

  const isAdmin = computed(() => userStore.user?.role === '管理员')

  const fetchPermissions = async () => {
    if (isAdmin.value) {
      permissions.value = null
      return
    }
    try {
      loading.value = true
      const res = await getMyPermissions()
      if (res.data.code === '1') {
        permissions.value = res.data.data
      }
    } catch (e) {
      console.error('获取权限失败:', e)
    } finally {
      loading.value = false
    }
  }

  const canCreate = computed(() => isAdmin.value || permissions.value?.permCreate === 1)
  const canRead = computed(() => isAdmin.value || permissions.value?.permRead === 1)
  const canUpdate = computed(() => isAdmin.value || permissions.value?.permUpdate === 1)
  const canDelete = computed(() => isAdmin.value || permissions.value?.permDelete === 1)

  return {
    canCreate,
    canRead,
    canUpdate,
    canDelete,
    fetchPermissions,
    loading
  }
}
