package com.mingbo.service.impl;

import com.mingbo.mapper.WellMapper;
import com.mingbo.pojo.Well;
import com.mingbo.service.WellService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WellServiceImpl implements WellService {

    @Autowired
    private WellMapper wellMapper;

    @Override
    public Well getById(Long id) {
        return wellMapper.selectById(id);
    }

    @Override
    public List<Well> list(String wellName, String wellType, String wellStatus) {
        return wellMapper.selectByCondition(wellName, wellType, wellStatus);
    }

    @Override
    public void add(Well well) {
        wellMapper.insert(well);
    }

    @Override
    public void update(Well well) {
        wellMapper.update(well);
    }

    @Override
    public void delete(Long id) {
        wellMapper.deleteById(id);
    }
}
