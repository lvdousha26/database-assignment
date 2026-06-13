import request from '@/utils/request'

// 查询分页数据
export const employeeSelectAllService = (params) =>
  request.post('/employee/list',params)

// 分页对象查询数据
export const employeeSelectByPageAndConditionService = (params)  =>
  request.post('/employee/page',params)

// 添加员工
export const employeeAddService = (data) => 
  request.post('/employee',data)

// 修改员工
export const employeeEditService = (data) => 
  request.put('/employee',data)

// 删除一条数据
export const employeeDelOneService = (id) =>
  request({
    method: 'delete',
    url: `/employee/deleteById/${id}`
  });

// 删除一组数据
export const employeeDelIdsService = (data) =>
  request({
    method: 'delete',
    url: '/employee',
    data: data
  });

// 根据id查找数据
export const employeeSelectOneService = (id) =>
  request.get('/employee/' + id)

// 启用禁用员工账号
export const employeeEnableService = (status,id) =>
  request.put(`/employee/status/${status}/${id}`)