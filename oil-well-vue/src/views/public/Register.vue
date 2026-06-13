<script setup>
import { useRouter } from 'vue-router'
import {ref} from 'vue'
import { userRegisterService } from '@/api/user.js'
const message = ref("")
const user = ref({
  username: "",
  password: "",
  role: ""
})
const router = useRouter()
const register = async () => {
  if (
      user.value.username != "" &&
      user.value.password != "" &&
      user.value.role != ""
  ) {
    const resp = await userRegisterService(
        {username: user.value.username,
          password: user.value.password,
          role: user.value.role})
    if (resp.data.msg == "error_msg") {
      message.value = "参数不能为空";
    } else if (resp.data.msg == "success") {
      message.value = "注册成功，即将跳转登录页";
      router.push("/login");
    } else if (resp.data.msg == "error") {
      message.value = "注册失败！用户名已存在！";
    } else if( resp.data.msg == "管理员账号已满") {
      message.value = resp.data.msg;
    }
  }
}
const verify = async () => {
  if (user.value.username != "") {
    message.value = "用户名可用";
  } else {
    message.value = "用户名不能为空！";
  }
}
</script>

<!--<template>-->
<!--  <div class="con">-->
<!--    <div class="form-div">-->
<!--      <div class="reg-content">-->
<!--        <h1>欢迎注册</h1>-->
<!--        <span>已有帐号？</span> <router-link to="/login">登录</router-link>-->
<!--      </div>-->
<!--      <form id="reg-form" @submit.prevent="register">-->
<!--        <div class="cdv">-->
<!--          <div>-->
<!--            <p>用户名</p>-->
<!--            <p class="inputs">-->
<!--              <input-->
<!--                @blur="verify"-->
<!--                name="username"-->
<!--                v-model.trim="user.username"-->
<!--                type="text"-->
<!--                id="username" />-->
<!--              <span class="err_msg" style="display: block">-->
<!--                {{message}}</span>-->
<!--            </p>-->
<!--          </div>-->

<!--          <div>-->
<!--            <p>密码</p>-->
<!--            <p class="inputs">-->
<!--              <input-->
<!--                name="password"-->
<!--                v-model.trim="user.password"-->
<!--                type="password"-->
<!--                id="password" />-->
<!--            </p>-->
<!--          </div>-->
<!--          <div>-->
<!--            <p>角色</p>-->
<!--            <p class="inputs">-->
<!--              <select name="role" v-model="user.role" id="role">-->
<!--                <option value="志愿者">志愿者</option>-->
<!--                <option value="管理员">管理员</option>-->
<!--              </select>-->
<!--            </p>-->
<!--          </div>-->
<!--          -->
<!--        </div>-->

<!--        <div class="buttons">-->
<!--          <input value="注 册" type="submit" id="reg_btn" />-->
<!--        </div>-->
<!--        <br class="clear" />-->
<!--      </form>-->
<!--    </div>-->
<!--  </div>-->
<!--</template>-->


<!--<style scoped>-->
<!--* {-->
<!--  margin: 0;-->
<!--  padding: 0;-->
<!--  list-style-type: none;-->
<!--}-->
<!--.reg-content {-->
<!--  padding: 30px;-->
<!--  margin: 3px;-->
<!--}-->
<!--a,-->
<!--img {-->
<!--  border: 0;-->
<!--}-->

<!--.con {-->
<!--  background-image: url("@/assets/reg_bg_min.jpg");-->
<!--  text-align: center;-->
<!--  width: 100%;-->
<!--  height: 100vh;-->
<!--  overflow: hidden;-->
<!--}-->

<!--.cdv div {-->
<!--  display: flex;-->
<!--  justify-content: space-between;-->
<!--}-->

<!--.cdv p {-->
<!--  padding: 0;-->
<!--  height: 90px;-->
<!--  -->
<!--}-->
<!--.cdv p:nth-of-type(1) {-->
<!--  width: 100px;-->
<!--  text-indent: 2em;-->
<!--  line-height: 60px;-->
<!--}-->
<!--.inputs {-->
<!--  vertical-align: top;-->
<!--}-->

