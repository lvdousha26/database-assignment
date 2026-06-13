<script setup>
import AdminHome from './admin/AdminHome.vue'
</script>

<template>
  <div class="standalone-dashboard">
    <div class="dashboard-header">
      <h1 class="dashboard-title">采油厂油水井作业成本管理系统 - 数据大屏</h1>
    </div>
    <div class="dashboard-content">
      <AdminHome />
    </div>
  </div>
</template>

<style scoped>
.standalone-dashboard {
  min-height: 100vh;
  background: #f5f7fa;
  display: flex;
  flex-direction: column;
}

.dashboard-header {
  background: #fff;
  border-bottom: 1px solid rgba(0, 0, 0, 0.08);
  padding: 16px 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
  flex-shrink: 0;
}

.dashboard-title {
  font-size: 20px;
  font-weight: 600;
  color: rgba(0, 0, 0, 0.88);
  letter-spacing: 1px;
  margin: 0;
}

.dashboard-content {
  flex: 1;
  padding: 24px;
  overflow: auto;
}

/* 所有 el-card 毛玻璃 - 白色主题 */
.dashboard-content:deep(.el-card) {
  background: var(--glass-bg-card) !important;
  backdrop-filter: var(--glass-blur) !important;
  border: var(--glass-border) !important;
  border-radius: var(--glass-radius) !important;
  box-shadow: var(--glass-shadow) !important;
  color: var(--glass-text) !important;
  transition: all 0.3s ease;
}

.dashboard-content:deep(.el-card:hover) {
  background: var(--glass-bg-card-hover) !important;
  box-shadow: var(--glass-shadow-elevated) !important;
}

.dashboard-content:deep(.el-card__header) {
  border-bottom: var(--glass-border-light) !important;
  color: var(--glass-text) !important;
}

.dashboard-content:deep(.el-card__body) {
  color: var(--glass-text) !important;
}

/* 表格毛玻璃 */
.dashboard-content:deep(.el-table) {
  background: transparent !important;
  color: var(--glass-text) !important;
}

.dashboard-content:deep(.el-table th.el-table__cell) {
  background: var(--glass-bg-medium) !important;
  color: var(--glass-text) !important;
  border-bottom: var(--glass-border-light) !important;
}

.dashboard-content:deep(.el-table td.el-table__cell) {
  background: transparent !important;
  color: var(--glass-text) !important;
  border-bottom: var(--glass-border-light) !important;
}

.dashboard-content:deep(.el-table--striped .el-table__body tr.el-table__row--striped td.el-table__cell) {
  background: var(--glass-bg-light) !important;
}

.dashboard-content:deep(.el-table--enable-row-hover .el-table__body tr:hover > td) {
  background: var(--glass-bg-medium) !important;
}
</style>
