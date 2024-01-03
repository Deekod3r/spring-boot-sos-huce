package com.project.soshuceapi.repositories;

import com.project.soshuceapi.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {

    @Query(value = "SELECT * FROM students WHERE student_code = :studentCode and is_deleted = false", nativeQuery = true)
    Optional<Student> findByStudentCode(String studentCode);

    @Query(value = "SELECT COUNT(*) FROM students WHERE (student_code = :studentCode OR email = :email) and is_deleted = false", nativeQuery = true)
    Long countByStudentCodeOrEmail(@Param("studentCode") String studentCode, @Param("email") String email);

}
