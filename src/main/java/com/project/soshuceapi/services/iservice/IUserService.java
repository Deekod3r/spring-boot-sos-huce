package com.project.soshuceapi.services.iservice;

import com.project.soshuceapi.models.DTOs.UserDTO;
import com.project.soshuceapi.models.requests.UserCreateRequest;
import com.project.soshuceapi.models.requests.UserUpdateRequest;

import java.util.Map;

public interface IUserService {

    UserDTO create (UserCreateRequest request);

    UserDTO update (UserUpdateRequest request);

    UserDTO updatePassword (String email, String password, String updatedBy);

    Map<String, Object> getAll(Integer page, Integer limit, String name, String email, String phoneNumber, Boolean isActivated, String role);

    UserDTO getById(String id);

    UserDTO getByEmail(String email);

    UserDTO getByPhoneNumber(String phoneNumber);

    UserDTO getByPhoneNumberOrEmail(String phoneNumber, String email);

    Boolean isExistsById(String id);

    Boolean isExistByPhoneNumberOrEmail(String phoneNumber, String email);

}
