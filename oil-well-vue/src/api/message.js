import request from '@/utils/request'

export const getConversations = () => request.get('/news/conversations')

export const getConversation = (contactId, params) =>
  request.get(`/news/conversation/${contactId}`, { params })

export const sendMessage = (data) => request.post('/news/send', data)

export const markAsRead = (senderId) => request.put(`/news/read/${senderId}`)

export const getUnreadCount = () => request.get('/news/unread/count')
