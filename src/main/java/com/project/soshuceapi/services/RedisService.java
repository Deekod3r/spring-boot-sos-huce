package com.project.soshuceapi.services;

import com.project.soshuceapi.services.iservice.IRedisService;
import io.lettuce.core.RedisException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedisService implements IRedisService {

    private static final String TAG = "REDIS";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void saveDataToRedis(String key, Object value, Long expirationTime, TimeUnit timeUnit) {
        try {
            ValueOperations<String, Object> valueOps = redisTemplate.opsForValue();
            valueOps.set(key, value, expirationTime, timeUnit);
        } catch (Exception e) {
            log.error(TAG + ": error.save.data.redis");
            log.error(TAG + ": " + e.getMessage());
            throw new RedisException(e.getMessage());
        }
    }

    @Override
    public Object getDataFromRedis(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error(TAG + ": error.get.data.redis");
            log.error(TAG + ": " + e.getMessage());
            throw new RedisException(e.getMessage());
        }
    }

    @Override
    public void deleteDataFromRedis(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error(TAG + ": error.delete.data.redis");
            log.error(TAG + ": " + e.getMessage());
            throw new RedisException(e.getMessage());
        }
    }

}
