<script setup>
import { ref } from 'vue';
import { userLoginService } from '@/api/user.js';
import { useUserStore } from '@/stores/index.js';
import { useRouter } from 'vue-router'

// 定义 getCookie 函数
const getCookie = (name) => {
  const match = document.cookie.match(
      new RegExp("(^| )" + name + "=([^;]+)")
  );
  console.log(match);
  if (match) {
    return decodeURIComponent(match[2]);
  } else {
    return null;
  }
};

// 初始化 user 对象
const user = ref({
  username: "",
  password: "",
  role: ""
});


const msg = ref("");
const router = useRouter()
const login = async () => {
  if (user.value.username && user.value.password && user.value.role) {
    try {
      const resp = await userLoginService(
          {
            username:user.value.username,
            password:user.value.password,
            role:user.value.role
          });

      // 检查响应是否成功
      if (resp.data.msg == 'success') {

        // 处理成功的逻辑
        const userStore = useUserStore();
        userStore.setToken(resp.data.data); // 存储 token
        msg.value = "登录成功，即将跳转主页！";
        await router.push(user.value.role == '普通用户' ? "/admin" : "/admin");
      } else {
        // 处理失败的逻辑
        msg.value = resp.data.msg || "登录失败，请稍后再试！";
      }
    } catch (error) {
      // 捕获真正的错误
      console.error("Login error:", error);
      msg.value = "登录失败，请稍后再试！";
    }
  } else {
    msg.value = "用户名或者密码不能为空！";
  }
};
</script>

<!--<template>-->
<!--  <div class="body">-->
<!--    <div id="loginDiv" style="height: 350px">-->
<!--      <form @submit.prevent="login" id="form">-->
<!--        <h1 id="loginMsg">LOGIN IN</h1>-->
<!--        <div style="display: block" id="errorMsg">{{ msg }}</div>-->
<!--        <p>-->
<!--          Username:<input-->
<!--            id="username"-->
<!--            v-model.trim="user.username"-->
<!--            name="username"-->
<!--            type="text"-->
<!--          />-->
<!--        </p>-->
<!--        <p>-->
<!--          Password:<input-->
<!--            id="password"-->
<!--            v-model.trim="user.password"-->
<!--            name="password"-->
<!--            type="password"-->
<!--          />-->
<!--        </p>-->
<!--        <p>-->
<!--          Role:<select name="role"-->
<!--            id="remember"-->
<!--            v-model="user.role"-->
<!--          >-->
<!--            <option value="普通用户">普通用户</option>-->
<!--            <option value="管理员">管理员</option>-->
<!--          </select>-->
<!--        </p>-->
<!--        <div id="subDiv">-->
<!--          <input type="submit" class="btn button" value="login up" />-->
<!--          <input type="reset" class="button" value="reset" />&nbsp;&nbsp;&nbsp;-->
<!--          <router-link to="/register">没有账号？</router-link>-->
<!--        </div>-->
<!--      </form>-->
<!--    </div>-->
<!--  </div>-->
<!--</template>-->

<!--<style scoped>-->
<!--* {-->
<!--  margin: 0;-->
<!--  padding: 0;-->
<!--}-->


<!--.body {-->
<!--  height: 100vh;-->
<!--  width: 100%;-->
<!--  overflow: hidden;-->
<!--  margin: 0;-->
<!--  padding: 0;-->
<!--  background: url("@/assets/Desert1.jpg") no-repeat 0px 0px;-->
<!--  background-repeat: no-repeat;-->
<!--  background-size: 100% 100%;-->
<!--  -moz-background-size: 100% 100%;-->
<!--  display: flex;-->
<!--  align-items: center;-->
<!--  justify-content: center;-->
<!--}-->

<!--#loginDiv {-->
<!--  width: 37%;-->
<!--  display: flex;-->
<!--  justify-content: center;-->
<!--  align-items: center;-->
<!--  height: 380px;-->
<!--  background-color: rgba(75, 81, 95, 0.3);-->
<!--  box-shadow: 7px 7px 17px rgba(52, 56, 66, 0.5);-->
<!--  border-radius: 5px;-->
<!--}-->

<!--#name_trip {-->
<!--  margin-left: 50px;-->
<!--  color: red;-->
<!--}-->

<!--p {-->
<!--  margin-top: 30px;-->
<!--  margin-left: 20px;-->
<!--  color: azure;-->
<!--}-->

