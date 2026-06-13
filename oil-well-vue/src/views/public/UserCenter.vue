<script setup>
import { ref } from 'vue';
import { useUserStore } from '@/stores';
import { userUpdateInfoService, userUpdateAvatarService, userUpdatePasswordService } from '@/api/user';
import { ElMessage } from 'element-plus';
import { Plus } from '@element-plus/icons-vue';

const userStore = useUserStore();

// 基本资料表单
const profileForm = ref({
  id: userStore.user.id,
  username: userStore.user.username,
  gender: userStore.user.gender || '',
  addr: userStore.user.addr || ''
});

// 密码表单
const passwordForm = ref({
  username: userStore.user.username,
  old_pwd: '',
  new_pwd: '',
  re_pwd: ''
});

// 头像URL
const avatarUrl = ref(userStore.user.avatar || '');
const uploadUrl = ref("http://localhost:8080/uploads/avatar");

// 头像上传成功处理
const onUploadSuccess = (response) => {
  avatarUrl.value = response.data;
};

// 更新基本资料
const updateProfile = async () => {
  try {
    const res = await userUpdateInfoService(profileForm.value);
    if (res.data.msg === 'success') {
      ElMessage.success('资料更新成功');
      await userStore.getUser();
    }
  } catch (error) {
    ElMessage.error('资料更新失败');
  }
};

// 更新头像
const updateAvatar = async () => {
  try {
    const res = await userUpdateAvatarService({
      id: userStore.user.id,
      avatar: avatarUrl.value
    });
    if (res.data.msg === 'success') {
      ElMessage.success('头像更新成功');
      await userStore.getUser();
    }
  } catch (error) {
    ElMessage.error('头像更新失败');
  }
};

// 更新密码
const updatePassword = async () => {
  try {
    const res = await userUpdatePasswordService(passwordForm.value);
    if (res.data.msg === 'success') {
      ElMessage.success('密码更新成功');
      // 清空表单
      passwordForm.value.old_pwd = '';
      passwordForm.value.new_pwd = '';
      passwordForm.value.re_pwd = '';
    } else {
      ElMessage.error(res.data.msg || '密码更新失败');
    }
  } catch (error) {
    ElMessage.error('密码更新失败');
  }
};

// 密码校验规则
const checkDifferent = (rule, value, callback) => {
  if (value === passwordForm.value.old_pwd) {
    callback(new Error('新密码不能与原密码一样'));
  } else {
    callback();
  }
};

const checkSameAsNewPwd = (rule, value, callback) => {
  if (value !== passwordForm.value.new_pwd) {
    callback(new Error('确认密码必须和新密码一样'));
  } else {
    callback();
  }
};

const passwordRules = ref({
  old_pwd: [
    {required: true, message: '请输入原密码', trigger: 'blur'},
    {min: 3, max: 15, message: '原密码长度在3-15位之间', trigger: 'blur'}
  ],
  new_pwd: [
    {required: true, message: '请输入新密码', trigger: 'blur'},
    {min: 3, max: 15, message: '新密码长度在3-15位之间', trigger: 'blur'},
    {validator: checkDifferent, trigger: 'blur'}
  ],
  re_pwd: [
    {required: true, message: '请再次输入新密码', trigger: 'blur'},
    {min: 3, max: 15, message: '确认密码长度在3-15位之间', trigger: 'blur'},
    {validator: checkSameAsNewPwd, trigger: 'blur'}
  ]
});
</script>

<template>
  <page-container title2="个人中心">
    <div class="user-center-container">
      <div class="profile-section">
        <div class="avatar-container">
          <el-upload
              class="avatar-uploader"
              :action="uploadUrl"
              :show-file-list="false"
              :on-success="onUploadSuccess"
          >
            <img v-if="avatarUrl" :src="avatarUrl" class="avatar"/>
            <el-icon v-else class="avatar-uploader-icon">
              <Plus/>
            </el-icon>
          </el-upload>
          <el-button type="success" @click="updateAvatar" class="avatar-btn">更新头像</el-button>
        </div>

        <div class="profile-form-container">
          <h3 class="section-title">基本资料</h3>
          <el-form :model="profileForm" label-width="80px" class="profile-form">
            <el-form-item label="用户名">
              <el-input v-model="profileForm.username" disabled></el-input>
            </el-form-item>
            <el-form-item label="性别">
              <el-input v-model="profileForm.gender"></el-input>
            </el-form-item>
            <el-form-item label="地址">
              <el-input v-model="profileForm.addr"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="updateProfile">保存资料</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>

      <div class="password-section">
        <h3 class="section-title">修改密码</h3>
        <el-form
            :model="passwordForm"
            :rules="passwordRules"
            label-width="100px"
            class="password-form"
        >
          <el-form-item label="原密码" prop="old_pwd">
            <el-input v-model="passwordForm.old_pwd" type="password" show-password></el-input>
          </el-form-item>
          <el-form-item label="新密码" prop="new_pwd">
            <el-input v-model="passwordForm.new_pwd" type="password" show-password></el-input>
          </el-form-item>
          <el-form-item label="确认密码" prop="re_pwd">
            <el-input v-model="passwordForm.re_pwd" type="password" show-password></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="updatePassword">修改密码</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </page-container>
</template>

<style lang="scss" scoped>
.user-center-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
  padding: 20px;

  @media (max-width: 992px) {
    grid-template-columns: 1fr;
  }
}

.profile-section {
  display: grid;
  grid-template-columns: 240px 1fr;
  gap: 30px;
  background: #fff;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
}

.password-section {
  background: #fff;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.section-title {
  font-size: 18px;
  color: #333;
  margin-top: 0;
  margin-bottom: 25px;
  padding-bottom: 12px;
  border-bottom: 1px solid #eee;
}

.avatar-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.avatar-uploader {
  :deep() {
    .avatar {
      width: 180px;
      height: 180px;
      display: block;
      border-radius: 50%;
      object-fit: cover;
      border: 3px solid #eee;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }

    .el-upload {
      border: 1px dashed var(--el-border-color);
      border-radius: 50%;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      width: 180px;
      height: 180px;
    }

    .el-upload:hover {
      border-color: var(--el-color-primary);
    }

    .el-icon.avatar-uploader-icon {
      font-size: 48px;
      color: #8c939d;
      width: 180px;
      height: 180px;
      text-align: center;
      line-height: 180px;
    }
  }
}

.avatar-btn {
  width: 180px;
  height: 40px;
  font-size: 16px;
}

.profile-form, .password-form {
  .el-form-item {
    margin-bottom: 22px;
  }

  .el-button {
    width: 100%;
    height: 42px;
    font-size: 16px;
  }
}
</style>