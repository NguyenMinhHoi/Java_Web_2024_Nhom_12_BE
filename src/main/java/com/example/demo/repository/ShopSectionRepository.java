package com.example.demo.repository;

import com.example.demo.model.Product;
import com.example.demo.model.ShopSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ShopSectionRepository extends JpaRepository<ShopSection, Long> {
    List<ShopSection> findAllByMerchantId(Long shopId);
    ShopSection findShopSectionByNameAndMerchantId(String name,Long id);
}