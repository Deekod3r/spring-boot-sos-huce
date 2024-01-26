package com.project.soshuceapi.services;

import com.project.soshuceapi.common.Constants;
import com.project.soshuceapi.entities.User;
import com.project.soshuceapi.exceptions.AuthenticationException;
import com.project.soshuceapi.models.DTOs.UserDTO;
import com.project.soshuceapi.models.mappers.UserMapper;
import com.project.soshuceapi.models.requests.LoginRequest;
import com.project.soshuceapi.security.JWTProvider;
import com.project.soshuceapi.services.iservice.IAuthService;
import com.project.soshuceapi.services.iservice.IRedisService;
import com.project.soshuceapi.services.iservice.IUserService;
import com.project.soshuceapi.services.iservice.IWebSocketService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService implements IAuthService {

    private final static String TAG = "AUTH";

    @Autowired
    private IUserService userService;
    @Autowired
    private IRedisService redisService;
    @Autowired
    private JWTProvider jwtProvider;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private IWebSocketService webSocketService;
    @Autowired
    private UserMapper userMapper;

    @Override
    public Map<String, Object> authenticate(LoginRequest loginRequest) {
        try {
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
            if (authenticate.isAuthenticated()) {
                User user = userMapper.mapFrom(userService.getByEmail(loginRequest.getEmail()));
                String token = jwtProvider.generateToken(null, user);
                String refreshToken = jwtProvider.generateRefreshToken(null, user);
                revokeAllUserTokens(user);
                saveUserToken(user, token);
                webSocketService.sendLogoutMessage(jwtProvider.extractEmail(token));
                return Map.of("token",token,"refreshToken",refreshToken,"user", userMapper.mapTo(user, UserDTO.class));
            }
            return null;
        } catch (org.springframework.security.core.AuthenticationException authenticationException){
            throw new AuthenticationException(authenticationException.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            final String authHeader = request.getHeader(Constants.Secutiry.REQUEST_HEADER_AUTH);
            if (authHeader == null || !authHeader.startsWith(Constants.Secutiry.TOKEN_PREFIX)) {
                return;
            }
            String jwtRefresh = authHeader.substring(Constants.Secutiry.TOKEN_PREFIX.length());
            String email = jwtProvider.extractEmail(jwtRefresh);
            if (email != null) {
                User user = userMapper.mapFrom(userService.getByEmail(email));
                String key = Constants.Secutiry.TOKEN_HEADER_KEY + email;
                String storedToken = (String) redisService.getDataFromRedis(key);
                if (storedToken == null && jwtProvider.isTokenValid(jwtRefresh, user)) {
                    String token = jwtProvider.generateToken(null, user);
                    saveUserToken(user, token);
                    response.setHeader(Constants.Secutiry.REQUEST_HEADER_AUTH, Constants.Secutiry.TOKEN_PREFIX + token);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void saveUserToken(User user, String jwtToken) {
        redisService.saveDataToRedis(Constants.Secutiry.TOKEN_HEADER_KEY + user.getEmail(), jwtToken, Constants.Secutiry.TOKEN_EXPIRATION_TIME, TimeUnit.MILLISECONDS);
    }

    private void revokeAllUserTokens(User user) {
        redisService.deleteDataFromRedis(Constants.Secutiry.TOKEN_HEADER_KEY + user.getEmail());
    }

}
