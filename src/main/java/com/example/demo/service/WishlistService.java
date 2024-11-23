package com.example.demo.service;

import com.example.demo.model.Wishlist;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

public interface WishlistService extends GenerateService<Wishlist> {
    @Transactional
    Wishlist addProductToWishlist(Long wishlistId, Long productId);

    @Transactional
    Wishlist removeProductFromWishlist(Long wishlistId, Long productId);

    @Transactional
    Wishlist addShopToWishlist(Long wishlistId, Long shopId);

    @Transactional
    Wishlist removeShopFromWishlist(Long wishlistId, Long shopId);
}
