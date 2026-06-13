package com.mingbo.aop;

import com.alibaba.fastjson.JSONObject;
import com.mingbo.service.RedisService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Slf4j
@Component
@Aspect
public class CacheAspect {

    @Autowired
    private RedisService redisService;

    @Around("@annotation(com.mingbo.anno.AutoCache)")
    public Object cache(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        com.mingbo.anno.AutoCache autoCache = signature.getMethod().getAnnotation(com.mingbo.anno.AutoCache.class);

        String cacheKey = generateKey(autoCache, joinPoint);

        // 尝试从缓存获取
        Object cached = redisService.get(cacheKey);
        if (cached != null) {
            redisService.increment("cache:stats:hits");
            setCacheHeader("HIT");
            log.debug("缓存命中: {}", cacheKey);
            return cached;
        }

        // 未命中，执行原方法
        redisService.increment("cache:stats:misses");
        setCacheHeader("MISS");
        Object result = joinPoint.proceed();

        // 缓存结果
        if (result != null) {
            redisService.set(cacheKey, result, autoCache.ttl(), java.util.concurrent.TimeUnit.SECONDS);
            log.debug("缓存写入: {}", cacheKey);
        }

        return result;
    }

    private String generateKey(com.mingbo.anno.AutoCache autoCache, ProceedingJoinPoint joinPoint) {
        String prefix = autoCache.prefix();
        String key = autoCache.key();
        if (!key.isEmpty()) {
            return prefix + ":" + key;
        }
        // 自动生成 key: prefix:ClassName.MethodName(args)
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        String argsStr = args.length > 0 ? Arrays.toString(args) : "";
        return prefix + ":" + className + "." + methodName + ":" + argsStr.hashCode();
    }

    private void setCacheHeader(String status) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletResponse response = attributes.getResponse();
                if (response != null) {
                    response.setHeader("X-Cache-Status", status);
                }
            }
        } catch (Exception e) {
            log.warn("设置缓存响应头失败", e);
        }
    }
}
