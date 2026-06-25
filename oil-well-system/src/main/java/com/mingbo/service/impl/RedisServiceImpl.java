package com.mingbo.service.impl;

import com.mingbo.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    @Lazy
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void set(String key, Object value) {
        try { redisTemplate.opsForValue().set(key, value); }
        catch (Exception e) { log.warn("Redis 不可用, set({}) 降级", key); }
    }

    @Override
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        try { redisTemplate.opsForValue().set(key, value, timeout, unit); }
        catch (Exception e) { log.warn("Redis 不可用, set({}) 降级", key); }
    }

    @Override
    public Object get(String key) {
        try { return redisTemplate.opsForValue().get(key); }
        catch (Exception e) { return null; }
    }

    @Override
    public Boolean delete(String key) {
        try { return redisTemplate.delete(key); }
        catch (Exception e) { return false; }
    }

    @Override
    public Boolean hasKey(String key) {
        try { return redisTemplate.hasKey(key); }
        catch (Exception e) { return false; }
    }

    @Override
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        try { return redisTemplate.expire(key, timeout, unit); }
        catch (Exception e) { return false; }
    }

    @Override
    public Long getExpire(String key) {
        try { return redisTemplate.getExpire(key); }
        catch (Exception e) { return 0L; }
    }

    @Override
    public Long sAdd(String key, Object... values) {
        try { return redisTemplate.opsForSet().add(key, values); }
        catch (Exception e) { return 0L; }
    }

    @Override
    public Long sRemove(String key, Object... values) {
        try { return redisTemplate.opsForSet().remove(key, values); }
        catch (Exception e) { return 0L; }
    }

    @Override
    public Set<Object> sMembers(String key) {
        try { return redisTemplate.opsForSet().members(key); }
        catch (Exception e) { return Collections.emptySet(); }
    }

    @Override
    public Long sSize(String key) {
        try { return redisTemplate.opsForSet().size(key); }
        catch (Exception e) { return 0L; }
    }

    @Override
    public Boolean sIsMember(String key, Object value) {
        try { return redisTemplate.opsForSet().isMember(key, value); }
        catch (Exception e) { return false; }
    }

    @Override
    public Long increment(String key) {
        try { return redisTemplate.opsForValue().increment(key); }
        catch (Exception e) { return 1L; }
    }

    @Override
    public Long incrementBy(String key, long delta) {
        try { return redisTemplate.opsForValue().increment(key, delta); }
        catch (Exception e) { return delta; }
    }

    @Override
    public void deleteByPattern(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        } catch (Exception e) { log.warn("Redis 不可用, deleteByPattern({}) 降级", pattern); }
    }
}
