import request from '@/utils/request'

export const sendChat = (data) => request.post('/api/ai/chat', data)
