<template>
  <div class="message-container">
    <div class="message-sidebar">
      <div class="sidebar-header">
        <h3>消息列表</h3>
      </div>
      <div class="sidebar-content">
        <div
            v-for="contact in contacts"
            :key="contact.id"
            class="contact-item"
            :class="{ active: activeContact === contact.id }"
            @click="selectContact(contact.id)"
        >
          <div class="contact-avatar" v-if="contact.id !== 0">
            <img :src="contact.avatar" :alt="contact.name" @error="handleAvatarError">
          </div>
          <div class="contact-info">
            <div class="contact-name">{{ contact.name }}</div>
            <div class="contact-last-msg">{{ contact.lastMessage }}</div>
          </div>
        </div>
      </div>
    </div>

    <div class="message-content">
      <div class="message-header">
        <div v-if="activeContact === 0" class="system-header">
          <h3>系统消息</h3>
        </div>
        <div v-else class="user-header">
          <div class="user-avatar">
            <img :src="currentContact.avatar" :alt="currentContact.name" @error="handleAvatarError">
          </div>
          <div class="user-name">{{ currentContact.name }}</div>
        </div>
      </div>

      <div class="message-list" ref="messageList">
        <div
            v-for="(message, index) in messages"
            :key="index"
            class="message-item"
            :class="{
            'system-message': message.senderId === 0,
            'received-message': message.senderId !== currentUserId,
            'sent-message': message.senderId === currentUserId
          }"
        >
          <div class="message-avatar" v-if="message.senderId !== 0 && message.senderId !== currentUserId">
            <img :src="getSenderAvatar(message.senderId)" :alt="getSenderName(message.senderId)" @error="handleAvatarError">
          </div>
          <div class="message-content-wrapper">
            <div class="message-sender" v-if="message.senderId !== currentUserId && message.senderId !== 0">
              {{ getSenderName(message.senderId) }}
            </div>
            <div class="message-text">{{ message.message }}</div>
            <div class="message-time">{{ formatTime(message.sentTime) }}</div>
          </div>
          <div class="message-avatar" v-if="message.senderId === currentUserId">
            <img :src="userInfo.avatar || defaultAvatar" :alt="userInfo.username" @error="handleAvatarError">
          </div>
        </div>
        <div v-if="loading" class="loading-more">加载中...</div>
      </div>

      <div class="message-input" v-if="activeContact !== 0">
        <el-input
            v-model="newMessage"
            type="textarea"
            :rows="3"
            placeholder="输入消息内容"
            @keyup.enter.native="sendMessage"
        ></el-input>
        <div class="input-actions">
          <el-button type="primary" :loading="sending" @click="sendMessage">发送</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getSenderList, getMessages, getUserInfo, sendMessage, getBatchUserInfo } from '@/api/message'
import { useUserStore } from '@/stores/modules/user'
import { ElMessage } from 'element-plus'
import defaultAvatar from '@/assets/avatar-default.png'
import systemAvatar from '@/assets/system-avatar.png'

