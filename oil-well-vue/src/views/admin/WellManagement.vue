<script setup>
import { ref, onMounted } from 'vue'
import { getWellList, addWell, updateWell, deleteWell } from '@/api/well'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { usePermission } from '@/composables/usePermission'

const { canCreate, canUpdate, canDelete, fetchPermissions } = usePermission()

const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const searchForm = ref({
  wellName: '',
  wellType: '',
  wellStatus: ''
})

const form = ref({
  id: null,
  wellName: '',
  wellType: '',
  wellStatus: '生产',
  fieldName: '',
  layer: '',
  depth: null,
  operator: '',
  drillingDate: ''
})

const wellTypeOptions = ['油井', '水井']
const statusOptions = ['生产', '关停', '报废', '注水']

const formRules = {
  wellName: [{ required: true, message: '请输入井号', trigger: 'blur' }],
  wellType: [{ required: true, message: '请选择井类型', trigger: 'change' }],
  wellStatus: [{ required: true, message: '请选择状态', trigger: 'change' }],
  fieldName: [{ required: true, message: '请输入所属油田', trigger: 'blur' }],
  operator: [{ required: true, message: '请输入负责人', trigger: 'blur' }]
}

const pagination = ref({
  page: 1,
  pageSize: 10
})

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.value.page,
      pageSize: pagination.value.pageSize,
      ...searchForm.value
    }
    Object.keys(params).forEach(k => { if (!params[k]) delete params[k] })

    const res = await getWellList(params)
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
  searchForm.value = { wellName: '', wellType: '', wellStatus: '' }
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  form.value = {
    id: null, wellName: '', wellType: '', wellStatus: '生产',
    fieldName: '', layer: '', depth: null, operator: '', drillingDate: ''
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确认删除该油水井吗？', '提示', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
    })
    const res = await deleteWell(id)
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
    res = await updateWell(form.value)
  } else {
    res = await addWell(form.value)
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
  loadData()
  fetchPermissions()
})
</script>

<template>
  <div class="management-page">
    <!-- 搜索栏 -->
    <el-card shadow="never" class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="井号">
          <el-input v-model="searchForm.wellName" placeholder="模糊搜索" clearable />
        </el-form-item>
        <el-form-item label="井类型">
          <el-select v-model="searchForm.wellType" placeholder="全部" clearable style="width: 130px">
            <el-option v-for="t in wellTypeOptions" :key="t" :label="t" :value="t" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.wellStatus" placeholder="全部" clearable style="width: 130px">
            <el-option v-for="s in statusOptions" :key="s" :label="s" :value="s" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作栏 -->
    <div class="action-bar">
      <el-button type="primary" :icon="Plus" @click="handleAdd" v-if="canCreate">新增油水井</el-button>
    </div>

    <!-- 表格 -->
    <el-card shadow="never">
      <el-table :data="tableData" v-loading="loading" stripe border style="width: 100%">
        <el-table-column prop="wellName" label="井号" min-width="140" />
        <el-table-column prop="wellType" label="类型" width="90">
          <template #default="{ row }">
            <el-tag :type="row.wellType === '油井' ? 'warning' : 'primary'">{{ row.wellType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="wellStatus" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.wellStatus === '生产' ? 'success' : row.wellStatus === '关停' ? 'danger' : row.wellStatus === '注水' ? 'warning' : 'info'">
              {{ row.wellStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="fieldName" label="所属油田" width="120" />
        <el-table-column prop="layer" label="层位" width="100" />
        <el-table-column prop="depth" label="井深(m)" width="100" />
        <el-table-column prop="operator" label="负责人" width="100" />
        <el-table-column prop="drillingDate" label="投产日期" width="120" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" :icon="Edit" @click="handleEdit(row)" v-if="canUpdate">编辑</el-button>
            <el-button size="small" type="danger" :icon="Delete" @click="handleDelete(row.id)" v-if="canDelete">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
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

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑油水井' : '新增油水井'"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="form" label-width="100px" :rules="formRules">
        <el-form-item label="井号" prop="wellName">
          <el-input v-model="form.wellName" placeholder="请输入井号" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="井类型" prop="wellType">
              <el-select v-model="form.wellType" style="width: 100%">
                <el-option v-for="t in wellTypeOptions" :key="t" :label="t" :value="t" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="wellStatus">
              <el-select v-model="form.wellStatus" style="width: 100%">
                <el-option v-for="s in statusOptions" :key="s" :label="s" :value="s" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属油田" prop="fieldName">
              <el-input v-model="form.fieldName" placeholder="请输入油田名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="层位" prop="layer">
              <el-input v-model="form.layer" placeholder="请输入层位" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="井深(m)" prop="depth">
              <el-input-number v-model="form.depth" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="负责人" prop="operator">
              <el-input v-model="form.operator" placeholder="请输入负责人" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="投产日期" prop="drillingDate">
          <el-date-picker v-model="form.drillingDate" type="date" placeholder="选择日期" style="width: 100%" value-format="YYYY-MM-DD" />
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
.management-page {
  max-width: 1400px;
  margin: 0 auto;
}

.search-card {
  margin-bottom: 16px;
  border-radius: 8px;
}

.action-bar {
  margin-bottom: 16px;
}

.pagination-wrap {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
