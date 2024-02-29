package com.project.soshuceapi.controllers;

import com.project.soshuceapi.common.Constants;
import com.project.soshuceapi.common.ResponseCode;
import com.project.soshuceapi.common.ResponseMessage;
import com.project.soshuceapi.common.enums.security.ERole;
import com.project.soshuceapi.exceptions.AuthenticationException;
import com.project.soshuceapi.exceptions.UserExistedException;
import com.project.soshuceapi.models.DTOs.UserDTO;
import com.project.soshuceapi.models.requests.LoginRequest;
import com.project.soshuceapi.models.requests.UserCreateRequest;
import com.project.soshuceapi.models.responses.Error;
import com.project.soshuceapi.models.responses.Response;
import com.project.soshuceapi.services.iservice.IAuthService;
import com.project.soshuceapi.services.iservice.IRedisService;
import com.project.soshuceapi.services.iservice.IUserService;
import com.project.soshuceapi.utils.DataUtil;
import com.project.soshuceapi.utils.StringUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/verify/{id}")
    public ResponseEntity<?> verify(@PathVariable("id") String id, @RequestParam("code") String code) {
        Response<UserDTO> response = new Response<>();
        response.setSuccess(false);
        try {
            if (StringUtil.isNullOrBlank(id) || StringUtil.isNullOrBlank(code)) {
                response.setError(Error.of(ResponseMessage.Common.INVALID_INPUT, ResponseCode.Common.INVALID));
                return ResponseEntity.badRequest().body(response);
            }
            String verifyCode = (String) redisService.getDataFromRedis(id + Constants.User.KEY_REGISTER_CODE);
            if (verifyCode == null) {
                response.setError(Error.of(ResponseMessage.Authentication.VERIFY_CODE_EXPIRED,
                        ResponseCode.Authentication.VERIFY_CODE_EXPIRED));
                return ResponseEntity.status(HttpStatus.GONE).body(response);
            }
            if (!verifyCode.equals(code)) {
                response.setError(Error.of(ResponseMessage.Authentication.VERIFY_CODE_INCORRECT,
                        ResponseCode.Authentication.VERIFY_CODE_INCORRECT));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            String data = (String) redisService.getDataFromRedis(id + Constants.User.KEY_REGISTER_INFO);
            UserCreateRequest request = DataUtil.fromJSON(data, UserCreateRequest.class);
            request.setRole(ERole.USER);
            request.setCreatedBy("SELF");
            redisService.deleteDataFromRedis(id + Constants.User.KEY_REGISTER_CODE);
            redisService.deleteDataFromRedis(id + Constants.User.KEY_REGISTER_INFO);
            response.setData(userService.create(request));
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        } catch (UserExistedException e) {
            response.setError(Error.of(ResponseMessage.Common.EXISTED, ResponseCode.Common.EXISTED));
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
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
