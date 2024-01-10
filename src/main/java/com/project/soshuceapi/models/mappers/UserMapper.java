package com.project.soshuceapi.models.mappers;

import com.project.soshuceapi.entities.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    @Autowired
    private ModelMapper modelMapper;

    public <T> T mapTo(User user, Class<T> destinationType) {
        return modelMapper.map(user, destinationType);
    }

    public <T> User mapFrom(T source) {
        return modelMapper.map(source, User.class);
    }

}
