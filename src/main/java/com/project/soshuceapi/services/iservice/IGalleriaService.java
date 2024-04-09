package com.project.soshuceapi.services.iservice;

import com.project.soshuceapi.models.DTOs.GalleriaDTO;
import com.project.soshuceapi.models.requests.GalleriaCreateRequest;
import com.project.soshuceapi.models.requests.GalleriaUpdateImageRequest;
import com.project.soshuceapi.models.requests.GalleriaUpdateRequest;

import java.util.List;

public interface IGalleriaService {

    List<GalleriaDTO> getAll(Boolean status);

    GalleriaDTO getById(String id);

    void create(GalleriaCreateRequest request);

    void update(GalleriaUpdateRequest request);

    void updateImage(GalleriaUpdateImageRequest request);

    void delete(String id, String deletedBy);

}
