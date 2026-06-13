import request from '@/utils/request'

// 上传文件（表单数据）
// file_operation.js
export const uploadFile = (metaData, file) => {
    const formData = new FormData();
    // 注意这里直接将对象转为JSON字符串，而不是整个对象
    formData.append('metaData', new Blob([JSON.stringify(metaData)], {
        type: 'application/json'
    }));
    formData.append('file', file);

    return request.post('/src/reference-upload', formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        },
        transformRequest: [function (data, headers) {
            delete headers['Content-Type'];
            return data;
        }]
    });
}

// 分页查询文件列表
export const getFileList = (userId, currentPage = 1, pageSize = 10) =>
    request.get('/src', {
        params: { userId, currentPage, pageSize }
    })

// 删除单个文件
export const deleteFile = (id) =>
    request.delete('/src', { params: { id } })

// 批量删除文件（如果需要）
export const deleteFiles = (ids) =>
    request.delete('/src/batch', { data: { ids } })

// 获取文件详情（如果需要）
export const getFileDetail = (id) =>
    request.get(`/src/detail/${id}`)

// 更新文件元数据（如果需要）
export const updateFileMeta = (data) =>
    request.put('/src/update', data)