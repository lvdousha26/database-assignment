<template>
  <div class="app-container">
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
        ></el-pagination>
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
  </div>
</template>

<script>
import { getReceivedRequest, responseToRequest } from '@/api/authority'
import { useUserStore } from '@/stores/modules/user'
import { ElMessage } from 'element-plus'

export default {
  name: 'AuthorityRequest',
  setup() {
    const userStore = useUserStore()
    return { userStore }
  },
  data() {
    return {
      list: null,
      total: 0,
      listLoading: true,
      listQuery: {
        currentPage: 1,
        pageSize: 10
      }
    }
  },
  computed: {
    userId() {
      return this.userStore.user.id || -1
    }
  },
  created() {
    this.getList()
  },
  methods: {
    async getList() {
      this.listLoading = true
      try {
        const params = {
          userId: this.userId,
          pageSize: this.listQuery.pageSize,
          currentPage: this.listQuery.currentPage
        }
        const res = await getReceivedRequest(params)
        this.list = res.data.data.rows
        this.total = res.data.data.totalCount || 0
      } catch (error) {
        console.error(error)
      } finally {
        this.listLoading = false
      }
    },
    formatTime(timestamp) {
      if (!timestamp) return ''
      return new Date(timestamp).toLocaleString()
    },
    statusText(status) {
      const statusMap = {
        '-1': '待处理',
        '0': '已拒绝',
        '1': '已批准'
      }
      return statusMap[status] || '未知状态'
    },
    statusType(status) {
      const typeMap = {
        '-1': 'warning',
        '0': 'danger',
        '1': 'success'
      }
      return typeMap[status] || ''
    },
    handleSizeChange(val) {
      this.listQuery.pageSize = val
      this.getList()
    },
    handleCurrentChange(val) {
      this.listQuery.currentPage = val
      this.getList()
    },
    async handleApprove(id) {
      await this.handleResponse(id, 1)
    },
    async handleReject(id) {
      await this.handleResponse(id, 0)
    },
    async handleResponse(id, status) {
      try {
        await responseToRequest(id, status)
        ElMessage.success('操作成功')
        this.getList() // 刷新列表
      } catch (error) {
        ElMessage.error(error.message || '操作失败')
      }
    }
  }
}
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
  font-size: 16px;  /* 增大表格字体 */
}

.table-text {
  font-size: 15px;  /* 单元格文字大小 */
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