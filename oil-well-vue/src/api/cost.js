import request from '@/utils/request'

export const getCostList = (params) => request.get('/api/cost/list', { params })
export const getCostById = (id) => request.get(`/api/cost/${id}`)
export const addCost = (data) => request.post('/api/cost/add', data)
export const updateCost = (data) => request.put('/api/cost/update', data)
export const deleteCost = (id) => request.delete(`/api/cost/delete/${id}`)
