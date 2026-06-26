<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores'
import { uploadFileService, updateUsernameService, updateUserInfoService } from '@/api/user'
import { addDynamicService, listDynamicService, deleteDynamicService } from '@/api/dynamic'
import { Edit, Delete } from '@element-plus/icons-vue'

const userStore = useUserStore()
const user = computed(() => userStore.user)

const editUsernameDialog = ref(false)
const newUsername = ref('')
const uploading = ref({ avatar: false, background: false })
const savingUsername = ref(false)

// 动态相关
const dynamics = ref([])
const newDynamicContent = ref('')
const submitting = ref(false)

const loadDynamics = async () => {
  try {
    const res = await listDynamicService({ userId: user.value.id })
    if (res.data.code === '1') {
      dynamics.value = res.data.data || []
    }
  } catch (e) {
    // 静默处理
  }
}

const submitDynamic = async () => {
  const content = newDynamicContent.value.trim()
  if (!content) {
    ElMessage.warning('请输入动态内容')
    return
  }
  submitting.value = true
  try {
    const res = await addDynamicService({
      userId: user.value.id,
      content
    })
    if (res.data.code === '1') {
      ElMessage.success('发布成功')
      newDynamicContent.value = ''
      await loadDynamics()
    } else {
      ElMessage.error(res.data.msg || '发布失败')
    }
  } catch (e) {
    ElMessage.error('发布失败')
  } finally {
    submitting.value = false
  }
}

const deleteDynamic = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除该动态？', '提示')
    const res = await deleteDynamicService(id)
    if (res.data.code === '1') {
      ElMessage.success('删除成功')
      await loadDynamics()
    }
  } catch (e) {
    // 取消或失败都静默处理
  }
}

const formatTime = (t) => {
  if (!t) return ''
  const d = new Date(t)
  const now = new Date()
  const diff = now - d
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  const month = (d.getMonth() + 1).toString().padStart(2, '0')
  const day = d.getDate().toString().padStart(2, '0')
  return month + '-' + day
}

// 个人简介编辑
const editingBio = ref(false)
const bioText = ref('')

const startEditBio = () => {
  bioText.value = user.value?.bio || ''
  editingBio.value = true
}

const saveBio = async () => {
  const val = bioText.value.trim()
  try {
    const res = await updateUserInfoService({
      username: user.value.username,
      bio: val
    })
    if (res.data.code === '1') {
      user.value.bio = val
      ElMessage.success('简介更新成功')
      editingBio.value = false
    } else {
      ElMessage.error(res.data.msg || '更新失败')
    }
  } catch (e) {
    ElMessage.error('更新失败')
  }
}

const cancelEditBio = () => {
  editingBio.value = false
}

onMounted(() => {
  if (user.value?.id) loadDynamics()
})

const avatarPreview = computed(() => {
  return user.value?.avatar || ''
})

const backgroundPreview = computed(() => {
  return user.value?.background || ''
})

const handleAvatarFile = async (e) => {
  const file = e.target.files?.[0]
  if (!file) return
  try {
    uploading.value.avatar = true
    const formData = new FormData()
    formData.append('file', file)
    formData.append('type', 'avatar')
    formData.append('username', user.value.username)
    const res = await uploadFileService(formData)
    if (res.data.code === '1') {
      user.value.avatar = res.data.data
      ElMessage.success('头像上传成功')
    } else {
      ElMessage.error(res.data.msg || '上传失败')
    }
  } catch (e) {
    ElMessage.error('头像上传失败')
  } finally {
    uploading.value.avatar = false
    e.target.value = ''
  }
}

const handleBgFile = async (e) => {
  const file = e.target.files?.[0]
  if (!file) return
  try {
    uploading.value.background = true
    const formData = new FormData()
    formData.append('file', file)
    formData.append('type', 'background')
    formData.append('username', user.value.username)
    const res = await uploadFileService(formData)
    if (res.data.code === '1') {
      user.value.background = res.data.data
      ElMessage.success('背景图上传成功')
    } else {
      ElMessage.error(res.data.msg || '上传失败')
    }
  } catch (e) {
    ElMessage.error('背景图上传失败')
  } finally {
    uploading.value.background = false
    e.target.value = ''
  }
}

const openEditUsername = () => {
  newUsername.value = user.value.username || ''
  editUsernameDialog.value = true
}

const saveUsername = async () => {
  if (!newUsername.value || newUsername.value.trim() === '' || savingUsername.value) return
  savingUsername.value = true
  try {
    const res = await updateUsernameService({
      oldUsername: user.value.username,
      newUsername: newUsername.value.trim()
    })
    if (res.data.code === '1') {
      user.value.username = newUsername.value.trim()
      ElMessage.success('用户名更新成功')
      editUsernameDialog.value = false
    } else {
      ElMessage.error(res.data.msg || '更新失败')
    }
  } catch (e) {
    ElMessage.error('用户名更新失败')
  } finally {
    savingUsername.value = false
  }
}
</script>

