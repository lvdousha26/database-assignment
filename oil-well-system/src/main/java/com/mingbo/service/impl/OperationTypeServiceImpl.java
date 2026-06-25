package com.mingbo.service.impl;

import com.mingbo.mapper.OperationTypeMapper;
import com.mingbo.pojo.OperationType;
import com.mingbo.service.OperationTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperationTypeServiceImpl implements OperationTypeService {

    @Autowired
    private OperationTypeMapper operationTypeMapper;

    @Override
    public OperationType getById(Long id) {
        return operationTypeMapper.selectById(id);
    }

    @Override
    public List<OperationType> list() {
        return operationTypeMapper.selectAll();
    }

    @Override
    public void add(OperationType operationType) {
        operationTypeMapper.insert(operationType);
    }

    @Override
    public void update(OperationType operationType) {
        operationTypeMapper.update(operationType);
    }

    @Override
    public void delete(Long id) {
        operationTypeMapper.deleteById(id);
    }
}
