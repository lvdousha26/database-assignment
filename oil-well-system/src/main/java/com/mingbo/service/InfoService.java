package com.mingbo.service;

import com.mingbo.exception.OperationInvalidException;
import com.mingbo.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class InfoService {

    @Autowired
    private HttpServletRequest request;

    public long getOperateUser() {
        String jwt = request.getHeader("token");
        if (!StringUtils.hasLength(jwt)) {
            throw new OperationInvalidException("未登录，请先登录");
        }
        try {
            return JwtUtils.parseJWT(jwt).get("id").asLong();
        } catch (Exception e) {
            throw new OperationInvalidException("登录已过期，请重新登录");
        }
    }
}
