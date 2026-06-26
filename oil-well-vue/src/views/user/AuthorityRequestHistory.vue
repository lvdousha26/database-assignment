<template>
  <div class="authority-history-container">
    <el-card shadow="never" class="current-perm-card">
      <template #header>
        <div class="card-header">
          <span>当前权限</span>
        </div>
      </template>
      <div class="current-perm-body" v-if="currentPerm">
        <el-tag v-if="currentPerm.permCreate" size="large" style="margin-right:12px">增</el-tag>
        <el-tag v-if="currentPerm.permRead" size="large" style="margin-right:12px" type="success">查</el-tag>
        <el-tag v-if="currentPerm.permUpdate" size="large" style="margin-right:12px" type="warning">改</el-tag>
        <el-tag v-if="currentPerm.permDelete" size="large" type="danger">删</el-tag>
        <span v-if="!currentPerm.permCreate && !currentPerm.permRead && !currentPerm.permUpdate && !currentPerm.permDelete" class="no-perm">暂无权限</span>
      </div>
      <div class="current-perm-body" v-else>
        <span class="no-perm">暂无权限</span>
      </div>
    </el-card>

    <el-card shadow="never" style="margin-top:16px">
      <template #header>
        <div class="card-header">
          <span>权限申请历史记录</span>
        </div>
      </template>

      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="adminName" label="处理管理员" width="150" />
        <el-table-column label="申请权限" width="200">
          <template #default="{ row }">
            <el-tag v-if="row.permCreate" size="small" style="margin-right:4px">增</el-tag>
            <el-tag v-if="row.permRead" size="small" style="margin-right:4px" type="success">查</el-tag>
            <el-tag v-if="row.permUpdate" size="small" style="margin-right:4px" type="warning">改</el-tag>
            <el-tag v-if="row.permDelete" size="small" type="danger">删</el-tag>
            <span v-if="!row.permCreate && !row.permRead && !row.permUpdate && !row.permDelete">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="requestMessage" label="申请理由" min-width="200" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="申请时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="processedAt" label="处理时间" width="180">
          <template #default="{ row }">
            {{ row.processedAt ? formatDateTime(row.processedAt) : '未处理' }}
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="totalCount"
            :page-sizes="[4, 10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores'
import { getAuthoritySentHistory } from '@/api/authority_request_api'
import { getMyPermissions } from '@/api/authority'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()

const loading = ref(false)
const tableData = ref([])
const totalCount = ref(0)
const currentPage = ref(1)
const pageSize = ref(4)
const currentPerm = ref(null)

const getStatusText = (status) => {
  const map = { '-1': '待处理', '0': '已拒绝', '1': '已批准' }
  return map[status] || '未知'
}

const getStatusTagType = (status) => {
  const map = { '-1': 'info', '0': 'danger', '1': 'success' }
  return map[status] || ''
}

const formatDateTime = (dateTimeString) => {
  if (!dateTimeString) return ''
  return new Date(dateTimeString).toLocaleString()
}

const fetchData = async () => {
  if (!userStore.user?.id) {
    ElMessage.error('无法获取用户ID')
    return
  }
  try {
    loading.value = true
    const res = await getAuthoritySentHistory({
      userId: userStore.user.id,
      currentPage: currentPage.value,
      pageSize: pageSize.value
    })
    if (res.data.code === '1') {
      tableData.value = res.data.data.rows || []
      totalCount.value = res.data.data.totalCount || 0
    } else {
      ElMessage.error(res.data.msg || '获取数据失败')
    }
  } catch (error) {
    ElMessage.error('请求失败，请稍后重试')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const fetchCurrentPerm = async () => {
  try {
    const res = await getMyPermissions()
    if (res.data.code === '1' && res.data.data) {
      currentPerm.value = res.data.data
    }
  } catch (error) {
    console.error('获取当前权限失败:', error)
  }
}

const handleSizeChange = (val) => {
  pageSize.value = val
  fetchData()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchData()
}

onMounted(() => {
  fetchCurrentPerm()
  fetchData()
})
</script>

<style scoped>
.authority-history-container {
  padding: 20px;
}
.card-header {
  font-size: 18px;
  font-weight: bold;
}
.current-perm-card {
  margin-bottom: 4px;
}
.current-perm-body {
  display: flex;
  align-items: center;
  min-height: 40px;
}
.no-perm {
  color: #999;
  font-size: 15px;
}
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
