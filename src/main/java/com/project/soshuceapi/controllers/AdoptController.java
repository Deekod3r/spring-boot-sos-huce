package com.project.soshuceapi.controllers;

import com.project.soshuceapi.common.ResponseCode;
import com.project.soshuceapi.common.ResponseMessage;
import com.project.soshuceapi.models.requests.AdoptCreateRequest;
import com.project.soshuceapi.models.responses.Error;
import com.project.soshuceapi.models.responses.Response;
import com.project.soshuceapi.services.AdoptService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/adopts")
public class AdoptController {

    @Autowired
    private AdoptService adoptService;
    @Autowired
    private AuditorAware<String> auditorAware;

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> create(@Valid AdoptCreateRequest request, BindingResult bindingResult) {
        Response<String> response = new Response<>();
        response.setSuccess(false);
        try {
            if (auditorAware.getCurrentAuditor().isEmpty()) {
                response.setError(Error.of(ResponseMessage.Authentication.PERMISSION_DENIED, ResponseCode.Authentication.PERMISSION_DENIED));
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            if (bindingResult.hasErrors()) {
                response.setError(Error.of(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage(),
                        ResponseCode.Common.INVALID));
                return ResponseEntity.badRequest().body(response);
            }
            request.setCreatedBy(auditorAware.getCurrentAuditor().get());
            response.setData(adoptService.create(request).getId());
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setError(Error.of(e.getMessage(), ResponseCode.Common.FAIL));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
