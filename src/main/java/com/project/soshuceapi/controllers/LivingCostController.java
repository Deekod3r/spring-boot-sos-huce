package com.project.soshuceapi.controllers;

import com.project.soshuceapi.common.ResponseMessage;
import com.project.soshuceapi.exceptions.BadRequestException;
import com.project.soshuceapi.models.DTOs.LivingCostDTO;
import com.project.soshuceapi.models.requests.LivingCostCreateRequest;
import com.project.soshuceapi.models.requests.LivingCostSearchRequest;
import com.project.soshuceapi.models.requests.LivingCostUpdateRequest;
import com.project.soshuceapi.models.responses.Response;
import com.project.soshuceapi.services.iservice.ILivingCostService;
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
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/living-costs")
public class LivingCostController {

    @Autowired
    private ILivingCostService livingCostService;
    @Autowired
    private AuditorAware<String> auditorAware;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') || hasRole('MANAGER')")
    public ResponseEntity<?> getAll(
            @RequestParam(value = "limit", required = false, defaultValue = "5") Integer limit,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "fullData", required = false, defaultValue = "false") Boolean fullData,
            @RequestParam(value = "fromDate", required = false, defaultValue = "") LocalDate fromDate,
            @RequestParam(value = "toDate", required = false, defaultValue = "") LocalDate toDate
    ) {
        Response<Map<String, Object>> response = new Response<>();
        response.setSuccess(false);
        try {
            response.setData(livingCostService.getAll(LivingCostSearchRequest.builder()
                    .page(page)
                    .limit(limit)
                    .fullData(fullData)
                    .fromDate(fromDate)
                    .toDate(toDate)
                    .build()));
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
    public ResponseEntity<?> getById(@PathVariable String id) {
        Response<LivingCostDTO> response = new Response<>();
        response.setSuccess(false);
        try {
            if (StringUtil.isNullOrBlank(id)) {
                response.setMessage(ResponseMessage.LivingCost.MISSING_ID);
                return ResponseEntity.badRequest().body(response);
            }
            response.setData(livingCostService.getById(id));
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

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN') || hasRole('MANAGER')")
    public ResponseEntity<?> create(@Valid @ModelAttribute LivingCostCreateRequest request, BindingResult bindingResult) {
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
            livingCostService.create(request);
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
    public ResponseEntity<?> update(@PathVariable String id,
                                    @Valid @RequestBody LivingCostUpdateRequest request, BindingResult bindingResult) {
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
                response.setMessage(ResponseMessage.LivingCost.NOT_MATCH);
                return ResponseEntity.badRequest().body(response);
            }
            request.setUpdatedBy(auditorAware.getCurrentAuditor().get());
            livingCostService.update(request);
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

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN') || hasRole('MANAGER')")
    public ResponseEntity<?> delete(@PathVariable String id) {
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
            livingCostService.delete(id, auditorAware.getCurrentAuditor().get());
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