<!--.clear {-->
<!--  clear: both;-->
<!--}-->

<!--.clear:before,-->
<!--.clear:after {-->
<!--  content: "";-->
<!--  display: block;-->
<!--}-->

<!--.clear:after {-->
<!--  clear: both;-->
<!--}-->

<!--.form-div {-->
<!--  background-color: rgba(255, 255, 255, 0.27);-->
<!--  border-radius: 10px;-->
<!--  border: 1px solid #aaa;-->
<!--  width: 424px;-->
<!--  margin-top: 150px;-->
<!--  margin-left: 1050px;-->
<!--  padding: 30px 0 20px 0px;-->
<!--  font-size: 16px;-->
<!--  box-shadow: inset 0px 0px 10px rgba(255, 255, 255, 0.5),-->
<!--    0px 0px 15px rgba(75, 75, 75, 0.3);-->
<!--  text-align: left;-->
<!--}-->

<!--.form-div input[type="text"],-->
<!--.form-div input[type="password"],-->
<!--.form-div input[type="email"] ,-->
<!--#role {-->
<!--  width: 268px;-->
<!--  margin: 10px;-->
<!--  line-height: 20px;-->
<!--  font-size: 16px;-->
<!--}-->

<!--.form-div input[type="checkbox"] {-->
<!--  margin: 20px 0 20px 10px;-->
<!--}-->

<!--.form-div input[type="button"],-->
<!--.form-div input[type="submit"] {-->
<!--  margin: 10px 20px 0 0;-->
<!--}-->

<!--.form-div .cdv {-->
<!--  margin: 0 auto;-->
<!--  color: rgba(64, 64, 64, 1);-->
<!--}-->

<!--.form-div .cdv img {-->
<!--  vertical-align: middle;-->
<!--  margin: 0 0 5px 0;-->
<!--}-->

<!--.footer {-->
<!--  color: rgba(64, 64, 64, 1);-->
<!--  font-size: 12px;-->
<!--  margin-top: 30px;-->
<!--}-->

<!--.form-div .buttons {-->
<!--  float: right;-->
<!--}-->

<!--input[type="text"],-->
<!--input[type="password"],-->
<!--input[type="email"] ,-->
<!--#role {-->
<!--  border-radius: 8px;-->
<!--  box-shadow: inset 0 2px 5px #eee;-->
<!--  padding: 10px;-->
<!--  border: 1px solid #d4d4d4;-->
<!--  color: #333333;-->
<!--  margin-top: 5px;-->
<!--  outline: 0;-->
<!--}-->

<!--input[type="text"]:focus,-->
<!--input[type="password"]:focus,-->
<!--input[type="email"]:focus {-->
<!--  border: 1px solid #50afeb;-->
<!--  outline: none;-->
<!--}-->

<!--input[type="button"],-->
<!--input[type="submit"] {-->
<!--  padding: 7px 15px;-->
<!--  background-color: #3c6db0;-->
<!--  text-align: center;-->
<!--  border-radius: 5px;-->
<!--  overflow: hidden;-->
<!--  min-width: 80px;-->
<!--  border: none;-->
<!--  color: #fff;-->
<!--  box-shadow: 1px 1px 1px rgba(75, 75, 75, 0.3);-->
<!--}-->

<!--input[type="button"]:hover,-->
<!--input[type="submit"]:hover {-->
<!--  background-color: #5a88c8;-->
<!--}-->

<!--input[type="button"]:active,-->
<!--input[type="submit"]:active {-->
<!--  background-color: #5a88c8;-->
<!--}-->
<!--.err_msg {-->
<!--  color: red;-->
<!--  height: 17px;-->
<!--}-->
<!--#password_err,-->
<!--#tel_err {-->
<!--  padding-right: 195px;-->
<!--}-->

<!--#reg_btn {-->
<!--  margin-right: 50px;-->
<!--  width: 285px;-->
<!--  height: 45px;-->
<!--  margin-top: 20px;-->
<!--}-->

<!--#checkCode {-->
<!--  width: 100px;-->
<!--}-->

<!--#changeImg {-->
<!--  color: aqua;-->
<!--}-->
<!--</style>-->

