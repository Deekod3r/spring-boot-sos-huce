package com.project.soshuceapi.services.iservice;

import com.project.soshuceapi.entities.locations.District;
import com.project.soshuceapi.entities.locations.Province;
import com.project.soshuceapi.entities.locations.Ward;

import java.util.List;

public interface ILocationService {

    List<Province> getAllProvinces();

    List<District> getAllDistricts(int provinceId);

    List<Ward> getAllWards(int districtId);

    Province getProvinceById(int id);

    District getDistrictById(int id);

    Ward getWardById(int id);

}
