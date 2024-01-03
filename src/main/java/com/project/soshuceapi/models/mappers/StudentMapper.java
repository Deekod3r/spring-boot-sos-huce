package com.project.soshuceapi.models.mappers;

import com.project.soshuceapi.entities.Student;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StudentMapper {

    @Autowired
    private ModelMapper modelMapper;

    public <T> T mapTo(Student student, Class<T> destinationType) {
        return modelMapper.map(student, destinationType);
    }

    public <T> Student mapFrom(T source) {
        return modelMapper.map(source, Student.class);
    }

}
