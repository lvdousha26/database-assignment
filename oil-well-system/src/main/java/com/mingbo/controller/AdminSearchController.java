package com.mingbo.controller;

import com.mingbo.pojo.PageResult;
import com.mingbo.pojo.Result;
import com.mingbo.service.AdminSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员查询控制器
 */
@RestController
@RequestMapping("/admin")
public class AdminSearchController {

    @Autowired
    private AdminSearchService adminSearchService;

    /**
     * 分页查询管理员列表
     * @param page 当前页码
     * @param pageSize 每页大小
     * @param search 搜索关键词(可选)
     * @return 分页结果
     */
    @GetMapping("/list")
    public Result getAdminList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String search) {

        PageResult pageResult = adminSearchService.getAdminList(page, pageSize, search);
        return Result.success(pageResult);
    }

    /**
     * 获取可申请的管理员列表(排除已申请过的)
     * @param page 当前页码
     * @param pageSize 每页大小
     * @param search 搜索关键词(可选)
     * @return 分页结果
     */
    @GetMapping("/available")
    public Result getAvailableAdmins(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String search) {

        PageResult pageResult = adminSearchService.getAvailableAdmins(page, pageSize, search, -1);
        return Result.success(pageResult);
    }
}
