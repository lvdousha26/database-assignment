<script setup>
	import {
		ref
	} from 'vue'
	import {
		SwitchButton,
		Avatar,
		SuccessFilled,
		Stamp,
		Menu,
		Delete,
		Edit
	} from '@element-plus/icons-vue'
	import {
		useUserStore
	} from "@/stores"
	import {
		useRouter
	} from 'vue-router'
	const welcomeName = ref('')
	const userStore = useUserStore()
	const userInfo = userStore.user
	welcomeName.value = userInfo.username
	const router = useRouter()
	const removeTokenByChild = () => {
		userStore.removeToken()
		router.push("/login")
	}
</script>
<template>
  <div>
    <div class="header">
      <span>觅影-行人重查找系统</span>
	  <div>
		  <el-menu
		    active-text-color="#ffd04b"
		    background-color="#3588CC"
		    :default-active="$route.path"
		    text-color="#fff"
		    router
			class="rt"
		  >
			<el-menu-item index="/home">
          <el-icon :size="20">
            <SuccessFilled />
          </el-icon>
          <span>首页</span>
        </el-menu-item>
			  <el-menu-item index="/message">
          <el-icon :size="20">
            <Menu />
          </el-icon>
          <span>消息通知</span>
        </el-menu-item>
         <el-menu-item index="/history">
          <el-icon :size="20">
            <SuccessFilled />
          </el-icon>
          <span>历史记录</span>
        </el-menu-item>
				<el-menu-item index="/chatai">
          <el-icon :size="20">
            <Edit />
          </el-icon>
          <span>AI提问</span>
        </el-menu-item>
			  <el-menu-item index="/">
          <el-icon :size="20">
            <Avatar />
          </el-icon>
          <span>{{ welcomeName || "游客" }}</span>
			  </el-menu-item>
			  <el-menu-item>
          <el-icon :size="20">
            <SwitchButton />
          </el-icon>
          <span @click="removeTokenByChild">退出登录</span>
			  </el-menu-item>
		</el-menu>
	  </div>
    </div>
  </div>
</template>
<style lang="scss" scoped>
* {
  margin: 0;
  padding: 0;
}
.header{
  width: 100%;
  background-color: #3588CC;
  color: #fff !important;
  height: 100px !important;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 30px;
  span {
	text-indent: 3em;
  }
  .rt {
	  width: 1000px;
	  display: flex;
	  height: 100px;
	  align-items: center;
	  justify-content: space-between;
	  margin-right: 3em;
	  border-right: 1px solid #3588CC;
	  .el-menu-item {
		  width: 200px;
		  height: 50px;
		  line-height: 50px;
	  }
  }
}
</style>