<!--#remember {-->
<!--  margin-left: 15px;-->
<!--  border-radius: 5px;-->
<!--  border-style: hidden;-->
<!--  background-color: rgba(216, 191, 216, 0.5);-->
<!--  outline: none;-->
<!--  padding-left: 10px;-->
<!--  height: 30px;-->
<!--  width: 200px;-->
<!--}-->
<!--#username {-->
<!--  width: 200px;-->
<!--  margin-left: 15px;-->
<!--  border-radius: 5px;-->
<!--  border-style: hidden;-->
<!--  height: 30px;-->
<!--  background-color: rgba(216, 191, 216, 0.5);-->
<!--  outline: none;-->
<!--  color: #f0edf3;-->
<!--  padding-left: 10px;-->
<!--}-->
<!--#password {-->
<!--  width: 202px;-->
<!--  margin-left: 15px;-->
<!--  border-radius: 5px;-->
<!--  border-style: hidden;-->
<!--  height: 30px;-->
<!--  background-color: rgba(216, 191, 216, 0.5);-->
<!--  outline: none;-->
<!--  color: #f0edf3;-->
<!--  padding-left: 10px;-->
<!--}-->
<!--.button {-->
<!--  border-color: cornsilk;-->
<!--  background-color: rgba(100, 149, 237, 0.7);-->
<!--  color: aliceblue;-->
<!--  border-style: hidden;-->
<!--  border-radius: 5px;-->
<!--  width: 100px;-->
<!--  height: 31px;-->
<!--  font-size: 16px;-->
<!--}-->
<!--.button[type="reset"] {-->
<!--  background-color: rgba(100, 100, 100, 0.7);-->
<!--  margin-left: 12px;-->
<!--}-->
<!--#subDiv {-->
<!--  text-align: center;-->
<!--  margin-top: 30px;-->
<!--}-->
<!--#loginMsg {-->
<!--  text-align: center;-->
<!--  color: aliceblue;-->
<!--}-->
<!--#errorMsg {-->
<!--  text-align: center;-->
<!--  color: red;-->
<!--}-->
<!--</style>-->

<template>
  <div class="login-container">
    <div class="login-background">
      <!-- 动态背景元素 -->
      <div class="particles">
        <div class="particle" v-for="(particle, index) in particles" :key="index"
             :style="particleStyle(particle)"></div>
      </div>
      <div class="gradient-overlay"></div>
    </div>

    <div class="login-card">
      <div class="login-header">
        <div class="logo-container">
          <!-- 圆形Logo -->
          <div class="logo-circle">
            <img src="https://img.icons8.com/fluency/96/oil-industry.png"
                 alt="Logo" class="login-logo">
          </div>
        </div>
        <h1 class="login-title">采油厂油水井作业成本管理系统</h1>
        <p class="login-subtitle">Oil & Water Well Cost Management</p>
      </div>

      <div v-if="msg" class="error-message">
        <el-alert :title="msg" type="error" show-icon :closable="false" />
      </div>

      <form ref="loginForm" @submit.prevent="login" class="login-form">
        <div class="form-group">
          <label for="username">用户名</label>
          <div class="input-container">
            <el-icon class="input-icon"><User /></el-icon>
            <input
                id="username"
                v-model.trim="user.username"
                name="username"
                type="text"
                placeholder="请输入用户名"
            />
          </div>
        </div>

        <div class="form-group">
          <label for="password">密码</label>
          <div class="input-container">
            <el-icon class="input-icon"><Lock /></el-icon>
            <input
                id="password"
                v-model.trim="user.password"
                name="password"
                type="password"
                placeholder="请输入密码"
            />
          </div>
        </div>

        <div class="form-group">
          <label for="role">角色</label>
          <div class="input-container">
            <el-icon class="input-icon"><UserFilled /></el-icon>
            <select
                id="role"
                v-model="user.role"
                class="role-selector"
            >
              <option value="" disabled>请选择角色</option>
              <option value="普通用户">普通用户</option>
              <option value="管理员">管理员</option>
            </select>
          </div>
        </div>

        <div class="action-buttons">
          <button
              type="submit"
              class="login-btn"
              :disabled="loading"
          >
            <span v-if="!loading">登 录</span>
            <el-icon v-else class="is-loading"><Loading /></el-icon>
          </button>

          <button
              type="button"
              class="reset-btn"
              @click="resetForm"
          >
            重置
          </button>
        </div>
      </form>

      <div class="login-footer">
        <router-link to="/register" class="register-link">
          <el-link type="primary">没有账号？立即注册</el-link>
        </router-link>
      </div>
    </div>

    <div class="login-footer-bottom">
      <p>©2025 采油厂油水井作业成本管理系统</p>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { User, Lock, UserFilled, Loading } from '@element-plus/icons-vue';
