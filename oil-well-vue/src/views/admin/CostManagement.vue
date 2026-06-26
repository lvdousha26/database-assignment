<script setup>
import { ref, onMounted } from 'vue'
import { getCostList, addCost, updateCost, deleteCost } from '@/api/cost'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Edit, Delete, Expand, Fold } from '@element-plus/icons-vue'
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

const searchForm = ref({
  wellcode: '',
  preunit: '',
  content: ''
})

const form = ref({
  code: '',
  preunit: '',
  wellcode: '',
  premoney: null,
  person: '',
  predate: '',
  startdate: '',
  finish: '',
  settleunit: '',
  content: '',
  mat1Code: '', mat1Num: null, mat1Price: null, mat1Sub: null,
  mat2Code: '', mat2Num: null, mat2Price: null, mat2Sub: null,
  mat3Code: '', mat3Num: null, mat3Price: null, mat3Sub: null,
  mat4Code: '', mat4Num: null, mat4Price: null, mat4Sub: null,
  matcost: null,
  humancost: null,
  equipcost: null,
  othercost: null,
  settlecost: null,
  settleperson: '',
  settledate: '',
  finalcost: null,
  finalperson: '',
  finaldate: ''
})

const formRules = {
  code: [{ required: true, message: '请输入费用编号', trigger: 'blur' }],
  preunit: [{ required: true, message: '请输入预算单位', trigger: 'blur' }],
  wellcode: [{ required: true, message: '请输入井号', trigger: 'blur' }],
  predate: [{ required: true, message: '请选择预算编制日期', trigger: 'change' }],
  settleunit: [{ required: true, message: '请输入施工/结算单位', trigger: 'blur' }]
}

const pagination = ref({
  page: 1,
  pageSize: 10
})

const expandedRows = ref([])

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.value.page,
      pageSize: pagination.value.pageSize,
      ...searchForm.value
    }
    if (!params.wellcode) delete params.wellcode
    if (!params.preunit) delete params.preunit
    if (!params.content) delete params.content
    const res = await getCostList(params)
    tableData.value = res.data.data.list || []
    total.value = res.data.data.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.value.page = 1
  loadData()
}

