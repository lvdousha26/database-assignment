package com.mingbo.exception;

import com.mingbo.pojo.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OperationInvalidException.class)
    public Result handleOperationInvalid(OperationInvalidException ex) {
        ex.printStackTrace();
        return Result.error(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result ex(Exception ex) {
        ex.printStackTrace();
        return Result.error("操作失败,请联系管理员");
    }
}
