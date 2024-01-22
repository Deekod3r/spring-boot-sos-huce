package com.project.soshuceapi.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/admins")
public class AdminController {

    @Autowired
    private AuditorAware<String> auditorAware;

    @GetMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_USER')")
    public String getAdmins() {
        if (auditorAware.getCurrentAuditor().isPresent()) {
            log.info("User: {}", auditorAware.getCurrentAuditor().get());
            return auditorAware.getCurrentAuditor().get();
        }
        return "No user found";
    }

}
