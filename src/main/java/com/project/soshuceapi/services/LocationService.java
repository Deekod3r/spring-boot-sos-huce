package com.project.soshuceapi.services;

import com.project.soshuceapi.entities.District;
import com.project.soshuceapi.entities.Province;
import com.project.soshuceapi.entities.Ward;
import com.project.soshuceapi.repositories.DistrictRepository;
import com.project.soshuceapi.repositories.ProvinceRepository;
import com.project.soshuceapi.repositories.WardRepository;
import com.project.soshuceapi.services.iservice.ILocationService;
import com.project.soshuceapi.utils.NumberUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
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
            throw new RuntimeException("error.get.all.province");
        }
    }

    @Override
    public List<District> getAllDistrics(int provinceId) {
        try {
            if (NumberUtil.isNullOrZero(provinceId)) {
                return districtRepository.findAllByProvince(provinceId);
            }
            return districtRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("error.get.all.district");
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
            throw new RuntimeException("error.get.all.ward");
        }
    }

    @Override
    public Province getProvinceById(int id) {
        return provinceRepository.findById(id).orElseThrow(() ->
                new RuntimeException("error.get.province.by.id"));
    }

    @Override
    public District getDistrictById(int id) {
        return districtRepository.findById(id).orElseThrow(() ->
                new RuntimeException("error.get.district.by.id"));
    }

    @Override
    public Ward getWardById(int id) {
        return wardRepository.findById(id).orElseThrow(() ->
                new RuntimeException("error.get.ward.by.id"));
    }

}
