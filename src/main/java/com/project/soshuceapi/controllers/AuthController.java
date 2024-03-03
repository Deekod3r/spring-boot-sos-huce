package com.project.soshuceapi.controllers;

import com.project.soshuceapi.common.ResponseCode;
import com.project.soshuceapi.common.ResponseMessage;
import com.project.soshuceapi.exceptions.AuthenticationException;
import com.project.soshuceapi.models.requests.LoginRequest;
import com.project.soshuceapi.models.responses.Error;
import com.project.soshuceapi.models.responses.Response;
import com.project.soshuceapi.services.iservice.IAuthService;
import com.project.soshuceapi.services.iservice.IRedisService;
import com.project.soshuceapi.services.iservice.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;

@RestController
public class AuthController {

    @Autowired
    private IAuthService authService;
    @Autowired
    private IRedisService redisService;
    @Autowired
    private IUserService userService;

    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, BindingResult bindingResult) {
        Response<Map<String, Object>> response = new Response<>();
        response.setSuccess(false);
        try {
            if (bindingResult.hasErrors()) {
                response.setError(Error.of(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage(),
                        ResponseCode.Common.INVALID));
                return ResponseEntity.badRequest().body(response);
            }
            Map<String, Object> data = authService.authenticate(loginRequest);
            response.setData(data);
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            response.setError(Error.of(ResponseMessage.Authentication.AUTHENTICATION_ERROR,
                    ResponseCode.Authentication.AUTHENTICATION_ERROR));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            response.setError(Error.of(e.getMessage(), ResponseCode.Common.FAIL));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        authService.refreshToken(request, response);
    }

}
