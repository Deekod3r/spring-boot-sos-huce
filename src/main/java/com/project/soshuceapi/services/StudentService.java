package com.project.soshuceapi.services;

import com.project.soshuceapi.entities.Student;
import com.project.soshuceapi.exceptions.StudentExistedException;
import com.project.soshuceapi.exceptions.StudentNotFoundException;
import com.project.soshuceapi.models.DTOs.StudentDTO;
import com.project.soshuceapi.models.mappers.StudentMapper;
import com.project.soshuceapi.models.requests.StudentCreateRequest;
import com.project.soshuceapi.models.requests.StudentUpdateRequest;
import com.project.soshuceapi.repositories.StudentRepository;
import com.project.soshuceapi.services.iservice.IFacultyService;
import com.project.soshuceapi.services.iservice.IStudentService;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Transactional
public class StudentService implements IStudentService {

    private final static String TAG = "STUDENT";

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private IFacultyService facultyService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private StudentMapper studentMapper;

    @Override
    public StudentDTO create(StudentCreateRequest request) {
        try {
            validStudentRequest(request, true);
            Student student = studentMapper.mapFrom(request);
            student.setDeleted(false);
            student.setActivated(true);
            student.setPassword(passwordEncoder.encode(request.getPassword()));
            student.setCreatedAt(LocalDateTime.now());
            student.setRole(request.getRole());
            student.setFaculty(facultyService.getById(request.getFaculty()));
            student = studentRepository.save(student);
            return studentMapper.mapTo(student, StudentDTO.class);
        } catch (StudentExistedException e) {
            throw new StudentExistedException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public StudentDTO update(StudentUpdateRequest request, String id) {
        try {
            validStudentRequest(request, false);
            Student student = studentMapper.mapFrom(request);
            student.setDeleted(false);
            student.setActivated(true);
//            student.setPassword(passwordEncoder.encode(request.getPassword()));
//            student.setCreatedAt(LocalDateTime.now());
//            student.setRole(request.getRole());
//            student.setFaculty(facultyService.getById(request.getFaculty()));
//            student = studentRepository.save(student);
            return studentMapper.mapTo(student, StudentDTO.class);
        } catch (StudentNotFoundException e) {
            throw new StudentNotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public StudentDTO getById(String id) {
        Student student = studentRepository.findById(id).orElseThrow(() ->
                new RuntimeException("not.found.student"));
        return studentMapper.mapTo(student, StudentDTO.class);
    }

    @Override
    public StudentDTO getByStudentCode(String studentCode) {
        Student student = studentRepository.findByStudentCode(studentCode).orElseThrow(() ->
                new RuntimeException("not.found.student"));
        return studentMapper.mapTo(student, StudentDTO.class);
    }

    @Override
    public boolean isExistsById(String id) {
        return studentRepository.existsById(id);
    }

    @Override
    public boolean isExistByStudentCodeOrEmail(String studentCode, String email) {
        return studentRepository.countByStudentCodeOrEmail(studentCode, email) > 0;
    }

    private void validStudentRequest(@NotNull Object obj, boolean type) {
        try {
            String facultyId;
            if (type) {
                StudentCreateRequest createRequest = (StudentCreateRequest) obj;
                if (isExistByStudentCodeOrEmail(createRequest.getStudentCode(), createRequest.getEmail())) {
                    throw new StudentExistedException("existed.student.by.code/email");
                }
                facultyId = createRequest.getFaculty();

            } else {
                StudentUpdateRequest updateRequest = (StudentUpdateRequest) obj;
                if (!isExistsById(updateRequest.getId())) {
                    throw new StudentNotFoundException("not.found.student");
                }
                facultyId = updateRequest.getFaculty();
            }
            if (!facultyService.isExistsById(facultyId)) {
                throw new RuntimeException("not.found.faculty");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
