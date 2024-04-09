package com.project.soshuceapi.controllers;

import com.project.soshuceapi.common.ResponseMessage;
import com.project.soshuceapi.exceptions.AuthException;
import com.project.soshuceapi.exceptions.BadRequestException;
import com.project.soshuceapi.models.requests.LoginRequest;
import com.project.soshuceapi.models.responses.Response;
import com.project.soshuceapi.services.iservice.IAuthService;
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

    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, BindingResult bindingResult) {
        Response<Map<String, Object>> response = new Response<>();
        response.setSuccess(false);
        try {
            if (bindingResult.hasErrors()) {
                response.setMessage(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
                return ResponseEntity.badRequest().body(response);
            }
            Map<String, Object> data = authService.authenticate(loginRequest);
            response.setData(data);
            response.setSuccess(true);
            response.setMessage(ResponseMessage.Common.SUCCESS);
            return ResponseEntity.ok(response);
        } catch (AuthException e) {
            response.setMessage(ResponseMessage.User.LOGIN_INFO_INCORRECT);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (BadRequestException e) {
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.setMessage(ResponseMessage.Common.SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        authService.refreshToken(request, response);
    }

}
