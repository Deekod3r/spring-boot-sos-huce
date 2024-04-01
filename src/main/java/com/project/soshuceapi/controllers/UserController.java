package com.project.soshuceapi.controllers;

import com.project.soshuceapi.common.Constants;
import com.project.soshuceapi.common.ResponseMessage;
import com.project.soshuceapi.common.enums.security.ERole;
import com.project.soshuceapi.exceptions.BadRequestException;
import com.project.soshuceapi.models.DTOs.UserDTO;
import com.project.soshuceapi.models.requests.*;
import com.project.soshuceapi.models.responses.Response;
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
        Response<Boolean> response = new Response<>();
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
            if (!Objects.equals(verifyCode, code)) {
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
            userService.create(request);
            response.setData(true);
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
            if (Objects.isNull(user)) {
                response.setData("NOT_FOUND");
                response.setMessage(ResponseMessage.User.NOT_FOUND);
                response.setSuccess(true);
                return ResponseEntity.ok(response);
            }
            response.setData(user.getEmail());
            response.setMessage(ResponseMessage.Common.SUCCESS);
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
            if (Objects.isNull(user)) {
                response.setMessage(ResponseMessage.User.NOT_FOUND);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            String verifyCode = StringUtil.generateRandomString(Constants.Security.VERIFY_CODE_LENGTH);
            String key = String.valueOf(System.currentTimeMillis());
            emailService.sendMail(user.getEmail(), String.format(Constants.Mail.SUBJECT, "Password Reset"),
                    String.format(Constants.Mail.FORGOT_PASSWORD_BODY, user.getEmail(), "đặt lại mật khẩu", verifyCode));
            redisService.saveDataToRedis(user.getPhoneNumber() + key + Constants.User.KEY_FORGOT_PASSWORD_CODE, verifyCode,
                    Constants.Security.VERIFICATION_EXPIRATION_TIME, TimeUnit.SECONDS);
            response.setData(Map.of("id", user.getPhoneNumber() + key));
            response.setMessage(ResponseMessage.Common.SUCCESS);
            response.setSuccess(true);
            return ResponseEntity.ok(response);
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
            if (!Objects.equals(verifyCode, code)) {
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
            if (!Objects.equals(verifyCode, request.getCode())) {
                response.setMessage(ResponseMessage.Authentication.VERIFY_CODE_INCORRECT);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            request.setUpdatedBy("SELF");
            userService.resetPassword(request);
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
        Response<UserDTO> response = new Response<>();
        response.setSuccess(false);
        try {
            if (auditorAware.getCurrentAuditor().isEmpty()) {
                response.setMessage(ResponseMessage.Authentication.PERMISSION_DENIED);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            if (StringUtil.isNullOrBlank(id) || StringUtil.isNullOrBlank(role)) {
                response.setMessage(ResponseMessage.User.MISSING_AUTHENTICATION_INFO);
                return ResponseEntity.badRequest().body(response);
            }
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            boolean roleExists = authorities.stream()
                    .anyMatch(grantedAuthority
                            -> Objects.equals(grantedAuthority.getAuthority(), Constants.User.KEY_ROLE + role));
            if (!roleExists) {
                response.setMessage(ResponseMessage.Authentication.PERMISSION_DENIED);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            if (Objects.equals(role, Constants.User.ROLE_USER)) {
                if (!Objects.equals(auditorAware.getCurrentAuditor().get(), id)) {
                    response.setMessage(ResponseMessage.Authentication.PERMISSION_DENIED);
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
                }
            }
            UserDTO user = userService.getById(id);
            response.setData(user);
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
                                      @RequestParam(value = "role", defaultValue = "", required = false) String role,
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
                    .anyMatch(grantedAuthority
                            -> Objects.equals(grantedAuthority.getAuthority(), Constants.User.KEY_ROLE + roleRequest));
            if (!roleExists) {
                response.setMessage(ResponseMessage.Authentication.PERMISSION_DENIED);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            if (Objects.equals(roleRequest, Constants.User.ROLE_ADMIN)) {
                role = Constants.User.ROLE_USER;
            }
            response.setData(userService.getAll(UserSearchRequest.of(name, email, phoneNumber, role, isActivated, page, limit)));
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

    @PutMapping("/update/name/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateName(@PathVariable(value = "id") String id,
                                        @Valid @RequestBody UserUpdateNameRequest request,
                                        BindingResult bindingResult) {
        Response<Boolean> response = new Response<>();
        response.setSuccess(false);
        try {
            if (auditorAware.getCurrentAuditor().isEmpty()) {
                response.setMessage(ResponseMessage.Authentication.PERMISSION_DENIED);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            if (bindingResult.hasErrors()) {
                response.setMessage(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
                return ResponseEntity.badRequest().body(response);
            }
            if (!Objects.equals(auditorAware.getCurrentAuditor().get(), id)) {
                response.setMessage(ResponseMessage.Authentication.PERMISSION_DENIED);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            request.setUpdatedBy(id);
            userService.updateName(request);
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

    @PutMapping("/update/phone/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updatePhone(@PathVariable(value = "id") String id,
                                         @Valid @RequestBody UserUpdatePhoneRequest request,
                                         BindingResult bindingResult) {
        Response<Boolean> response = new Response<>();
        response.setSuccess(false);
        try {
            if (auditorAware.getCurrentAuditor().isEmpty()) {
                response.setMessage(ResponseMessage.Authentication.PERMISSION_DENIED);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            if (bindingResult.hasErrors()) {
                response.setMessage(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
                return ResponseEntity.badRequest().body(response);
            }
            if (!Objects.equals(auditorAware.getCurrentAuditor().get(), id)) {
                response.setMessage(ResponseMessage.Authentication.PERMISSION_DENIED);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            request.setUpdatedBy(id);
            userService.updatePhone(request);
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

    @PutMapping("/update/email/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateEmail(@PathVariable(value = "id") String id,
                                         @Valid @RequestBody UserUpdateEmailRequest request,
                                         BindingResult bindingResult) {
        Response<Map<String, String>> response = new Response<>();
        response.setSuccess(false);
        try {
            if (auditorAware.getCurrentAuditor().isEmpty()) {
                response.setMessage(ResponseMessage.Authentication.PERMISSION_DENIED);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            if (bindingResult.hasErrors()) {
                response.setMessage(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
                return ResponseEntity.badRequest().body(response);
            }
            if (!Objects.equals(auditorAware.getCurrentAuditor().get(), id)) {
                response.setMessage(ResponseMessage.Authentication.PERMISSION_DENIED);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            UserDTO user = userService.getByEmail(request.getEmail().trim());
            if (Objects.nonNull(user) && !Objects.equals(user.getId(), id)) {
                response.setMessage(ResponseMessage.User.USER_EXISTED);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            if (!userService.checkPassword(id, request.getCurrentPassword())) {
                response.setMessage(ResponseMessage.User.INVALID_PASSWORD);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            String verifyCode = StringUtil.generateRandomString(Constants.Security.VERIFY_CODE_LENGTH);
            String key = String.valueOf(System.currentTimeMillis());
            emailService.sendMail(request.getEmail().trim(), String.format(Constants.Mail.SUBJECT, "Email Verification"),
                    String.format(Constants.Mail.UPDATE_EMAIL_BODY, request.getEmail().trim(), "đổi email", verifyCode));
            redisService.saveDataToRedis(request.getId() + key + Constants.User.KEY_UPDATE_EMAIL_INFO, DataUtil.toJSON(request),
                    Constants.Security.VERIFICATION_EXPIRATION_TIME, TimeUnit.SECONDS);
            redisService.saveDataToRedis(request.getId() + key + Constants.User.KEY_UPDATE_EMAIL_CODE, verifyCode,
                    Constants.Security.VERIFICATION_EXPIRATION_TIME, TimeUnit.SECONDS);
            response.setData(Map.of("id", request.getId() + key));
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

    @GetMapping("/update/verify-email/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> verifyUpdateEmail(@PathVariable(value = "id") String id,
                                               @RequestParam(value = "code") String code) {
        Response<Boolean> response = new Response<>();
        response.setSuccess(false);
        try {
            if (auditorAware.getCurrentAuditor().isEmpty()) {
                response.setMessage(ResponseMessage.User.PERMISSION_DENIED);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            if (StringUtil.isNullOrBlank(id) || StringUtil.isNullOrBlank(code)) {
                response.setMessage(ResponseMessage.User.MISSING_AUTHENTICATION_INFO);
                return ResponseEntity.badRequest().body(response);
            }
            String key = id + Constants.User.KEY_UPDATE_EMAIL_CODE;
            String verifyCode = (String) redisService.getDataFromRedis(key);
            if (StringUtil.isNullOrBlank(verifyCode)) {
                response.setMessage(ResponseMessage.User.VERIFY_CODE_EXPIRED);
                return ResponseEntity.status(HttpStatus.GONE).body(response);
            }
            if (!Objects.equals(verifyCode, code)) {
                response.setMessage(ResponseMessage.User.VERIFY_CODE_INCORRECT);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            String data = (String) redisService.getDataFromRedis(id + Constants.User.KEY_UPDATE_EMAIL_INFO);
            if (Objects.isNull(data)) {
                response.setMessage(ResponseMessage.User.NOT_FOUND);
                return ResponseEntity.status(HttpStatus.GONE).body(response);
            }
            UserUpdateEmailRequest request = DataUtil.fromJSON(data, UserUpdateEmailRequest.class);
            request.setUpdatedBy(auditorAware.getCurrentAuditor().get());
            userService.updateEmail(request);
            redisService.deleteDataFromRedis(key);
            redisService.deleteDataFromRedis(id + Constants.User.KEY_UPDATE_EMAIL_INFO);
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

    @PutMapping("/update/password/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updatePassword(@PathVariable(value = "id") String id,
                                            @Valid @RequestBody UserUpdatePasswordRequest request,
                                            BindingResult bindingResult) {
        Response<Boolean> response = new Response<>();
        response.setSuccess(false);
        try {
            if (auditorAware.getCurrentAuditor().isEmpty()) {
                response.setMessage(ResponseMessage.Authentication.PERMISSION_DENIED);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            if (bindingResult.hasErrors()) {
                response.setMessage(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
                return ResponseEntity.badRequest().body(response);
            }
            if (!Objects.equals(auditorAware.getCurrentAuditor().get(), id)) {
                response.setMessage(ResponseMessage.Authentication.PERMISSION_DENIED);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            request.setUpdatedBy(id);
            userService.updatePassword(request);
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

    @PutMapping("update/status/{id}")
    @PreAuthorize("hasRole('ADMIN') || hasRole('MANAGER')")
    public ResponseEntity<?> updateStatus(@PathVariable(value = "id") String id,
                                          @Valid @RequestBody UserUpdateStatusRequest request,
                                          BindingResult bindingResult) {
        Response<Boolean> response = new Response<>();
        response.setSuccess(false);
        try {
            if (auditorAware.getCurrentAuditor().isEmpty()) {
                response.setMessage(ResponseMessage.Authentication.PERMISSION_DENIED);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            if (bindingResult.hasErrors()) {
                response.setMessage(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
                return ResponseEntity.badRequest().body(response);
            }
            if (StringUtil.isNullOrBlank(id) || !Objects.equals(id, request.getId())) {
                response.setMessage(ResponseMessage.User.MISSING_AUTHENTICATION_INFO);
                return ResponseEntity.badRequest().body(response);
            }
            if (!Objects.equals(request.getRole(), Constants.User.ROLE_USER)) {
                response.setMessage(ResponseMessage.User.PERMISSION_DENIED);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            request.setUpdatedBy(auditorAware.getCurrentAuditor().get());
            userService.updateStatus(request);
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

    @PostMapping("/create/admin")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> createAdmin(@Valid @RequestBody UserCreateRequest request,
                                         BindingResult bindingResult) {
        Response<Boolean> response = new Response<>();
        response.setSuccess(false);
        try {
            if (auditorAware.getCurrentAuditor().isEmpty()) {
                response.setMessage(ResponseMessage.Authentication.PERMISSION_DENIED);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            if (bindingResult.hasErrors()) {
                response.setMessage(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
                return ResponseEntity.badRequest().body(response);
            }
            request.setRole(ERole.ADMIN);
            request.setCreatedBy(auditorAware.getCurrentAuditor().get());
            userService.create(request);
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

    @PutMapping("/update/admin/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> updateAdmin(@PathVariable(value = "id") String id,
                                         @Valid @RequestBody AdminUpdateRequest request,
                                         BindingResult bindingResult) {
        Response<Boolean> response = new Response<>();
        response.setSuccess(false);
        try {
            if (auditorAware.getCurrentAuditor().isEmpty()) {
                response.setMessage(ResponseMessage.Authentication.PERMISSION_DENIED);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            if (bindingResult.hasErrors()) {
                response.setMessage(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
                return ResponseEntity.badRequest().body(response);
            }
            if (StringUtil.isNullOrBlank(id) || !Objects.equals(id, request.getId())) {
                response.setMessage(ResponseMessage.User.MISSING_AUTHENTICATION_INFO);
                return ResponseEntity.badRequest().body(response);
            }
            request.setUpdatedBy(auditorAware.getCurrentAuditor().get());
            userService.update(request);
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

    @PutMapping("/update/password/admin/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> updateAdminPassword(@PathVariable(value = "id") String id,
                                                 @Valid @RequestBody AdminUpdatePasswordRequest request,
                                                 BindingResult bindingResult) {
        Response<Boolean> response = new Response<>();
        response.setSuccess(false);
        try {
            if (auditorAware.getCurrentAuditor().isEmpty()) {
                response.setMessage(ResponseMessage.Authentication.PERMISSION_DENIED);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            if (bindingResult.hasErrors()) {
                response.setMessage(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
                return ResponseEntity.badRequest().body(response);
            }
            if (StringUtil.isNullOrBlank(id) || !id.equals(request.getId())) {
                response.setMessage(ResponseMessage.User.MISSING_AUTHENTICATION_INFO);
                return ResponseEntity.badRequest().body(response);
            }
            request.setUpdatedBy(auditorAware.getCurrentAuditor().get());
            userService.updatePasswordAdmin(request);
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

}
