package com.project.soshuceapi.models.mappers;

import com.project.soshuceapi.entities.Adopt;
import com.project.soshuceapi.entities.Pet;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdoptMapper {

    @Autowired
    private ModelMapper modelMapper;

    public <T> T mapTo(Adopt adopt, Class<T> destinationType) {
        return modelMapper.map(adopt, destinationType);
    }

    public <T> Adopt mapFrom(T source) {
        return modelMapper.map(source, Adopt.class);
    }

}
