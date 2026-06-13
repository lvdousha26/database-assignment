<template>
  <div class="permission-request-container">
    <div class="search-bar">
      <el-input
          v-model="searchQuery"
          placeholder="搜索管理员"
          clearable
          @clear="handleSearchClear"
          @keyup.enter="handleSearch"
      >
        <template #append>
          <el-button :icon="Search" @click="handleSearch" />
        </template>
      </el-input>
      <el-radio-group v-model="listType" @change="fetchAdmins">
        <el-radio-button label="all">所有管理员</el-radio-button>
        <el-radio-button label="available">可申请管理员</el-radio-button>
      </el-radio-group>
    </div>

    <div class="admin-list">
      <el-table :data="adminList" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="100" />
        <el-table-column prop="username" label="管理员名称" />
        <el-table-column label="头像" width="120">
          <template #default="{ row }">
            <el-avatar :src="row.avatar" :alt="row.username" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button
                type="primary"
                size="small"
                @click="handleRequest(row)"
                :disabled="requesting"
            >
              申请权限
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchAdmins"
          @current-change="fetchAdmins"
      />
    </div>

    <el-dialog
        v-model="dialogVisible"
        title="申请权限"
        width="30%"
        :before-close="handleClose"
    >
      <el-input
          v-model="requestMessage"
          type="textarea"
          placeholder="请输入申请原因"
          :rows="4"
      />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmRequest" :loading="requesting">
            确认
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores'
import {
  getAdminList,
  getAvailableAdmins,
  addPermissionRequest
} from '@/api/authority_request_api'

const userStore = useUserStore()

// 搜索相关
const searchQuery = ref('')
const listType = ref('all')
const loading = ref(false)
const requesting = ref(false)

// 管理员列表相关
const adminList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 申请相关
const dialogVisible = ref(false)
const requestMessage = ref('')
const selectedAdmin = ref(null)

onMounted(() => {
  fetchAdmins()
})

const fetchAdmins = async () => {
  try {
    loading.value = true
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value
    }

    if (searchQuery.value) {
      params.search = searchQuery.value
    }

    let res
    if (listType.value === 'all') {
      res = await getAdminList(params)
    } else {
      // 对于可申请管理员，可能需要传递当前用户ID
      res = await getAvailableAdmins({
        ...params,
        userId: userStore.user.id
      })
    }

    if (res.data.code == '1') {
      adminList.value = res.data.data.records
      total.value = res.data.data.total
    } else {
      ElMessage.error(res.msg || '获取管理员列表失败')
    }
  } catch (error) {
    ElMessage.error('网络错误，请稍后重试')
    console.error('获取管理员列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchAdmins()
}

const handleSearchClear = () => {
  searchQuery.value = ''
  currentPage.value = 1
  fetchAdmins()
}

const handleRequest = (admin) => {
  selectedAdmin.value = admin
  requestMessage.value = ''
  dialogVisible.value = true
}

const confirmRequest = async () => {
  if (!requestMessage.value.trim()) {
    ElMessage.warning('请输入申请原因')
    return
  }

  try {
    requesting.value = true
    const res = await addPermissionRequest({
      userId: userStore.user.id,
      adminId: selectedAdmin.value.id,
      requestMessage: requestMessage.value
    })

    // 成功情况
    if (res.data.code === '1') {
      ElMessage.success('申请已提交，等待管理员审核')
      dialogVisible.value = false
      await fetchAdmins()
    }
    // 后端返回的业务错误（包括重复提交）
    else {
      ElMessage.warning(res.data.msg || '申请提交失败') // 使用warning更合适
    }
  } catch (error) {
    // 仅处理真正的网络错误（status !== 200）
    if (!error.response && !res.data.code === '-1') {
      ElMessage.error('网络错误，请稍后重试')
      console.error('网络请求失败:', error)
    }
    // 其他HTTP错误（如400等）已经在res.data.msg中处理
  } finally {
    requesting.value = false
  }
}

const handleClose = (done) => {
  ElMessageBox.confirm('确认关闭？未提交的申请将丢失', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
      .then(() => {
        done()
      })
      .catch(() => {})
}
</script>

<style scoped>
.permission-request-container {
  padding: 20px;
}

.search-bar {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.search-bar .el-input {
  width: 300px;
}

.admin-list {
  margin-top: 20px;
}

.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>