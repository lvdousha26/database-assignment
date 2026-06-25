package com.mingbo.service.impl;

import com.mingbo.mapper.OperationMapper;
import com.mingbo.pojo.Operation;
import com.mingbo.service.OperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperationServiceImpl implements OperationService {

    @Autowired
    private OperationMapper operationMapper;

    @Override
    public Operation getById(Long id) {
        return operationMapper.selectById(id);
    }

    @Override
    public List<Operation> list(Long wellId, String status, String operationName) {
        return operationMapper.selectByCondition(wellId, status, operationName);
    }

    @Override
    public void add(Operation operation) {
        operationMapper.insert(operation);
    }

    @Override
    public void update(Operation operation) {
        operationMapper.update(operation);
    }

    @Override
    public void delete(Long id) {
        operationMapper.deleteById(id);
    }
}
