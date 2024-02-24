package com.project.soshuceapi.services;

import com.project.soshuceapi.common.Constants;
import com.project.soshuceapi.entities.Adopt;
import com.project.soshuceapi.entities.User;
import com.project.soshuceapi.entities.logging.ActionLogDetail;
import com.project.soshuceapi.models.DTOs.ActionLogDTO;
import com.project.soshuceapi.models.DTOs.AdoptDTO;
import com.project.soshuceapi.models.mappers.AdoptMapper;
import com.project.soshuceapi.models.mappers.PetMapper;
import com.project.soshuceapi.models.mappers.UserMapper;
import com.project.soshuceapi.models.requests.AdoptCreateRequest;
import com.project.soshuceapi.models.requests.AdoptUpdateRequest;
import com.project.soshuceapi.repositories.AdoptRepository;
import com.project.soshuceapi.services.iservice.IAdoptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class AdoptService implements IAdoptService {

    private final String TAG = "ADOPT";

    @Autowired
    private AdoptRepository adoptRepository;
    @Autowired
    private ActionLogService actionLogService;
    @Autowired
    private PetService petService;
    @Autowired
    private UserService userService;
    @Autowired
    private PetMapper petMapper;
    @Autowired
    private AdoptMapper adoptMapper;

    @Override
    @Transactional
    public AdoptDTO create(AdoptCreateRequest request) {
        try {
            Adopt adopt = new Adopt();
            adopt.setPet(petMapper.mapFrom(petService.getById(request.getPetId())));
            adopt.setCreatedBy(new User(userService.getById(request.getCreatedBy()).getId()));
            adopt.setCode(generateCode());
            adopt.setCreatedAt(LocalDateTime.now());
            adopt.setWardId(request.getWardId());
            adopt.setDistrictId(request.getDistrictId());
            adopt.setProvinceId(request.getProvinceId());
            adopt.setAddress(request.getAddress());
            adopt.setStatus(Constants.AdoptStatus.WAIT_FOR_PROGRESSING);
            adopt.setIsDeleted(false);
            adopt = adoptRepository.save(adopt);
            logCreate(adopt);
            return adoptMapper.mapTo(adopt, AdoptDTO.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AdoptDTO update(AdoptUpdateRequest request) {
        return null;
    }

    @Override
    public boolean deleteSoft(String id) {
        return true;
    }

    private String generateCode() {
        try {
            Long seq = adoptRepository.getSEQ();
            return "ADT" + String.format("%05d", seq);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void logCreate(Adopt adopt) {
        actionLogService.create(ActionLogDTO.builder()
                .action(Constants.ActionLog.CREATE)
                .description(Constants.ActionLog.CREATE + TAG)
                .createdBy(adopt.getCreatedBy().getId())
                .details(List.of(
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .columnName("id")
                                .newValue(adopt.getId())
                                .build(),
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .columnName("code")
                                .newValue(adopt.getCode())
                                .build(),
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .columnName("pet_id")
                                .newValue(adopt.getPet().getId())
                                .build(),
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .columnName("ward_id")
                                .newValue(String.valueOf(adopt.getWardId()))
                                .build(),
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .columnName("district_id")
                                .newValue(String.valueOf(adopt.getDistrictId()))
                                .build(),
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .columnName("province_id")
                                .newValue(String.valueOf(adopt.getProvinceId()))
                                .build(),
                        ActionLogDetail.builder()
                                .tableName(TAG)
                                .columnName("address")
                                .newValue(adopt.getAddress())
                                .build()
                ))
                .build());
    }

    private void logUpdate(Adopt newValue, Adopt oldValue) {

    }
}
