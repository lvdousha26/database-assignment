package com.mingbo.aop;

import com.auth0.jwt.interfaces.Claim;
import com.mingbo.service.RedisService;
import com.mingbo.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;

@Slf4j
@Component
@Aspect
public class RateLimitAspect {

    @Autowired
    private RedisService redisService;

    @Around("@annotation(rateLimit)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, com.mingbo.anno.RateLimit rateLimit) throws Throwable {
        int maxRequests = rateLimit.maxRequests();
        int windowSeconds = rateLimit.windowSeconds();

        // 获取用户ID
        String userId = "anonymous";
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String jwt = request.getHeader("token");
                if (jwt != null && !jwt.isEmpty()) {
                    Map<String, Claim> claims = JwtUtils.parseJWT(jwt);
                    userId = claims.get("id").asString();
                }
            }
        } catch (Exception e) {
            log.warn("获取用户ID失败", e);
        }

        String methodName = joinPoint.getSignature().toShortString();
        String redisKey = "rate_limit:" + methodName + ":" + userId;

        Long count = redisService.increment(redisKey);
        if (count == 1) {
            redisService.expire(redisKey, windowSeconds, java.util.concurrent.TimeUnit.SECONDS);
        }

        if (count > maxRequests) {
            log.warn("请求过于频繁, userId={}, method={}, count={}", userId, methodName, count);
            throw new RuntimeException("请求过于频繁，请稍后重试");
        }

        return joinPoint.proceed();
    }
}
