package com.project.soshuceapi.controllers;

import com.project.soshuceapi.common.ResponseMessage;
import com.project.soshuceapi.models.DTOs.DonateDTO;
import com.project.soshuceapi.models.DTOs.TotalAmountStatisticDTO;
import com.project.soshuceapi.models.requests.DonateCreateRequest;
import com.project.soshuceapi.models.requests.DonateSearchRequest;
import com.project.soshuceapi.models.requests.DonateUpdateRequest;
import com.project.soshuceapi.models.requests.TotalDonateSearchRequest;
import com.project.soshuceapi.models.responses.Response;
import com.project.soshuceapi.services.iservice.IDonateService;
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
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/donates")
public class DonateController {

    @Autowired
    private IDonateService donateService;
    @Autowired
    private AuditorAware<String> auditorAware;

    @GetMapping
    public ResponseEntity<?> getDonates(
            @RequestParam(value = "remitter", defaultValue = "", required = false) String remitter,
            @RequestParam(value = "payee", defaultValue = "", required = false) String payee,
            @RequestParam(value = "fromDate", defaultValue = "", required = false) LocalDate fromDate,
            @RequestParam(value = "toDate", defaultValue = "", required = false) LocalDate toDate,
            @RequestParam(value = "limit", defaultValue = "5", required = false) Integer limit,
            @RequestParam(value = "page", defaultValue = "1", required = false) Integer page,
            @RequestParam(value = "fullData", required = false, defaultValue = "false") Boolean fullData
    ) {
        Response<Map<String, Object>> response = new Response<>();
        response.setSuccess(false);
        try {
            response.setData(donateService.getAll(DonateSearchRequest.of(remitter.trim(), payee.trim(), fromDate, toDate, limit, page, fullData)));
            response.setMessage(ResponseMessage.Common.SUCCESS);
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setMessage(ResponseMessage.Common.SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') || hasRole('MANAGER')")
    public ResponseEntity<?> getDonateById(@PathVariable("id") String id) {
        Response<DonateDTO> response = new Response<>();
        response.setSuccess(false);
        try {
            if (StringUtil.isNullOrBlank(id)) {
                response.setMessage(ResponseMessage.Donate.MISSING_ID);
                return ResponseEntity.badRequest().body(response);
            }
            response.setData(donateService.getById(id));
            response.setMessage(ResponseMessage.Common.SUCCESS);
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setMessage(ResponseMessage.Common.SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/total")
    @PreAuthorize("hasRole('ADMIN') || hasRole('MANAGER')")
    public ResponseEntity<?> getTotalDonation(
            @RequestParam(value = "year", defaultValue = "", required = false) Integer year
    ) {
        Response<List<TotalAmountStatisticDTO>> response = new Response<>();
        response.setSuccess(false);
        try {
            response.setData(donateService.getTotalDonation(TotalDonateSearchRequest.of(year)));
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
    public ResponseEntity<?> createDonate(@Valid @RequestBody DonateCreateRequest request, BindingResult bindingResult) {
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
            donateService.create(request);
            response.setMessage(ResponseMessage.Common.SUCCESS);
            response.setData(true);
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setMessage(ResponseMessage.Common.SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN') || hasRole('MANAGER')")
    public ResponseEntity<?> updateDonate(@PathVariable("id") String id,
                                          @Valid @RequestBody DonateUpdateRequest request, BindingResult bindingResult) {
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
                response.setMessage(ResponseMessage.Donate.NOT_MATCH);
                return ResponseEntity.badRequest().body(response);
            }
            request.setUpdatedBy(auditorAware.getCurrentAuditor().get());
            donateService.update(request);
            response.setMessage(ResponseMessage.Common.SUCCESS);
            response.setData(true);
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setMessage(ResponseMessage.Common.SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN') || hasRole('MANAGER')")
    public ResponseEntity<?> deleteDonate(@PathVariable("id") String id) {
        Response<Boolean> response = new Response<>();
        response.setSuccess(false);
        try {
            if (auditorAware.getCurrentAuditor().isEmpty()) {
                response.setMessage(ResponseMessage.Authentication.PERMISSION_DENIED);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            if (StringUtil.isNullOrBlank(id)) {
                response.setMessage(ResponseMessage.Adopt.MISSING_ID);
                return ResponseEntity.badRequest().body(response);
            }
            donateService.delete(id, auditorAware.getCurrentAuditor().get());
            response.setMessage(ResponseMessage.Common.SUCCESS);
            response.setData(true);
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setMessage(ResponseMessage.Common.SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
