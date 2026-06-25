import request from '@/utils/request'

export const addDynamicService = (data) =>
  request.post('/dynamic/add', data)

export const listDynamicService = (params) =>
  request.get('/dynamic/list', { params })

export const deleteDynamicService = (id) =>
  request.delete(`/dynamic/delete/${id}`)
