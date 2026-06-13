<script setup>
import {
  DataAnalysis, Platform, Tools, Money, List, Collection, ChatDotSquare
} from "@element-plus/icons-vue";
import { useRouter } from "vue-router";
import { useRoute } from "vue-router";

const router = useRouter();
const route = useRoute();

const iconMap = {
  DataAnalysis, Platform, Tools, Money, List, Collection, ChatDotSquare
};

const menuItems = [
  { name: '仪表盘', path: '/dashboard', icon: 'DataAnalysis' },
  { name: '油水井管理', path: '/admin/well', icon: 'Platform' },
  { name: '作业管理', path: '/admin/operation', icon: 'Tools' },
  { name: '成本管理', path: '/admin/cost', icon: 'Money' },
  { name: '作业类型', path: '/admin/operation-type', icon: 'List' },
  { name: '成本类别', path: '/admin/cost-category', icon: 'Collection' },
  { name: 'AI 助手', path: '/admin/ai', icon: 'ChatDotSquare' },
]
</script>

<template>
  <el-aside class="aside">
    <el-menu
      :default-active="route.path"
      background-color="#1e2a3a"
      text-color="#b0bec5"
      active-text-color="#409eff"
      router
    >
      <el-menu-item
        v-for="item in menuItems"
        :key="item.path"
        :index="item.path"
      >
        <el-icon :size="20">
          <component :is="iconMap[item.icon]" />
        </el-icon>
        <span>{{ item.name }}</span>
      </el-menu-item>
    </el-menu>
  </el-aside>
</template>

<style scoped>
.aside {
  width: 220px;
  position: fixed;
  top: 80px;
  left: 12px;
  bottom: 12px;
  border-radius: var(--glass-radius);
  background: var(--glass-bg-heavy);
  backdrop-filter: var(--glass-blur-strong);
  -webkit-backdrop-filter: var(--glass-blur-strong);
  border: var(--glass-border);
  box-shadow: var(--glass-shadow);
  overflow-y: auto;
  z-index: 100;
  transition: all 0.3s ease;
}

.aside:hover {
  box-shadow: var(--glass-shadow-elevated);
}

.el-menu {
  border-right: none;
}

.aside:deep(.el-menu) {
  background: transparent !important;
  border-right: none;
}

.aside:deep(.el-menu-item) {
  color: var(--glass-text-secondary) !important;
  background: transparent !important;
  border-radius: 8px;
  margin: 2px 8px;
  width: auto;
  transition: all 0.2s ease;
}

.aside:deep(.el-menu-item:hover) {
  background: var(--glass-bg-light) !important;
  color: var(--glass-text) !important;
}

.aside:deep(.el-menu-item.is-active) {
  background: var(--glass-bg-medium) !important;
  color: #409eff !important;
}

.aside:deep(.el-menu-item i) {
  color: inherit !important;
}
</style>
