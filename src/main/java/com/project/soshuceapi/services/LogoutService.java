package com.project.soshuceapi.services;

import com.project.soshuceapi.common.Constants;
import com.project.soshuceapi.security.JWTProvider;
import com.project.soshuceapi.services.iservice.IRedisService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
public class LogoutService implements LogoutHandler {

    private final static String TAG = "LOGOUT";

    @Autowired
    private IRedisService redisService;
    @Autowired
    private JWTProvider jwtProvider;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try {
            final String authHeader = request.getHeader(Constants.Secutiry.REQUEST_HEADER_AUTH);
            if (authHeader == null || !authHeader.startsWith(Constants.Secutiry.TOKEN_PREFIX)) {
                return;
            }
            String jwt = authHeader.substring(Constants.Secutiry.TOKEN_PREFIX.length());
            String key = Constants.Secutiry.TOKEN_HEADER_KEY + jwtProvider.extractEmail(jwt);
            String storedToken = (String) redisService.getDataFromRedis(key);
            if (storedToken != null) {
                redisService.deleteDataFromRedis(key);
                SecurityContextHolder.clearContext();
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
