package com.mingbo.service;

import com.mingbo.pojo.PageResult;

public interface AdminSearchService {

    /**
     * 获取管理员列表
     * @param page 当前页码
     * @param pageSize 每页大小
     * @param search 搜索关键词
     * @return 分页结果
     */
    PageResult getAdminList(Integer page, Integer pageSize, String search);

    /**
     * 获取可申请的管理员列表(排除已申请过的)
     * @param page 当前页码
     * @param pageSize 每页大小
     * @param search 搜索关键词
     * @param userId 当前用户ID
     * @return 分页结果
     */
    PageResult getAvailableAdmins(Integer page, Integer pageSize, String search, int userId);
}
