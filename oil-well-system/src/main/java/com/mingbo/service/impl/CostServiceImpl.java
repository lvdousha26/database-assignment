package com.mingbo.service.impl;

import com.mingbo.mapper.CostMapper;
import com.mingbo.pojo.Cost;
import com.mingbo.service.CostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CostServiceImpl implements CostService {

    @Autowired
    private CostMapper costMapper;

    @Override
    public Cost getByCode(String code) {
        return costMapper.selectByCode(code);
    }

    @Override
    public List<Cost> list(String wellcode, String preunit, String content) {
        return costMapper.selectByCondition(wellcode, preunit, content);
    }

    @Override
    public void add(Cost cost) {
        costMapper.insert(cost);
    }

    @Override
    public void update(Cost cost) {
        costMapper.update(cost);
    }

    @Override
    public void delete(String code) {
        costMapper.deleteByCode(code);
    }
}
