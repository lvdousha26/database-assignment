package com.mingbo.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mingbo.pojo.Operation;
import com.mingbo.pojo.Result;
import com.mingbo.service.OperationService;
import com.mingbo.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/operation")
public class OperationController {

    @Autowired
    private OperationService operationService;

    @Autowired
    private PermissionService permissionService;

    @GetMapping("/list")
    public Result list(@RequestParam(required = false) Long wellId,
                       @RequestParam(required = false) String status,
                       @RequestParam(required = false) String operationName,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer pageSize) {
        permissionService.requireRead();
        PageHelper.startPage(page, pageSize);
        List<Operation> list = operationService.list(wellId, status, operationName);
        PageInfo<Operation> pageInfo = new PageInfo<>(list);
        return Result.success(pageInfo);
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        permissionService.requireRead();
        return Result.success(operationService.getById(id));
    }

    @PostMapping("/add")
    public Result add(@RequestBody Operation operation) {
        permissionService.requireCreate();
        operationService.add(operation);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody Operation operation) {
        permissionService.requireUpdate();
        operationService.update(operation);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        permissionService.requireDelete();
        operationService.delete(id);
        return Result.success();
    }
}
