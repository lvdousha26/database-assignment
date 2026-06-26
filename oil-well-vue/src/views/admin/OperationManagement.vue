<script setup>
import { ref, onMounted } from 'vue'
import { getOperationList, addOperation, updateOperation, deleteOperation } from '@/api/operation'
import { getWellList } from '@/api/well'
import { getOperationTypeList } from '@/api/operationType'
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
const saving = ref(false)
const deleting = ref(false)

const wellOptions = ref([])
const operationTypeOptions = ref([])

const searchForm = ref({
  wellName: '',
  operationTypeId: '',
  status: ''
})

const form = ref({
  id: null,
  operationName: '',
  wellId: null,
  operationTypeId: null,
  startDate: '',
  endDate: '',
  teamName: '',
  teamLeader: '',
  status: '计划'
})

const statusOptions = ['计划', '进行中', '已完成', '暂停']

const formRules = {
  operationName: [{ required: true, message: '请输入作业名称', trigger: 'blur' }],
  wellId: [{ required: true, message: '请选择关联井', trigger: 'change' }],
  operationTypeId: [{ required: true, message: '请选择作业类型', trigger: 'change' }],
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }]
}

const pagination = ref({
  page: 1,
  pageSize: 10
})

const loadOptions = async () => {
  try {
    const [wellRes, typeRes] = await Promise.all([
      getWellList({ page: 1, pageSize: 9999 }),
      getOperationTypeList({ page: 1, pageSize: 9999 })
    ])
    if (wellRes.data.code === '1') {
      wellOptions.value = wellRes.data.data.rows || wellRes.data.data.list || []
    }
    if (typeRes.data.code === '1') {
      operationTypeOptions.value = typeRes.data.data.rows || typeRes.data.data.list || typeRes.data.data || []
    }
  } catch (e) {
    // 静默处理
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.value.page,
      pageSize: pagination.value.pageSize,
      ...searchForm.value
    }
    Object.keys(params).forEach(k => { if (!params[k]) delete params[k] })

    const res = await getOperationList(params)
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
  searchForm.value = { wellName: '', operationTypeId: '', status: '' }
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  form.value = {
    id: null, operationName: '', wellId: null, operationTypeId: null,
    startDate: '', endDate: '', team: '', teamLeader: '', status: '计划'
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  form.value = { ...row, wellId: row.wellId || row.well?.id, operationTypeId: row.operationTypeId || row.operationType?.id }
  dialogVisible.value = true
}

const handleDelete = async (id) => {
  if (deleting.value) return
  try {
    await ElMessageBox.confirm('确认删除该作业吗？', '提示', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
    })
    deleting.value = true
    const res = await deleteOperation(id)
    if (res.data.code === '1') {
      ElMessage.success('删除成功')
      loadData()
    }
  } catch (e) {
    // cancel or error
  } finally {
    deleting.value = false
  }
}

const handleSave = async () => {
  if (saving.value) return
  if (!formRef.value) return
  await formRef.value.validate()
  saving.value = true

  let res
  if (isEdit.value) {
    res = await updateOperation(form.value)
  } else {
    res = await addOperation(form.value)
  }

  if (res.data.code === '1') {
    ElMessage.success(isEdit.value ? '更新成功' : '新增成功')
    dialogVisible.value = false
    loadData()
  }
  saving.value = false
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
  fetchPermissions()
  loadOptions()
  loadData()
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
        <el-form-item label="作业类型">
          <el-select v-model="searchForm.operationTypeId" placeholder="全部" clearable style="width: 150px">
            <el-option v-for="t in operationTypeOptions" :key="t.id" :label="t.typeName" :value="t.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 120px">
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
      <el-button type="primary" :icon="Plus" @click="handleAdd" v-if="canCreate">新增作业</el-button>
    </div>

    <!-- 表格 -->
    <el-card shadow="never">
      <el-table :data="tableData" v-loading="loading" stripe border style="width: 100%">
        <el-table-column prop="operationName" label="作业名称" min-width="150" />
        <el-table-column prop="wellName" label="关联井号" width="130" />
        <el-table-column prop="operationTypeName" label="作业类型" width="110" />
        <el-table-column prop="startDate" label="开始日期" width="110" />
        <el-table-column prop="endDate" label="结束日期" width="110" />
        <el-table-column prop="team" label="队伍" width="110" />
        <el-table-column prop="teamLeader" label="队长" width="100" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === '已完成' ? 'success' : row.status === '进行中' ? 'warning' : row.status === '计划' ? 'info' : 'danger'">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" :icon="Edit" @click="handleEdit(row)" v-if="canUpdate">编辑</el-button>
            <el-button size="small" type="danger" :icon="Delete" @click="handleDelete(row.id)" v-if="canDelete">删除</el-button>
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

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑作业' : '新增作业'"
      width="650px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="form" label-width="100px" :rules="formRules">
        <el-form-item label="作业名称" prop="operationName">
          <el-input v-model="form.operationName" placeholder="请输入作业名称" maxlength="200" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="关联井" prop="wellId">
              <el-select v-model="form.wellId" placeholder="请选择关联井" style="width: 100%" filterable>
                <el-option v-for="w in wellOptions" :key="w.id" :label="w.wellName" :value="w.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="作业类型" prop="operationTypeId">
              <el-select v-model="form.operationTypeId" placeholder="请选择类型" style="width: 100%">
                <el-option v-for="t in operationTypeOptions" :key="t.id" :label="t.typeName" :value="t.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始日期" prop="startDate">
              <el-date-picker v-model="form.startDate" type="date" placeholder="选择日期" style="width: 100%" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束日期" prop="endDate">
              <el-date-picker v-model="form.endDate" type="date" placeholder="选择日期" style="width: 100%" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="队伍" prop="team">
              <el-input v-model="form.teamName" placeholder="请输入队伍名称" maxlength="100" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="队长" prop="teamLeader">
              <el-input v-model="form.teamLeader" placeholder="请输入队长姓名" maxlength="50" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" style="width: 200px">
            <el-option v-for="s in statusOptions" :key="s" :label="s" :value="s" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
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
