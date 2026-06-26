<script setup>
import {
  DataAnalysis, Platform, Tools, Money, ChatDotSquare, User, Message, Lock, Document
} from "@element-plus/icons-vue";
import { useRouter, useRoute } from "vue-router";

const router = useRouter();
const route = useRoute();

const menuItems = [
  { name: '仪表盘', path: '/user/home', icon: 'DataAnalysis' },
  { name: '油水井管理', path: '/user/well', icon: 'Platform' },
  { name: '作业管理', path: '/user/operation', icon: 'Tools' },
  { name: '成本管理', path: '/user/cost', icon: 'Money' },
  { name: 'AI 助手', path: '/user/ai', icon: 'ChatDotSquare' },
  { name: '消息', path: '/user/message', icon: 'Message' },
  { name: '权限申请', path: '/user/authority', icon: 'Lock' },
  { name: '申请记录', path: '/user/history', icon: 'Document' },
  { name: '个人主页', path: '/user/profile', icon: 'User' },
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
          <DataAnalysis v-if="item.icon === 'DataAnalysis'" />
          <Platform v-else-if="item.icon === 'Platform'" />
          <Tools v-else-if="item.icon === 'Tools'" />
          <Money v-else-if="item.icon === 'Money'" />
          <ChatDotSquare v-else-if="item.icon === 'ChatDotSquare'" />
          <Message v-else-if="item.icon === 'Message'" />
          <User v-else-if="item.icon === 'User'" />
          <Lock v-else-if="item.icon === 'Lock'" />
          <Document v-else-if="item.icon === 'Document'" />
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
</style>