export default {

  name: 'MessagePage',
  setup() {
    const userStore = useUserStore()
    return { userStore }
  },
  data() {
    return {
      contacts: [], // 联系人列表
      activeContact: null, // 当前选中的联系人ID
      messages: [], // 当前联系人的消息列表
      newMessage: '', // 新消息内容
      loading: false,
      sending: false,
      hasMore: true,
      currentPage: 1,
      pageSize: 10,
      contactInfoMap: {}, // 缓存联系人信息
      defaultAvatar,
      systemAvatar
    }
  },
  computed: {
    currentUserId() {
      return this.userStore.user?.id || null
    },
    userInfo() {
      return this.userStore.user || {}
    },
    currentContact() {
      return this.contactInfoMap[this.activeContact] || {
        id: this.activeContact,
        name: `用户${this.activeContact}`,
        avatar: defaultAvatar
      }
    }
  },
  async created() {
    await this.loadContacts()
    if (this.contacts.length > 0) {
      this.selectContact(this.contacts[0].id)
    }
  },
  methods: {
    handleAvatarError(e) {
      e.target.src = this.defaultAvatar
    },
    async loadContacts() {
      try {
        // 获取发送人列表
        const senderRes = await getSenderList()
        if (senderRes.data.code === '1') {
          const senderIds = senderRes.data.data

          // 初始化联系人列表
          this.contacts = senderIds.map(id => ({
            id,
            name: id === 0 ? '系统消息' : `用户${id}`,
            lastMessage: '',
            avatar: id === 0 ? this.systemAvatar : this.defaultAvatar
          }))

          // 批量获取用户信息（过滤掉系统消息）
          const userIds = senderIds.filter(id => id !== 0)
          if (userIds.length > 0) {
            await this.loadBatchContactInfo(userIds)
          }

          // 为每个联系人加载最后一条消息
          await this.loadLastMessages()
        }
      } catch (error) {
        console.error('加载联系人失败:', error)
        ElMessage.error('加载联系人失败')
      }
    },

    async loadBatchContactInfo(userIds) {
      try {
        const res = await getBatchUserInfo(userIds)
        if (res.data.code === '1') {
          res.data.data.forEach(user => {
            // 直接赋值给对象，Vue 3 会自动处理响应式
            this.contactInfoMap[user.id] = {
              id: user.id,
              name: user.username,
              avatar: user.avatar || this.defaultAvatar
            }

            // 更新联系人列表中的信息
            const contact = this.contacts.find(c => c.id === user.id)
            if (contact) {
              contact.name = user.username
              contact.avatar = user.avatar || this.defaultAvatar
            }
          })
        }
      } catch (error) {
        console.error('批量加载用户信息失败:', error)
      }
    },

    async loadLastMessages() {
      try {
        for (const contact of this.contacts) {
          const res = await getMessages({
            userId: contact.id,
            currentPage: 1,
            pageSize: 1
          })

          if (res.data.code === '1' && res.data.data.rows.length > 0) {
            contact.lastMessage = res.data.data.rows[0].message
          }
        }
      } catch (error) {
        console.error('加载最后消息失败:', error)
      }
    },

    async selectContact(contactId) {
      this.activeContact = contactId
      this.messages = []
      this.currentPage = 1
      this.hasMore = true
      await this.loadMessages()

      // 滚动到底部
      this.$nextTick(() => {
        this.scrollToBottom()
      })
    },

    async loadMessages() {
      if (!this.hasMore || this.loading) return

      this.loading = true
      try {
        const res = await getMessages({
          userId: this.activeContact,
          currentPage: this.currentPage,
          pageSize: this.pageSize
        })

        if (res.data.code === '1') {
          const newMessages = res.data.data.rows.reverse() // 因为API返回的是最新的在前面
          this.messages = [...newMessages, ...this.messages]

          if (newMessages.length < this.pageSize) {
            this.hasMore = false
          } else {
            this.currentPage++
          }
        }
      } catch (error) {
        console.error('加载消息失败:', error)
      } finally {
        this.loading = false
      }
    },

    async sendMessage() {
      if (!this.newMessage.trim() || this.activeContact === 0 || this.sending) return
      this.sending = true

      try {
        const res = await sendMessage({
          senderId: this.currentUserId,
          receiverId: this.activeContact,
          message: this.newMessage
        })

        if (res.data.code === '1') {
          // 添加到消息列表
          this.messages.push({
            senderId: this.currentUserId,
            receiverId: this.activeContact,
            message: this.newMessage,
            sentTime: new Date().toISOString()
          })

          // 更新联系人最后一条消息
          const contact = this.contacts.find(c => c.id === this.activeContact)
          if (contact) {
            contact.lastMessage = this.newMessage
          }

          this.newMessage = ''

          // 滚动到底部
          this.$nextTick(() => {
            this.scrollToBottom()
          })
        }
      } catch (error) {
        console.error('发送消息失败:', error)
      } finally {
        this.sending = false
      }
    },

    getSenderName(senderId) {
      if (senderId === 0) return '系统消息'
      if (senderId === this.currentUserId) return '我'
      return this.contactInfoMap[senderId]?.name || `用户${senderId}`
    },

    getSenderAvatar(senderId) {
      if (senderId === 0) return this.systemAvatar
      if (senderId === this.currentUserId) return this.userInfo.avatar || this.defaultAvatar
      return this.contactInfoMap[senderId]?.avatar || this.defaultAvatar
    },

    formatTime(time) {
      return new Date(time).toLocaleString()
    },

    scrollToBottom() {
      const container = this.$refs.messageList
      if (container) {
        container.scrollTop = container.scrollHeight
      }
    },

    handleScroll() {
      const container = this.$refs.messageList
      if (container.scrollTop === 0 && this.hasMore) {
        this.loadMessages()
      }
    }
  },
  mounted() {
    const container = this.$refs.messageList
    if (container) {
      container.addEventListener('scroll', this.handleScroll)
    }
  },
  beforeDestroy() {
    const container = this.$refs.messageList
    if (container) {
      container.removeEventListener('scroll', this.handleScroll)
    }
  }
}
</script>

