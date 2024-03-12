package com.project.soshuceapi.controllers;

import com.project.soshuceapi.common.Constants;
import com.project.soshuceapi.common.ResponseMessage;
import com.project.soshuceapi.common.enums.security.ERole;
import com.project.soshuceapi.exceptions.BadRequestException;
import com.project.soshuceapi.exceptions.NotFoundException;
import com.project.soshuceapi.models.DTOs.UserDTO;
import com.project.soshuceapi.models.requests.UserCreateRequest;
import com.project.soshuceapi.models.requests.UserResetPasswordRequest;
import com.project.soshuceapi.models.responses.Response;
import com.project.soshuceapi.services.iservice.IAdoptService;
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
    private IAdoptService adoptService;
    @Autowired
    private AuditorAware<String> auditorAware;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserCreateRequest request,
                                      BindingResult bindingResult) {
        Response<Map<String, String>> response = new Response<>();
        response.setSuccess(false);
        try {
            if (bindingResult.hasErrors()) {
                response.setMessage(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
                return ResponseEntity.badRequest().body(response);
            }
            if (userService.isExistByPhoneNumberOrEmail(request.getPhoneNumber(), request.getEmail())) {
                response.setMessage(ResponseMessage.User.USER_EXISTED);
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
            response.setMessage(ResponseMessage.Common.SUCCESS);
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        } catch (BadRequestException e) {
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.setMessage(ResponseMessage.Common.SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/verify-register/{id}")
    public ResponseEntity<?> verifyRegister(@PathVariable("id") String id, @RequestParam("code") String code) {
        Response<UserDTO> response = new Response<>();
        response.setSuccess(false);
        try {
            if (StringUtil.isNullOrBlank(id) || StringUtil.isNullOrBlank(code)) {
                response.setMessage(ResponseMessage.User.MISSING_AUTHENTICATION_INFO);
                return ResponseEntity.badRequest().body(response);
            }
            String verifyCode = (String) redisService.getDataFromRedis(id + Constants.User.KEY_REGISTER_CODE);
            if (StringUtil.isNullOrBlank(verifyCode)) {
                response.setMessage(ResponseMessage.User.VERIFY_CODE_EXPIRED);
                return ResponseEntity.status(HttpStatus.GONE).body(response);
            }
            if (!verifyCode.equals(code)) {
                response.setMessage(ResponseMessage.User.VERIFY_CODE_INCORRECT);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            String data = (String) redisService.getDataFromRedis(id + Constants.User.KEY_REGISTER_INFO);
            if (Objects.isNull(data)) {
                response.setMessage(ResponseMessage.User.NOT_FOUND);
                return ResponseEntity.status(HttpStatus.GONE).body(response);
            }
            UserCreateRequest request = DataUtil.fromJSON(data, UserCreateRequest.class);
            request.setRole(ERole.USER);
            request.setCreatedBy("SELF");
            response.setData(userService.create(request));
            redisService.deleteDataFromRedis(id + Constants.User.KEY_REGISTER_CODE);
            redisService.deleteDataFromRedis(id + Constants.User.KEY_REGISTER_INFO);
            response.setSuccess(true);
            response.setMessage(ResponseMessage.Common.SUCCESS);
            return ResponseEntity.ok(response);
        } catch (BadRequestException e) {
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.setMessage(ResponseMessage.Common.SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/check-exist")
    public ResponseEntity<?> checkUserExist(@RequestParam(value = "account") String account) {
        Response<String> response = new Response<>();
        response.setSuccess(false);
        try {
            if (StringUtil.isNullOrBlank(account)) {
                response.setMessage(ResponseMessage.User.MISSING_AUTHENTICATION_INFO);
                return ResponseEntity.badRequest().body(response);
            }
            UserDTO user = userService.getByPhoneNumberOrEmail(account, account);
            response.setData(user.getEmail());
            response.setMessage(ResponseMessage.Common.SUCCESS);
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        } catch (NotFoundException e) {
            response.setData("NOT_FOUND");
            response.setMessage(ResponseMessage.User.NOT_FOUND);
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setMessage(ResponseMessage.Common.SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam(value = "email") String email) {
        Response<Map<String, String>> response = new Response<>();
        response.setSuccess(false);
        try {
            if (StringUtil.isNullOrBlank(email)) {
                response.setMessage(ResponseMessage.User.MISSING_EMAIL);
                return ResponseEntity.badRequest().body(response);
            }
            UserDTO user = userService.getByEmail(email);
            if (user == null) {
                response.setMessage(ResponseMessage.User.NOT_FOUND);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            String verifyCode = StringUtil.generateRandomString(Constants.Security.VERIFY_CODE_LENGTH);
            String key = String.valueOf(System.currentTimeMillis());
            emailService.sendMail(user.getEmail(), String.format(Constants.Mail.SUBJECT, "Password Reset"),
                    String.format(Constants.Mail.VERIFY_BODY, user.getEmail(), "đặt lại mật khẩu", verifyCode));
            redisService.saveDataToRedis(user.getPhoneNumber() + key + Constants.User.KEY_FORGOT_PASSWORD_CODE, verifyCode,
                    Constants.Security.VERIFICATION_EXPIRATION_TIME, TimeUnit.SECONDS);
            response.setData(Map.of("id", user.getPhoneNumber() + key));
            response.setMessage(ResponseMessage.Common.SUCCESS);
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        } catch (BadRequestException e) {
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.setMessage(ResponseMessage.Common.SERVER_ERROR);
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
                response.setMessage(ResponseMessage.User.MISSING_AUTHENTICATION_INFO);
                return ResponseEntity.badRequest().body(response);
            }
            String key = id + Constants.User.KEY_FORGOT_PASSWORD_CODE;
            String verifyCode = (String) redisService.getDataFromRedis(key);
            if (StringUtil.isNullOrBlank(verifyCode)) {
                response.setMessage(ResponseMessage.User.VERIFY_CODE_EXPIRED);
                return ResponseEntity.status(HttpStatus.GONE).body(response);
            }
            if (!verifyCode.equals(code)) {
                response.setMessage(ResponseMessage.User.VERIFY_CODE_INCORRECT);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            response.setData(Map.of("code", verifyCode));
            response.setMessage(ResponseMessage.Common.SUCCESS);
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        } catch (BadRequestException e) {
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.setMessage(ResponseMessage.Common.SERVER_ERROR);
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
                response.setMessage(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
                return ResponseEntity.badRequest().body(response);
            }
            String key = request.getId() + Constants.User.KEY_FORGOT_PASSWORD_CODE;
            String verifyCode = (String) redisService.getDataFromRedis(key);
            if (StringUtil.isNullOrBlank(verifyCode)) {
                response.setMessage(ResponseMessage.Authentication.VERIFY_CODE_EXPIRED);
                return ResponseEntity.status(HttpStatus.GONE).body(response);
            }
            if (!verifyCode.equals(request.getCode())) {
                response.setMessage(ResponseMessage.Authentication.VERIFY_CODE_INCORRECT);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            userService.updatePassword(request.getEmail(), request.getNewPassword(), "SELF");
            redisService.deleteDataFromRedis(key);
            response.setData(true);
            response.setSuccess(true);
            response.setMessage(ResponseMessage.Common.SUCCESS);
            return ResponseEntity.ok(response);
        } catch (BadRequestException e) {
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.setMessage(ResponseMessage.Common.SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable(value = "id") String id,
                                         @RequestParam(value = "role") String role) {
        Response<Map<String, Object>> response = new Response<>();
        response.setSuccess(false);
        try {
            if (StringUtil.isNullOrBlank(id) || StringUtil.isNullOrBlank(role)) {
                response.setMessage(ResponseMessage.User.MISSING_AUTHENTICATION_INFO);
                return ResponseEntity.badRequest().body(response);
            }
            if (auditorAware.getCurrentAuditor().isEmpty()) {
                response.setMessage(ResponseMessage.User.PERMISSION_DENIED);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            boolean roleExists = authorities.stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(Constants.User.KEY_ROLE + role));
            if (!roleExists) {
                response.setMessage(ResponseMessage.Authentication.PERMISSION_DENIED);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            if (Objects.equals(role, Constants.User.ROLE_USER)) {
                if (!auditorAware.getCurrentAuditor().get().equals(id)) {
                    response.setMessage(ResponseMessage.Authentication.PERMISSION_DENIED);
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
            response.setMessage(ResponseMessage.Common.SUCCESS);
            return ResponseEntity.ok(response);
        } catch (BadRequestException e) {
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.setMessage(ResponseMessage.Common.SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') || hasRole('MANAGER')")
    public ResponseEntity<?> getUsers(@RequestParam(value = "page", defaultValue = "1", required = false) Integer page,
                                      @RequestParam(value = "size", defaultValue = "1000000", required = false) Integer limit,
                                      @RequestParam(value = "name", defaultValue = "", required = false) String name,
                                      @RequestParam(value = "email", defaultValue = "", required = false) String email,
                                      @RequestParam(value = "phoneNumber", defaultValue = "", required = false) String phoneNumber,
                                      @RequestParam(value = "isActivated", defaultValue = "", required = false) Boolean isActivated,
                                      @RequestParam(value = "role", defaultValue = "USER", required = false) String role,
                                      @RequestParam(value = "roleRequest", defaultValue = "", required = false) String roleRequest) {
        Response<Map<String, Object>> response = new Response<>();
        response.setSuccess(false);
        try {
            if (auditorAware.getCurrentAuditor().isEmpty()) {
                response.setMessage(ResponseMessage.User.PERMISSION_DENIED);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            boolean roleExists = authorities.stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(Constants.User.KEY_ROLE + roleRequest));
            if (!roleExists) {
                response.setMessage(ResponseMessage.Authentication.PERMISSION_DENIED);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            if (Objects.equals(roleRequest, Constants.User.ROLE_ADMIN)) {
                role = Constants.User.ROLE_USER;
            }
            response.setData(userService.getAll(page, limit, name, email, phoneNumber, isActivated, role));
            response.setMessage(ResponseMessage.Common.SUCCESS);
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        } catch (BadRequestException e) {
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.setMessage(ResponseMessage.Common.SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
