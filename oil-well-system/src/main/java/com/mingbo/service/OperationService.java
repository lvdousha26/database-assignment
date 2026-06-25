package com.mingbo.service;

import com.mingbo.pojo.Operation;

import java.util.List;

public interface OperationService {

    Operation getById(Long id);

    List<Operation> list(Long wellId, String status, String operationName);

    void add(Operation operation);

    void update(Operation operation);

    void delete(Long id);
}
