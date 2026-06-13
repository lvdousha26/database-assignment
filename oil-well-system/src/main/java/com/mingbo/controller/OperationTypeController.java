package com.oilwell.controller;

import com.oilwell.pojo.OperationType;
import com.oilwell.pojo.Result;
import com.oilwell.service.OperationTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/operation-type")
public class OperationTypeController {

    @Autowired
    private OperationTypeService operationTypeService;

    @GetMapping("/list")
    public Result list() {
        List<OperationType> list = operationTypeService.list();
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.success(operationTypeService.getById(id));
    }

    @PostMapping("/add")
    public Result add(@RequestBody OperationType operationType) {
        operationTypeService.add(operationType);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody OperationType operationType) {
        operationTypeService.update(operationType);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        operationTypeService.delete(id);
        return Result.success();
    }
}
