package com.project.soshuceapi.services;

import com.project.soshuceapi.entities.Image;
import com.project.soshuceapi.repositories.ImageRepository;
import com.project.soshuceapi.services.iservice.IImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImageService implements IImageService {

    @Autowired
    private ImageRepository imageRepository;

    @Override
    public Image create(Image image) {
        try {
            return imageRepository.save(image);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
