package com.project.soshuceapi.controllers;

import com.project.soshuceapi.common.ResponseCode;
import com.project.soshuceapi.common.ResponseMessage;
import com.project.soshuceapi.models.requests.PetCreateRequest;
import com.project.soshuceapi.models.responses.Error;
import com.project.soshuceapi.models.responses.Response;
import com.project.soshuceapi.services.PetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/pets")
public class PetController {

    @Autowired
    private PetService petService;
    @Autowired
    private AuditorAware<String> auditorAware;

    @GetMapping
    public ResponseEntity<?> getPets() {
        try {
            return ResponseEntity.ok(petService.getPets());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN') || hasRole('MANAGER')")
    public ResponseEntity<?> create(@Valid @ModelAttribute PetCreateRequest request, BindingResult bindingResult) {
        Response<Map<String, Object>> response = new Response<>();
        try {
            if (bindingResult.hasErrors()) {
                response.setError(Error.of(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage(), ResponseCode.Common.FAIL));
                return ResponseEntity.badRequest().body(response);
            }
            if (auditorAware.getCurrentAuditor().isEmpty()) {
                response.setError(Error.of(ResponseMessage.Authentication.PERMISSION_DENIED, ResponseCode.Authentication.PERMISSION_DENIED));
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            request.setCreatedBy(auditorAware.getCurrentAuditor().get());
            return ResponseEntity.ok(petService.create(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
