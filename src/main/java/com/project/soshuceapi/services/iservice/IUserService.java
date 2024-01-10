package com.project.soshuceapi.services.iservice;

import com.project.soshuceapi.models.DTOs.UserDTO;
import com.project.soshuceapi.models.requests.UserCreateRequest;
import com.project.soshuceapi.models.requests.UserUpdateRequest;

public interface IUserService {

    UserDTO create (UserCreateRequest request);

    UserDTO update (UserUpdateRequest request, String id);

    UserDTO getById(String id);

    UserDTO getByEmail(String email);

    boolean isExistsById(String id);

    boolean isExistByPhoneNumberOrEmail(String studentCode, String email);

}
