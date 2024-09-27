package com.project.soshuceapi.controllers;

import com.project.soshuceapi.common.ResponseMessage;
import com.project.soshuceapi.models.DTOs.PetCareLogDTO;
import com.project.soshuceapi.models.requests.PetCareLogCreateRequest;
import com.project.soshuceapi.models.requests.PetCareLogSearchRequest;
import com.project.soshuceapi.models.requests.PetCareLogUpdateRequest;
import com.project.soshuceapi.models.responses.Response;
import com.project.soshuceapi.services.iservice.IPetCareLogService;
import com.project.soshuceapi.utils.StringUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/pet-care-logs")
public class PetCareLogController {

    @Autowired
    private IPetCareLogService petCareLogService;
    @Autowired
    private AuditorAware<String> auditorAware;

    @GetMapping
    public ResponseEntity<Response<List<PetCareLogDTO>>> getPetCareLogs(
            @RequestParam(value = "adoptId", required = false, defaultValue = "") String adoptId,
            @RequestParam(value = "petId", required = false, defaultValue = "") String petId,
            @RequestParam(value = "fromDate", required = false, defaultValue = "") LocalDate fromDate,
            @RequestParam(value = "toDate", required = false, defaultValue = "") LocalDate toDate)
    {
        Response<List<PetCareLogDTO>> response = new Response<>();
        response.setSuccess(false);
        try {
            response.setSuccess(true);
            response.setMessage(ResponseMessage.Common.SUCCESS);
            response.setData(petCareLogService.getAll(PetCareLogSearchRequest.of(adoptId, petId, fromDate, toDate)));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setMessage(ResponseMessage.Common.SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') || hasRole('MANAGER')")
    public ResponseEntity<Response<PetCareLogDTO>> getPetCareLog(@PathVariable String id)
    {
        Response<PetCareLogDTO> response = new Response<>();
        response.setSuccess(false);
        try {
            if (StringUtil.isNullOrBlank(id)) {
                response.setMessage(ResponseMessage.PetCareLog.MISSING_ID);
                return ResponseEntity.badRequest().body(response);
            }
            response.setSuccess(true);
            response.setMessage(ResponseMessage.Common.SUCCESS);
            response.setData(petCareLogService.getById(id));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setMessage(ResponseMessage.Common.SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN') || hasRole('MANAGER')")
    public ResponseEntity<Response<Boolean>> createPetCareLog(
            @RequestBody @Valid PetCareLogCreateRequest request, BindingResult bindingResult)
    {
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
            request.setCreatedBy(auditorAware.getCurrentAuditor().get());
            petCareLogService.create(request);
            response.setData(true);
            response.setSuccess(true);
            response.setMessage(ResponseMessage.Common.SUCCESS);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setMessage(ResponseMessage.Common.SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN') || hasRole('MANAGER')")
    public ResponseEntity<Response<Boolean>> updatePetCareLog(
            @PathVariable String id,
            @RequestBody @Valid PetCareLogUpdateRequest request, BindingResult bindingResult)
    {
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
            if (!Objects.equals(id, request.getId())) {
                response.setMessage(ResponseMessage.PetCareLog.NOT_MATCH);
                return ResponseEntity.badRequest().body(response);
            }
            request.setId(id);
            request.setUpdatedBy(auditorAware.getCurrentAuditor().get());
            petCareLogService.update(request);
            response.setData(true);
            response.setSuccess(true);
            response.setMessage(ResponseMessage.Common.SUCCESS);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setMessage(ResponseMessage.Common.SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN') || hasRole('MANAGER')")
    public ResponseEntity<Response<Boolean>> deletePetCareLog(@PathVariable String id)
    {
        Response<Boolean> response = new Response<>();
        response.setSuccess(false);
        try {
            if (auditorAware.getCurrentAuditor().isEmpty()) {
                response.setMessage(ResponseMessage.Authentication.PERMISSION_DENIED);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            if (StringUtil.isNullOrBlank(id)) {
                response.setMessage(ResponseMessage.PetCareLog.MISSING_ID);
                return ResponseEntity.badRequest().body(response);
            }
            petCareLogService.delete(id, auditorAware.getCurrentAuditor().get());
            response.setData(true);
            response.setSuccess(true);
            response.setMessage(ResponseMessage.Common.SUCCESS);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setMessage(ResponseMessage.Common.SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
