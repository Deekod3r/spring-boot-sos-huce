package com.project.soshuceapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.hateoas.config.EnableHypermediaSupport;

@SpringBootApplication(exclude = RedisRepositoriesAutoConfiguration.class)
public class SOSHUCEAPIApplication {

    public static void main(String[] args) {
        SpringApplication.run(SOSHUCEAPIApplication.class, args);
    }

}
