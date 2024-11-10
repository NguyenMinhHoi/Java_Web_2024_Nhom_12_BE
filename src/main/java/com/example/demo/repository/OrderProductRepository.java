package com.example.demo.repository;

import com.example.demo.model.OrderProduct;
import com.example.demo.model.Orders;
import com.example.demo.model.ProductOrderPK;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderProductRepository extends JpaRepository<OrderProduct, ProductOrderPK> {
    List<OrderProduct> findByProductOrderPK_Order_Id(Long orderId);
    List<OrderProduct> findByProductOrderPK_Variant_Id(Long variantId);
}
