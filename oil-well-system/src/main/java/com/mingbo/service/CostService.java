package com.mingbo.service;

import com.mingbo.pojo.Cost;

import java.util.List;

public interface CostService {

    Cost getByCode(String code);

    List<Cost> list(String wellcode, String preunit, String content);

    void add(Cost cost);

    void update(Cost cost);

    void delete(String code);
}
