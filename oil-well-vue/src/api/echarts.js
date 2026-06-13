import request from '@/utils/request'

// 获取echarts数据
// 查询分页数据
export const echartsSelectAll= () =>
  request.get('/echarts/list')