<style scoped>
.message-container {
  display: flex;
  height: calc(100vh - 60px);
  background-color: #f5f5f5;
}

.message-sidebar {
  width: 300px;
  background-color: #fff;
  border-right: 1px solid #e6e6e6;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  padding: 15px;
  border-bottom: 1px solid #e6e6e6;
}

.sidebar-content {
  flex: 1;
  overflow-y: auto;
}

.contact-item {
  display: flex;
  padding: 12px 15px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.contact-item:hover {
  background-color: #f0f0f0;
}

.contact-item.active {
  background-color: #e6f7ff;
}

.contact-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 10px;
}

.contact-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.contact-info {
  flex: 1;
  min-width: 0;
}

.contact-name {
  font-weight: 500;
  margin-bottom: 3px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.contact-last-msg {
  font-size: 12px;
  color: #999;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.message-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: #fff;
}

.message-header {
  padding: 15px;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
}

.user-header {
  display: flex;
  align-items: center;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 10px;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.message-list {
  flex: 1;
  padding: 15px;
  overflow-y: auto;
  background-color: #f5f5f5;
}

.message-item {
  display: flex;
  margin-bottom: 15px;
}

.message-item.system-message {
  justify-content: center;
}

.message-item.system-message .message-content-wrapper {
  background-color: #e6f7ff;
  padding: 8px 15px;
  border-radius: 4px;
  max-width: 70%;
}

.message-item.received-message {
  justify-content: flex-start;
}

.message-item.sent-message {
  justify-content: flex-end;
}

.message-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 10px;
  align-self: flex-end;
}

.message-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.message-content-wrapper {
  max-width: 60%;
}

.message-item.sent-message .message-content-wrapper {
  text-align: right;
}

.message-sender {
  font-size: 12px;
  color: #666;
  margin-bottom: 3px;
}

.message-text {
  background-color: #fff;
  padding: 8px 12px;
  border-radius: 4px;
  word-break: break-word;
}

.message-item.received-message .message-text {
  background-color: #e6f7ff;
}

.message-item.sent-message .message-text {
  background-color: #69c0ff;
  color: #fff;
}

.message-time {
  font-size: 10px;
  color: #999;
  margin-top: 3px;
}

.message-input {
  padding: 15px;
  border-top: 1px solid #e6e6e6;
}

.input-actions {
  margin-top: 10px;
  text-align: right;
}

.loading-more {
  text-align: center;
  padding: 10px;
  color: #999;
  font-size: 14px;
}
</style>