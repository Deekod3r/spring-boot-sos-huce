package com.project.soshuceapi.controllers;

import com.project.soshuceapi.common.Constants;
import com.project.soshuceapi.common.ResponseCode;
import com.project.soshuceapi.models.requests.StudentCreateRequest;
import com.project.soshuceapi.models.responses.Error;
import com.project.soshuceapi.models.responses.Response;
import com.project.soshuceapi.services.EmailService;
import com.project.soshuceapi.services.RedisService;
import com.project.soshuceapi.services.StudentService;
import com.project.soshuceapi.utils.DataUtil;
import com.project.soshuceapi.utils.StringUtil;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/students")
@CrossOrigin(origins = "http://localhost:4200")
public class StudentController {

    @Autowired
    private StudentService studentService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private EmailService emailService;

    @PostMapping("/register")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> register(@Valid @RequestBody StudentCreateRequest request, BindingResult bindingResult) throws MessagingException {
        Response<Map<String, String>> response = new Response<>();
        try {
            if (bindingResult.hasErrors()) {
                response.setError(Error.of(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage(), ResponseCode.Common.FAIL));
                return ResponseEntity.badRequest().body(response);
            }
            if (studentService.isExistByStudentCodeOrEmail(request.getStudentCode(), request.getEmail())) {
                response.setError(Error.of("existed.student.by.code/email", ResponseCode.Common.EXISTED));
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            String verifyCode = StringUtil.generateRandomString(Constants.Secutiry.VERIFY_CODE_LENGTH);
            String key = String.valueOf(System.currentTimeMillis());
            emailService.sendMail(request.getEmail(), String.format(Constants.Mail.SUBJECT, "Email Verification"), String.format(Constants.Mail.VERIFY_BODY, request.getEmail(), "đăng ký tài khoản", verifyCode));
            redisService.saveDataToRedis(request.getStudentCode() + key + "-REGISTER-INFO", DataUtil.toJSON(request), Constants.Secutiry.VERIFICATION_EXPIRATION_TIME, TimeUnit.SECONDS);
            redisService.saveDataToRedis(request.getStudentCode() + key + "-REGISTER-CODE", verifyCode, Constants.Secutiry.VERIFICATION_EXPIRATION_TIME, TimeUnit.SECONDS);
            response.setData(Map.of("id", request.getStudentCode() + key));
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setError(Error.of(e.getMessage(), ResponseCode.Common.FAIL));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
