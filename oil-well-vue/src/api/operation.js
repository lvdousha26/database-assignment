import request from '@/utils/request'

export const getOperationList = (params) => request.get('/api/operation/list', { params })
export const getOperationById = (id) => request.get(`/api/operation/${id}`)
export const addOperation = (data) => request.post('/api/operation/add', data)
export const updateOperation = (data) => request.put('/api/operation/update', data)
export const deleteOperation = (id) => request.delete(`/api/operation/delete/${id}`)
