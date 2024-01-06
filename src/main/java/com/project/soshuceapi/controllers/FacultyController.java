package com.project.soshuceapi.controllers;

import com.project.soshuceapi.common.ResponseCode;
import com.project.soshuceapi.entities.Faculty;
import com.project.soshuceapi.models.responses.Error;
import com.project.soshuceapi.models.responses.Response;
import com.project.soshuceapi.services.FacultyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/faculties")
@CrossOrigin(origins = "http://localhost:4200")
public class FacultyController {

    @Autowired
    private FacultyService facultyService;

    @GetMapping()
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> getAllFaculties() {
        Response<List<Faculty>> response = new Response<>();
        try {
            response.setData(facultyService.getAll());
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setError(Error.of(e.getMessage(), ResponseCode.Common.FAIL));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }

}
