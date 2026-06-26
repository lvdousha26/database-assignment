<template>
  <div class="app-container">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="待处理申请" name="requests">
        <el-card class="box-card">
          <div class="filter-container">
            <el-pagination
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
                :current-page="listQuery.currentPage"
                :page-sizes="[10, 20, 30, 50]"
                :page-size="listQuery.pageSize"
                layout="total, sizes, prev, pager, next, jumper"
                :total="total"
                class="pagination-container"
            />
          </div>

          <el-table
              v-loading="listLoading"
              :data="list"
              border
              fit
              highlight-current-row
              style="width: 100%"
              class="wide-table"
          >
            <el-table-column label="申请人" prop="username" align="center" width="180">
              <template #default="{row}">
                <span class="table-text">{{ row.username }}</span>
              </template>
            </el-table-column>

            <el-table-column label="申请权限" width="200" align="center">
              <template #default="{ row }">
                <el-tag v-if="row.permCreate" size="small" style="margin-right:4px">增</el-tag>
                <el-tag v-if="row.permRead" size="small" style="margin-right:4px" type="success">查</el-tag>
                <el-tag v-if="row.permUpdate" size="small" style="margin-right:4px" type="warning">改</el-tag>
                <el-tag v-if="row.permDelete" size="small" type="danger">删</el-tag>
                <span v-if="!row.permCreate && !row.permRead && !row.permUpdate && !row.permDelete">-</span>
              </template>
            </el-table-column>

            <el-table-column label="申请理由" prop="requestMessage" align="center" min-width="300">
              <template #default="{row}">
                <span class="table-text">{{ row.requestMessage }}</span>
              </template>
            </el-table-column>

            <el-table-column label="申请时间" prop="createdAt" align="center" width="220">
              <template #default="{row}">
                <span class="table-text">{{ formatTime(row.createdAt) }}</span>
              </template>
            </el-table-column>

            <el-table-column label="状态" prop="status" align="center" width="150">
              <template #default="{row}">
                <el-tag :type="statusType(row.status)" size="medium">
                  {{ statusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>

            <el-table-column label="操作" align="center" width="220">
              <template #default="{row}">
                <el-button
                    v-if="row.status === -1"
                    type="success"
                    size="medium"
                    @click="handleApprove(row.id)"
                >
                  批准
                </el-button>
                <el-button
                    v-if="row.status === -1"
                    type="danger"
                    size="medium"
                    @click="handleReject(row.id)"
                >
                  拒绝
                </el-button>
                <span v-if="row.status !== -1" class="table-text">已处理</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="已授权用户" name="users">
        <el-card class="box-card">
          <el-table
              v-loading="usersLoading"
              :data="authorizedUsers"
              border
              fit
              style="width: 100%"
              class="wide-table"
          >
            <el-table-column label="用户ID" prop="userId" width="80" align="center" />
            <el-table-column label="用户名" prop="username" width="150" align="center" />
            <el-table-column label="授予权限" width="300" align="center">
              <template #default="{ row }">
                <el-tag
                    :type="perm.tagType"
                    style="margin-right:8px; cursor:pointer"
                    @click="togglePerm(row, 'permCreate')"
                >
                  增 {{ row.permCreate ? '✓' : '✗' }}
                </el-tag>
                <el-tag
                    :type="perm.tagType"
                    style="margin-right:8px; cursor:pointer"
                    @click="togglePerm(row, 'permRead')"
                >
                  查 {{ row.permRead ? '✓' : '✗' }}
                </el-tag>
                <el-tag
                    :type="perm.tagType"
                    style="margin-right:8px; cursor:pointer"
                    @click="togglePerm(row, 'permUpdate')"
                >
                  改 {{ row.permUpdate ? '✓' : '✗' }}
                </el-tag>
                <el-tag
                    :type="perm.tagType"
                    style="cursor:pointer"
                    @click="togglePerm(row, 'permDelete')"
                >
                  删 {{ row.permDelete ? '✓' : '✗' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="160" align="center">
              <template #default="{ row }">
                <el-button type="danger" size="small" @click="handleRevokeAll(row)">
                  全部收回
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="filter-container" style="margin-top:16px">
            <el-pagination
                @current-change="handleUsersPageChange"
                :current-page="usersPage"
                :page-size="usersPageSize"
                layout="total, prev, pager, next"
                :total="usersTotal"
            />
          </div>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { getReceivedRequest, responseToRequest, getAuthorizedUsers, updateUserAuthority } from '@/api/authority'
import { useUserStore } from '@/stores'
import { ElMessage, ElMessageBox } from 'element-plus'

const userStore = useUserStore()
const activeTab = ref('requests')

// ---- 待处理申请 ----
const list = ref([])
const total = ref(0)
const listLoading = ref(false)
const listQuery = ref({ currentPage: 1, pageSize: 10 })

const userId = userStore.user?.id || -1

const perm = { tagType: 'info' }

const getList = async () => {
  listLoading.value = true
  try {
    const params = { userId, pageSize: listQuery.value.pageSize, currentPage: listQuery.value.currentPage }
    const res = await getReceivedRequest(params)
    list.value = res.data.data.rows || []
    total.value = res.data.data.totalCount || 0
  } catch (error) {
    console.error(error)
  } finally {
    listLoading.value = false
  }
}

const formatTime = (timestamp) => {
  if (!timestamp) return ''
  return new Date(timestamp).toLocaleString()
}

const statusText = (status) => {
  const map = { '-1': '待处理', '0': '已拒绝', '1': '已批准' }
  return map[status] || '未知'
}

const statusType = (status) => {
  const map = { '-1': 'warning', '0': 'danger', '1': 'success' }
  return map[status] || ''
}

const handleApprove = async (id) => { await handleResponse(id, 1) }
const handleReject = async (id) => { await handleResponse(id, 0) }

const handleResponse = async (id, status) => {
  try {
    await responseToRequest(id, status)
    ElMessage.success('操作成功')
    getList()
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  }
}

const handleSizeChange = (val) => { listQuery.value.pageSize = val; getList() }
const handleCurrentChange = (val) => { listQuery.value.currentPage = val; getList() }

// ---- 已授权用户 ----
const authorizedUsers = ref([])
const usersLoading = ref(false)
const usersPage = ref(1)
const usersPageSize = ref(10)
const usersTotal = ref(0)

const fetchAuthorizedUsers = async () => {
  usersLoading.value = true
  try {
    const res = await getAuthorizedUsers({
      username: userStore.user?.username || '',
      currentPage: usersPage.value,
      pageSize: usersPageSize.value
    })
    if (res.data.code === '1') {
      const data = res.data.data
      authorizedUsers.value = (data.records || []).map(u => ({
        ...u,
        permCreate: u.permCreate ?? 0,
        permRead: u.permRead ?? 0,
        permUpdate: u.permUpdate ?? 0,
        permDelete: u.permDelete ?? 0
      }))
      usersTotal.value = data.total || 0
    }
  } catch (error) {
    console.error(error)
  } finally {
    usersLoading.value = false
  }
}

const togglePerm = async (row, field) => {
  const newVal = row[field] ? 0 : 1
  try {
    await updateUserAuthority(row.userId, {
      status: 1,
      permCreate: field === 'permCreate' ? newVal : row.permCreate,
      permRead: field === 'permRead' ? newVal : row.permRead,
      permUpdate: field === 'permUpdate' ? newVal : row.permUpdate,
      permDelete: field === 'permDelete' ? newVal : row.permDelete
    })
    row[field] = newVal
    ElMessage.success('权限已更新')
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleRevokeAll = async (row) => {
  try {
    await ElMessageBox.confirm(`确定收回用户「${row.username}」的所有权限？`, '确认', {
      type: 'warning', confirmButtonText: '确定', cancelButtonText: '取消'
    })
    await updateUserAuthority(row.userId, {
      status: 0,
      permCreate: 0, permRead: 0, permUpdate: 0, permDelete: 0
    })
    ElMessage.success('已全部收回')
    fetchAuthorizedUsers()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('操作失败')
  }
}

const handleUsersPageChange = (val) => {
  usersPage.value = val
  fetchAuthorizedUsers()
}

onMounted(() => {
  getList()
})

// 切换 Tab 时加载已授权用户
watch(activeTab, (val) => {
  if (val === 'users') fetchAuthorizedUsers()
})
</script>

<style scoped>
.app-container {
  width: calc(100% - 40px);
  margin: 20px auto;
  padding: 0;
}

.box-card {
  width: 100%;
  padding: 20px;
}

.filter-container {
  margin-bottom: 20px;
  display: flex;
  justify-content: flex-end;
}

.pagination-container {
  width: 100%;
}

.wide-table {
  font-size: 16px;
}

.table-text {
  font-size: 15px;
}

.el-table {
  --el-table-header-text-color: #333;
  --el-table-text-color: #333;
  --el-table-font-size: 16px;
}

.el-tag {
  font-size: 15px;
}

.el-button {
  font-size: 15px;
}

.el-pagination {
  --el-pagination-font-size: 15px;
}
</style>
