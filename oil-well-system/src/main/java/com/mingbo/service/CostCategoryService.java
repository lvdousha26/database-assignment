package com.oilwell.service;

import com.oilwell.pojo.CostCategory;

import java.util.List;

public interface CostCategoryService {

    CostCategory getById(Long id);

    List<CostCategory> list();

    void add(CostCategory costCategory);

    void update(CostCategory costCategory);

    void delete(Long id);
}
