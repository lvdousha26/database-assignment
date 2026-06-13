package com.oilwell.service;

import com.oilwell.pojo.OperationType;

import java.util.List;

public interface OperationTypeService {

    OperationType getById(Long id);

    List<OperationType> list();

    void add(OperationType operationType);

    void update(OperationType operationType);

    void delete(Long id);
}
