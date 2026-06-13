import request from '@/utils/request'

export const getDashboardStats = () => request.get('/api/dashboard/stats')
export const getDashboardSummary = () => request.get('/api/dashboard/summary')