<template>
  <div class="register-container">
    <div class="register-background">
      <!-- 动态背景元素 -->
      <div class="particles">
        <div class="particle" v-for="(particle, index) in particles" :key="index"
             :style="particleStyle(particle)"></div>
      </div>
      <div class="gradient-overlay"></div>
    </div>

    <div class="register-card">
      <div class="register-header">
        <div class="logo-container">
          <!-- 圆形Logo -->
          <div class="logo-circle">
            <img src="https://img.icons8.com/fluency/96/oil-industry.png"
                 alt="Logo" class="register-logo">
          </div>
        </div>
        <h1 class="register-title">注册新账号</h1>
        <p class="register-subtitle">采油厂油水井作业成本管理系统</p>
      </div>

      <div v-if="message" class="message" :class="{'success-message': message.includes('成功') || message.includes('可用'), 'error-message': !message.includes('成功') && !message.includes('可用')}">
        <el-alert :title="message" :type="message.includes('成功') || message.includes('可用') ? 'success' : 'error'" show-icon :closable="false" />
      </div>

      <form id="reg-form" @submit.prevent="register" class="register-form">
        <div class="form-group">
          <label for="username">用户名</label>
          <div class="input-container">
            <el-icon class="input-icon"><User /></el-icon>
            <input
                @blur="verify"
                name="username"
                v-model.trim="user.username"
                type="text"
                id="username"
                placeholder="请输入用户名"
            />
          </div>
        </div>

        <div class="form-group">
          <label for="password">密码</label>
          <div class="input-container">
            <el-icon class="input-icon"><Lock /></el-icon>
            <input
                name="password"
                v-model.trim="user.password"
                type="password"
                id="password"
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
              <option value="志愿者">志愿者</option>
              <option value="管理员">管理员</option>
            </select>
          </div>
        </div>

        <div class="action-buttons">
          <button
              type="submit"
              class="register-btn"
          >
            立即注册
          </button>

          <div class="login-link">
            已有帐号？<router-link to="/login">立即登录</router-link>
          </div>
        </div>
      </form>
    </div>

    <div class="register-footer-bottom">
      <p>©2025 采油厂油水井作业成本管理系统</p>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { User, Lock, UserFilled } from '@element-plus/icons-vue';
import { userRegisterService } from '@/api/user.js';
import { useRouter } from 'vue-router';

const router = useRouter();
const user = ref({
  username: "",
  password: "",
  role: ""
});

const message = ref("");

// 创建粒子背景
const particles = ref([]);

const register = async () => {
  if (
      user.value.username != "" &&
      user.value.password != "" &&
      user.value.role != ""
  ) {
    const resp = await userRegisterService({
      username: user.value.username,
      password: user.value.password,
      role: user.value.role
    });

    if (resp.data.msg == "error_msg") {
      message.value = "参数不能为空";
    } else if (resp.data.msg == "success") {
      message.value = "注册成功，即将跳转登录页";
      setTimeout(() => {
        router.push("/login");
      }, 1500);
    } else if (resp.data.msg == "error") {
      message.value = "注册失败！用户名已存在！";
    } else if (resp.data.msg == "管理员账号已满") {
      message.value = resp.data.msg;
    }
  } else {
    message.value = "请填写所有必填字段";
  }
};

const verify = async () => {
  if (user.value.username != "") {
    message.value = "用户名可用";
  } else {
    message.value = "用户名不能为空！";
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

.register-container {
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
.register-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
  background: url('https://images.unsplash.com/photo-1513151233558-d860c5398176?q=80&w=1920') center/cover;
  filter: blur(2px) brightness(0.6);
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
  background: rgba(255, 126, 95, 0.6);
  border-radius: 50%;
  pointer-events: none;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0); }
  25% { transform: translate(10px, 10px); }
  50% { transform: translate(-10px, 15px); }
  75% { transform: translate(5px, -10px); }
}

