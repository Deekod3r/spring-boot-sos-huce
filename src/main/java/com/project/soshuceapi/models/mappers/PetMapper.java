package com.project.soshuceapi.models.mappers;

import com.project.soshuceapi.entities.Pet;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PetMapper {

    @Autowired
    private ModelMapper modelMapper;

    public <T> T mapTo(Pet pet, Class<T> destinationType) {
        return modelMapper.map(pet, destinationType);
    }

    public <T> Pet mapFrom(T source) {
        return modelMapper.map(source, Pet.class);
    }

}
