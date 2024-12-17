package com.example.demo.service;

import com.example.demo.model.ShopSection;

import java.util.List;

public interface ShopSectionService extends GenerateService<ShopSection>  {
    List<ShopSection> findByName(Long userId);

    ShopSection update(ShopSection shopSection);
}