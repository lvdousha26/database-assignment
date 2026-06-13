<script setup>
import { ref, onMounted } from 'vue';
import { useUserStore } from '@/stores';
import { useRouter } from 'vue-router';
import { SwitchButton, Avatar, SuccessFilled, Stamp, Menu, Delete, Edit, ChatLineRound } from '@element-plus/icons-vue';
import { ElMessageBox, ElMessage } from 'element-plus';
import PageContainerView from "@/components/PageContainerView.vue";

const userStore = useUserStore();
const router = useRouter();

// 导航项数据
const userNavItems = ref([
  {
    id: "1",
    uname: "功能中心",
    path: "/func",
    content: "上传文字或图片查找行人",
    icon: Menu
  },
  {
    id: "2",
    uname: "消息通知",
    path: "/message",
    content: "获取行人查找，权限申请等进度",
    icon: SuccessFilled
  },
  {
    id: "3",
    uname: "个人中心",
    path: "/center",
    content: "修改个人资料，保障信息安全",
    icon: Avatar
  },
  {
    id: "4",
    uname: "权限申请",
    path: "/authority",
    content: "申请权限，获取全部信息",
    icon: Edit
  },
  {
    id: "5",
    uname: "智能问答",
    path: "/chatai",
    content: "获取行人重识别系统相关问题解答",
    icon: ChatLineRound
  },
  {
    id: "6",
    uname: "历史记录",
    path: "/history",
    content: "查询历史申请记录",
    icon: Stamp
  }
]);

// 导航项点击处理
const handleNavItemClick = (path) => {
  router.push(path);
}

// 页面加载完成后执行
onMounted(() => {
  // 这里可以添加页面初始化逻辑
});
</script>

<template>
  <page-container-view title3="首页导航">
    <el-main class="main-container">
      <!-- 导航卡片网格 -->
      <div class="nav-grid">
        <el-card
          v-for="item in userNavItems"
          :key="item.id"
          class="nav-card"
          @click="handleNavItemClick(item.path)"
        >
          <template #header>
            <div class="card-header">
              <h3>{{ item.uname }}</h3>
              <component :is="item.icon" class="card-icon"></component>
            </div>
          </template>
          <div class="card-content">
            <p>{{ item.content }}</p>
          </div>
        </el-card>
      </div>
    </el-main>
  </page-container-view>
</template>

<style scoped>
/* 基础样式变量 */
:root {
  --primary-color: #454545;
  --text-color: #303133;
  --text-secondary-color: #606266;
  --bg-color: #f5f7fa;
  --card-bg: #ffffff;
  --card-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  --card-hover-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.15);
  --transition-duration: 0.3s;
}

/* 主容器样式 */
.main-container {
  background-color: var(--bg-color);
  /* min-height: calc(100vh - 100px);
  padding: 16px; */
}

@media (min-width: 768px) {
  .main-container {
    padding: 24px;
  }
}

/* 导航网格样式 */
.nav-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 16px;
}

@media (min-width: 640px) {
  .nav-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (min-width: 768px) {
  .nav-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (min-width: 1024px) {
  .nav-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

/* 导航卡片样式 */
.nav-card {
  background-color: var(--card-bg);
  border-radius: 8px;
  box-shadow: var(--card-shadow);
  cursor: pointer;
  transition: all .3s ease;
}

.nav-card:hover {
  box-shadow: var(--card-hover-shadow);
  transform: translateY(-2px);
}

/* 卡片头部样式 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 16px 0;
}

.card-header h3 {
  font-weight: 600;
  color: var(--text-color);
  margin: 0;
}

.card-icon {
  color: #8c8c8c;
  font-size: 16px;
  width: 130px;
  height: 130px;
}

/* 卡片内容样式 */
.card-content {
  padding: 16px;
}

.card-content p {
  color: var(--text-secondary-color);
  font-size: 14px;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

@media (min-width: 768px) {
  .card-content p {
    font-size: 16px;
  }
}
</style>