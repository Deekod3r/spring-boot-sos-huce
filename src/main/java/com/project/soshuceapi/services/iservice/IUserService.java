package com.project.soshuceapi.services.iservice;

import com.project.soshuceapi.models.DTOs.UserDTO;
import com.project.soshuceapi.models.requests.*;

import java.util.Map;

public interface IUserService {

    void create(UserCreateRequest request);

    void update(AdminUpdateRequest request);

    void resetPassword(UserResetPasswordRequest request);

    void updateName(UserUpdateNameRequest request);

    void updatePhone(UserUpdatePhoneRequest request);

    void updateEmail(UserUpdateEmailRequest request);

    void updatePassword(UserUpdatePasswordRequest request);

    void updatePasswordAdmin(AdminUpdatePasswordRequest request);

    void updateStatus(UserUpdateStatusRequest request);

    Map<String, Object> getAll(UserSearchRequest request);

    UserDTO getById(String id);

    UserDTO getByEmail(String email);

    UserDTO getByPhoneNumber(String phoneNumber);

    UserDTO getByPhoneNumberOrEmail(String phoneNumber, String email);

    Boolean isExistByPhoneNumberOrEmail(String phoneNumber, String email);

    Boolean checkPassword(String id, String password);

}
