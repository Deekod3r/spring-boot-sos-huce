package com.project.soshuceapi.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Getter
@Configuration
public class ResourceConfig {

    @Value("${jwt.secret-key}")
    private String secretKey;
    @Value("${application.name}")
    private String applicationName;
    @Value("${application.version}")
    private String applicationVersion;
    @Value("${firebase.bucket.name}")
    private String firebaseBucketName;

}
