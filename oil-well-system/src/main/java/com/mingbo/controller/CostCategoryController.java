package com.mingbo.controller;

import com.mingbo.pojo.CostCategory;
import com.mingbo.pojo.Result;
import com.mingbo.service.CostCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cost-category")
public class CostCategoryController {

    @Autowired
    private CostCategoryService costCategoryService;

    @GetMapping("/list")
    public Result list() {
        List<CostCategory> list = costCategoryService.list();
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.success(costCategoryService.getById(id));
    }

    @PostMapping("/add")
    public Result add(@RequestBody CostCategory costCategory) {
        costCategoryService.add(costCategory);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody CostCategory costCategory) {
        costCategoryService.update(costCategory);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        costCategoryService.delete(id);
        return Result.success();
    }
}
