package com.example.demo.service.impl;

import com.example.demo.model.Image;
import com.example.demo.model.Product;
import com.example.demo.repository.ImageRepository;
import com.example.demo.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    private ImageRepository imageRepository;

    @Override
    public List<Image> findAll() {
        return List.of();
    }

    @Override
    public Image findById(Long id) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public Image save(Image entity) {

        return null;
    }
}