import { userLoginService } from '@/api/user.js';
import { useUserStore } from '@/stores/index.js';
import { useRouter } from 'vue-router';

const router = useRouter();
const loginForm = ref(null); // 添加表单引用
const user = ref({
  username: "",
  password: "",
  role: ""
});

const msg = ref("");
const loading = ref(false);

// 创建粒子背景
const particles = ref([]);

// 修复重置功能
const resetForm = () => {
  user.value = {
    username: "",
    password: "",
    role: ""
  };
  msg.value = "";

  // 重置表单验证状态
  if (loginForm.value) {
    loginForm.value.reset();
  }
};

const login = async () => {
  if (user.value.username && user.value.password && user.value.role) {
    try {
      loading.value = true;
      msg.value = "";

      const resp = await userLoginService({
        username: user.value.username,
        password: user.value.password,
        role: user.value.role
      });

      if (resp.data.msg == 'success') {
        const userStore = useUserStore();
        userStore.setToken(resp.data.data);
        msg.value = "登录成功，即将跳转主页！";

        // 延迟跳转让用户看到成功消息
        setTimeout(() => {
          router.push(user.value.role == '普通用户' ? "/" : "/admin");
        }, 1000);
      } else {
        msg.value = resp.data.msg || "登录失败，请稍后再试！";
      }
    } catch (error) {
      console.error("Login error:", error);
      msg.value = "登录失败，请稍后再试！";
    } finally {
      loading.value = false;
    }
  } else {
    msg.value = "用户名、密码和角色不能为空！";
  }
};

// 粒子背景初始化
const initParticles = () => {
  const count = 50;
  for (let i = 0; i < count; i++) {
    particles.value.push({
      x: Math.random() * 100,
      y: Math.random() * 100,
      size: Math.random() * 5 + 2,
      speed: Math.random() * 3 + 1,
      opacity: Math.random() * 0.5 + 0.2,
      delay: Math.random() * 10
    });
  }
};

const particleStyle = (particle) => {
  return {
    left: `${particle.x}%`,
    top: `${particle.y}%`,
    width: `${particle.size}px`,
    height: `${particle.size}px`,
    opacity: particle.opacity,
    animation: `float ${15/particle.speed}s infinite ${particle.delay}s ease-in-out`
  };
};

onMounted(() => {
  initParticles();
});
</script>

<style scoped>
/* 基础样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #0f0c29, #302b63, #24243e);
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

/* 动态背景 */
.login-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: url('https://images.unsplash.com/photo-1506744038136-46273834b3fb?q=80&w=1920') center/cover;
  z-index: 1;
  filter: blur(2px) brightness(0.7);
}

.gradient-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, rgba(66, 185, 131, 0.2), rgba(52, 152, 219, 0.4));
  z-index: 2;
}

.particles {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 3;
  overflow: hidden;
}

.particle {
  position: absolute;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 50%;
  pointer-events: none;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0); }
  25% { transform: translate(10px, 10px); }
  50% { transform: translate(-10px, 15px); }
  75% { transform: translate(5px, -10px); }
}

/* 登录卡片 - 毛玻璃 */
.login-card {
  position: relative;
  width: 100%;
  max-width: 450px;
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 24px;
  padding: 25px 25px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  z-index: 10;
  overflow: hidden;
  transform: translateY(0);
  transition: transform 0.5s ease, box-shadow 0.5s ease;
}

.login-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 20px 48px rgba(0, 0, 0, 0.4);
}

.login-card::before {
  display: none;
}

/* Logo和标题 */
.login-header {
  text-align: center;
  margin-bottom: 25px;
  animation: fadeInDown 0.8s ease;
}

.logo-container {
  display: flex;
  justify-content: center;
  margin-bottom: 35px;
}

.logo-circle {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #42b983, #3498db);
  padding: 6px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.2);
  transition: all 0.5s ease;
}

.logo-circle:hover {
  transform: rotate(10deg) scale(1.05);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.3);
}

.login-logo {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid white;
}

