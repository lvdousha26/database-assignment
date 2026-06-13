import request from '@/utils/request'

export const getCostCategoryList = (params) => request.get('/api/cost-category/list', { params })
export const getCostCategoryById = (id) => request.get(`/api/cost-category/${id}`)
export const addCostCategory = (data) => request.post('/api/cost-category/add', data)
export const updateCostCategory = (data) => request.put('/api/cost-category/update', data)
export const deleteCostCategory = (id) => request.delete(`/api/cost-category/delete/${id}`)
