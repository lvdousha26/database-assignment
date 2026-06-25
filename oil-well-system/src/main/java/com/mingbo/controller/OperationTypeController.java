package com.mingbo.controller;

import com.mingbo.pojo.Result;
import com.mingbo.service.OperationTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/operation-type")
public class OperationTypeController {

    @Autowired
    private OperationTypeService operationTypeService;

    @GetMapping("/list")
    public Result list() {
        return Result.success(operationTypeService.list());
    }
}
