import axios from 'axios'
import { useUserStore } from '@/stores'
import { ElMessage } from 'element-plus'
import router from '@/router'
const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

const instance = axios.create({
  baseURL,
  timeout: 200000
})

// 请求拦截器
instance.interceptors.request.use(
  (config) => {
    // TODO 2. 携带token
    const useStore = useUserStore()
    if (useStore.token) {
      config.headers.token = useStore.token
    }
    return config
  },
  (err) => Promise.reject(err)
)

// 响应拦截器
instance.interceptors.response.use(
  (res) => {
    if (res.data.code == '1') {
      return res
    }
    if (res.data.msg === 'NOT_LOGIN') {
      const useStore = useUserStore()
      useStore.removeToken()
      router.push('/login')
      return Promise.reject(res.data)
    }
    ElMessage.error(res.data.msg || '服务异常')
    return Promise.reject(res.data)
  },
  (err) => {
    // TODO 5. 处理401错误
    // 错误的特殊情况 => 401 权限不足 或 token 过期 => 拦截到登录
    if (err.response.data?.msg === '对不起,操作失败,请联系管理员') {
      router.push('/login')
    }

    // 错误的默认情况 => 只要给提示
    ElMessage.error(err.response.data.msg || '服务异常')
    return Promise.reject(err)
  }
)

export default instance
export { baseURL }
