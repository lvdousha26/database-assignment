<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { userListService, userUpdateRoleService, userUpdateStatusService } from '@/api/user'
import { useUserStore } from '@/stores'
import { Delete } from '@element-plus/icons-vue'

const userStore = useUserStore()
const users = ref([])

const loadUsers = async () => {
  const res = await userListService()
  users.value = res.data.data || []
}

const onRoleChange = async (row) => {
  try {
    await userUpdateRoleService(row.id, row.role)
    ElMessage.success('角色已更新')
  } catch {
    loadUsers()
  }
}

const onStatusChange = async (row) => {
  try {
    await userUpdateStatusService(row.id, row.status)
    ElMessage.success(row.status === 1 ? '已启用' : '已禁用')
  } catch {
    loadUsers()
  }
}

onMounted(loadUsers)
</script>

<template>
  <div class="user-mgmt">
    <h3 style="margin-bottom: 20px;">用户管理</h3>
    <el-table :data="users" stripe border style="width: 100%">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="username" label="用户名" min-width="140" />
      <el-table-column label="角色" width="140">
        <template #default="{ row }">
          <el-select
            v-model="row.role"
            size="small"
            :disabled="row.id === userStore.user?.id"
            @change="onRoleChange(row)"
          >
            <el-option label="管理员" value="管理员" />
            <el-option label="普通用户" value="普通用户" />
          </el-select>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-switch
            v-model="row.status"
            :active-value="1"
            :inactive-value="0"
            :disabled="row.id === userStore.user?.id"
            @change="onStatusChange(row)"
          />
        </template>
      </el-table-column>
      <el-table-column prop="gender" label="性别" width="70" />
      <el-table-column prop="phone" label="手机号" min-width="130" />
      <el-table-column prop="createdAt" label="注册时间" min-width="170" />
    </el-table>
  </div>
</template>

<style scoped>
.user-mgmt {
  padding: 20px;
  background: #fff;
  border-radius: 8px;
}
</style>
