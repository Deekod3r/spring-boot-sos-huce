package com.project.soshuceapi.controllers;

import com.project.soshuceapi.common.Constants;
import com.project.soshuceapi.common.ResponseMessage;
import com.project.soshuceapi.models.DTOs.TotalAmountStatisticDTO;
import com.project.soshuceapi.models.DTOs.TreatmentDTO;
import com.project.soshuceapi.models.requests.TotalTreatmentCostSearchRequest;
import com.project.soshuceapi.models.requests.TreatmentCreateRequest;
import com.project.soshuceapi.models.requests.TreatmentSearchRequest;
import com.project.soshuceapi.models.requests.TreatmentUpdateRequest;
import com.project.soshuceapi.models.responses.Response;
import com.project.soshuceapi.services.iservice.ITreatmentService;
import com.project.soshuceapi.utils.SecurityUtil;
import com.project.soshuceapi.utils.StringUtil;
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
@RequestMapping("/treatments")
public class TreatmentController {

    @Autowired
    private ITreatmentService treatmentService;
    @Autowired
    private AuditorAware<String> auditorAware;

    @GetMapping
    public ResponseEntity<?> getPets(
            @RequestParam(value = "page", defaultValue = "1", required = false) Integer page,
            @RequestParam(value = "limit", defaultValue = "5", required = false) Integer limit,
            @RequestParam(value = "status", defaultValue = "", required = false) Boolean status,
            @RequestParam(value = "fullData", required = false, defaultValue = "false") Boolean fullData,
            @RequestParam(value = "petId", defaultValue = "", required = false) String petId,
            @RequestParam(value = "type", defaultValue = "", required = false) Integer type,
            @RequestParam(value = "daysOfTreatment", defaultValue = "", required = false) Integer daysOfTreatment
    ) {
        Response<Map<String, Object>> response = new Response<>();
        response.setSuccess(false);
        try {
            if (auditorAware.getCurrentAuditor().isEmpty()
                    || SecurityUtil.checkRole(Constants.User.KEY_ROLE + Constants.User.ROLE_USER)) {
                status = true;
            }
            response.setData(treatmentService.getAll(TreatmentSearchRequest.of(petId, status, page, limit, fullData, type, daysOfTreatment)));
            response.setSuccess(true);
            response.setMessage(ResponseMessage.Common.SUCCESS);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setMessage(ResponseMessage.Common.SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') || hasRole('MANAGER')")
    public ResponseEntity<?> getPet(@PathVariable String id) {
        Response<TreatmentDTO> response = new Response<>();
        response.setSuccess(false);
        try {
            if (StringUtil.isNullOrBlank(id)) {
                response.setMessage(ResponseMessage.PetCareLog.MISSING_ID);
                return ResponseEntity.badRequest().body(response);
            }
            response.setData(treatmentService.getById(id));
            response.setSuccess(true);
            response.setMessage(ResponseMessage.Common.SUCCESS);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setMessage(ResponseMessage.Common.SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/total-cost")
    @PreAuthorize("hasRole('ADMIN') || hasRole('MANAGER')")
    public ResponseEntity<?> getTotalTreatmentCost(
            @RequestParam(value = "year", defaultValue = "") Integer year,
            @RequestParam(value = "month", defaultValue = "", required = false) Integer month,
            @RequestParam(value = "byType", required = false, defaultValue = "false") Boolean byType
    ) {
        Response<List<TotalAmountStatisticDTO>> response = new Response<>();
        response.setSuccess(false);
        try {
            if (byType) {
                response.setData(treatmentService
                        .getTotalTreatmentCostByType(TotalTreatmentCostSearchRequest.builder()
                                .year(year)
                                .month(month)
                                .build())
                );
            } else {
                response.setData(treatmentService
                        .getTotalTreatmentCost(TotalTreatmentCostSearchRequest.builder()
                                .year(year)
                                .build())
                );
            }
            response.setMessage(ResponseMessage.Common.SUCCESS);
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setMessage(ResponseMessage.Common.SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN') || hasRole('MANAGER')")
    public ResponseEntity<?> createPet(@Valid @ModelAttribute TreatmentCreateRequest request, BindingResult bindingResult) {
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
            treatmentService.create(request);
            response.setData(true);
            response.setMessage(ResponseMessage.Common.SUCCESS);
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setMessage(ResponseMessage.Common.SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN') || hasRole('MANAGER')")
    public ResponseEntity<?> updatePet(@PathVariable String id, @Valid @RequestBody TreatmentUpdateRequest request,
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
            if (!Objects.equals(id, request.getId())) {
                response.setMessage(ResponseMessage.Treatment.NOT_MATCH);
                return ResponseEntity.badRequest().body(response);
            }
            request.setUpdatedBy(auditorAware.getCurrentAuditor().get());
            treatmentService.update(request);
            response.setData(true);
            response.setMessage(ResponseMessage.Common.SUCCESS);
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setMessage(ResponseMessage.Common.SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN') || hasRole('MANAGER')")
    public ResponseEntity<?> deletePet(@PathVariable String id) {
        Response<Boolean> response = new Response<>();
        response.setSuccess(false);
        try {
            if (auditorAware.getCurrentAuditor().isEmpty()) {
                response.setMessage(ResponseMessage.Authentication.PERMISSION_DENIED);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            if (StringUtil.isNullOrBlank(id)) {
                response.setMessage(ResponseMessage.LivingCost.MISSING_ID);
                return ResponseEntity.badRequest().body(response);
            }
            treatmentService.delete(id, auditorAware.getCurrentAuditor().get());
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
