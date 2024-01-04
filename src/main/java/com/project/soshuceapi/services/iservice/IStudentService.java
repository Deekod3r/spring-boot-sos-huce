package com.project.soshuceapi.services.iservice;

import com.project.soshuceapi.models.DTOs.StudentDTO;
import com.project.soshuceapi.models.requests.StudentCreateRequest;
import com.project.soshuceapi.models.requests.StudentUpdateRequest;

public interface IStudentService {

    StudentDTO create (StudentCreateRequest request);

    StudentDTO update (StudentUpdateRequest request, String id);

    StudentDTO getById(String id);

    StudentDTO getByStudentCode(String studentCode);

    boolean isExistsById(String id);

    boolean isExistByStudentCodeOrEmail(String studentCode, String email);

}
