package com.project.soshuceapi.services;

import com.project.soshuceapi.common.Constants;
import com.project.soshuceapi.entities.User;
import com.project.soshuceapi.entities.logging.ActionLogDetail;
import com.project.soshuceapi.exceptions.NotFoundException;
import com.project.soshuceapi.exceptions.UserExistedException;
import com.project.soshuceapi.models.DTOs.ActionLogDTO;
import com.project.soshuceapi.models.DTOs.UserDTO;
import com.project.soshuceapi.models.mappers.UserMapper;
import com.project.soshuceapi.models.requests.UserCreateRequest;
import com.project.soshuceapi.models.requests.UserUpdateRequest;
import com.project.soshuceapi.repositories.UserRepository;
import com.project.soshuceapi.services.iservice.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
            request.setPhoneNumber(request.getPhoneNumber().trim());
            request.setEmail(request.getEmail().trim());
            request.setName(request.getName().trim());
            User user = userMapper.mapFrom(request);
            user.setIsActivated(true);
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setCreatedAt(LocalDateTime.now());
            user.setRole(request.getRole());
            user = userRepository.save(user);
            actionLogService.create(ActionLogDTO.builder()
                    .action(Constants.ActionLog.CREATE)
                    .description(Constants.ActionLog.CREATE + "." + TAG)
                    .createdBy(user.getId())
                    .details(List.of(
                            ActionLogDetail.builder()
                                    .tableName(TAG)
                                    .rowId(user.getId())
                                    .columnName("email")
                                    .oldValue("")
                                    .newValue(user.getEmail().trim())
                                    .build(),
                            ActionLogDetail.builder()
                                    .tableName(TAG)
                                    .rowId(user.getId())
                                    .columnName("name")
                                    .oldValue("")
                                    .newValue(user.getName().trim())
                                    .build(),
                            ActionLogDetail.builder()
                                    .tableName(TAG)
                                    .rowId(user.getId())
                                    .columnName("phone_number")
                                    .oldValue("")
                                    .newValue(user.getPhoneNumber().trim())
                                    .build(),
                            ActionLogDetail.builder()
                                    .tableName(TAG)
                                    .rowId(user.getId())
                                    .columnName("role")
                                    .oldValue("")
                                    .newValue(String.valueOf(user.getRole()).trim())
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
               throw new NotFoundException("user.not.found.by.id");
           }
            User user = userMapper.mapFrom(request);
            return userMapper.mapTo(user, UserDTO.class);
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public UserDTO updatePassword(String email, String password, String updatedBy) {
        try {
            User user = userRepository.findByEmail(email).orElseThrow(() ->
                    new NotFoundException("user.not.found.by.email"));
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
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getAll(Integer page, Integer limit, String name,
                                      String email, String phoneNumber, String role) {
        try {
            Page<User> users = userRepository.getAll(
                    name.trim(),
                    email.trim(),
                    phoneNumber.trim(),
                    role.trim(),
                    PageRequest.ofSize(limit).withPage(page - 1)
            );
            List<UserDTO> userDTOs = users.getContent().stream().map(user ->
                    userMapper.mapTo(user, UserDTO.class)).toList();
            return Map.of(
                    "users", userDTOs,
                    "total", users.getTotalElements(),
                    "page", users.getNumber() + 1,
                    "limit", users.getSize(),
                    "totalPage", users.getTotalPages()
            );
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public UserDTO getById(String id) {
        try {
            User user = userRepository.findById(id).orElseThrow(() ->
                    new RuntimeException("user.not.found.by.id"));
            return userMapper.mapTo(user, UserDTO.class);
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public UserDTO getByEmail(String email) {
        try {
            User user = userRepository.findByEmail(email).orElseThrow(() ->
                    new NotFoundException("user.not.found.by.email"));
            return userMapper.mapTo(user, UserDTO.class);
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public UserDTO getByPhoneNumber(String phoneNumber) {
        return null;
    }

    @Override
    public UserDTO getByPhoneNumberOrEmail(String phoneNumber, String email) {
        try {
            return userRepository.findByPhoneNumberOrEmail(phoneNumber, email).map(user ->
                    userMapper.mapTo(user, UserDTO.class)).orElseThrow(() ->
                    new NotFoundException("user.not.found.by.phone_number/email"));
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Boolean isExistsById(String id) {
        try {
            return userRepository.existsById(id);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Boolean isExistByPhoneNumberOrEmail(String phoneNumber, String email) {
        try {
            return userRepository.countByPhoneNumberOrEmail(phoneNumber, email) > 0;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
