package com.example.demo.service.impl;

import com.example.demo.model.Merchant;
import com.example.demo.model.ShopSection;
import com.example.demo.repository.ShopSectionRepository;
import com.example.demo.service.MerchantService;
import com.example.demo.service.ProductService;
import com.example.demo.service.ShopSectionService;
import com.example.demo.service.dto.ProductDTO;
import com.example.demo.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ShopSectionServiceImpl implements ShopSectionService {

    @Autowired
    private ShopSectionRepository shopSectionRepository;

    @Autowired
    private MerchantService merchantService;
    @Autowired
    private ProductService productService;

    @Override
    public List<ShopSection> findAll() {
        return List.of();
    }

    @Override
    public ShopSection findById(Long id) {
        return null;
    }

    @Override
    public void deleteById(Long id) {
        shopSectionRepository.deleteById(id);
    }

    @Override
    public ShopSection save(ShopSection entity) {
        entity.setCreatedAt(new Date());
        ShopSection exist = shopSectionRepository.findShopSectionByNameAndMerchantId(entity.getName(),entity.getMerchant().getId());
        if(!CommonUtils.isEmpty(exist)){
            return null;
        }
        return shopSectionRepository.save(entity);
    }

    @Override
    public List<ShopSection> findByName(Long userId) {
        return shopSectionRepository.findAllByMerchantId(userId);
    }

    @Override
    public ShopSection update(ShopSection shopSection) {
        ShopSection ss = shopSectionRepository.findById(shopSection.getId()).orElse(null);
        if (ss!=null){
            ss.setUpdateAt(new Date());
            ss.setDescription(shopSection.getDescription());
            ss.setProducts(shopSection.getProducts());
            ss.setName(shopSection.getName());
            ss = shopSectionRepository.save(ss);
            return shopSectionRepository.save(ss);
        }
        return shopSectionRepository.save(ss);
    };

    @Override
    public List<ShopSection> findByUserId(Long userId) {
        return shopSectionRepository.findAllByMerchantId(userId);
    }

        @Override
    public List<ProductDTO> getProductsInShopSection(Long shopSectionId) {
        ShopSection shopSection = shopSectionRepository.findById(shopSectionId).orElse(new ShopSection());
        if (!CommonUtils.isEmpty(shopSection)) {
            return shopSection.getProducts().stream()
                    .map(product -> productService.toProductDTO(product))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}