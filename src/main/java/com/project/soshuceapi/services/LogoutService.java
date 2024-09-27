package com.project.soshuceapi.services;

import com.project.soshuceapi.common.Constants;
import com.project.soshuceapi.security.JWTProvider;
import com.project.soshuceapi.services.iservice.IRedisService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
public class LogoutService implements LogoutHandler {

    private static final String TAG = "LOGOUT";

    @Autowired
    private IRedisService redisService;
    @Autowired
    private JWTProvider jwtProvider;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try {
            final String authHeader = request.getHeader(Constants.Security.REQUEST_HEADER_AUTH);
            if (Objects.isNull(authHeader) || !authHeader.startsWith(Constants.Security.TOKEN_PREFIX)) {
                return;
            }
            String jwt = authHeader.substring(Constants.Security.TOKEN_PREFIX.length());
            String key = Constants.Security.TOKEN_HEADER_KEY + jwtProvider.extractEmail(jwt);
            String storedToken = (String) redisService.getDataFromRedis(key);
            if (Objects.nonNull(storedToken)) {
                redisService.deleteDataFromRedis(key);
                SecurityContextHolder.clearContext();
            }
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

}
