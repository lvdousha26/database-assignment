import request from '@/utils/request'

export const userLoginService = (params) =>
  request.post('/user/login', null, { params })

export const userRegisterService = (params) =>
  request.post('/user/register', null, { params })

export const userGetInfoService = (params) =>
  request.get('/personal/userinfo', { params })

export const uploadFileService = (formData) =>
  request.post('/personal/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })

export const updateUsernameService = (data) =>
  request.post('/personal/updateUsername', data)

export const updateUserInfoService = (data) =>
  request.put('/personal/profile', data)

export const userListService = () =>
  request.get('/user/list')

export const userUpdateRoleService = (id, role) =>
  request.put(`/user/${id}/role`, { role })

export const userUpdateStatusService = (id, status) =>
  request.put(`/user/${id}/status`, { status })
