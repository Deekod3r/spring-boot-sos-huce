package com.project.soshuceapi.services.iservice;

import com.project.soshuceapi.models.DTOs.UserDTO;
import com.project.soshuceapi.models.requests.UserCreateRequest;
import com.project.soshuceapi.models.requests.UserUpdateRequest;

public interface IUserService {

    UserDTO create (UserCreateRequest request);

    UserDTO update (UserUpdateRequest request);

    UserDTO updatePassword (String email, String password, String updatedBy);

    UserDTO getById(String id);

    UserDTO getByEmail(String email);

    UserDTO getByPhoneNumber(String phoneNumber);

    UserDTO getByPhoneNumberOrEmail(String phoneNumber, String email);

    boolean isExistsById(String id);

    boolean isExistByPhoneNumberOrEmail(String phoneNumber, String email);

}
