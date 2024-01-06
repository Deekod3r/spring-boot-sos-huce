package com.project.soshuceapi.controllers;

import com.project.soshuceapi.common.ResponseCode;
import com.project.soshuceapi.common.ResponseMessage;
import com.project.soshuceapi.common.enums.security.ERole;
import com.project.soshuceapi.exceptions.AuthenticationException;
import com.project.soshuceapi.exceptions.StudentExistedException;
import com.project.soshuceapi.models.DTOs.StudentDTO;
import com.project.soshuceapi.models.requests.LoginRequest;
import com.project.soshuceapi.models.requests.StudentCreateRequest;
import com.project.soshuceapi.models.responses.Error;
import com.project.soshuceapi.models.responses.Response;
import com.project.soshuceapi.services.AuthService;
import com.project.soshuceapi.services.RedisService;
import com.project.soshuceapi.services.StudentService;
import com.project.soshuceapi.utils.DataUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200/")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private StudentService studentService;

    @PostMapping(value = "/login")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, BindingResult bindingResult) {
        Response<Map<String, Object>> response = new Response<>();
        try {
            if (bindingResult.hasErrors()) {
                response.setError(Error.of(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage(),
                        ResponseCode.Common.FAIL));
                return ResponseEntity.badRequest().body(response);
            }
            Map<String, Object> data = authService.authenticate(loginRequest);
            response.setSuccess(true);
            response.setData(data);
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
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> verify(@PathVariable("id") String id, @RequestParam("code") String code) {
        Response<StudentDTO> response = new Response<>();
        try {
            String verifyCode = (String) redisService.getDataFromRedis(id + "-REGISTER-CODE");
            if (verifyCode == null) {
                response.setError(Error.of(ResponseMessage.Common.NOT_FOUND,
                        ResponseCode.Authentication.VERIFY_CODE_EXPIRED));
                return ResponseEntity.status(HttpStatus.GONE).body(response);
            }
            if (!verifyCode.equals(code)) {
                response.setError(Error.of(ResponseMessage.Common.FAIL,
                        ResponseCode.Authentication.VERIFY_CODE_INCORRECT));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            String data = (String) redisService.getDataFromRedis(id + "-REGISTER-INFO");
            StudentCreateRequest request = DataUtil.fromJSON(data, StudentCreateRequest.class);
            request.setRole(ERole.STUDENT);
            request.setCreatedBy("SELF");
            response.setData(studentService.create(request));
            response.setSuccess(true);
            redisService.deleteDataFromRedis(id + "-REGISTER-CODE");
            redisService.deleteDataFromRedis(id + "-REGISTER-INFO");
            return ResponseEntity.ok(response);
        } catch (StudentExistedException e) {
            response.setError(Error.of(e.getMessage(), ResponseCode.Common.EXISTED));
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            response.setError(Error.of(e.getMessage(), ResponseCode.Common.FAIL));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/refresh-token")
    @PreAuthorize("permitAll()")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        authService.refreshToken(request, response);
    }

}
