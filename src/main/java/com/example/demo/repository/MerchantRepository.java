package com.example.demo.repository;

import com.example.demo.model.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface MerchantRepository extends JpaRepository<Merchant,Long> {
    Merchant findByUserId(Long userId);
}
