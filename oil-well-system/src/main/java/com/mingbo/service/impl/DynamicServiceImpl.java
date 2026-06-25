package com.mingbo.service.impl;

import com.mingbo.mapper.DynamicMapper;
import com.mingbo.pojo.Dynamic;
import com.mingbo.service.DynamicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DynamicServiceImpl implements DynamicService {

    @Autowired
    private DynamicMapper dynamicMapper;

    @Override
    public void add(Dynamic dynamic) {
        dynamicMapper.insert(dynamic);
    }

    @Override
    public List<Dynamic> listByUserId(Long userId) {
        return dynamicMapper.selectByUserId(userId);
    }

    @Override
    public List<Dynamic> listAll() {
        return dynamicMapper.selectAll();
    }

    @Override
    public void delete(Long id) {
        dynamicMapper.deleteById(id);
    }
}
