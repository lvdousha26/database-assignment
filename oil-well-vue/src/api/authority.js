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

/**
 * 获取已授权用户列表
 * @param {Object} params - 查询参数
 * @returns {Promise} 已授权用户列表
 */
export const getAuthorizedUsers = (params) => {
    return request.get('/authority/users', { params });
};

/**
 * 更新用户授权（收回/修改权限）
 * @param {number} userId - 用户ID
 * @param {Object} data - 权限数据
 * @returns {Promise} 操作结果
 */
export const updateUserAuthority = (userId, data) => {
    return request.put(`/authority/user/${userId}`, null, { params: data });
};

/**
 * 获取当前用户的有效权限
 * @returns {Promise} 当前权限状态
 */
export const getMyPermissions = () => {
    return request.get('/authority/my');
};