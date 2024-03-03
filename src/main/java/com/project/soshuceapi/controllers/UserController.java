package com.project.soshuceapi.controllers;

import com.project.soshuceapi.common.Constants;
import com.project.soshuceapi.common.ResponseCode;
import com.project.soshuceapi.common.ResponseMessage;
import com.project.soshuceapi.common.enums.security.ERole;
import com.project.soshuceapi.exceptions.NotFoundException;
import com.project.soshuceapi.exceptions.UserExistedException;
import com.project.soshuceapi.models.DTOs.UserDTO;
import com.project.soshuceapi.models.requests.UserCreateRequest;
import com.project.soshuceapi.models.requests.UserResetPasswordRequest;
import com.project.soshuceapi.models.responses.Error;
import com.project.soshuceapi.models.responses.Response;
import com.project.soshuceapi.services.AdoptService;
import com.project.soshuceapi.services.iservice.IEmailService;
import com.project.soshuceapi.services.iservice.IRedisService;
import com.project.soshuceapi.services.iservice.IUserService;
import com.project.soshuceapi.utils.DataUtil;
import com.project.soshuceapi.utils.StringUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUserService userService;
    @Autowired
    private IRedisService redisService;
    @Autowired
    private IEmailService emailService;
    @Autowired
    private AdoptService adoptService;
    @Autowired
    private AuditorAware<String> auditorAware;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserCreateRequest request,
                                      BindingResult bindingResult) {
        Response<Map<String, String>> response = new Response<>();
        response.setSuccess(false);
        try {
            if (bindingResult.hasErrors()) {
                response.setError(Error.of(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage(),
                        ResponseCode.Common.INVALID));
                return ResponseEntity.badRequest().body(response);
            }
            if (userService.isExistByPhoneNumberOrEmail(request.getPhoneNumber(), request.getEmail())) {
                response.setError(Error.of(ResponseMessage.Common.EXISTED, ResponseCode.Common.EXISTED));
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            String verifyCode = StringUtil.generateRandomString(Constants.Security.VERIFY_CODE_LENGTH);
            String key = String.valueOf(System.currentTimeMillis());
            emailService.sendMail(request.getEmail(), String.format(Constants.Mail.SUBJECT, "Email Verification"),
                    String.format(Constants.Mail.VERIFY_BODY, request.getEmail(), "đăng ký tài khoản", verifyCode));
            redisService.saveDataToRedis(request.getPhoneNumber() + key + Constants.User.KEY_REGISTER_INFO, DataUtil.toJSON(request),
                    Constants.Security.VERIFICATION_EXPIRATION_TIME, TimeUnit.SECONDS);
            redisService.saveDataToRedis(request.getPhoneNumber() + key + Constants.User.KEY_REGISTER_CODE, verifyCode,
                    Constants.Security.VERIFICATION_EXPIRATION_TIME, TimeUnit.SECONDS);
            response.setData(Map.of("id", request.getPhoneNumber() + key));
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setError(Error.of(e.getMessage(), ResponseCode.Common.FAIL));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/verify-register/{id}")
    public ResponseEntity<?> verifyRegister(@PathVariable("id") String id, @RequestParam("code") String code) {
        Response<UserDTO> response = new Response<>();
        response.setSuccess(false);
        try {
            if (StringUtil.isNullOrBlank(id) || StringUtil.isNullOrBlank(code)) {
                response.setError(Error.of(ResponseMessage.Common.INVALID_INPUT, ResponseCode.Common.INVALID));
                return ResponseEntity.badRequest().body(response);
            }
            String verifyCode = (String) redisService.getDataFromRedis(id + Constants.User.KEY_REGISTER_CODE);
            if (StringUtil.isNullOrBlank(verifyCode)) {
                response.setError(Error.of(ResponseMessage.Authentication.VERIFY_CODE_EXPIRED,
                        ResponseCode.Authentication.VERIFY_CODE_EXPIRED));
                return ResponseEntity.status(HttpStatus.GONE).body(response);
            }
            if (!verifyCode.equals(code)) {
                response.setError(Error.of(ResponseMessage.Authentication.VERIFY_CODE_INCORRECT,
                        ResponseCode.Authentication.VERIFY_CODE_INCORRECT));
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            String data = (String) redisService.getDataFromRedis(id + Constants.User.KEY_REGISTER_INFO);
            if (Objects.isNull(data)) {
                response.setError(Error.of(ResponseMessage.Common.NOT_FOUND,
                        ResponseCode.Common.NOT_FOUND));
                return ResponseEntity.status(HttpStatus.GONE).body(response);
            }
            UserCreateRequest request = DataUtil.fromJSON(data, UserCreateRequest.class);
            request.setRole(ERole.USER);
            request.setCreatedBy("SELF");
            response.setData(userService.create(request));
            redisService.deleteDataFromRedis(id + Constants.User.KEY_REGISTER_CODE);
            redisService.deleteDataFromRedis(id + Constants.User.KEY_REGISTER_INFO);
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

    @GetMapping("/check-exist")
    public ResponseEntity<?> checkUserExist(@RequestParam(value = "account") String account) {
        Response<String> response = new Response<>();
        response.setSuccess(false);
        try {
            if (StringUtil.isNullOrBlank(account)) {
                response.setError(Error.of(ResponseMessage.Common.INVALID_INPUT, ResponseCode.Common.INVALID));
                return ResponseEntity.badRequest().body(response);
            }
            UserDTO user = userService.getByPhoneNumberOrEmail(account, account);
            response.setData(user.getEmail());
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        } catch (NotFoundException e) {
            response.setData("NOT_FOUND");
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setError(Error.of(e.getMessage(), ResponseCode.Common.FAIL));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam(value = "email") String email) {
        Response<Map<String, String>> response = new Response<>();
        response.setSuccess(false);
        try {
            if (StringUtil.isNullOrBlank(email)) {
                response.setError(Error.of(ResponseMessage.Common.INVALID_INPUT, ResponseCode.Common.INVALID));
                return ResponseEntity.badRequest().body(response);
            }
            UserDTO user = userService.getByEmail(email);
            if (user == null) {
                response.setError(Error.of(ResponseMessage.Common.NOT_FOUND, ResponseCode.Common.NOT_FOUND));
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            String verifyCode = StringUtil.generateRandomString(Constants.Security.VERIFY_CODE_LENGTH);
            String key = String.valueOf(System.currentTimeMillis());
            emailService.sendMail(user.getEmail(), String.format(Constants.Mail.SUBJECT, "Password Reset"),
                    String.format(Constants.Mail.VERIFY_BODY, user.getEmail(), "đặt lại mật khẩu", verifyCode));
            redisService.saveDataToRedis(user.getPhoneNumber() + key + Constants.User.KEY_FORGOT_PASSWORD_CODE, verifyCode,
                    Constants.Security.VERIFICATION_EXPIRATION_TIME, TimeUnit.SECONDS);
            response.setData(Map.of("id", user.getPhoneNumber() + key));
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setError(Error.of(e.getMessage(), ResponseCode.Common.FAIL));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/verify-forgot-password/{id}")
    public ResponseEntity<?> verifyForgotPassword(@PathVariable(value = "id") String id,
                                                  @RequestParam(value = "code") String code) {
        Response<Map<String, String>> response = new Response<>();
        response.setSuccess(false);
        try {
            if (StringUtil.isNullOrBlank(id) || StringUtil.isNullOrBlank(code)) {
                response.setError(Error.of(ResponseMessage.Common.INVALID_INPUT, ResponseCode.Common.INVALID));
                return ResponseEntity.badRequest().body(response);
            }
            String key = id + Constants.User.KEY_FORGOT_PASSWORD_CODE;
            String verifyCode = (String) redisService.getDataFromRedis(key);
            if (StringUtil.isNullOrBlank(verifyCode)) {
                response.setError(Error.of(ResponseMessage.Authentication.VERIFY_CODE_EXPIRED,
                        ResponseCode.Authentication.VERIFY_CODE_EXPIRED));
                return ResponseEntity.status(HttpStatus.GONE).body(response);
            }
            if (!verifyCode.equals(code)) {
                response.setError(Error.of(ResponseMessage.Authentication.VERIFY_CODE_INCORRECT,
                        ResponseCode.Authentication.VERIFY_CODE_INCORRECT));
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            response.setData(Map.of("code", verifyCode));
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setError(Error.of(e.getMessage(), ResponseCode.Common.FAIL));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody UserResetPasswordRequest request,
                                           BindingResult bindingResult) {
        Response<Boolean> response = new Response<>();
        response.setSuccess(false);
        try {
            if (bindingResult.hasErrors()) {
                response.setError(Error.of(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage(),
                        ResponseCode.Common.INVALID));
                return ResponseEntity.badRequest().body(response);
            }
            String key = request.getId() + Constants.User.KEY_FORGOT_PASSWORD_CODE;
            String verifyCode = (String) redisService.getDataFromRedis(key);
            if (StringUtil.isNullOrBlank(verifyCode)) {
                response.setError(Error.of(ResponseMessage.Authentication.VERIFY_CODE_EXPIRED,
                        ResponseCode.Authentication.VERIFY_CODE_EXPIRED));
                return ResponseEntity.status(HttpStatus.GONE).body(response);
            }
            if (!verifyCode.equals(request.getCode())) {
                response.setError(Error.of(ResponseMessage.Authentication.VERIFY_CODE_INCORRECT,
                        ResponseCode.Authentication.VERIFY_CODE_INCORRECT));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            userService.updatePassword(request.getEmail(), request.getNewPassword(), "SELF");
            redisService.deleteDataFromRedis(key);
            response.setData(true);
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setError(Error.of(e.getMessage(), ResponseCode.Common.FAIL));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable(value = "id") String id,
                                         @RequestParam(value = "role") String role) {
        Response<Map<String, Object>> response = new Response<>();
        response.setSuccess(false);
        try {
            if (StringUtil.isNullOrBlank(id)) {
                response.setError(Error.of(ResponseMessage.Common.INVALID_INPUT, ResponseCode.Common.INVALID));
                return ResponseEntity.badRequest().body(response);
            }
            if (auditorAware.getCurrentAuditor().isEmpty()) {
                response.setError(Error.of(ResponseMessage.Authentication.PERMISSION_DENIED,
                        ResponseCode.Authentication.PERMISSION_DENIED));
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            boolean roleExists = authorities.stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(Constants.User.KEY_ROLE + role));
            if (!roleExists) {
                response.setError(Error.of(ResponseMessage.Authentication.PERMISSION_DENIED,
                        ResponseCode.Authentication.PERMISSION_DENIED));
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            if (Objects.equals(role, Constants.User.ROLE_USER)) {
                if (!auditorAware.getCurrentAuditor().get().equals(id)) {
                    response.setError(Error.of(ResponseMessage.Authentication.PERMISSION_DENIED,
                            ResponseCode.Authentication.PERMISSION_DENIED));
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
                }
            }
            Map<String, Object> data = new HashMap<>();
            UserDTO user = userService.getById(id);
            Map<String, Long> statistic = adoptService.statisticStatus(id);
            data.put("user", user);
            data.put("statistic", statistic);
            response.setData(data);
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        } catch (NotFoundException e) {
            response.setError(Error.of(ResponseMessage.Common.NOT_FOUND, ResponseCode.Common.NOT_FOUND));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.setError(Error.of(e.getMessage(), ResponseCode.Common.FAIL));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') || hasRole('MANAGER')")
    public ResponseEntity<?> getUsers(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                      @RequestParam(value = "size", defaultValue = "10") Integer limit,
                                      @RequestParam(value = "name", defaultValue = "") String name,
                                      @RequestParam(value = "email", defaultValue = "") String email,
                                      @RequestParam(value = "phoneNumber", defaultValue = "") String phoneNumber,
                                      @RequestParam(value = "role", defaultValue = "") String role) {
        Response<Map<String, Object>> response = new Response<>();
        response.setSuccess(false);
        try {
            response.setData(userService.getAll(page, limit, name, email, phoneNumber, role));
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setError(Error.of(e.getMessage(), ResponseCode.Common.FAIL));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
