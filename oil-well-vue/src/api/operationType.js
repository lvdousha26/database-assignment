import request from '@/utils/request'

export const getOperationTypeList = (params) => request.get('/api/operation-type/list', { params })
export const getOperationTypeById = (id) => request.get(`/api/operation-type/${id}`)
export const addOperationType = (data) => request.post('/api/operation-type/add', data)
export const updateOperationType = (data) => request.put('/api/operation-type/update', data)
export const deleteOperationType = (id) => request.delete(`/api/operation-type/delete/${id}`)
