<script setup>
import { echartsSelectAll } from "@/api/echarts.js";
import { fetchDashboardStats } from '@/api/dashboard';
import { useUserStore } from "@/stores";
import { useRouter } from "vue-router";
import { onMounted, ref, nextTick, watch, onUnmounted } from "vue";
import * as echarts from "echarts";
import { ElMessage } from "element-plus";
import { User, Tickets, DataBoard, TrendCharts } from '@element-plus/icons-vue';

const userStore = useUserStore();
const router = useRouter();
const chartRef = ref(null);
const barChartRef = ref(null);
const echartsList = ref([]);
const chartInstance = ref(null);
const barChartInstance = ref(null);
const welcomeName = ref("");
const cacheStatus = ref(null);

// 仪表盘统计
const stats = ref({
  online: 0,
  totalActivities: 0,
  totalUsers: 0,
  cacheHitRate: 0
});

let statsTimer = null;

const loadStats = async () => {
  try {
    const res = await fetchDashboardStats();
    if (res.data.code === '1') {
      stats.value = res.data.data;
    }
  } catch (e) { /* ignore */ }
};

// 图表配置项 - 面积图
const getAreaChartOption = () => {
  return {
    title: {
      text: "志愿活动趋势",
      left: "center",
      textStyle: { color: "#333", fontSize: 16 }
    },
    tooltip: {
      trigger: "axis",
      axisPointer: { type: "cross", label: { backgroundColor: "#6a7985" } },
      textStyle: { color: "#fff" }
    },
    legend: {
      data: echartsList.value.map(item => item.uname),
      bottom: 10,
      textStyle: { color: "#666" }
    },
    grid: { left: "3%", right: "4%", bottom: "20%", containLabel: true },
    xAxis: [{
      type: "category",
      boundaryGap: false,
      data: ["周一", "周二", "周三", "周四", "周五", "周六", "周日"],
      axisLine: { lineStyle: { color: "#999" } },
      axisLabel: { textStyle: { color: "#666" } }
    }],
    yAxis: [{
      type: "value",
      axisLine: { lineStyle: { color: "#999" } },
      axisLabel: { textStyle: { color: "#666" } },
      splitLine: { lineStyle: { color: "#eee" } }
    }],
    series: echartsList.value.map((item, idx) => ({
      name: item.uname,
      type: "line",
      stack: "Total",
      areaStyle: { opacity: 0.2 },
      lineStyle: { width: 2 },
      symbol: "circle",
      symbolSize: 6,
      data: JSON.parse(item.uvalue),
      color: ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#9B59B6', '#1ABC9C'][idx]
    }))
  };
};

// 图表配置项 - 柱状图
const getBarChartOption = () => {
  const names = echartsList.value.map(item => item.uname);
  const activityCounts = echartsList.value.map(item => item.activity_count || 0);
  const volunteerCounts = echartsList.value.map(item => item.volunteer_count || 0);
  const participationCounts = echartsList.value.map(item => item.participation_count || 0);

  return {
    title: {
      text: "活动数据统计",
      left: "center",
      textStyle: { color: "#333", fontSize: 16 }
    },
    tooltip: {
      trigger: "axis",
      axisPointer: { type: "shadow" }
    },
    legend: {
      data: ["活动数量", "志愿者数量", "参与人次"],
      bottom: 10,
      textStyle: { color: "#666" }
    },
    grid: { left: "3%", right: "4%", bottom: "20%", containLabel: true },
    xAxis: [{
      type: "category",
      data: names,
      axisLabel: {
        textStyle: { color: "#666" },
        interval: 0,
        rotate: 15,
        fontSize: 10
      }
    }],
    yAxis: [{
      type: "value",
      axisLabel: { textStyle: { color: "#666" } },
      splitLine: { lineStyle: { color: "#eee" } }
    }],
    series: [
      {
        name: "活动数量",
        type: "bar",
        data: activityCounts,
        itemStyle: { color: '#409EFF', borderRadius: [4, 4, 0, 0] },
        barWidth: '20%'
      },
      {
        name: "志愿者数量",
        type: "bar",
        data: volunteerCounts,
        itemStyle: { color: '#67C23A', borderRadius: [4, 4, 0, 0] },
        barWidth: '20%'
      },
      {
        name: "参与人次",
        type: "bar",
        data: participationCounts,
        itemStyle: { color: '#E6A23C', borderRadius: [4, 4, 0, 0] },
        barWidth: '20%'
      }
    ]
  };
};

