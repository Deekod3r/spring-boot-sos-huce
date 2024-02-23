package com.project.soshuceapi.controllers;

import com.project.soshuceapi.common.ResponseCode;
import com.project.soshuceapi.common.ResponseMessage;
import com.project.soshuceapi.exceptions.NotFoundException;
import com.project.soshuceapi.models.DTOs.PetDTO;
import com.project.soshuceapi.models.requests.PetCreateRequest;
import com.project.soshuceapi.models.requests.PetUpdateImageRequest;
import com.project.soshuceapi.models.requests.PetUpdateRequest;
import com.project.soshuceapi.models.responses.Error;
import com.project.soshuceapi.models.responses.Response;
import com.project.soshuceapi.services.PetService;
import com.project.soshuceapi.utils.StringUtil;
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
    public ResponseEntity<?> getPets(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "limit", defaultValue = "5") Integer limit,
            @RequestParam(value = "name", defaultValue = "", required = false) String name,
            @RequestParam(value = "breed", defaultValue = "", required = false) String breed,
            @RequestParam(value = "color", defaultValue = "", required = false) String color,
            @RequestParam(value = "code", defaultValue = "", required = false) String code,
            @RequestParam(value = "type", defaultValue = "", required = false) Integer type,
            @RequestParam(value = "age", defaultValue = "", required = false) Integer age,
            @RequestParam(value = "gender", defaultValue = "", required = false) Integer gender,
            @RequestParam(value = "status", defaultValue = "", required = false) Integer status,
            @RequestParam(value = "diet", defaultValue = "", required = false) Integer diet,
            @RequestParam(value = "vaccin", defaultValue = "", required = false) Integer vaccin,
            @RequestParam(value = "sterilization", defaultValue = "", required = false) Integer sterilization,
            @RequestParam(value = "rabies", defaultValue = "", required = false) Integer rabies
    ) {
        Response<Map<String, Object>> response = new Response<>();
        response.setSuccess(false);
        try {
            Integer a = type;
            response.setData(petService.getAll(page, limit,
                    name, breed, color, code, type, age, gender,
                    status, diet, vaccin, sterilization, rabies));
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setError(Error.of(e.getMessage(), ResponseCode.Common.FAIL));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPetById(@PathVariable("id") String id) {
        Response<PetDTO> response = new Response<>();
        response.setSuccess(false);
        try {
            if (StringUtil.isNullOrBlank(id)) {
                response.setError(Error.of(ResponseMessage.Common.INVALID_INPUT, ResponseCode.Common.INVALID));
                return ResponseEntity.badRequest().body(response);
            }
            response.setData(petService.getById(id));
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

    @GetMapping("/statistic-cases")
    public ResponseEntity<?> getStatisticStatus() {
        Response<Map<String, Long>> response = new Response<>();
        response.setSuccess(false);
        try {
            response.setData(petService.getStatisticCases());
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setError(Error.of(e.getMessage(), ResponseCode.Common.FAIL));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN') || hasRole('MANAGER')")
    public ResponseEntity<?> create(@Valid @ModelAttribute PetCreateRequest request, BindingResult bindingResult) {
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
            response.setData(petService.create(request).getId());
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setError(Error.of(e.getMessage(), ResponseCode.Common.FAIL));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN') || hasRole('MANAGER')")
    public ResponseEntity<?> update(@Valid @ModelAttribute PetUpdateRequest request,
                                    @PathVariable("id") String id,
                                    BindingResult bindingResult) {
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
            if (!id.equals(request.getId())) {
                response.setError(Error.of(ResponseMessage.Common.NOT_MATCH, ResponseCode.Common.NOT_MATCH));
                return ResponseEntity.badRequest().body(response);
            }
            request.setUpdatedBy(auditorAware.getCurrentAuditor().get());
            response.setData(petService.update(request).getId());
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

    @PutMapping("/update-image/{id}")
    @PreAuthorize("hasRole('ADMIN') || hasRole('MANAGER')")
    public ResponseEntity<?> updateImage(@Valid @ModelAttribute PetUpdateImageRequest request,
                                         @PathVariable("id") String id,
                                         BindingResult bindingResult) {
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
            if (!id.equals(request.getId())) {
                response.setError(Error.of(ResponseMessage.Common.NOT_MATCH, ResponseCode.Common.NOT_MATCH));
                return ResponseEntity.badRequest().body(response);
            }
            request.setUpdatedBy(auditorAware.getCurrentAuditor().get());
            response.setData(petService.updateImage(request).getId());
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

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN') || hasRole('MANAGER')")
    public ResponseEntity<?> deleteSoft(@PathVariable("id") String id) {
        Response<String> response = new Response<>();
        response.setSuccess(false);
        try {
            if (auditorAware.getCurrentAuditor().isEmpty()) {
                response.setError(Error.of(ResponseMessage.Authentication.PERMISSION_DENIED, ResponseCode.Authentication.PERMISSION_DENIED));
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            if (StringUtil.isNullOrBlank(id)) {
                response.setError(Error.of(ResponseMessage.Common.INVALID_INPUT, ResponseCode.Common.INVALID));
                return ResponseEntity.badRequest().body(response);
            }
            response.setSuccess(petService.deleteSoft(id));
            return ResponseEntity.ok(response);
        } catch (NotFoundException e) {
            response.setError(Error.of(ResponseMessage.Common.NOT_FOUND, ResponseCode.Common.NOT_FOUND));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.setError(Error.of(e.getMessage(), ResponseCode.Common.FAIL));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
