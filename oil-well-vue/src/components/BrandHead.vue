<script setup>
import { ref } from 'vue'
import { SwitchButton, HomeFilled } from '@element-plus/icons-vue'
import { useUserStore } from "@/stores"
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const router = useRouter()
const welcomeName = ref(userStore.user?.username || '用户')

const handleLogout = () => {
  userStore.removeToken()
  router.push("/login")
}
</script>

<template>
  <div class="header">
    <div class="header-left">
      <span class="header-title">采油厂油水井作业成本管理系统</span>
    </div>
    <div class="header-right">
      <el-menu
        mode="horizontal"
        :ellipsis="false"
        class="header-menu"
      >
        <el-menu-item>
          <el-icon :size="18"><HomeFilled /></el-icon>
          <span class="welcome-text">{{ welcomeName }}</span>
        </el-menu-item>
        <el-menu-item @click="handleLogout">
          <el-icon :size="18"><SwitchButton /></el-icon>
          <span>退出登录</span>
        </el-menu-item>
      </el-menu>
    </div>
  </div>
</template>

<style scoped>
.header {
  width: auto;
  height: 56px;
  position: fixed;
  top: 12px;
  left: 12px;
  right: 12px;
  background: var(--glass-bg-heavy);
  backdrop-filter: var(--glass-blur-strong);
  -webkit-backdrop-filter: var(--glass-blur-strong);
  color: var(--glass-text);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  border: var(--glass-border);
  border-radius: var(--glass-radius);
  box-shadow: var(--glass-shadow);
  flex-shrink: 0;
  z-index: 200;
  transition: all 0.3s ease;
}

.header:hover {
  box-shadow: var(--glass-shadow-elevated);
}

.header-left {
  display: flex;
  align-items: center;
}

.header-title {
  font-size: 20px;
  font-weight: 600;
  letter-spacing: 2px;
}

.header-right {
  display: flex;
  align-items: center;
}

.header-menu {
  background: transparent !important;
  border-bottom: none !important;
}

.header-menu .el-menu-item {
  color: var(--glass-text) !important;
  background: transparent !important;
  border-bottom: none !important;
  height: 60px;
  line-height: 60px;
}

.header-menu .el-menu-item:hover {
  background: var(--glass-bg-medium) !important;
}

.header-menu .el-menu-item.is-active {
  border-bottom: none !important;
}

.welcome-text {
  margin-left: 6px;
}
</style>
