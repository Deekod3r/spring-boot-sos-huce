package com.project.soshuceapi.controllers;

import com.project.soshuceapi.common.Constants;
import com.project.soshuceapi.common.ResponseMessage;
import com.project.soshuceapi.exceptions.BadRequestException;
import com.project.soshuceapi.models.DTOs.AdoptDTO;
import com.project.soshuceapi.models.requests.AdoptCreateRequest;
import com.project.soshuceapi.models.responses.Response;
import com.project.soshuceapi.services.AdoptService;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/adopts")
public class AdoptController {

    @Autowired
    private AdoptService adoptService;
    @Autowired
    private AuditorAware<String> auditorAware;

    @GetMapping
    @PreAuthorize("hasRole('MANAGER') || hasRole('ADMIN')")
    public ResponseEntity<?> getAdopts() {
        Response<List<AdoptDTO>> response = new Response<>();
        response.setSuccess(false);
        try {
            if (auditorAware.getCurrentAuditor().isEmpty()) {
                response.setMessage(ResponseMessage.Authentication.PERMISSION_DENIED);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            response.setData(adoptService.getAll());
            response.setMessage(ResponseMessage.Common.SUCCESS);
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setMessage(ResponseMessage.Common.SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getAdoptsByUser(@PathVariable(name = "userId") String userId){
        Response<List<AdoptDTO>> response = new Response<>();
        response.setSuccess(false);
        try {
            if (auditorAware.getCurrentAuditor().isEmpty() || !auditorAware.getCurrentAuditor().get().equals(userId)) {
                response.setMessage(ResponseMessage.Authentication.PERMISSION_DENIED);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            response.setData(adoptService.getAllByUser(userId));
            response.setMessage(ResponseMessage.Common.SUCCESS);
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setMessage(ResponseMessage.Common.SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAdoptById(@PathVariable(name = "id") String id,
                                     @RequestParam(name = "role") String role) {
        Response<Map<String, Object>> response = new Response<>();
        response.setSuccess(false);
        try {
            if (auditorAware.getCurrentAuditor().isEmpty()) {
                response.setMessage(ResponseMessage.Authentication.PERMISSION_DENIED);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            boolean roleExists = authorities.stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(Constants.User.KEY_ROLE + role));
            if (!roleExists) {
                response.setMessage(ResponseMessage.Authentication.PERMISSION_DENIED);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            Map<String, Object> data = adoptService.getById(id);
            if (Objects.equals(role, Constants.User.ROLE_USER)) {
                String createdBy = ((AdoptDTO) data.get("adopt")).getCreatedBy();
                if (!auditorAware.getCurrentAuditor().get().equals(createdBy)) {
                    response.setMessage(ResponseMessage.Authentication.PERMISSION_DENIED);
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
                }
            }
            response.setData(data);
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
    public ResponseEntity<?> createAdopt(@RequestBody @Valid AdoptCreateRequest request, BindingResult bindingResult) {
        Response<String> response = new Response<>();
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
            response.setData(adoptService.create(request).getId());
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

    @PutMapping("cancel/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> cancelAdopt(@PathVariable(name = "id") String id) {
        Response<String> response = new Response<>();
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
            adoptService.cancel(id, auditorAware.getCurrentAuditor().get());
            response.setData(id);
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
