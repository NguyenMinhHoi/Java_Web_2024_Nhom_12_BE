package com.example.demo.repository;

import com.example.demo.model.OrderProduct;
import com.example.demo.model.Orders;
import com.example.demo.model.Product;
import com.example.demo.utils.enumeration.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository

public interface OrderRepository extends JpaRepository<Orders,Long> {

    List<Orders> findByUserId(Long userId);
    Page<Orders> findByUserId(Long userId, Pageable pageable);

    List<Orders> findByMerchantId(Long merchantId);

    Page<Orders> findByMerchantId(Long merchantId, Pageable pageable);
}
