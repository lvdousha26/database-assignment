import request from '@/utils/request'

export const userLoginService = (params) =>
  request.post('/user/login', null, { params })

export const userRegisterService = (params) =>
  request.post('/user/register', null, { params })

export const userGetInfoService = (params) => 
  request.get('/personal/userinfo', { params })
