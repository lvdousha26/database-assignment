package com.oilwell.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.oilwell.pojo.CostDetail;
import com.oilwell.pojo.Result;
import com.oilwell.service.CostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cost")
public class CostController {

    @Autowired
    private CostService costService;

    @GetMapping("/list")
    public Result list(@RequestParam(required = false) Long operationId,
                       @RequestParam(required = false) Long categoryId,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<CostDetail> list = costService.list(operationId, categoryId);
        PageInfo<CostDetail> pageInfo = new PageInfo<>(list);
        return Result.success(pageInfo);
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.success(costService.getById(id));
    }

    @PostMapping("/add")
    public Result add(@RequestBody CostDetail costDetail) {
        costService.add(costDetail);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody CostDetail costDetail) {
        costService.update(costDetail);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        costService.delete(id);
        return Result.success();
    }

    @GetMapping("/sum-by-category")
    public Result sumByCategory() {
        return Result.success(costService.sumByCategory());
    }

    @GetMapping("/sum-by-month")
    public Result sumByMonth() {
        return Result.success(costService.sumByMonth());
    }
}
