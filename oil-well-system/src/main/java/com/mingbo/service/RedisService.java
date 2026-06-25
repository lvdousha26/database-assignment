package com.mingbo.service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface RedisService {

    void set(String key, Object value);

    void set(String key, Object value, long timeout, TimeUnit unit);

    Object get(String key);

    Boolean delete(String key);

    Boolean hasKey(String key);

    Boolean expire(String key, long timeout, TimeUnit unit);

    Long getExpire(String key);

    Long sAdd(String key, Object... values);

    Long sRemove(String key, Object... values);

    Set<Object> sMembers(String key);

    Long sSize(String key);

    Boolean sIsMember(String key, Object value);

    Long increment(String key);

    Long incrementBy(String key, long delta);

    void deleteByPattern(String pattern);
}
