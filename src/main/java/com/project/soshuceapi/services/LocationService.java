package com.project.soshuceapi.services;

import com.project.soshuceapi.entities.District;
import com.project.soshuceapi.entities.Ward;
import com.project.soshuceapi.repositories.DistrictRepository;
import com.project.soshuceapi.repositories.WardRepository;
import com.project.soshuceapi.services.iservice.ILocationService;
import com.project.soshuceapi.utils.NumberUtil;
import com.project.soshuceapi.utils.StringUtil;
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

    @Override
    public List<District> getAllDistrics() {
        try {
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
