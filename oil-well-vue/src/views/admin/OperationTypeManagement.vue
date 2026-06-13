<script setup>
import { ref, onMounted } from 'vue'
import { getOperationTypeList, addOperationType, updateOperationType, deleteOperationType } from '@/api/operationType'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Edit, Delete } from '@element-plus/icons-vue'

const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const searchForm = ref({ typeName: '' })

const form = ref({ id: null, typeName: '', description: '' })

const formRules = {
  typeName: [{ required: true, message: '请输入类型名称', trigger: 'blur' }]
}

const pagination = ref({ page: 1, pageSize: 10 })

const loadData = async () => {
  loading.value = true
  try {
    const params = { page: pagination.value.page, pageSize: pagination.value.pageSize }
    if (searchForm.value.typeName) params.typeName = searchForm.value.typeName

    const res = await getOperationTypeList(params)
    if (res.data.code === '1') {
      tableData.value = res.data.data.rows || res.data.data.list || []
      total.value = res.data.data.total || 0
    }
  } catch (e) {
    // 静默处理
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.value.page = 1
  loadData()
}

const handleReset = () => {
  searchForm.value = { typeName: '' }
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  form.value = { id: null, typeName: '', description: '' }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确认删除该作业类型吗？', '提示', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
    })
    const res = await deleteOperationType(id)
    if (res.data.code === '1') {
      ElMessage.success('删除成功')
      loadData()
    }
  } catch (e) {
    // cancel or error
  }
}

const handleSave = async () => {
  if (!formRef.value) return
  await formRef.value.validate()

  let res
  if (isEdit.value) {
    res = await updateOperationType(form.value)
  } else {
    res = await addOperationType(form.value)
  }

  if (res.data.code === '1') {
    ElMessage.success(isEdit.value ? '更新成功' : '新增成功')
    dialogVisible.value = false
    loadData()
  }
}

const handlePageChange = (page) => {
  pagination.value.page = page
  loadData()
}

const handleSizeChange = (size) => {
  pagination.value.pageSize = size
  pagination.value.page = 1
  loadData()
}

onMounted(() => { loadData() })
</script>

<template>
  <div class="management-page">
    <el-card shadow="never" class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="类型名称">
          <el-input v-model="searchForm.typeName" placeholder="模糊搜索" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <div class="action-bar">
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增作业类型</el-button>
    </div>

    <el-card shadow="never">
      <el-table :data="tableData" v-loading="loading" stripe border style="width: 100%">
        <el-table-column type="index" label="序号" width="70" />
        <el-table-column prop="typeName" label="类型名称" min-width="200" />
        <el-table-column prop="description" label="描述" min-width="300" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" :icon="Delete" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑作业类型' : '新增作业类型'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="form" label-width="100px" :rules="formRules">
        <el-form-item label="类型名称" prop="typeName">
          <el-input v-model="form.typeName" placeholder="请输入类型名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { Search, Plus, Edit, Delete } from '@element-plus/icons-vue'
export default { components: { Search, Plus, Edit, Delete } }
</script>

<style scoped>
.management-page { max-width: 1400px; margin: 0 auto; }
.search-card { margin-bottom: 16px; border-radius: 8px; }
.action-bar { margin-bottom: 16px; }
.pagination-wrap { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>
