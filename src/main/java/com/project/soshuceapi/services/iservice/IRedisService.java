package com.project.soshuceapi.services.iservice;

import java.util.concurrent.TimeUnit;

public interface IRedisService {

    void saveDataToRedis(String key, Object value, Long expirationTime, TimeUnit timeUnit);

    Object getDataFromRedis(String key);

    void deleteDataFromRedis(String key);

}
