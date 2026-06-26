<script setup>
import BrandHead from "@/components/BrandHead.vue";
import UserAside from "@/components/UserAside.vue";
</script>

<template>
  <div class="user-layout">
    <BrandHead />
    <el-container class="main-container">
      <UserAside />
      <el-main class="content-area">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<style scoped>
.user-layout {
  display: flex;
  flex-direction: column;
  height: 100vh;
  position: relative;
  z-index: 1;
}

.main-container {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.content-area {
  flex: 1;
  overflow: auto;
  padding: 88px 20px 20px 248px;
  background: transparent;
  min-height: 100vh;
}

/* 所有 el-card 毛玻璃 */
.content-area:deep(.el-card) {
  background: var(--glass-bg-card) !important;
  backdrop-filter: var(--glass-blur) !important;
  -webkit-backdrop-filter: var(--glass-blur) !important;
  border: var(--glass-border) !important;
  border-radius: var(--glass-radius) !important;
  box-shadow: var(--glass-shadow) !important;
  color: var(--glass-text) !important;
  transition: all 0.3s ease;
}

.content-area:deep(.el-card:hover) {
  background: var(--glass-bg-card-hover) !important;
  box-shadow: var(--glass-shadow-elevated) !important;
  transform: translateY(-2px);
}

.content-area:deep(.el-card__header) {
  border-bottom: var(--glass-border-light) !important;
  color: var(--glass-text) !important;
}

.content-area:deep(.el-card__body) {
  color: var(--glass-text) !important;
}

/* 表格毛玻璃 */
.content-area:deep(.el-table) {
  background: transparent !important;
  color: var(--glass-text) !important;
}

.content-area:deep(.el-table th.el-table__cell) {
  background: var(--glass-bg-medium) !important;
  color: var(--glass-text) !important;
  border-bottom: var(--glass-border-light) !important;
}

.content-area:deep(.el-table td.el-table__cell) {
  background: transparent !important;
  color: var(--glass-text) !important;
  border-bottom: var(--glass-border-light) !important;
}

.content-area:deep(.el-table--striped .el-table__body tr.el-table__row--striped td.el-table__cell) {
  background: var(--glass-bg-light) !important;
}

.content-area:deep(.el-table--enable-row-hover .el-table__body tr:hover > td) {
  background: var(--glass-bg-medium) !important;
}

.content-area:deep(.el-table__body tr.el-table__row--striped) {
  background: transparent !important;
}

/* 分页毛玻璃 */
.content-area:deep(.el-pagination) {
  color: var(--glass-text) !important;
}

.content-area:deep(.el-pagination button) {
  background: var(--glass-bg) !important;
  color: var(--glass-text) !important;
  border-radius: 6px;
}

.content-area:deep(.el-pager li) {
  background: var(--glass-bg-light) !important;
  color: var(--glass-text-secondary) !important;
  border-radius: 6px;
  margin: 0 2px;
}

.content-area:deep(.el-pager li.is-active) {
  background: var(--glass-bg-heavy) !important;
  color: #409eff !important;
}

/* 输入框毛玻璃 */
.content-area:deep(.el-input__wrapper) {
  background: var(--glass-bg-light) !important;
  border: var(--glass-border-light) !important;
  box-shadow: none !important;
}

.content-area:deep(.el-input__inner) {
  color: var(--glass-text) !important;
}

.content-area:deep(.el-input__inner::placeholder) {
  color: var(--glass-text-secondary) !important;
}

.content-area:deep(.el-textarea__inner) {
  background: var(--glass-bg-light) !important;
  border: var(--glass-border-light) !important;
  color: var(--glass-text) !important;
  box-shadow: none !important;
}

/* 对话框 */
.content-area:deep(.el-dialog) {
  background: var(--glass-bg-card) !important;
  backdrop-filter: var(--glass-blur-strong) !important;
  border: var(--glass-border) !important;
  border-radius: var(--glass-radius-lg) !important;
  box-shadow: var(--glass-shadow-elevated) !important;
}

.content-area:deep(.el-dialog__title) {
  color: var(--glass-text) !important;
}

.content-area:deep(.el-dialog__body) {
  color: var(--glass-text) !important;
}

/* 按钮 */
.content-area:deep(.el-button--default) {
  background: var(--glass-bg) !important;
  border: var(--glass-border) !important;
  color: var(--glass-text) !important;
}

.content-area:deep(.el-button--default:hover) {
  background: var(--glass-bg-medium) !important;
}
</style>
