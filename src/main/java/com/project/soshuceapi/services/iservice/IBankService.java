package com.project.soshuceapi.services.iservice;

import com.project.soshuceapi.models.DTOs.BankDTO;
import com.project.soshuceapi.models.requests.BankCreateRequest;
import com.project.soshuceapi.models.requests.BankUpdateRequest;

import java.util.List;

public interface IBankService {

    List<BankDTO> getAll();

    BankDTO getById(String id);

    void create(BankCreateRequest request);

    void update(BankUpdateRequest request);

    void delete(String id, String deletedBy);

}
