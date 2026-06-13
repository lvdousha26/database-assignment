import request from '@/utils/request';

// 发送问题到AI
export const chatWithAI = (question) => {
  return request.post('/api/ai/chat',  {question} );
};