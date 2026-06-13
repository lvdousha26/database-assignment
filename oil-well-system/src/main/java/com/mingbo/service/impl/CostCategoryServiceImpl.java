package com.oilwell.service.impl;

import com.oilwell.mapper.CostCategoryMapper;
import com.oilwell.pojo.CostCategory;
import com.oilwell.service.CostCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CostCategoryServiceImpl implements CostCategoryService {

    @Autowired
    private CostCategoryMapper costCategoryMapper;

    @Override
    public CostCategory getById(Long id) {
        return costCategoryMapper.selectById(id);
    }

    @Override
    public List<CostCategory> list() {
        return costCategoryMapper.selectAll();
    }

    @Override
    public void add(CostCategory costCategory) {
        costCategoryMapper.insert(costCategory);
    }

    @Override
    public void update(CostCategory costCategory) {
        costCategoryMapper.update(costCategory);
    }

    @Override
    public void delete(Long id) {
        costCategoryMapper.deleteById(id);
    }
}
