package com.mingbo.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.mingbo.pojo.Result;
import com.mingbo.service.RedisService;
import com.mingbo.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisService redisService;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            return true;
        }

        String url = req.getRequestURL().toString();
        log.info("请求的url: {}", url);

        boolean DEBUG_MODE = true;

        if (DEBUG_MODE) {
            try {
                String jwt = req.getHeader("token");
                String userId = "debug";
                if (StringUtils.hasLength(jwt)) {
                    try {
                        userId = JwtUtils.parseJWT(jwt).get("id").asString();
                    } catch (Exception ignored) {}
                }
                redisService.sAdd("online:users", userId);
                redisService.expire("online:users", 5, TimeUnit.MINUTES);
            } catch (Exception e) {
                log.warn("记录在线用户失败", e);
            }
            return true;
        }

        String jwt = req.getHeader("token");
        if (!StringUtils.hasLength(jwt)) {
            log.info("请求头token为空,返回未登录的信息");
            Result error = Result.error("NOT_LOGIN");
            String notLogin = JSONObject.toJSONString(error);
            resp.getWriter().write(notLogin);
            return false;
        }

        try {
            JwtUtils.parseJWT(jwt);
        } catch (Exception e) {
            log.info("解析令牌失败, 返回未登录错误信息");
            Result error = Result.error("NOT_LOGIN");
            String notLogin = JSONObject.toJSONString(error);
            resp.getWriter().write(notLogin);
            return false;
        }

        try {
            String userId = JwtUtils.parseJWT(jwt).get("id").asString();
            redisService.sAdd("online:users", userId);
            redisService.expire("online:users", 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.warn("记录在线用户失败", e);
        }

        return true;
    }
}
