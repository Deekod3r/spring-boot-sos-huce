package com.project.soshuceapi.services;

import com.project.soshuceapi.common.Constants;
import com.project.soshuceapi.entities.User;
import com.project.soshuceapi.entities.logging.ActionLogDetail;
import com.project.soshuceapi.exceptions.NotFoundException;
import com.project.soshuceapi.exceptions.UserExistedException;
import com.project.soshuceapi.exceptions.UserNotFoundException;
import com.project.soshuceapi.models.DTOs.ActionLogDTO;
import com.project.soshuceapi.models.DTOs.UserDTO;
import com.project.soshuceapi.models.mappers.UserMapper;
import com.project.soshuceapi.models.requests.UserCreateRequest;
import com.project.soshuceapi.models.requests.UserUpdateRequest;
import com.project.soshuceapi.repositories.UserRepository;
import com.project.soshuceapi.services.iservice.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService implements IUserService {

    private final static String TAG = "USER";

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ActionLogService actionLogService;

    @Override
    @Transactional
    public UserDTO create(UserCreateRequest request) {
        try {
            if (isExistByPhoneNumberOrEmail(request.getPhoneNumber(), request.getEmail())) {
                throw new UserExistedException("existed.user.by.code/email");
            }
            User user = userMapper.mapFrom(request);
            user.setIsActivated(true);
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setCreatedAt(LocalDateTime.now());
            user.setRole(request.getRole());
            user = userRepository.save(user);
            actionLogService.create(ActionLogDTO.builder()
                    .action(Constants.ActionLog.CREATE)
                    .description(Constants.ActionLog.CREATE + "." + TAG)
                    .createdBy(request.getCreatedBy())
                    .details(List.of(
                            ActionLogDetail.builder()
                                    .tableName(TAG)
                                    .rowId(user.getId())
                                    .columnName("email")
                                    .newValue(user.getEmail())
                                    .build(),
                            ActionLogDetail.builder()
                                    .tableName(TAG)
                                    .rowId(user.getId())
                                    .columnName("name")
                                    .newValue(user.getName())
                                    .build(),
                            ActionLogDetail.builder()
                                    .tableName(TAG)
                                    .rowId(user.getId())
                                    .columnName("phone_number")
                                    .newValue(user.getPhoneNumber())
                                    .build(),
                            ActionLogDetail.builder()
                                    .tableName(TAG)
                                    .rowId(user.getId())
                                    .columnName("role")
                                    .newValue(String.valueOf(user.getRole()))
                                    .build()

                    ))
                    .build());
            return userMapper.mapTo(user, UserDTO.class);
        } catch (UserExistedException e) {
            throw new UserExistedException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public UserDTO update(UserUpdateRequest request) {
        try {
           if (!isExistsById(request.getId())) {
               throw new UserNotFoundException("not.found.user");
           }
            User user = userMapper.mapFrom(request);
            return userMapper.mapTo(user, UserDTO.class);
        } catch (UserNotFoundException e) {
            throw new UserNotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public UserDTO updatePassword(String email, String password, String updatedBy) {
        try {
            User user = userRepository.findByEmail(email).orElseThrow(() ->
                    new UserNotFoundException("not.found.user"));
            String oldPassword = user.getPassword();
            user.setPassword(passwordEncoder.encode(password));
            user.setUpdatedBy(updatedBy);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
            actionLogService.create(ActionLogDTO.builder()
                    .action(Constants.ActionLog.UPDATE)
                    .description(Constants.ActionLog.UPDATE + "." + TAG)
                    .createdBy(updatedBy)
                    .details(List.of(
                            ActionLogDetail.builder()
                                    .tableName(TAG)
                                    .rowId(user.getId())
                                    .columnName("password")
                                    .oldValue(oldPassword)
                                    .newValue(passwordEncoder.encode(password))
                                    .build()
                    ))
                    .build());
            return userMapper.mapTo(user, UserDTO.class);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public UserDTO getById(String id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new RuntimeException("not.found.user.by.id"));
        return userMapper.mapTo(user, UserDTO.class);
    }

    @Override
    public UserDTO getByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new RuntimeException("not.found.user.by.email"));
        return userMapper.mapTo(user, UserDTO.class);
    }

    @Override
    public UserDTO getByPhoneNumber(String phoneNumber) {
        return null;
    }

    @Override
    public UserDTO getByPhoneNumberOrEmail(String phoneNumber, String email) {
        return userRepository.findByPhoneNumberOrEmail(phoneNumber, email).map(user ->
                userMapper.mapTo(user, UserDTO.class)).orElseThrow(() ->
                new NotFoundException("not.found.user.by.phone_number/email"));
    }

    @Override
    public boolean isExistsById(String id) {
        return userRepository.existsById(id);
    }

    @Override
    public boolean isExistByPhoneNumberOrEmail(String phoneNumber, String email) {
        return userRepository.countByPhoneNumberOrEmail(phoneNumber, email) > 0;
    }

}
