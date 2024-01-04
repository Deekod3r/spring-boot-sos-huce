package com.project.soshuceapi.services;

import com.project.soshuceapi.common.Constants;
import com.project.soshuceapi.entities.Student;
import com.project.soshuceapi.exceptions.AuthenticationException;
import com.project.soshuceapi.models.DTOs.StudentDTO;
import com.project.soshuceapi.models.mappers.StudentMapper;
import com.project.soshuceapi.models.requests.LoginRequest;
import com.project.soshuceapi.security.JWTProvider;
import com.project.soshuceapi.services.iservice.IAuthService;
import com.project.soshuceapi.services.iservice.IStudentService;
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
    private IStudentService studentService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private JWTProvider jwtProvider;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private StudentMapper studentMapper;

    @Override
    public Map<String, Object> authenticate(LoginRequest loginRequest) {
        try {
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getStudentCode(), loginRequest.getPassword())
            );
            if (authenticate.isAuthenticated()) {
                Student student = studentMapper.mapFrom(studentService.getByStudentCode(loginRequest.getStudentCode()));
                String token = jwtProvider.generateToken(null, student);
                String refreshToken = jwtProvider.generateRefreshToken(null, student);
                revokeAllStudentTokens(student);
                saveUserToken(student, token);
                return Map.of("token",token,"refreshToken",refreshToken,"student",studentMapper.mapTo(student, StudentDTO.class));
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
            String studentCode = jwtProvider.extractStudentCode(jwtRefresh);
            if (studentCode != null) {
                Student student = studentMapper.mapFrom(studentService.getByStudentCode(studentCode));
                String key = Constants.Secutiry.TOKEN_HEADER_KEY + studentCode;
                String storedToken = (String) redisService.getDataFromRedis(key);
                if (storedToken == null && jwtProvider.isTokenValid(jwtRefresh, student)) {
                    String token = jwtProvider.generateToken(null, student);
                    saveUserToken(student, token);
                    response.setHeader(Constants.Secutiry.REQUEST_HEADER_AUTH, Constants.Secutiry.TOKEN_PREFIX + token);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void saveUserToken(Student student, String jwtToken) {
        redisService.saveDataToRedis(Constants.Secutiry.TOKEN_HEADER_KEY + student.getStudentCode(), jwtToken, Constants.Secutiry.TOKEN_EXPIRATION_TIME, TimeUnit.MILLISECONDS);
    }

    private void revokeAllStudentTokens(Student student) {
        redisService.deleteDataFromRedis(Constants.Secutiry.TOKEN_HEADER_KEY + student.getEmail());
    }

}
