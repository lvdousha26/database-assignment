package com.mingbo.service;

import com.mingbo.pojo.OperationType;

import java.util.List;

public interface OperationTypeService {

    OperationType getById(Long id);

    List<OperationType> list();

    void add(OperationType operationType);

    void update(OperationType operationType);

    void delete(Long id);
}
