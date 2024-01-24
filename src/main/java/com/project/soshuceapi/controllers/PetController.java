package com.project.soshuceapi.controllers;

import com.project.soshuceapi.common.ResponseCode;
import com.project.soshuceapi.common.ResponseMessage;
import com.project.soshuceapi.entities.Pet;
import com.project.soshuceapi.models.DTOs.PetDTO;
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

import java.util.List;
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
    public ResponseEntity<?> getPets(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "5") int limit,
            @RequestParam(value = "name", defaultValue = "", required = false) String name,
            @RequestParam(value = "breed", defaultValue = "", required = false) String breed,
            @RequestParam(value = "color", defaultValue = "", required = false) String color,
            @RequestParam(value = "code", defaultValue = "", required = false) String code,
            @RequestParam(value = "type", defaultValue = "", required = false) Integer type,
            @RequestParam(value = "age", defaultValue = "", required = false) Integer age,
            @RequestParam(value = "gender", defaultValue = "", required = false) Integer gender,
            @RequestParam(value = "status", defaultValue = "", required = false) Integer status
    ) {
        Response<Map<String, Object>> response = new Response<>();
        try {
            response.setSuccess(true);
            response.setData(petService.getPets(page, limit, name, breed, color, code, type, age, gender, status));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN') || hasRole('MANAGER')")
    public ResponseEntity<?> create(@Valid @ModelAttribute PetCreateRequest request, BindingResult bindingResult) {
        Response<String> response = new Response<>();
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
            response.setSuccess(true);
            response.setData(petService.create(request).getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
