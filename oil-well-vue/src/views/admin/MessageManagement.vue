<script setup>
import { ref, onMounted, computed, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { getConversations, getConversation, sendMessage, markAsRead, getUnreadCount } from '@/api/message'
import { useUserStore } from '@/stores'
import { User, Message } from '@element-plus/icons-vue'

const userStore = useUserStore()
const conversations = ref([])
const activeContact = ref(null)
const messages = ref([])
const inputText = ref('')
const loading = ref(false)
const messagesEnd = ref(null)
const unreadCount = ref(0)

const currentUser = computed(() => userStore.user)

const loadConversations = async () => {
  try {
    const res = await getConversations()
    if (res.data.code === '1') {
      conversations.value = res.data.data || []
    }
  } catch (e) {
    // 静默
  }
}

const loadUnread = async () => {
  try {
    const res = await getUnreadCount()
    if (res.data.code === '1') {
      unreadCount.value = res.data.data?.count || 0
    }
  } catch (e) {
    // 静默
  }
}

const openConversation = async (contact) => {
  activeContact.value = contact
  loading.value = true
  try {
    const res = await getConversation(contact.contactId)
    if (res.data.code === '1') {
      messages.value = (res.data.data || []).reverse()
      await markAsRead(contact.contactId)
      contact.unread = 0
      await loadUnread()
      await loadConversations()
      nextTick(() => scrollToBottom())
    }
  } catch (e) {
    // 静默
  } finally {
    loading.value = false
  }
}

const doSend = async () => {
  if (!inputText.value.trim() || !activeContact.value) return
  const text = inputText.value.trim()
  inputText.value = ''
  try {
    const res = await sendMessage({ receiverId: activeContact.value.contactId, message: text })
    if (res.data.code === '1') {
      const msg = {
        id: Date.now(),
        senderId: currentUser.value.id,
        receiverId: activeContact.value.contactId,
        message: text,
        sentTime: new Date().toISOString()
      }
      messages.value.push(msg)
      nextTick(() => scrollToBottom())
      await loadConversations()
    } else {
      ElMessage.error('发送失败')
    }
  } catch (e) {
    ElMessage.error('发送失败')
  }
}

const scrollToBottom = () => {
  if (messagesEnd.value) {
    messagesEnd.value.scrollIntoView({ behavior: 'smooth' })
  }
}

const formatTime = (t) => {
  if (!t) return ''
  const d = new Date(t)
  const now = new Date()
  const pad = (n) => String(n).padStart(2, '0')
  if (d.toDateString() === now.toDateString()) {
    return `${pad(d.getHours())}:${pad(d.getMinutes())}`
  }
  return `${pad(d.getMonth() + 1)}/${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

const getInitial = (name) => name ? name.charAt(0).toUpperCase() : '?'

onMounted(() => {
  loadUnread()
  loadConversations()
})
</script>

<template>
  <div class="message-page">
    <div class="msg-container">
      <!-- 联系人列表 -->
      <div class="contact-list">
        <div class="contact-header">
          <h3><el-icon><Message /></el-icon> 消息</h3>
          <el-tag v-if="unreadCount > 0" type="danger" size="small">{{ unreadCount }}</el-tag>
        </div>
        <div class="contact-items">
          <div
            v-for="conv in conversations"
            :key="conv.contactId"
            class="contact-item"
            :class="{ active: activeContact?.contactId === conv.contactId }"
            @click="openConversation(conv)"
          >
            <div class="contact-avatar">
              <span class="avatar-text">{{ getInitial(conv.contactName) }}</span>
            </div>
            <div class="contact-info">
              <div class="contact-top">
                <span class="contact-name">{{ conv.contactName }}</span>
                <span class="contact-role" :class="conv.contactRole === '管理员' ? 'role-admin' : 'role-user'">
                  {{ conv.contactRole }}
                </span>
              </div>
              <div class="contact-bottom">
                <span class="contact-msg">{{ conv.lastMessage || '暂无消息' }}</span>
                <div class="contact-meta">
                  <span v-if="conv.unread > 0" class="unread-badge">{{ conv.unread > 99 ? '99+' : conv.unread }}</span>
                  <span class="contact-time">{{ formatTime(conv.lastTime) }}</span>
                </div>
              </div>
            </div>
          </div>
          <div v-if="conversations.length === 0" class="empty-contacts">
            <el-empty description="暂无会话" :image-size="80" />
          </div>
        </div>
      </div>

      <!-- 消息面板 -->
      <div class="chat-panel">
        <template v-if="activeContact">
          <div class="chat-header">
            <div class="chat-user">
              <div class="chat-avatar">
                <span class="avatar-text">{{ getInitial(activeContact.contactName) }}</span>
              </div>
              <div>
                <span class="chat-name">{{ activeContact.contactName }}</span>
                <span class="chat-role" :class="activeContact.contactRole === '管理员' ? 'role-admin' : 'role-user'">
                  {{ activeContact.contactRole }}
                </span>
              </div>
            </div>
          </div>
          <div class="chat-messages" ref="chatMessages">
            <div v-if="loading" class="loading-wrap">
              <el-icon class="is-loading"><Loading /></el-icon>
            </div>
            <div
              v-for="msg in messages"
              :key="msg.id"
              class="msg-row"
              :class="msg.senderId === currentUser?.id ? 'msg-self' : 'msg-other'"
            >
              <div class="msg-bubble">
                <div class="msg-text">{{ msg.message }}</div>
                <div class="msg-time">{{ formatTime(msg.sentTime) }}</div>
              </div>
            </div>
            <div ref="messagesEnd" />
          </div>
          <div class="chat-input">
            <el-input
              v-model="inputText"
              type="textarea"
              :rows="3"
              placeholder="输入消息..."
              @keydown.enter.prevent="doSend"
            />
            <el-button type="primary" @click="doSend" :disabled="!inputText.trim()">发送</el-button>
          </div>
        </template>
        <template v-else>
          <div class="no-chat">
            <el-empty description="选择一个联系人开始聊天" :image-size="120" />
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<style scoped>
.message-page {
  height: calc(100vh - 120px);
  padding: 0;
  background: transparent;
}

.msg-container {
  display: flex;
  height: 100%;
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
}

/* 联系人列表 */
.contact-list {
  width: 320px;
  border-right: 1px solid #eee;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
}

.contact-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid #eee;
}

.contact-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
  display: flex;
  align-items: center;
  gap: 6px;
}

.contact-items {
  flex: 1;
  overflow-y: auto;
}

.contact-item {
  display: flex;
  padding: 14px 20px;
  cursor: pointer;
  transition: background 0.2s;
  border-bottom: 1px solid #f5f5f5;
}

.contact-item:hover {
  background: #f0f7ff;
}

.contact-item.active {
  background: #e6f2ff;
}

.contact-avatar, .chat-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #409eff, #42b983);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.avatar-text {
  color: #fff;
  font-weight: 600;
  font-size: 16px;
}

.contact-info {
  flex: 1;
  margin-left: 12px;
  min-width: 0;
}

.contact-top {
  display: flex;
  align-items: center;
  gap: 8px;
}

.contact-name {
  font-weight: 600;
  color: #333;
  font-size: 14px;
}

.contact-role {
  font-size: 11px;
  padding: 1px 6px;
  border-radius: 4px;
}

.role-admin {
  background: #fef0f0;
  color: #f56c6c;
}

.role-user {
  background: #ecf5ff;
  color: #409eff;
}

.contact-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 4px;
}

.contact-msg {
  font-size: 13px;
  color: #999;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 160px;
}

.contact-meta {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-shrink: 0;
}

.unread-badge {
  background: #f56c6c;
  color: #fff;
  font-size: 11px;
  padding: 1px 6px;
  border-radius: 10px;
  min-width: 18px;
  text-align: center;
}

.contact-time {
  font-size: 11px;
  color: #bbb;
}

.empty-contacts {
  padding: 40px 0;
}

/* 聊天面板 */
.chat-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #fafafa;
}

.chat-header {
  padding: 14px 24px;
  border-bottom: 1px solid #eee;
  background: #fff;
}

.chat-user {
  display: flex;
  align-items: center;
  gap: 12px;
}

.chat-avatar {
  width: 42px;
  height: 42px;
}

.chat-name {
  font-weight: 600;
  color: #333;
  font-size: 15px;
}

.chat-role {
  font-size: 11px;
  padding: 1px 6px;
  border-radius: 4px;
  margin-left: 8px;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px 24px;
}

.loading-wrap {
  text-align: center;
  padding: 40px;
  font-size: 24px;
  color: #999;
}

.msg-row {
  margin-bottom: 16px;
  display: flex;
}

.msg-self {
  justify-content: flex-end;
}

.msg-other {
  justify-content: flex-start;
}

.msg-bubble {
  max-width: 70%;
  padding: 10px 16px;
  border-radius: 12px;
  position: relative;
}

.msg-self .msg-bubble {
  background: #409eff;
  color: #fff;
  border-bottom-right-radius: 4px;
}

.msg-other .msg-bubble {
  background: #fff;
  color: #333;
  border: 1px solid #e8e8e8;
  border-bottom-left-radius: 4px;
}

.msg-text {
  font-size: 14px;
  line-height: 1.6;
  word-break: break-word;
}

.msg-time {
  font-size: 11px;
  margin-top: 4px;
  opacity: 0.7;
  text-align: right;
}

.msg-other .msg-time {
  color: #999;
}

.chat-input {
  padding: 12px 24px 16px;
  border-top: 1px solid #eee;
  background: #fff;
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.chat-input .el-input {
  flex: 1;
}

.chat-input .el-button {
  height: 74px;
  width: 80px;
  font-size: 15px;
}

.no-chat {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
}
</style>
