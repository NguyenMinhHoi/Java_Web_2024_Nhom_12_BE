package com.example.demo.repository;

import com.example.demo.model.Category;
import com.example.demo.model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE " +
            "(:keyword IS NULL OR p.name LIKE %:keyword% OR p.description LIKE %:keyword%) AND " +
            "(:categoryId IS NULL OR p.category.id = :categoryId)")
    List<Product> searchProducts(String keyword, Long categoryId);

    @Query("SELECT p FROM Product p ORDER BY p.rating DESC, p.sold DESC")
    List<Product> findFeaturedProducts(Pageable pageable);

    List<Product> findByCategoryId(Long categoryId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.isDiscount IS NOT NULL AND p.isDiscount = true")
    List<Product> findDiscountedProducts(Pageable pageable);

    List<Product> findAllByMerchantId(Long merchantId);

    List<Product> findByCategoryAndIdNot(Category category, Long productId, Pageable pageable);

    List<Product> findByIdNot(Long productId, Pageable pageable);
}
