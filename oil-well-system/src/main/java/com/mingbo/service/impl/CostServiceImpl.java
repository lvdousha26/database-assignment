package com.mingbo.service.impl;

import com.mingbo.mapper.CostDetailMapper;
import com.mingbo.pojo.CostDetail;
import com.mingbo.service.CostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CostServiceImpl implements CostService {

    @Autowired
    private CostDetailMapper costDetailMapper;

    @Override
    public CostDetail getById(Long id) {
        return costDetailMapper.selectById(id);
    }

    @Override
    public List<CostDetail> list(Long operationId, Long categoryId) {
        return costDetailMapper.selectByCondition(operationId, categoryId);
    }

    @Override
    public void add(CostDetail costDetail) {
        costDetailMapper.insert(costDetail);
    }

    @Override
    public void update(CostDetail costDetail) {
        costDetailMapper.update(costDetail);
    }

    @Override
    public void delete(Long id) {
        costDetailMapper.deleteById(id);
    }

    @Override
    public List<Map<String, Object>> sumByCategory() {
        return costDetailMapper.sumByCategory();
    }

    @Override
    public List<Map<String, Object>> sumByMonth() {
        return costDetailMapper.sumByMonth();
    }
}
