package com.mingbo.exception;

/**
 * 不合法操作错误类
 */
public class OperationInvalidException extends RuntimeException {
    public OperationInvalidException(String message) {
        super(message);
    }
}
