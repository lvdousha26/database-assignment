"<template>
  <div class="authority-history-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>权限申请历史记录</span>
        </div>
      </template>

      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="adminName" label="处理管理员" width="150" />
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

<script>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/modules/user'
import { getAuthoritySentHistory } from '@/api/authority_request_api'
import { ElMessage } from 'element-plus'

export default {
  name: 'AuthoritySentHistory',
  setup() {
    const userStore = useUserStore()
    const userId = computed(() => userStore.user.id || -1)

    const loading = ref(false)
    const tableData = ref([])
    const totalCount = ref(0)
    const currentPage = ref(1)
    const pageSize = ref(4)

    // 获取状态文本
    const getStatusText = (status) => {
      const statusMap = {
        '-1': '未处理',
        '0': '已拒绝',
        '1': '已接受'
      }
      return statusMap[status] || '未知状态'
    }

    // 获取状态标签类型
    const getStatusTagType = (status) => {
      const typeMap = {
        '-1': 'info',
        '0': 'danger',
        '1': 'success'
      }
      return typeMap[status] || ''
    }

    // 格式化日期时间
    const formatDateTime = (dateTimeString) => {
      if (!dateTimeString) return ''
      const date = new Date(dateTimeString)
      return date.toLocaleString()
    }

    // 获取历史记录数据
    const fetchData = async () => {
      if (userId.value === -1) {
        ElMessage.error('无法获取用户ID')
        return
      }

      try {
        loading.value = true
        const params = {
          userId: userId.value,
          currentPage: currentPage.value,
          pageSize: pageSize.value
        }
        const res = await getAuthoritySentHistory(params)

        if (res.data.code === '1') {
          tableData.value = res.data.data.rows
          totalCount.value = res.data.data.totalCount
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

    // 分页大小改变
    const handleSizeChange = (val) => {
      pageSize.value = val
      fetchData()
    }

    // 当前页改变
    const handleCurrentChange = (val) => {
      currentPage.value = val
      fetchData()
    }

    onMounted(() => {
      fetchData()
    })

    return {
      userId,
      loading,
      tableData,
      totalCount,
      currentPage,
      pageSize,
      getStatusText,
      getStatusTagType,
      formatDateTime,
      handleSizeChange,
      handleCurrentChange
    }
  }
}
</script>

<style scoped>
.authority-history-container {
  padding: 20px;
}
.card-header {
  font-size: 18px;
  font-weight: bold;
}
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>