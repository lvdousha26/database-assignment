package com.mingbo.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mingbo.pojo.Cost;
import com.mingbo.pojo.Result;
import com.mingbo.service.CostService;
import com.mingbo.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cost")
public class CostController {

    @Autowired
    private CostService costService;

    @Autowired
    private PermissionService permissionService;

    @GetMapping("/list")
    public Result list(@RequestParam(required = false) String wellcode,
                       @RequestParam(required = false) String preunit,
                       @RequestParam(required = false) String content,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer pageSize) {
        permissionService.requireRead();
        PageHelper.startPage(page, pageSize);
        List<Cost> list = costService.list(wellcode, preunit, content);
        PageInfo<Cost> pageInfo = new PageInfo<>(list);
        return Result.success(pageInfo);
    }

    @GetMapping("/{code}")
    public Result getByCode(@PathVariable String code) {
        permissionService.requireRead();
        return Result.success(costService.getByCode(code));
    }

    @PostMapping("/add")
    public Result add(@RequestBody Cost cost) {
        permissionService.requireCreate();
        costService.add(cost);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody Cost cost) {
        permissionService.requireUpdate();
        costService.update(cost);
        return Result.success();
    }

    @DeleteMapping("/delete/{code}")
    public Result delete(@PathVariable String code) {
        permissionService.requireDelete();
        costService.delete(code);
        return Result.success();
    }
}