<template>
  <div class="profile-page">
    <!-- 背景横幅 -->
    <div class="profile-banner" :style="{ backgroundImage: backgroundPreview ? `url(${backgroundPreview})` : 'none' }">
      <div class="banner-gradient" />
      <input
        type="file"
        accept="image/*"
        class="banner-input-overlay"
        @change="handleBgFile"
      >
      <div class="banner-overlay">
        <div class="banner-avatar">
          <img v-if="avatarPreview" :src="avatarPreview" class="avatar-img" />
          <div v-else class="avatar-placeholder">
            {{ user?.username?.charAt(0)?.toUpperCase() || '?' }}
          </div>
          <div class="avatar-edit-overlay">
            <el-icon :size="20"><Edit /></el-icon>
          </div>
          <div v-if="uploading.avatar" class="avatar-loading" />
          <input
            type="file"
            accept="image/*"
            class="avatar-input-overlay"
            @change="handleAvatarFile"
          >
        </div>
        <div class="banner-info">
          <h1 class="banner-username">{{ user?.username || '-' }}</h1>
          <div class="banner-meta">
            <el-tag :type="user?.role === 'admin' ? 'danger' : 'primary'" size="small" effect="dark">
              {{ user?.role === 'admin' ? '管理员' : user?.role === 'user' ? '普通用户' : user?.role || '-' }}
            </el-tag>
            <el-button text size="small" class="banner-edit-btn" @click="openEditUsername">
              修改用户名
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 双栏内容区 -->
    <div class="profile-body">
      <!-- 左侧 -->
      <div class="profile-sidebar">
        <el-card shadow="never" class="info-card">
          <template #header><span class="card-title">个人信息</span></template>
          <div class="info-list">
            <div class="info-item">
              <span class="info-label">性别</span>
              <span class="info-value">{{ user?.gender || '未设置' }}</span>
            </div>
            <el-divider />
            <div class="info-item">
              <span class="info-label">手机号</span>
              <span class="info-value">{{ user?.phone || '未绑定' }}</span>
            </div>
            <el-divider />
            <div class="info-item">
              <span class="info-label">角色</span>
              <span class="info-value">
                <el-tag :type="user?.role === 'admin' ? 'danger' : 'primary'" size="small">
                  {{ user?.role === 'admin' ? '管理员' : user?.role === 'user' ? '普通用户' : user?.role || '-' }}
                </el-tag>
              </span>
            </div>
          </div>
        </el-card>

        <el-card shadow="never" class="info-card">
          <template #header><span class="card-title">统计</span></template>
          <div class="stats-grid">
            <div class="stat-block">
              <span class="stat-num">--</span>
              <span class="stat-lbl">今日新增</span>
            </div>
            <div class="stat-block">
              <span class="stat-num">--</span>
              <span class="stat-lbl">本月累计</span>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 右侧 -->
      <div class="profile-main">
        <el-card shadow="never" class="main-card">
          <template #header><span class="card-title">个人简介</span></template>
          <div class="bio-area" v-if="!editingBio" @click="startEditBio">
            <p class="bio-text" v-if="user?.bio">{{ user.bio }}</p>
            <p class="bio-text bio-placeholder" v-else>点击添加个人简介...</p>
          </div>
          <div v-else class="bio-edit-area">
            <el-input
              v-model="bioText"
              type="textarea"
              :rows="3"
              maxlength="200"
              show-word-limit
              placeholder="介绍一下自己..."
              autofocus
            />
            <div class="bio-edit-actions">
              <el-button size="small" @click="cancelEditBio">取消</el-button>
              <el-button size="small" type="primary" @click="saveBio">保存</el-button>
            </div>
          </div>
        </el-card>

        <el-card shadow="never" class="main-card dynamic-card">
          <template #header><span class="card-title">最近动态</span></template>
          <!-- 发布框 -->
          <div class="dynamic-input-area">
            <el-input
              v-model="newDynamicContent"
              type="textarea"
              :rows="3"
              placeholder="分享你的想法..."
              maxlength="500"
              show-word-limit
            />
            <div class="dynamic-input-footer">
              <span class="dynamic-input-tip">记录工作动态</span>
              <el-button type="primary" size="small" :loading="submitting" @click="submitDynamic">
                发布
              </el-button>
            </div>
          </div>
          <el-divider />
          <!-- 动态列表 -->
          <div v-if="dynamics.length" class="dynamic-list">
            <div v-for="item in dynamics" :key="item.id" class="dynamic-item">
              <div class="dynamic-item-header">
                <span class="dynamic-item-user">{{ user?.username }}</span>
                <span class="dynamic-item-time">{{ formatTime(item.createdAt) }}</span>
                <el-button
                  text
                  size="small"
                  class="dynamic-delete-btn"
                  :icon="Delete"
                  @click="deleteDynamic(item.id)"
                />
              </div>
              <p class="dynamic-item-content">{{ item.content }}</p>
            </div>
          </div>
          <el-empty v-else description="暂无动态" :image-size="60" />
        </el-card>
      </div>
    </div>

    <!-- 修改用户名对话框 -->
    <el-dialog v-model="editUsernameDialog" title="修改用户名" width="420px" :close-on-click-modal="false">
      <el-form label-position="top">
        <el-form-item label="新用户名">
          <el-input v-model="newUsername" placeholder="请输入新用户名" maxlength="20" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editUsernameDialog = false">取消</el-button>
        <el-button type="primary" :loading="savingUsername" @click="saveUsername">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.profile-page {
  max-width: 1400px;
  margin: 0 auto;
}

