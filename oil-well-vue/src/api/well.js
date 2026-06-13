import request from '@/utils/request'

export const getWellList = (params) => request.get('/api/well/list', { params })
export const getWellById = (id) => request.get(`/api/well/${id}`)
export const addWell = (data) => request.post('/api/well/add', data)
export const updateWell = (data) => request.put('/api/well/update', data)
export const deleteWell = (id) => request.delete(`/api/well/delete/${id}`)
