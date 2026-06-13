<script setup>
import { ref, computed, onMounted } from 'vue'
import { getCostList, addCost, updateCost, deleteCost } from '@/api/cost'
import { getOperationList } from '@/api/operation'
import { getCostCategoryList } from '@/api/costCategory'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Edit, Delete } from '@element-plus/icons-vue'

const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const operationOptions = ref([])
const costCategoryOptions = ref([])

const searchForm = ref({
  operationId: '',
  categoryId: ''
})

const form = ref({
  id: null,
  operationId: null,
  categoryId: null,
  itemName: '',
  quantity: 1,
  unitPrice: 0,
  amount: 0,
  costDate: '',
  payee: ''
})

const formRules = {
  operationId: [{ required: true, message: '请选择所属作业', trigger: 'change' }],
  categoryId: [{ required: true, message: '请选择成本类别', trigger: 'change' }],
  itemName: [{ required: true, message: '请输入费用项目', trigger: 'blur' }],
  quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }],
  unitPrice: [{ required: true, message: '请输入单价', trigger: 'blur' }],
  costDate: [{ required: true, message: '请选择日期', trigger: 'change' }]
}

const pagination = ref({
  page: 1,
  pageSize: 10
})

// 金额自动计算
const computedAmount = computed(() => {
  return (form.value.quantity || 0) * (form.value.unitPrice || 0)
})

// 当数量或单价变化时更新金额
const updateAmount = () => {
  form.value.amount = computedAmount.value
}

const loadOptions = async () => {
  try {
    const [opRes, catRes] = await Promise.all([
      getOperationList({ page: 1, pageSize: 9999 }),
      getCostCategoryList({ page: 1, pageSize: 9999 })
    ])
    if (opRes.data.code === '1') {
      operationOptions.value = opRes.data.data.rows || opRes.data.data.list || []
    }
    if (catRes.data.code === '1') {
      costCategoryOptions.value = catRes.data.data.rows || catRes.data.data.list || []
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

    const res = await getCostList(params)
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
  searchForm.value = { operationId: '', categoryId: '' }
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  form.value = {
    id: null, operationId: null, categoryId: null, itemName: '',
    quantity: 1, unitPrice: 0, amount: 0, costDate: '', payee: ''
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  form.value = {
    ...row,
    operationId: row.operationId || row.operation?.id,
    categoryId: row.categoryId || row.category?.id
  }
  dialogVisible.value = true
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确认删除该成本记录吗？', '提示', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
    })
    const res = await deleteCost(id)
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
  form.value.amount = computedAmount.value

  let res
  if (isEdit.value) {
    res = await updateCost(form.value)
  } else {
    res = await addCost(form.value)
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
    <!-- 搜索栏 -->
    <el-card shadow="never" class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="所属作业">
          <el-select v-model="searchForm.operationId" placeholder="全部" clearable style="width: 180px" filterable>
            <el-option v-for="op in operationOptions" :key="op.id" :label="op.operationName" :value="op.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="成本类别">
          <el-select v-model="searchForm.categoryId" placeholder="全部" clearable style="width: 150px">
            <el-option v-for="c in costCategoryOptions" :key="c.id" :label="c.categoryName" :value="c.id" />
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
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增成本</el-button>
    </div>

    <!-- 表格 -->
    <el-card shadow="never">
      <el-table :data="tableData" v-loading="loading" stripe border style="width: 100%">
        <el-table-column prop="operationName" label="所属作业" min-width="150" />
        <el-table-column prop="categoryName" label="成本类别" width="120" />
        <el-table-column prop="itemName" label="费用项目" width="130" />
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="unitPrice" label="单价" width="100">
          <template #default="{ row }">¥{{ row.unitPrice?.toLocaleString() }}</template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="120">
          <template #default="{ row }"><span style="font-weight: 600; color: #e6a23c;">¥{{ row.amount?.toLocaleString() }}</span></template>
        </el-table-column>
        <el-table-column prop="costDate" label="日期" width="110" />
        <el-table-column prop="payee" label="收款方" width="130" />
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

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑成本' : '新增成本'"
      width="650px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="form" label-width="100px" :rules="formRules">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属作业" prop="operationId">
              <el-select v-model="form.operationId" placeholder="请选择" style="width: 100%" filterable>
                <el-option v-for="op in operationOptions" :key="op.id" :label="op.operationName" :value="op.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="成本类别" prop="categoryId">
              <el-select v-model="form.categoryId" placeholder="请选择" style="width: 100%">
                <el-option v-for="c in costCategoryOptions" :key="c.id" :label="c.categoryName" :value="c.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="费用项目" prop="itemName">
          <el-input v-model="form.itemName" placeholder="请输入费用项目名称" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="数量" prop="quantity">
              <el-input-number v-model="form.quantity" :min="0" :precision="2" style="width: 100%" @change="updateAmount" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="单价" prop="unitPrice">
              <el-input-number v-model="form.unitPrice" :min="0" :precision="2" style="width: 100%" @change="updateAmount" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="金额">
              <el-input :model-value="computedAmount" disabled style="width: 100%">
                <template #prefix>¥</template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="日期" prop="costDate">
              <el-date-picker v-model="form.costDate" type="date" placeholder="选择日期" style="width: 100%" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="收款方" prop="payee">
              <el-input v-model="form.payee" placeholder="请输入收款方名称" />
            </el-form-item>
          </el-col>
        </el-row>
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
