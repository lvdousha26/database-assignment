package com.mingbo.service.impl;

import com.mingbo.anno.AutoCache;
import com.mingbo.mapper.EchartsMapper;
import com.mingbo.pojo.Echarts;
import com.mingbo.service.EchartsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EchartsServiceImpl implements EchartsService {

    @Autowired
    private EchartsMapper echartsMapper;

    @AutoCache(ttl = 300, prefix = "cache:echarts")
    @Override
    public List<Echarts> GetEchartsList() {
        List<Echarts> echarts = echartsMapper.getEchartsList();
        return echarts;
    }
}
