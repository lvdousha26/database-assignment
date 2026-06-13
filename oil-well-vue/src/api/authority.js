import request from '@/utils/request';

/**
 * 获取收到的申请记录
 * @param {Object} params - 包含查询人id，页码和页项数的对象
 * @returns {Promise} 收到的申请记录页
 */
export const getReceivedRequest = (params) => {
    return request.get('/authority/received', { params });
};

/**
 * 获取发出的申请记录
 * @param {Object} params - 包含查询人id，页码和页项数的对象
 * @returns {Promise} 发送的申请记录页
 */
export const getSentRequest = (params) => {
    return request.get('/authority/sent', { params });
};

/**
 * 回复权限申请
 * @param {number} id - 申请记录id
 * @param {number} status - 申请回复代码(1:接受, 0:拒绝)
 * @returns {Promise} 申请状态修改执行结果
 */
export const responseToRequest = (id, status) => {
    return request.put('/authority', null, {
        params: { id, status }
    });
};