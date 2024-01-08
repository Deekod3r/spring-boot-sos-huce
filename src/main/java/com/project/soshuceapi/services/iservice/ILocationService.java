package com.project.soshuceapi.services.iservice;

import com.project.soshuceapi.entities.District;
import com.project.soshuceapi.entities.Ward;

import java.util.List;

public interface ILocationService {

    List<District> getAllDistrics();

    List<Ward> getAllWards(int districtId);

    District getDistrictById(int id);

    Ward getWardById(int id);

}
