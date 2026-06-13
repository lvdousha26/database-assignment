import request from '@/utils/request'

/**
 * 获取所有管理员列表
 * @param {Object} params 查询参数
 * @returns {Promise} 管理员列表
 */
export const getAdminList = (params) => {
    return request({
        url: '/admin/list',
        method: 'get',
        params
    })
}

/**
 * 获取可申请的管理员列表
 * @param {Object} params 查询参数
 * @returns {Promise} 可申请的管理员列表
 */
export const getAvailableAdmins = (params) => {
    return request({
        url: '/admin/available',
        method: 'get',
        params
    })
}

/**
 * 向管理员申请权限
 * @param {Object} data 申请数据
 * @returns {Promise} 申请结果
 */
export const addPermissionRequest = (data) => {
    return request({
        url: '/authority',
        method: 'post',
        data,
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        transformRequest: [
            function (data) {
                let ret = ''
                for (const it in data) {
                    ret +=
                        encodeURIComponent(it) + '=' + encodeURIComponent(data[it]) + '&'
                }
                return ret.slice(0, -1)
            }
        ]
    })
}

// 查询发送的权限申请历史记录
export const getAuthoritySentHistory = (params) =>
    request.get('/authority/sent', { params })