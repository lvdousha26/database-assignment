package com.oilwell.service;

import com.oilwell.pojo.Well;

import java.util.List;

public interface WellService {

    Well getById(Long id);

    List<Well> list(String wellName, String wellType, String wellStatus);

    void add(Well well);

    void update(Well well);

    void delete(Long id);
}
