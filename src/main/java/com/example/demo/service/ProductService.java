package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.service.dto.ProductDTO;

import java.util.HashMap;
import java.util.List;

public interface ProductService extends GenerateService<Product>{
    Product createProduct(Product product);

    List<ProductDTO> findAllPage(int page, int size);

    void saveVariants(List<GroupOption> groupOptions,Long productId);

    Variant getVariantByOption(List<OptionProduct> options);

    void updateVariant(List<Variant> variants);

    List<Product> getAllProductByShopId (Long shopId);

    Product getProductById(Long id);

    List<Product> searchProducts(String keyword, Long categoryId, Double minPrice, Double maxPrice);
    List<Product> getFeaturedProducts(int limit);
    List<Product> getProductsByCategory(Long categoryId, int page, int size);
    List<Product> getDiscountedProducts(int limit);
    void updateProductStock(Long productId, int quantityChange, Long variantId);
    List<Review> getProductReviews(Long productId, int page, int size);
    void addToCart(Long userId, Long productId, int quantity);
    List<Variant> getVariantsByProductId(Long productId);

    HashMap<String,Object> getDetailsProducts(Long productId);

    List<ProductDTO> getRelatedProducts(Long productId);

}