/* 注册卡片 */
.register-card {
  position: relative;
  width: 100%;
  max-width: 480px;
  background: var(--glass-bg-heavy);
  backdrop-filter: var(--glass-blur-strong);
  -webkit-backdrop-filter: var(--glass-blur-strong);
  border: var(--glass-border);
  border-radius: 24px;
  padding: 45px 35px;
  box-shadow: var(--glass-shadow-elevated);
  z-index: 10;
  overflow: hidden;
  transform: translateY(0);
  transition: transform 0.5s ease, box-shadow 0.5s ease;
}

.register-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 30px 60px rgba(0, 0, 0, 0.4);
}

.register-card::before {
  display: none;
}

/* Logo和标题 */
.register-header {
  text-align: center;
  margin-bottom: 30px;
  animation: fadeInDown 0.8s ease;
}

.logo-container {
  display: flex;
  justify-content: center;
  margin-bottom: 25px;
}

.logo-circle {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #FF7E5F, #FEB47B);
  padding: 5px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.2);
}

.register-logo {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid white;
}

.register-title {
  font-size: 32px;
  margin-bottom: 10px;
  font-weight: 700;
  background: linear-gradient(135deg, #FF7E5F, #FEB47B);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  letter-spacing: 1px;
}

.register-subtitle {
  color: var(--glass-text-secondary);
  font-size: 18px;
  margin-bottom: 10px;
  letter-spacing: 1px;
  font-weight: 500;
}

/* 表单样式 */
.register-form {
  margin-bottom: 20px;
  animation: fadeInUp 0.8s ease;
}

.message {
  margin-bottom: 25px;
  animation: fadeIn 0.5s ease;
}

.success-message .el-alert {
  background-color: #f0f9eb;
  color: #67c23a;
}

.error-message .el-alert {
  background-color: #fef0f0;
  color: #f56c6c;
}

.form-group {
  margin-bottom: 25px;
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
  height: 52px;
  padding: 0 20px 0 52px;
  border: var(--glass-border);
  border-radius: 14px;
  font-size: 16px;
  transition: all 0.3s;
  outline: none;
  background: var(--glass-bg);
  backdrop-filter: var(--glass-blur-light);
  -webkit-backdrop-filter: var(--glass-blur-light);
  color: #fff;
  font-weight: 500;
}

select option {
  background: #1e1c3a;
  color: #fff;
}

input:focus, select:focus {
  border-color: rgba(255, 255, 255, 0.4);
  box-shadow: 0 0 0 4px rgba(255, 255, 255, 0.08);
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
  flex-direction: column;
  gap: 18px;
  width: 100%;
  margin-top: 30px;
}

.register-btn {
  height: 56px;
  font-size: 20px;
  font-weight: 600;
  letter-spacing: 1px;
  border-radius: 14px;
  background: linear-gradient(135deg, #FF7E5F, #FEB47B);
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

.register-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: 0.5s;
}

.register-btn:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 25px rgba(66, 185, 131, 0.4);
}

.register-btn:hover::before {
  left: 100%;
}

.register-btn:active {
  transform: translateY(0);
}

.login-link {
  text-align: center;
  color: var(--glass-text-secondary);
  font-size: 16px;
}

.login-link a {
  color: #FF7E5F;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.3s;
}

.login-link a:hover {
  color: #3498db;
  text-decoration: underline;
}

/* 底部链接 */
.register-footer-bottom {
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
  .register-card {
    padding: 35px 25px;
    margin: 0 20px;
    border-radius: 20px;
    max-width: 90%;
  }

  .register-title {
    font-size: 28px;
  }

  .logo-circle {
    width: 90px;
    height: 90px;
  }

  .register-btn {
    height: 52px;
    font-size: 18px;
  }

  input, select {
    height: 50px;
    font-size: 15px;
  }
}

@media (max-width: 480px) {
  .register-card {
    padding: 30px 20px;
    border-radius: 18px;
  }

  .register-title {
    font-size: 24px;
  }

  .register-subtitle {
    font-size: 16px;
  }

  .logo-circle {
    width: 80px;
    height: 80px;
  }

  .register-btn {
    height: 50px;
    font-size: 17px;
  }

  .register-footer-bottom {
    font-size: 14px;
    bottom: 20px;
  }
}
</style>