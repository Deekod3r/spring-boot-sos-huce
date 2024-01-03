package com.project.soshuceapi.services;

import com.project.soshuceapi.services.iservice.IRedisService;
import io.lettuce.core.RedisException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService implements IRedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void saveDataToRedis(String key, Object value, Long expirationTime, TimeUnit timeUnit) {
        try {
            ValueOperations<String, Object> valueOps = redisTemplate.opsForValue();
            valueOps.set(key, value, expirationTime, timeUnit);
        } catch (Exception e) {
            throw new RedisException("error.save.data.redis");
        }
    }

    @Override
    public Object getDataFromRedis(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            throw new RedisException("error.get.data.redis");
        }
    }

    @Override
    public void deleteDataFromRedis(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            throw new RuntimeException("error.delete.data.redis");
        }
    }

}