// 获取ECharts数据
const fetchEchartsData = async () => {
  try {
    const res = await echartsSelectAll();
    if (res.data.msg === "success") {
      echartsList.value = res.data.data;
      cacheStatus.value = res.headers?.['x-cache-status'] || null;
    } else {
      ElMessage.error(res.data.msg || "数据获取失败");
    }
  } catch (error) {
    console.error("获取数据失败", error);
    ElMessage.error("网络错误，数据获取失败");
  }
};

// 更新图表
const updateCharts = () => {
  if (echartsList.value.length > 0) {
    if (chartInstance.value) {
      chartInstance.value.setOption(getAreaChartOption(), true);
    }
    if (barChartInstance.value) {
      barChartInstance.value.setOption(getBarChartOption(), true);
    }
  }
};

onMounted(() => {
  loadStats();
  statsTimer = setInterval(loadStats, 15000);

  nextTick(() => {
    if (chartRef.value) {
      chartInstance.value = echarts.init(chartRef.value);
    }
    if (barChartRef.value) {
      barChartInstance.value = echarts.init(barChartRef.value);
    }

    watch(() => echartsList.value, updateCharts, { deep: true });

    window.addEventListener('resize', () => {
      chartInstance.value?.resize();
      barChartInstance.value?.resize();
    });

    fetchEchartsData();
  });
});

onUnmounted(() => {
  if (statsTimer) clearInterval(statsTimer);
  chartInstance.value?.dispose();
  barChartInstance.value?.dispose();
});
</script>

<template>
  <page-container title2="数据大屏">
    <el-main class="main1">
      <!-- 统计卡片 -->
      <div class="stats-grid">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-card-inner">
            <div class="stat-icon icon-online">
              <el-icon :size="28"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">在线用户</div>
              <div class="stat-value pulse">{{ stats.online }}</div>
            </div>
          </div>
        </el-card>
        <el-card class="stat-card" shadow="hover">
          <div class="stat-card-inner">
            <div class="stat-icon icon-activity">
              <el-icon :size="28"><Tickets /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">总活动数</div>
              <div class="stat-value">{{ stats.totalActivities }}</div>
            </div>
          </div>
        </el-card>
        <el-card class="stat-card" shadow="hover">
          <div class="stat-card-inner">
            <div class="stat-icon icon-users">
              <el-icon :size="28"><DataBoard /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">总用户数</div>
              <div class="stat-value">{{ stats.totalUsers }}</div>
            </div>
          </div>
        </el-card>
        <el-card class="stat-card" shadow="hover">
          <div class="stat-card-inner">
            <div class="stat-icon icon-cache">
              <el-icon :size="28"><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">缓存命中率</div>
              <div class="stat-value">{{ stats.cacheHitRate }}%</div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 缓存状态 + 图表区域 -->
      <div class="chart-header">
        <el-tag v-if="cacheStatus" :type="cacheStatus === 'HIT' ? 'success' : 'warning'" size="small" effect="dark">
          缓存: {{ cacheStatus }}
        </el-tag>
      </div>

      <div class="charts-row">
        <div class="chart-container main-chart" ref="chartRef"></div>
        <div class="chart-container bar-chart" ref="barChartRef"></div>
      </div>
    </el-main>
  </page-container>
</template>

<style lang="scss" scoped>
.main1 {
  width: 100%;
  padding: 20px 0;
}

/* 统计卡片 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 14px;
  margin-bottom: 20px;
}

@media (min-width: 768px) {
  .stats-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

.stat-card {
  border-radius: 10px;
  border: none;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12) !important;
}

.stat-card-inner {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 4px 0;
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.icon-online {
  background: linear-gradient(135deg, #a8edea, #fed6e3);
  color: #409EFF;
}
.icon-activity {
  background: linear-gradient(135deg, #f6d365, #fda085);
  color: #E6A23C;
}
.icon-users {
  background: linear-gradient(135deg, #a18cd1, #fbc2eb);
  color: #9B59B6;
}
.icon-cache {
  background: linear-gradient(135deg, #d4fc79, #96e6a1);
  color: #67C23A;
}

.stat-info .stat-label {
  font-size: 12px;
  color: #909399;
}
.stat-info .stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #303133;
}

.pulse {
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% { opacity: 1; }
  50% { opacity: 0.6; }
  100% { opacity: 1; }
}

/* 图表头部 */
.chart-header {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 8px;
}

/* 图表行 */
.charts-row {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

@media (min-width: 1024px) {
  .charts-row {
    flex-direction: row;
  }
  .main-chart { flex: 1.5; }
  .bar-chart { flex: 1; }
}

.chart-container {
  height: 400px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  background-color: #fff;
  overflow: hidden;
  padding: 10px;
}

@media (min-width: 768px) {
  .chart-container {
    height: 450px;
  }
}
</style>
