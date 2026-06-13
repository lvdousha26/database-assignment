import './assets/main.css'
import './assets/glass.css'

import { createApp } from 'vue'
import pinia from '@/stores'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

const app = createApp(App)
app.use(ElementPlus)
app.use(pinia)
app.use(router)

app.mount('#app')
