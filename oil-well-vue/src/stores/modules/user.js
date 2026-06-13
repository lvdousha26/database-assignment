import { defineStore } from 'pinia'
import { ref } from 'vue'
import { userGetInfoService } from '@/api/user'

//const router = useRouter()
export const useUserStore = defineStore('token', () => {
  const token = ref('') // 存储 token
  const user = ref({}) // 存储用户信息

  const setToken = (newToken) => {
      token.value = newToken;
      if (newToken) {
        try {
          const parsedToken = parseJWT(newToken);
          user.value.username = parsedToken.username;
          // 调用获取用户信息的方法
          getUser();
        } catch (error) {
          console.error('解析jwt失败:', error);
        }
      }
    }

  // 移除 token
  const removeToken = () => {
    token.value = ''
    user.value = {}
  }

  function parseJWT(jwt) {
    if (!jwt || typeof jwt!== 'string') {
      throw new Error('错误的 JWT');
    }
    const parts = jwt.split('.');
    if (parts.length!== 3) {
      throw new Error('错误的 JWT 格式');
    }
    const base64Url = parts[1];
    const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split("")
        .map(function (c) {
          return "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2);
        })
        .join("")
    );
    return JSON.parse(jsonPayload);
  }

  // 获取用户信息
  const getUser = async () => {
    try {
      const res = await userGetInfoService({
        'username': user.value.username
      }); // 请求获取数据
      user.value = res.data.data || {};
    } catch (error) {
      console.error('获取用户数据失败:', error);
      user.value = {}; // 如果请求失败，清空用户信息
    }
  }
  // 设置用户信息
  const setUser = (obj) => {
    user.value = obj
  }

  return {
    token,
    setToken,
    removeToken,
    user,
    setUser,
	  getUser
  }
}, {
  persist: true // 启用持久化
})