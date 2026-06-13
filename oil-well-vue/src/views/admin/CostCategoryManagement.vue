<script setup>
import { ref, onMounted } from 'vue'
import { getCostCategoryList, addCostCategory, updateCostCategory, deleteCostCategory } from '@/api/costCategory'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Edit, Delete } from '@element-plus/icons-vue'

const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const parentCategoryOptions = ref([])

const searchForm = ref({ categoryName: '' })

const form = ref({ id: null, categoryName: '', parentId: null, description: '' })

const formRules = {
  categoryName: [{ required: true, message: '请输入类别名称', trigger: 'blur' }]
}

const pagination = ref({ page: 1, pageSize: 10 })

const loadOptions = async () => {
  try {
    const res = await getCostCategoryList({ page: 1, pageSize: 9999 })
    if (res.data.code === '1') {
      parentCategoryOptions.value = res.data.data.rows || res.data.data.list || []
    }
  } catch (e) {
    // 静默处理
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const params = { page: pagination.value.page, pageSize: pagination.value.pageSize }
    if (searchForm.value.categoryName) params.categoryName = searchForm.value.categoryName

    const res = await getCostCategoryList(params)
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
  searchForm.value = { categoryName: '' }
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  form.value = { id: null, categoryName: '', parentId: null, description: '' }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  form.value = { ...row, parentId: row.parentId || null }
  dialogVisible.value = true
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确认删除该成本类别吗？', '提示', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
    })
    const res = await deleteCostCategory(id)
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
    res = await updateCostCategory(form.value)
  } else {
    res = await addCostCategory(form.value)
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

onMounted(() => {
  loadOptions()
  loadData()
})
</script>

<template>
  <div class="management-page">
    <el-card shadow="never" class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="类别名称">
          <el-input v-model="searchForm.categoryName" placeholder="模糊搜索" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <div class="action-bar">
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增成本类别</el-button>
    </div>

    <el-card shadow="never">
      <el-table :data="tableData" v-loading="loading" stripe border style="width: 100%">
        <el-table-column type="index" label="序号" width="70" />
        <el-table-column prop="categoryName" label="类别名称" min-width="180" />
        <el-table-column prop="parentName" label="父类别" width="150" />
        <el-table-column prop="description" label="描述" min-width="280" />
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
      :title="isEdit ? '编辑成本类别' : '新增成本类别'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="form" label-width="100px" :rules="formRules">
        <el-form-item label="类别名称" prop="categoryName">
          <el-input v-model="form.categoryName" placeholder="请输入类别名称" />
        </el-form-item>
        <el-form-item label="父类别" prop="parentId">
          <el-select v-model="form.parentId" placeholder="无（顶级类别）" clearable style="width: 100%">
            <el-option v-for="p in parentCategoryOptions" :key="p.id" :label="p.categoryName" :value="p.id" />
          </el-select>
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
