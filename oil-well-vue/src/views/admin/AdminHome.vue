<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getDashboardStats } from '@/api/dashboard'

const stats = ref({
  totalWells: 0,
  productionWells: 0,
  monthlyOperations: 0,
  monthlyCost: 0
})

const latestOperations = ref([])

let pieChart = null
let lineChart = null

const loadStats = async () => {
  try {
    const res = await getDashboardStats()
    if (res.data.code === '1') {
      stats.value = res.data.data
      latestOperations.value = res.data.data.latestOperations || []
      nextTick(() => {
        initCharts(res.data.data)
      })
    }
  } catch (e) {
    // 静默处理
  }
}

const initCharts = (data) => {
  // 井类型分布饼图
  const pieDom = document.getElementById('pieChart')
  if (pieDom) {
    if (pieChart) pieChart.dispose()
    pieChart = echarts.init(pieDom)
    pieChart.setOption({
      title: { text: '井类型分布', left: 'center' },
      tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
      series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['50%', '55%'],
        data: data.wellTypeDistribution || [
          { name: '油井', value: 0 },
          { name: '水井', value: 0 }
        ],
        label: { show: true, formatter: '{b}\n{d}%' },
        emphasis: {
          label: { show: true, fontSize: 16, fontWeight: 'bold' }
        }
      }]
    })
  }

  // 成本趋势折线图
  const lineDom = document.getElementById('lineChart')
  if (lineDom) {
    if (lineChart) lineChart.dispose()
    lineChart = echarts.init(lineDom)
    lineChart.setOption({
      title: { text: '近12个月成本趋势', left: 'center' },
      tooltip: { trigger: 'axis' },
      xAxis: {
        type: 'category',
        data: data.monthLabels || []
      },
      yAxis: {
        type: 'value',
        name: '金额（元）'
      },
      series: [{
        type: 'line',
        smooth: true,
        data: data.monthlyCostTrend || [],
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.4)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
          ])
        },
        lineStyle: { color: '#409eff', width: 3 },
        itemStyle: { color: '#409eff' }
      }]
    })
  }
}

onMounted(() => {
  loadStats()
})

onUnmounted(() => {
  if (pieChart) pieChart.dispose()
  if (lineChart) lineChart.dispose()
})
</script>

<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-inner">
            <div class="stat-icon icon-total">
              <el-icon :size="32"><Platform /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalWells }}</div>
              <div class="stat-label">总井数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-inner">
            <div class="stat-icon icon-prod">
              <el-icon :size="32"><DataBoard /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.productionWells }}</div>
              <div class="stat-label">生产井数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-inner">
            <div class="stat-icon icon-op">
              <el-icon :size="32"><Tools /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.monthlyOperations }}</div>
              <div class="stat-label">本月作业数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-inner">
            <div class="stat-icon icon-cost">
              <el-icon :size="32"><Money /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.monthlyCost ? '¥' + stats.monthlyCost.toLocaleString() : '¥0' }}</div>
              <div class="stat-label">本月总成本</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :md="10">
        <el-card shadow="hover">
          <div id="pieChart" style="height: 360px"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="14">
        <el-card shadow="hover">
          <div id="lineChart" style="height: 360px"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最新作业列表 -->
    <el-card shadow="hover" class="op-list-card">
      <template #header>
        <span style="font-weight: 600; font-size: 16px;">最新作业</span>
      </template>
      <el-table :data="latestOperations" stripe style="width: 100%">
        <el-table-column prop="operationName" label="作业名称" min-width="160" />
        <el-table-column prop="wellName" label="关联井号" width="140" />
        <el-table-column prop="operationTypeName" label="作业类型" width="120" />
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '已完成' ? 'success' : row.status === '进行中' ? 'warning' : 'info'">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { Platform, DataBoard, Tools, Money } from '@element-plus/icons-vue'
export default { components: { Platform, DataBoard, Tools, Money } }
</script>

<style scoped>
.dashboard {
  max-width: 1400px;
  margin: 0 auto;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 10px;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12) !important;
}

.stat-inner {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 8px 0;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.icon-total { background: linear-gradient(135deg, #a8edea, #fed6e3); color: #409eff; }
.icon-prod { background: linear-gradient(135deg, #d4fc79, #96e6a1); color: #67c23a; }
.icon-op { background: linear-gradient(135deg, #a18cd1, #fbc2eb); color: #9b59b6; }
.icon-cost { background: linear-gradient(135deg, #ffecd2, #fcb69f); color: #e6a23c; }

.stat-info { flex: 1; }

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}

.chart-row {
  margin-bottom: 20px;
}

.op-list-card {
  border-radius: 10px;
}
</style>
