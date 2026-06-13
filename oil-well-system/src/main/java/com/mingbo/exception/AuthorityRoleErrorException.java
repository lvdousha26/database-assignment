package com.mingbo.exception;

/**
 * 权限相关身份错误类
 */
public class AuthorityRoleErrorException extends Exception {
    public AuthorityRoleErrorException(String message) {
        super(message);
    }
}
