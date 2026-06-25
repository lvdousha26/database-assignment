package com.mingbo.service;

import com.mingbo.pojo.Dynamic;

import java.util.List;

public interface DynamicService {

    void add(Dynamic dynamic);

    List<Dynamic> listByUserId(Long userId);

    List<Dynamic> listAll();

    void delete(Long id);
}
