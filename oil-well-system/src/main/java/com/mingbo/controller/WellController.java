package com.mingbo.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mingbo.pojo.Result;
import com.mingbo.pojo.Well;
import com.mingbo.service.WellService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/well")
public class WellController {

    @Autowired
    private WellService wellService;

    @GetMapping("/list")
    public Result list(@RequestParam(required = false) String wellName,
                       @RequestParam(required = false) String wellType,
                       @RequestParam(required = false) String wellStatus,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<Well> list = wellService.list(wellName, wellType, wellStatus);
        PageInfo<Well> pageInfo = new PageInfo<>(list);
        return Result.success(pageInfo);
    }

    @GetMapping("/all")
    public Result all() {
        return Result.success(wellService.list(null, null, null));
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.success(wellService.getById(id));
    }

    @PostMapping("/add")
    public Result add(@RequestBody Well well) {
        wellService.add(well);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody Well well) {
        wellService.update(well);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        wellService.delete(id);
        return Result.success();
    }
}
