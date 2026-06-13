package com.mingbo.service.impl;

import com.mingbo.mapper.AdminMapper;
import com.mingbo.mapper.AuthorityMapper;
import com.mingbo.pojo.PageResult;
import com.mingbo.service.AdminSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminSearchSearchServiceImpl implements AdminSearchService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private AuthorityMapper authorityMapper;

    @Override
    public PageResult getAdminList(Integer page, Integer pageSize, String search) {
        // 计算偏移量
        int offset = (page - 1) * pageSize;

        // 查询数据
        List<?> list = adminMapper.selectAdminsByPage(offset, pageSize, search);
        long total = adminMapper.countAdmins(search);

        return new PageResult(total, list);
    }

    @Override
    public PageResult getAvailableAdmins(Integer page, Integer pageSize, String search, int userId) {
        // 计算偏移量
        int offset = (page - 1) * pageSize;

        // 查询数据
        List<?> list = adminMapper.selectAvailableAdmins(offset, pageSize, search, userId);
        long total = adminMapper.countAvailableAdmins(search);

        return new PageResult(total, list);
    }
}