/* ===== 背景横幅 ===== */
.profile-banner {
  height: 260px;
  border-radius: 16px 16px 0 0;
  background-size: cover;
  background-position: center;
  position: relative;
  overflow: hidden;
  background-color: #667eea;
  background-image: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.banner-gradient {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, transparent 50%, rgba(0, 0, 0, 0.4) 100%);
  pointer-events: none;
}

.banner-input-overlay {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  opacity: 0;
  cursor: pointer;
  z-index: 2;
}

.banner-overlay {
  position: absolute;
  bottom: 20px;
  left: 24px;
  display: flex;
  align-items: flex-end;
  gap: 16px;
  z-index: 3;
  pointer-events: none;
}

.banner-avatar {
  position: relative;
  flex-shrink: 0;
  pointer-events: auto;
}

.banner-avatar .avatar-img {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #fff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.25);
  background: #fff;
  display: block;
}

.banner-avatar .avatar-placeholder {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  font-weight: 600;
  color: #667eea;
  border: 3px solid #fff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.25);
  background: #fff;
}

.banner-avatar .avatar-edit-overlay {
  position: absolute;
  inset: 3px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  opacity: 0;
  transition: opacity 0.25s;
}

.banner-avatar:hover .avatar-edit-overlay {
  opacity: 1;
}

.banner-avatar .avatar-loading {
  position: absolute;
  inset: 3px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.3);
}

.banner-avatar .avatar-input-overlay {
  position: absolute;
  inset: 3px;
  width: calc(100% - 6px);
  height: calc(100% - 6px);
  border-radius: 50%;
  opacity: 0;
  cursor: pointer;
}

.banner-info {
  padding-bottom: 4px;
  pointer-events: none;
}

.banner-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  pointer-events: auto;
}

.banner-username {
  font-size: 26px;
  font-weight: 700;
  color: #fff;
  margin: 0 0 4px;
  text-shadow: 0 2px 12px rgba(0, 0, 0, 0.5);
}

.banner-edit-btn {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.8) !important;
}

/* ===== 双栏布局 ===== */
.profile-body {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 20px;
  padding: 0 0 24px;
  margin-top: 20px;
}

/* ===== 左侧栏 ===== */
.profile-sidebar {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.info-card, .main-card {
  border-radius: 12px;
  background: #fff;
  border: 1px solid #f0f0f0;
}

.info-card :deep(.el-card__header) {
  border-bottom: 1px solid #f5f5f5;
  padding: 14px 20px;
}

.main-card :deep(.el-card__header) {
  border-bottom: 1px solid #f5f5f5;
  padding: 14px 20px;
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
}

/* 个人信息 */
.info-list {
  padding: 0;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 4px 0;
}

.info-label {
  font-size: 14px;
  color: #999;
}

.info-value {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

/* 统计 */
.stats-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 4px;
  text-align: center;
}

.stat-block {
  padding: 12px 0;
}

.stat-num {
  display: block;
  font-size: 24px;
  font-weight: 700;
  color: #409eff;
}

.stat-lbl {
  display: block;
  font-size: 12px;
  color: #999;
  margin-top: 2px;
}

/* ===== 右侧主区 ===== */
.profile-main {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.bio-text {
  color: #666;
  font-size: 14px;
  margin: 0;
  line-height: 1.6;
}

.bio-placeholder {
  color: #bbb;
}

.bio-area {
  cursor: pointer;
  transition: background 0.2s;
  border-radius: 8px;
  padding: 2px 0;
}

.bio-area:hover {
  background: #f9f9f9;
}

.bio-edit-area {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.bio-edit-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

/* ===== 动态 ===== */
.dynamic-card :deep(.el-card__body) {
  padding: 0;
}

.dynamic-input-area {
  padding: 16px 20px 0;
}

.dynamic-input-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}

.dynamic-input-tip {
  font-size: 12px;
  color: #999;
}

.dynamic-list {
  padding: 0 20px 16px;
}

.dynamic-item {
  padding: 14px 0;
  border-bottom: 1px solid #f5f5f5;
}

.dynamic-item:last-child {
  border-bottom: none;
}

.dynamic-item-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

.dynamic-item-user {
  font-size: 13px;
  font-weight: 600;
  color: #333;
}

.dynamic-item-time {
  font-size: 12px;
  color: #999;
  flex: 1;
}

.dynamic-delete-btn {
  opacity: 0;
  transition: opacity 0.2s;
}

.dynamic-item:hover .dynamic-delete-btn {
  opacity: 1;
}

.dynamic-item-content {
  font-size: 14px;
  color: #444;
  line-height: 1.6;
  margin: 0;
  white-space: pre-wrap;
}

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  .profile-body {
    grid-template-columns: 1fr;
  }

  .banner-overlay {
    left: 16px;
    bottom: 16px;
    gap: 12px;
  }

  .banner-username {
    font-size: 20px;
  }
}
</style>
