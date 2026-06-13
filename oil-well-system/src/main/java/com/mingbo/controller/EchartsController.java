package com.mingbo.controller;

import com.mingbo.pojo.Echarts;
import com.mingbo.pojo.Result;
import com.mingbo.service.EchartsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Deprecated
@RestController
@RequestMapping("/echarts")
@Slf4j
public class EchartsController {

    @Autowired
    private EchartsService echartsService;

    @GetMapping("/list")
    public Result GetEchartsList(){
        List<Echarts> echarts = echartsService.GetEchartsList();
        return Result.success(echarts);
    }
}
