package com.project.soshuceapi.services;

import com.project.soshuceapi.common.Constants;
import com.project.soshuceapi.common.ResponseMessage;
import com.project.soshuceapi.entities.User;
import com.project.soshuceapi.entities.logging.ActionLogDetail;
import com.project.soshuceapi.exceptions.BadRequestException;
import com.project.soshuceapi.models.DTOs.ActionLogDTO;
import com.project.soshuceapi.models.DTOs.UserDTO;
import com.project.soshuceapi.models.mappers.UserMapper;
import com.project.soshuceapi.models.requests.*;
import com.project.soshuceapi.repositories.UserRepo;
import com.project.soshuceapi.services.iservice.IActionLogService;
import com.project.soshuceapi.services.iservice.IUserService;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class UserService implements IUserService {

    private final static String TAG = "USER";

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private IActionLogService actionLogService;

    @Override
    @Transactional
    public void create(UserCreateRequest request) {
        try {
            if (isExistByPhoneNumberOrEmail(request.getPhoneNumber(), request.getEmail())) {
                throw new BadRequestException(ResponseMessage.User.USER_EXISTED);
            }
            request.setPhoneNumber(request.getPhoneNumber().trim());
            request.setEmail(request.getEmail().trim());
            request.setName(request.getName().trim());
            User user = userMapper.mapFrom(request);
            user.setIsActivated(true);
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setCreatedAt(LocalDateTime.now());
            user.setRole(request.getRole());
            user = userRepo.save(user);
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
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void update(UserUpdateNameRequest request) {
        try {
           if (!isExistsById(request.getId())) {
               throw new BadRequestException(ResponseMessage.User.NOT_FOUND);
           }
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void resetPassword(UserResetPasswordRequest request) {
        try {
            User user = userRepo.findByEmail(request.getEmail()).orElseThrow(() ->
                    new BadRequestException(ResponseMessage.User.NOT_FOUND));
            String oldPassword = user.getPassword();
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            user.setUpdatedBy(request.getUpdatedBy());
            user.setUpdatedAt(LocalDateTime.now());
            userRepo.save(user);
            actionLogService.create(ActionLogDTO.builder()
                    .action(Constants.ActionLog.UPDATE)
                    .description(Constants.ActionLog.UPDATE + "." + TAG)
                    .createdBy(request.getUpdatedBy())
                    .details(List.of(
                            ActionLogDetail.builder()
                                    .tableName(TAG)
                                    .rowId(user.getId())
                                    .columnName("password")
                                    .oldValue(oldPassword)
                                    .newValue(passwordEncoder.encode(request.getNewPassword()))
                                    .build()
                    ))
                    .build());
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void updateName(UserUpdateNameRequest request) {
        try {
            User user = userRepo.findById(request.getId()).orElseThrow(() ->
                    new BadRequestException(ResponseMessage.User.NOT_FOUND));
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new BadRequestException(ResponseMessage.User.INVALID_PASSWORD);
            }
            String oldName = user.getName();
            user.setName(request.getName().trim());
            user.setUpdatedBy(request.getUpdatedBy());
            user.setUpdatedAt(LocalDateTime.now());
            userRepo.save(user);
            actionLogService.create(ActionLogDTO.builder()
                    .action(Constants.ActionLog.UPDATE)
                    .description(Constants.ActionLog.UPDATE + "." + TAG)
                    .createdBy(request.getUpdatedBy())
                    .details(List.of(
                            ActionLogDetail.builder()
                                    .tableName(TAG)
                                    .rowId(user.getId())
                                    .columnName("name")
                                    .oldValue(oldName)
                                    .newValue(user.getName().trim())
                                    .build()
                    ))
                    .build());
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void updatePhone(UserUpdatePhoneRequest request) {
        try {
            User user = userRepo.findById(request.getId()).orElseThrow(() ->
                    new BadRequestException(ResponseMessage.User.NOT_FOUND));
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new BadRequestException(ResponseMessage.User.INVALID_PASSWORD);
            }
            String oldPhoneNumber = user.getPhoneNumber();
            user.setPhoneNumber(request.getPhoneNumber().trim());
            user.setUpdatedBy(request.getUpdatedBy());
            user.setUpdatedAt(LocalDateTime.now());
            userRepo.save(user);
            actionLogService.create(ActionLogDTO.builder()
                    .action(Constants.ActionLog.UPDATE)
                    .description(Constants.ActionLog.UPDATE + "." + TAG)
                    .createdBy(request.getUpdatedBy())
                    .details(List.of(
                            ActionLogDetail.builder()
                                    .tableName(TAG)
                                    .rowId(user.getId())
                                    .columnName("phone_number")
                                    .oldValue(oldPhoneNumber)
                                    .newValue(user.getPhoneNumber().trim())
                                    .build()
                    ))
                    .build());
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void updateEmail(UserUpdateEmailRequest request) {
        try {
            User user = userRepo.findById(request.getId()).orElseThrow(() ->
                    new BadRequestException(ResponseMessage.User.NOT_FOUND));
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new BadRequestException(ResponseMessage.User.INVALID_PASSWORD);
            }
            String oldEmail = user.getEmail();
            user.setEmail(request.getEmail().trim());
            user.setUpdatedBy(request.getUpdatedBy());
            user.setUpdatedAt(LocalDateTime.now());
            userRepo.save(user);
            actionLogService.create(ActionLogDTO.builder()
                    .action(Constants.ActionLog.UPDATE)
                    .description(Constants.ActionLog.UPDATE + "." + TAG)
                    .createdBy(request.getUpdatedBy())
                    .details(List.of(
                            ActionLogDetail.builder()
                                    .tableName(TAG)
                                    .rowId(user.getId())
                                    .columnName("email")
                                    .oldValue(oldEmail)
                                    .newValue(user.getEmail().trim())
                                    .build()
                    ))
                    .build());
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void updatePassword(UserUpdatePasswordRequest request) {
        try {
            User user = userRepo.findById(request.getId()).orElseThrow(() ->
                    new BadRequestException(ResponseMessage.User.NOT_FOUND));
            if (!passwordEncoder.matches(request.getCurrentPassword(),user.getPassword())) {
                throw new BadRequestException(ResponseMessage.User.INVALID_OLD_PASSWORD);
            }
            if (request.getNewPassword().equals(request.getCurrentPassword())) {
                throw new BadRequestException(ResponseMessage.User.PASSWORD_DUPLICATE);
            }
            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                throw new BadRequestException(ResponseMessage.User.INVALID_CONFIRM_PASSWORD);
            }
            String oldPassword = user.getPassword();
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            user.setUpdatedBy(request.getUpdatedBy());
            user.setUpdatedAt(LocalDateTime.now());
            user = userRepo.save(user);
            actionLogService.create(ActionLogDTO.builder()
                    .action(Constants.ActionLog.UPDATE)
                    .description(Constants.ActionLog.UPDATE + "." + TAG)
                    .createdBy(request.getUpdatedBy())
                    .details(List.of(
                            ActionLogDetail.builder()
                                    .tableName(TAG)
                                    .rowId(user.getId())
                                    .columnName("password")
                                    .oldValue(oldPassword)
                                    .newValue(user.getPassword())
                                    .build()
                    ))
                    .build());
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void updateStatus(UserUpdateStatusRequest request) {
        try {
            User user = userRepo.findById(request.getId()).orElseThrow(() ->
                    new BadRequestException(ResponseMessage.User.NOT_FOUND));
            if (!user.getRole().name().equals(Constants.User.ROLE_USER)) {
                throw new BadRequestException(ResponseMessage.User.NOT_AVAILABLE_FOR_UPDATE);
            }
            if (user.getIsActivated().equals(request.getStatus())) {
                return;
            }
            user.setIsActivated(request.getStatus());
            user.setUpdatedBy(request.getUpdatedBy());
            user.setUpdatedAt(LocalDateTime.now());
            userRepo.save(user);
            actionLogService.create(ActionLogDTO.builder()
                    .action(Constants.ActionLog.UPDATE)
                    .description(Constants.ActionLog.UPDATE + "." + TAG)
                    .createdBy(request.getUpdatedBy())
                    .details(List.of(
                            ActionLogDetail.builder()
                                    .tableName(TAG)
                                    .rowId(user.getId())
                                    .columnName("is_activated")
                                    .oldValue(String.valueOf(!request.getStatus()))
                                    .newValue(String.valueOf(request.getStatus()))
                                    .build()
                    ))
                    .build());
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getAll(UserSearchRequest request) {
        try {
            Page<User> users = userRepo.getAll(
                    request.getName().trim(),
                    request.getEmail().trim(),
                    request.getPhoneNumber().trim(),
                    request.getIsActivated(),
                    request.getRole().trim(),
                    PageRequest.ofSize(request.getLimit()).withPage(request.getPage() - 1)
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
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public UserDTO getById(String id) {
        try {
            User user = userRepo.findById(id).orElseThrow(() ->
                    new BadRequestException(ResponseMessage.User.NOT_FOUND));
            return userMapper.mapTo(user, UserDTO.class);
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public UserDTO getByEmail(String email) {
        try {
            return userRepo.findByEmail(email).map(user ->
                    userMapper.mapTo(user, UserDTO.class)).orElse(null);
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public UserDTO getByPhoneNumber(String phoneNumber) {
        try {
            return userRepo.findByPhoneNumber(phoneNumber).map(user ->
                    userMapper.mapTo(user, UserDTO.class)).orElse(null);
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public UserDTO getByPhoneNumberOrEmail(String phoneNumber, String email) {
        try {
            return userRepo.findByPhoneNumberOrEmail(phoneNumber, email).map(user ->
                    userMapper.mapTo(user, UserDTO.class)).orElse(null);
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Boolean isExistsById(String id) {
        try {
            return userRepo.existsById(id);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Boolean isExistByPhoneNumberOrEmail(String phoneNumber, String email) {
        try {
            return userRepo.countByPhoneNumberOrEmail(phoneNumber, email) > 0;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Boolean checkPassword(String id, String password) {
        try {
            User user = userRepo.findById(id).orElseThrow(() ->
                    new BadRequestException(ResponseMessage.User.NOT_FOUND));
            return passwordEncoder.matches(password, user.getPassword());
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

}
