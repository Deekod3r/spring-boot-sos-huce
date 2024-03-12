package com.project.soshuceapi.services;

import com.project.soshuceapi.common.ResponseMessage;
import com.project.soshuceapi.entities.locations.District;
import com.project.soshuceapi.entities.locations.Province;
import com.project.soshuceapi.entities.locations.Ward;
import com.project.soshuceapi.repositories.DistrictRepo;
import com.project.soshuceapi.repositories.ProvinceRepo;
import com.project.soshuceapi.repositories.WardRepo;
import com.project.soshuceapi.services.iservice.ILocationService;
import com.project.soshuceapi.utils.NumberUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class LocationService implements ILocationService {

    private final static String TAG = "LOCATION";

    @Autowired
    private DistrictRepo districtRepo;
    @Autowired
    private WardRepo wardRepo;
    @Autowired
    private ProvinceRepo provinceRepo;

    @Override
    public List<Province> getAllProvinces() {
        try {
            return provinceRepo.findAll();
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<District> getAllDistricts(int provinceId) {
        try {
            if (NumberUtil.isNullOrZero(provinceId)) {
                return districtRepo.findAll();
            }
            return districtRepo.findAllByProvince(provinceId);
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<Ward> getAllWards(int districtId) {
        try {
            if (NumberUtil.isNullOrZero(districtId)) {
                return wardRepo.findAll();
            }
            return wardRepo.findAllByDistrict(districtId);
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Province getProvinceById(int id) {
        try {
            return provinceRepo.findById(id).orElseThrow(() ->
                    new RuntimeException(ResponseMessage.Location.GET_INFO_PROVINCE_FAIL));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public District getDistrictById(int id) {
        try {
            return districtRepo.findById(id).orElseThrow(() ->
                    new RuntimeException(ResponseMessage.Location.GET_INFO_DISTRICT_FAIL));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Ward getWardById(int id) {
        try {
            return wardRepo.findById(id).orElseThrow(() ->
                    new RuntimeException(ResponseMessage.Location.GET_INFO_WARD_FAIL));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
