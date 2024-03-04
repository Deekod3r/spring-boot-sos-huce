package com.project.soshuceapi.services;

import com.project.soshuceapi.common.ResponseMessage;
import com.project.soshuceapi.entities.locations.District;
import com.project.soshuceapi.entities.locations.Province;
import com.project.soshuceapi.entities.locations.Ward;
import com.project.soshuceapi.repositories.DistrictRepository;
import com.project.soshuceapi.repositories.ProvinceRepository;
import com.project.soshuceapi.repositories.WardRepository;
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
    private DistrictRepository districtRepository;
    @Autowired
    private WardRepository wardRepository;
    @Autowired
    private ProvinceRepository provinceRepository;

    @Override
    public List<Province> getAllProvinces() {
        try {
            return provinceRepository.findAll();
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<District> getAllDistricts(int provinceId) {
        try {
            if (NumberUtil.isNullOrZero(provinceId)) {
                return districtRepository.findAll();
            }
            return districtRepository.findAllByProvince(provinceId);
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<Ward> getAllWards(int districtId) {
        try {
            if (NumberUtil.isNullOrZero(districtId)) {
                return wardRepository.findAll();
            }
            return wardRepository.findAllByDistrict(districtId);
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Province getProvinceById(int id) {
        try {
            return provinceRepository.findById(id).orElseThrow(() ->
                    new RuntimeException(ResponseMessage.Location.GET_INFO_PROVINCE_FAIL));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public District getDistrictById(int id) {
        try {
            return districtRepository.findById(id).orElseThrow(() ->
                    new RuntimeException(ResponseMessage.Location.GET_INFO_DISTRICT_FAIL));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Ward getWardById(int id) {
        try {
            return wardRepository.findById(id).orElseThrow(() ->
                    new RuntimeException(ResponseMessage.Location.GET_INFO_WARD_FAIL));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
