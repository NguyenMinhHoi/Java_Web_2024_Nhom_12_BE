package com.example.demo.service;

import com.example.demo.model.ShopSection;
import com.example.demo.service.dto.ProductDTO;

import java.util.List;

public interface ShopSectionService extends GenerateService<ShopSection>  {
    List<ShopSection> findByName(Long userId);

    ShopSection update(ShopSection shopSection);

    List<ShopSection> findByUserId(Long userId);
    List<ProductDTO> getProductsInShopSection(Long shopSectionId);
}