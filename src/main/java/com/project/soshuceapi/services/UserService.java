package com.project.soshuceapi.services;

import com.project.soshuceapi.entities.User;
import com.project.soshuceapi.exceptions.UserExistedException;
import com.project.soshuceapi.exceptions.UserNotFoundException;
import com.project.soshuceapi.models.DTOs.UserDTO;
import com.project.soshuceapi.models.mappers.UserMapper;
import com.project.soshuceapi.models.requests.UserCreateRequest;
import com.project.soshuceapi.models.requests.UserUpdateRequest;
import com.project.soshuceapi.repositories.UserRepository;
import com.project.soshuceapi.services.iservice.IUserService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserService implements IUserService {

    private final static String TAG = "USER";

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public UserDTO create(UserCreateRequest request) {
        try {
            validUserRequest(request, true);
            User user = userMapper.mapFrom(request);
            user.setActivated(true);
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setCreatedAt(LocalDateTime.now());
            user.setRole(request.getRole());
            user = userRepository.save(user);
            return userMapper.mapTo(user, UserDTO.class);
        } catch (UserExistedException e) {
            throw new UserExistedException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public UserDTO update(UserUpdateRequest request, String id) {
        try {
            validUserRequest(request, false);
            User user = userMapper.mapFrom(request);
            user.setActivated(true);
            return userMapper.mapTo(user, UserDTO.class);
        } catch (UserNotFoundException e) {
            throw new UserNotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public UserDTO getById(String id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new RuntimeException("not.found.user"));
        return userMapper.mapTo(user, UserDTO.class);
    }

    @Override
    public UserDTO getByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new RuntimeException("not.found.user"));
        return userMapper.mapTo(user, UserDTO.class);
    }

    @Override
    public boolean isExistsById(String id) {
        return userRepository.existsById(id);
    }

    @Override
    public boolean isExistByPhoneNumberOrEmail(String phoneNumber, String email) {
        return userRepository.countByPhoneNumberOrEmail(phoneNumber, email) > 0;
    }

    private void validUserRequest(@NotNull Object obj, boolean type) {
        try {
            if (type) {
                UserCreateRequest createRequest = (UserCreateRequest) obj;
                if (isExistByPhoneNumberOrEmail(createRequest.getPhoneNumber(), createRequest.getEmail())) {
                    throw new UserExistedException("existed.user.by.code/email");
                }
            } else {
                UserUpdateRequest updateRequest = (UserUpdateRequest) obj;
                if (!isExistsById(updateRequest.getId())) {
                    throw new UserNotFoundException("not.found.user");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