.login-title {
  font-size: 36px;
  margin-bottom: 10px;
  font-weight: 700;
  background: linear-gradient(135deg, #42b983, #3498db);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  letter-spacing: 1px;
}

.login-subtitle {
  color: var(--glass-text-secondary);
  font-size: 20px;
  margin-bottom: 10px;
  letter-spacing: 1px;
  font-weight: 500;
}

/* 表单样式 */
.login-form {
  margin-bottom: 20px;
  animation: fadeInUp 0.8s ease;
}

.error-message {
  margin-bottom: 25px;
  animation: fadeIn 0.5s ease;
}

.form-group {
  margin-bottom: 28px;
}

.form-group label {
  display: block;
  margin-bottom: 10px;
  color: #fff;
  font-weight: 600;
  font-size: 16px;
}

.input-container {
  position: relative;
}

.input-icon {
  position: absolute;
  left: 16px;
  top: 50%;
  transform: translateY(-50%);
  color: rgba(255, 255, 255, 0.9);
  font-size: 22px;
  z-index: 2;
  filter: drop-shadow(0 0 2px rgba(0, 0, 0, 0.3));
}

input, select {
  width: 100%;
  height: 56px;
  padding: 0 20px 0 52px;
  border: 1px solid rgba(255, 255, 255, 0.25);
  border-radius: 14px;
  font-size: 17px;
  transition: all 0.3s;
  outline: none;
  background: rgba(255, 255, 255, 0.12);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  color: #fff;
  font-weight: 500;
}

select option {
  background: rgba(50, 50, 80, 0.95);
  color: #fff;
}

input:focus, select:focus {
  border-color: rgba(255, 255, 255, 0.5);
  box-shadow: 0 0 0 4px rgba(255, 255, 255, 0.12);
}

input::placeholder {
  color: rgba(255, 255, 255, 0.5);
}

.role-selector {
  appearance: none;
  background-image: url("data:image/svg+xml;charset=UTF-8,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3e%3cpolyline points='6 9 12 15 18 9'%3e%3c/polyline%3e%3c/svg%3e");
  background-repeat: no-repeat;
  background-position: right 18px center;
  background-size: 20px;
}

/* 按钮样式 */
.action-buttons {
  display: flex;
  gap: 18px;
  width: 100%;
  margin-top: 25px;
}

.login-btn {
  flex: 1;
  height: 56px;
  font-size: 20px;
  font-weight: 600;
  letter-spacing: 1px;
  border-radius: 14px;
  background: linear-gradient(135deg, #42b983, #3498db);
  color: white;
  border: none;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  position: relative;
}

.login-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: 0.5s;
}

.login-btn:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 25px rgba(66, 185, 131, 0.4);
}

.login-btn:hover::before {
  left: 100%;
}

.login-btn:active {
  transform: translateY(0);
}

.login-btn:disabled {
  background: #c0c4cc;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.reset-btn {
  flex: 1;
  height: 56px;
  font-size: 20px;
  font-weight: 600;
  letter-spacing: 1px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.12);
  color: #fff;
  border: 1px solid rgba(255, 255, 255, 0.25);
  cursor: pointer;
  transition: all 0.3s;
  backdrop-filter: blur(8px);
}

.reset-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.35);
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
}

/* 底部链接 */
.login-footer {
  display: flex;
  justify-content: center;
  padding: 0 10px;
  animation: fadeIn 1s ease;
  margin-top: 15px;
}

.register-link {
  text-decoration: none;
}

.register-link .el-link {
  font-size: 18px;
  font-weight: 500;
}

.register-link .el-link:hover {
  color: #42b983 !important;
}

.login-footer-bottom {
  position: absolute;
  bottom: 30px;
  text-align: center;
  width: 100%;
  color: rgba(255, 255, 255, 0.9);
  font-size: 16px;
  z-index: 10;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.5);
  letter-spacing: 0.5px;
  font-weight: 500;
}

/* 加载动画 */
.is-loading {
  animation: rotating 1s linear infinite;
  font-size: 24px;
}

@keyframes rotating {
  from {
    transform: rotate(0);
  }
  to {
    transform: rotate(360deg);
  }
}

/* 动画效果 */
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(25px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translateY(-25px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式调整 */
@media (max-width: 768px) {
  .login-card {
    padding: 35px 25px;
    margin: 0 20px;
    border-radius: 20px;
    max-width: 90%;
  }

  .login-title {
    font-size: 30px;
  }

  .logo-circle {
    width: 100px;
    height: 100px;
  }

  .action-buttons {
    flex-direction: column;
    gap: 15px;
  }

  .login-btn, .reset-btn {
    width: 100%;
    height: 52px;
    font-size: 18px;
  }

  input, select {
    height: 52px;
    font-size: 16px;
  }
}

@media (max-width: 480px) {
  .login-card {
    padding: 30px 20px;
    border-radius: 18px;
  }

  .login-title {
    font-size: 26px;
  }

  .login-subtitle {
    font-size: 16px;
  }

  .logo-circle {
    width: 85px;
    height: 85px;
  }

  .login-btn, .reset-btn {
    height: 50px;
    font-size: 17px;
  }

  .login-footer-bottom {
    font-size: 14px;
    bottom: 20px;
  }
}
</style>