const handleReset = () => {
  searchForm.value = { wellcode: '', preunit: '', content: '' }
  pagination.value.page = 1
  loadData()
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

const handleAdd = () => {
  isEdit.value = false
  form.value = {
    code: '', preunit: '', wellcode: '', premoney: null, person: '', predate: '',
    startdate: '', finish: '', settleunit: '', content: '',
    mat1Code: '', mat1Num: null, mat1Price: null, mat1Sub: null,
    mat2Code: '', mat2Num: null, mat2Price: null, mat2Sub: null,
    mat3Code: '', mat3Num: null, mat3Price: null, mat3Sub: null,
    mat4Code: '', mat4Num: null, mat4Price: null, mat4Sub: null,
    matcost: null, humancost: null, equipcost: null, othercost: null,
    settlecost: null, settleperson: '', settledate: '',
    finalcost: null, finalperson: '', finaldate: ''
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = (code) => {
  if (deleting.value) return
  ElMessageBox.confirm('确定要删除该成本记录吗？', '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    deleting.value = true
    await deleteCost(code)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {}).finally(() => { deleting.value = false })
}

const handleSave = async () => {
  if (saving.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    if (isEdit.value) {
      await updateCost(form.value)
      ElMessage.success('更新成功')
    } else {
      await addCost(form.value)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (e) {
    // error handled by interceptor
  } finally {
    saving.value = false
  }
}

const toggleExpand = (row) => {
  const idx = expandedRows.value.indexOf(row.code)
  if (idx > -1) {
    expandedRows.value.splice(idx, 1)
  } else {
    expandedRows.value.push(row.code)
  }
}

const formatMoney = (val) => {
  if (val === null || val === undefined) return '-'
  return '¥' + Number(val).toLocaleString('zh-CN', { minimumFractionDigits: 2 })
}

onMounted(() => {
  loadData()
  fetchPermissions()
})
</script>

<template>
  <div class="cost-page">
    <el-card shadow="never">
      <template #header>
        <div class="page-header">
          <span class="page-title">成本管理</span>
          <el-button type="primary" :icon="Plus" @click="handleAdd" v-if="canCreate">新增成本</el-button>
        </div>
      </template>

      <!-- 搜索 -->
      <el-form :model="searchForm" layout="inline" class="search-form">
        <el-form-item label="井号">
          <el-input v-model="searchForm.wellcode" placeholder="井号" clearable style="width:150px" />
        </el-form-item>
        <el-form-item label="预算单位">
          <el-input v-model="searchForm.preunit" placeholder="预算单位" clearable style="width:150px" />
        </el-form-item>
        <el-form-item label="作业内容">
          <el-input v-model="searchForm.content" placeholder="作业内容" clearable style="width:150px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table
        :data="tableData"
        v-loading="loading"
        stripe
        style="width:100%"
        @expand-change="toggleExpand"
        :row-key="row => row.code"
      >
        <el-table-column type="expand" width="40">
          <template #default="{ row }">
            <el-descriptions :column="3" border size="small" class="expand-detail">
              <template #title>
                <span style="font-weight:600">详细信息</span>
              </template>
              <el-descriptions-item label="预算单位">{{ row.preunit }}</el-descriptions-item>
              <el-descriptions-item label="预算编制人">{{ row.person || '-' }}</el-descriptions-item>
              <el-descriptions-item label="预算金额">{{ formatMoney(row.premoney) }}</el-descriptions-item>
              <el-descriptions-item label="开工日期">{{ row.startdate || '-' }}</el-descriptions-item>
              <el-descriptions-item label="完工日期">{{ row.finish || '-' }}</el-descriptions-item>
              <el-descriptions-item label="施工/结算单位">{{ row.settleunit }}</el-descriptions-item>

              <el-descriptions-item label="材料1编码" :span="1">{{ row.mat1Code || '-' }}</el-descriptions-item>
              <el-descriptions-item label="材料1数量">{{ row.mat1Num ?? '-' }}</el-descriptions-item>
              <el-descriptions-item label="材料1单价">{{ row.mat1Price ? formatMoney(row.mat1Price) : '-' }}</el-descriptions-item>
              <el-descriptions-item label="材料1小计">{{ row.mat1Sub ? formatMoney(row.mat1Sub) : '-' }}</el-descriptions-item>

              <el-descriptions-item label="材料2编码">{{ row.mat2Code || '-' }}</el-descriptions-item>
              <el-descriptions-item label="材料2数量">{{ row.mat2Num ?? '-' }}</el-descriptions-item>
              <el-descriptions-item label="材料2单价">{{ row.mat2Price ? formatMoney(row.mat2Price) : '-' }}</el-descriptions-item>
              <el-descriptions-item label="材料2小计">{{ row.mat2Sub ? formatMoney(row.mat2Sub) : '-' }}</el-descriptions-item>

              <el-descriptions-item label="材料3编码">{{ row.mat3Code || '-' }}</el-descriptions-item>
              <el-descriptions-item label="材料3数量">{{ row.mat3Num ?? '-' }}</el-descriptions-item>
              <el-descriptions-item label="材料3单价">{{ row.mat3Price ? formatMoney(row.mat3Price) : '-' }}</el-descriptions-item>
              <el-descriptions-item label="材料3小计">{{ row.mat3Sub ? formatMoney(row.mat3Sub) : '-' }}</el-descriptions-item>

              <el-descriptions-item label="材料4编码">{{ row.mat4Code || '-' }}</el-descriptions-item>
              <el-descriptions-item label="材料4数量">{{ row.mat4Num ?? '-' }}</el-descriptions-item>
              <el-descriptions-item label="材料4单价">{{ row.mat4Price ? formatMoney(row.mat4Price) : '-' }}</el-descriptions-item>
              <el-descriptions-item label="材料4小计">{{ row.mat4Sub ? formatMoney(row.mat4Sub) : '-' }}</el-descriptions-item>

              <el-descriptions-item label="材料总成本">{{ formatMoney(row.matcost) }}</el-descriptions-item>
              <el-descriptions-item label="人工成本">{{ formatMoney(row.humancost) }}</el-descriptions-item>
              <el-descriptions-item label="设备成本">{{ formatMoney(row.equipcost) }}</el-descriptions-item>
              <el-descriptions-item label="其他成本">{{ formatMoney(row.othercost) }}</el-descriptions-item>

              <el-descriptions-item label="结算金额">{{ formatMoney(row.settlecost) }}</el-descriptions-item>
              <el-descriptions-item label="结算经办人">{{ row.settleperson || '-' }}</el-descriptions-item>
              <el-descriptions-item label="结算日期">{{ row.settledate || '-' }}</el-descriptions-item>

              <el-descriptions-item label="终审金额">{{ row.finalcost != null ? formatMoney(row.finalcost) : '-' }}</el-descriptions-item>
              <el-descriptions-item label="终审人">{{ row.finalperson || '-' }}</el-descriptions-item>
              <el-descriptions-item label="终审日期">{{ row.finaldate || '-' }}</el-descriptions-item>
            </el-descriptions>
          </template>
        </el-table-column>

        <el-table-column prop="code" label="费用编号" width="130" />
        <el-table-column prop="wellcode" label="井号" width="100" />
        <el-table-column prop="preunit" label="预算单位" width="100" />
        <el-table-column prop="person" label="编制人" width="80" />
        <el-table-column prop="predate" label="预算日期" width="100" />
        <el-table-column prop="content" label="作业内容" width="120" />
        <el-table-column prop="premoney" label="预算金额" width="120" align="right">
          <template #default="{ row }">{{ formatMoney(row.premoney) }}</template>
        </el-table-column>
        <el-table-column prop="settlecost" label="结算金额" width="120" align="right">
          <template #default="{ row }">{{ formatMoney(row.settlecost) }}</template>
        </el-table-column>
        <el-table-column prop="settleunit" label="结算单位" width="120" />
        <el-table-column label="操作" width="200" >
          <template #default="{ row }">
            <el-button text size="small" type="primary" :icon="Edit" @click="handleEdit(row)" v-if="canUpdate">编辑</el-button>
            <el-button text size="small" type="danger" :icon="Delete" @click="handleDelete(row.code)" v-if="canDelete">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑成本' : '新增成本'"
      width="90%"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="120px" class="cost-form">
        <el-divider content-position="left">预算信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="费用编号" prop="code">
              <el-input v-model="form.code" placeholder="费用编号" :disabled="isEdit" />
            </el-form-item>
            <el-form-item label="预算单位" prop="preunit">
              <el-input v-model="form.preunit" placeholder="采油队代码" />
            </el-form-item>
            <el-form-item label="井号" prop="wellcode">
              <el-input v-model="form.wellcode" placeholder="油水井编号" />
            </el-form-item>
            <el-form-item label="预算金额">
              <el-input-number v-model="form.premoney" :min="0" :precision="2" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="编制人">
              <el-input v-model="form.person" placeholder="预算编制人" />
            </el-form-item>
            <el-form-item label="预算日期" prop="predate">
              <el-date-picker v-model="form.predate" type="date" value-format="YYYY-MM-DD" style="width:100%" />
            </el-form-item>
            <el-form-item label="开工日期">
              <el-date-picker v-model="form.startdate" type="date" value-format="YYYY-MM-DD" style="width:100%" />
            </el-form-item>
            <el-form-item label="完工日期">
              <el-date-picker v-model="form.finish" type="date" value-format="YYYY-MM-DD" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">施工信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="结算单位" prop="settleunit">
              <el-input v-model="form.settleunit" placeholder="施工/结算单位" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="作业内容">
              <el-input v-model="form.content" placeholder="作业内容" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">材料明细</el-divider>
        <template v-for="i in 4" :key="i">
          <el-row :gutter="20" class="mat-row">
            <el-col :span="6">
              <el-form-item :label="`材料${i}编码`" :label-width="100">
                <el-input v-model="form['mat' + i + 'Code']" :placeholder="`材料${i}编码`" />
              </el-form-item>
            </el-col>
            <el-col :span="5">
              <el-form-item :label="`数量`" :label-width="50">
                <el-input-number v-model="form['mat' + i + 'Num']" :min="0" style="width:100%" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item :label="`单价`" :label-width="50">
                <el-input-number v-model="form['mat' + i + 'Price']" :min="0" :precision="2" style="width:100%" />
              </el-form-item>
            </el-col>
            <el-col :span="5">
              <el-form-item :label="`小计`" :label-width="50">
                <el-input-number v-model="form['mat' + i + 'Sub']" :min="0" :precision="2" style="width:100%" />
              </el-form-item>
            </el-col>
          </el-row>
        </template>

        <el-divider content-position="left">成本汇总</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="材料总成本">
              <el-input-number v-model="form.matcost" :min="0" :precision="2" style="width:100%" />
            </el-form-item>
            <el-form-item label="人工成本">
              <el-input-number v-model="form.humancost" :min="0" :precision="2" style="width:100%" />
            </el-form-item>
            <el-form-item label="设备成本">
              <el-input-number v-model="form.equipcost" :min="0" :precision="2" style="width:100%" />
            </el-form-item>
            <el-form-item label="其他成本">
              <el-input-number v-model="form.othercost" :min="0" :precision="2" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">结算</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="结算金额">
              <el-input-number v-model="form.settlecost" :min="0" :precision="2" style="width:100%" />
            </el-form-item>
            <el-form-item label="结算经办人">
              <el-input v-model="form.settleperson" placeholder="结算经办人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结算日期">
              <el-date-picker v-model="form.settledate" type="date" value-format="YYYY-MM-DD" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">终审</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="终审金额">
              <el-input-number v-model="form.finalcost" :min="0" :precision="2" style="width:100%" />
            </el-form-item>
            <el-form-item label="终审人">
              <el-input v-model="form.finalperson" placeholder="终审人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="终审日期">
              <el-date-picker v-model="form.finaldate" type="date" value-format="YYYY-MM-DD" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.cost-page {
  width: 100%;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-title {
  font-size: 16px;
  font-weight: 600;
}

.search-form {
  margin-bottom: 16px;
}

.pagination-wrap {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.cost-form {
  max-height: 60vh;
  overflow-y: auto;
  padding: 0 8px;
}

.mat-row {
  margin-bottom: 8px;
}

.expand-detail {
  margin: 8px 0;
}
</style>
