// src/api/message.js
import request from '@/utils/request';

/**
 * 获取消息发送人ID列表
 * @returns {Promise} 发送人ID列表
 */
export const getSenderList = () => {
    return request.get('/news/senders');
};

/**
 * 获取消息列表
 * @param {Object} params - 包含接收人id，当前页码和每页数量的对象
 * @returns {Promise} 消息列表
 */
export const getMessages = (params) => {
    return request.get('/news', { params });
};

/**
 * 获取用户信息
 * @param {number} userId - 用户ID
 * @returns {Promise} 用户信息
 */
export const getUserInfo = (userId) => {
    return request.get('/user/get_info', {
        params: { user_id: userId }
    });
};

/**
 * 发送消息
 * @param {Object} data - 包含发送人id、接收人id和消息内容的对象
 * @returns {Promise} 发送结果
 */
export const sendMessage = (data) => {
    return request.post('/news', data);
};

export const getBatchUserInfo = (userIds) => {
    return request.get('/user/batch_get_info', {
        params: { user_ids: userIds.join(',') }
    });
};