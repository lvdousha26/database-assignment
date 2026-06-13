<script setup>
import { ref, nextTick } from 'vue'
import { sendChat } from '@/api/ai'
import { ElMessage } from 'element-plus'
import { Promotion } from '@element-plus/icons-vue'

const messages = ref([
  { role: 'assistant', content: '你好！我是采油厂成本管理助手，可以帮你分析油水井作业成本数据、解答相关问题。请问有什么可以帮你的？' }
])
const inputText = ref('')
const loading = ref(false)
const chatListRef = ref(null)

const scrollToBottom = async () => {
  await nextTick()
  if (chatListRef.value) {
    chatListRef.value.scrollTop = chatListRef.value.scrollHeight
  }
}

const handleSend = async () => {
  const text = inputText.value.trim()
  if (!text || loading.value) return

  messages.value.push({ role: 'user', content: text })
  inputText.value = ''
  loading.value = true
  await scrollToBottom()

  try {
    const res = await sendChat({ message: text })
    if (res.data.code === '1') {
      messages.value.push({ role: 'assistant', content: res.data.data })
    } else {
      messages.value.push({ role: 'assistant', content: '抱歉，AI 服务返回了错误：' + (res.data.msg || '未知错误') })
    }
  } catch (e) {
    messages.value.push({ role: 'assistant', content: '抱歉，AI 服务暂时不可用，请稍后再试。' })
  } finally {
    loading.value = false
    await scrollToBottom()
  }
}

const handleKeydown = (e) => {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    handleSend()
  }
}
</script>

<template>
  <div class="ai-chat-page">
    <el-card shadow="never" class="chat-card">
      <template #header>
        <div class="chat-header">
          <span class="chat-title">AI 智能助手</span>
          <el-tag type="info" size="small">采油厂成本管理</el-tag>
        </div>
      </template>

      <div ref="chatListRef" class="chat-list">
        <div
          v-for="(msg, idx) in messages"
          :key="idx"
          class="message-row"
          :class="msg.role === 'user' ? 'user-row' : 'assistant-row'"
        >
          <div class="avatar" :class="msg.role">
            {{ msg.role === 'user' ? 'U' : 'AI' }}
          </div>
          <div class="message-bubble" :class="msg.role">
            <div class="message-content">{{ msg.content }}</div>
          </div>
        </div>

        <div v-if="loading" class="message-row assistant-row">
          <div class="avatar assistant">AI</div>
          <div class="message-bubble assistant">
            <div class="typing-indicator">
              <span></span><span></span><span></span>
            </div>
          </div>
        </div>
      </div>

      <div class="input-area">
        <el-input
          v-model="inputText"
          type="textarea"
          :rows="2"
          placeholder="输入您的问题，例如：最近三个月的作业成本趋势如何？"
          :disabled="loading"
          @keydown="handleKeydown"
        />
        <el-button
          type="primary"
          :icon="Promotion"
          :loading="loading"
          class="send-btn"
          @click="handleSend"
        >
          发送
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.ai-chat-page {
  max-width: 900px;
  margin: 0 auto;
  height: calc(100vh - 120px);
  display: flex;
  flex-direction: column;
}

.chat-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  border-radius: 12px;
}

.chat-card :deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 16px;
  overflow: hidden;
}

.chat-header {
  display: flex;
  align-items: center;
  gap: 12px;
}

.chat-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--glass-text);
}

.chat-list {
  flex: 1;
  overflow-y: auto;
  padding: 16px 8px;
  margin-bottom: 16px;
  border: var(--glass-border);
  border-radius: 8px;
  background: var(--glass-bg-light);
  backdrop-filter: var(--glass-blur-light);
}

.message-row {
  display: flex;
  margin-bottom: 16px;
  align-items: flex-start;
}

.user-row {
  justify-content: flex-end;
}

.assistant-row {
  justify-content: flex-start;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 600;
  flex-shrink: 0;
}

.avatar.user {
  background: linear-gradient(135deg, #409eff, #667eea);
  color: #fff;
  margin-left: 10px;
  order: 1;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

.avatar.assistant {
  background: linear-gradient(135deg, #67c23a, #42b983);
  color: #fff;
  margin-right: 10px;
  box-shadow: 0 2px 8px rgba(103, 194, 58, 0.3);
}

.message-bubble {
  max-width: 70%;
  padding: 10px 14px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
}

.message-bubble.user {
  background: linear-gradient(135deg, #409eff, #667eea);
  color: #fff;
  border-bottom-right-radius: 4px;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
}

.message-bubble.assistant {
  background: var(--glass-bg-medium);
  backdrop-filter: var(--glass-blur-light);
  color: var(--glass-text);
  border: var(--glass-border);
  border-bottom-left-radius: 4px;
}

.message-content {
  white-space: pre-wrap;
}

.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 4px 0;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--glass-text-secondary);
  animation: typing 1.2s infinite ease-in-out;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%, 60%, 100% { transform: translateY(0); opacity: 0.4; }
  30% { transform: translateY(-6px); opacity: 1; }
}

.input-area {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.send-btn {
  height: 56px;
  flex-shrink: 0;
}
</style>
