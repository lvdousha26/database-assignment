import request from '@/utils/request'

export const getCostList = (params) => request.get('/api/cost/list', { params })
export const getCostByCode = (code) => request.get(`/api/cost/${code}`)
export const addCost = (data) => request.post('/api/cost/add', data)
export const updateCost = (data) => request.put('/api/cost/update', data)
export const deleteCost = (code) => request.delete(`/api/cost/delete/${code}`)
