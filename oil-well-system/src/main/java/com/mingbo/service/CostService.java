package com.mingbo.service;

import com.mingbo.pojo.CostDetail;

import java.util.List;
import java.util.Map;

public interface CostService {

    CostDetail getById(Long id);

    List<CostDetail> list(Long operationId, Long categoryId);

    void add(CostDetail costDetail);

    void update(CostDetail costDetail);

    void delete(Long id);

    List<Map<String, Object>> sumByCategory();

    List<Map<String, Object>> sumByMonth();
}
