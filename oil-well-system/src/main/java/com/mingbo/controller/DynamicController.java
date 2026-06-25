package com.mingbo.controller;

import com.mingbo.pojo.Dynamic;
import com.mingbo.pojo.Result;
import com.mingbo.service.DynamicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dynamic")
public class DynamicController {

    @Autowired
    private DynamicService dynamicService;

    @PostMapping("/add")
    public Result add(@RequestBody Dynamic dynamic) {
        if (dynamic.getContent() == null || dynamic.getContent().trim().isEmpty()) {
            return Result.error("内容不能为空");
        }
        dynamicService.add(dynamic);
        return Result.success();
    }

    @GetMapping("/list")
    public Result list(@RequestParam(required = false) Long userId) {
        List<Dynamic> list;
        if (userId != null) {
            list = dynamicService.listByUserId(userId);
        } else {
            list = dynamicService.listAll();
        }
        return Result.success(list);
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        dynamicService.delete(id);
        return Result.success();
    }
}
