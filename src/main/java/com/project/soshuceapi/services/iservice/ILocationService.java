package com.project.soshuceapi.services.iservice;

import com.project.soshuceapi.entities.District;
import com.project.soshuceapi.entities.Province;
import com.project.soshuceapi.entities.Ward;

import java.util.List;

public interface ILocationService {

    List<Province> getAllProvinces();

    List<District> getAllDistrics(int provinceId);

    List<Ward> getAllWards(int districtId);

    Province getProvinceById(int id);

    District getDistrictById(int id);

    Ward getWardById(int id);

}
