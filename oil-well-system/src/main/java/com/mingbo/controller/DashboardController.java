package com.mingbo.controller;

import com.mingbo.mapper.CostDetailMapper;
import com.mingbo.mapper.OperationMapper;
import com.mingbo.mapper.WellMapper;
import com.mingbo.pojo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private WellMapper wellMapper;

    @Autowired
    private OperationMapper operationMapper;

    @Autowired
    private CostDetailMapper costDetailMapper;

    @GetMapping("/stats")
    public Result stats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalWells", wellMapper.countAll() != null ? wellMapper.countAll() : 0);
        stats.put("productionWells", wellMapper.countProduction() != null ? wellMapper.countProduction() : 0);
        stats.put("monthlyOperations", operationMapper.countMonthly() != null ? operationMapper.countMonthly() : 0);
        stats.put("monthlyCost", costDetailMapper.sumMonthly() != null ? costDetailMapper.sumMonthly() : 0);

        List<Map<String, Object>> wellTypeDistribution = wellMapper.selectWellTypeStats()
                .stream()
                .map(s -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("name", s.getWellType());
                    m.put("value", s.getCnt());
                    return m;
                })
                .collect(Collectors.toList());
        stats.put("wellTypeDistribution", wellTypeDistribution);

        List<Map<String, Object>> trend = costDetailMapper.sumMonthlyTrend();
        List<String> monthLabels = trend.stream().map(t -> (String) t.get("month")).collect(Collectors.toList());
        List<Number> monthlyCostTrend = trend.stream().map(t -> (Number) t.get("total")).collect(Collectors.toList());
        stats.put("monthLabels", monthLabels);
        stats.put("monthlyCostTrend", monthlyCostTrend);

        stats.put("latestOperations", operationMapper.selectLatest());

        return Result.success(stats);
    }

    @GetMapping("/health")
    public Result health() {
        return Result.success("ok");
    }

    @GetMapping("/summary")
    public Result summary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalWells", wellMapper.countAll());
        summary.put("totalOperations", operationMapper.countAll());
        summary.put("totalCost", costDetailMapper.sumAll());
        summary.put("plannedOperations", operationMapper.countByStatus("计划"));
        summary.put("inProgressOperations", operationMapper.countByStatus("进行中"));
        summary.put("completedOperations", operationMapper.countByStatus("已完成"));
        return Result.success(summary);
    }
}
