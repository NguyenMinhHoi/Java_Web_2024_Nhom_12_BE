package com.example.demo.service;

import com.example.demo.model.Cart;

public interface CartService extends GenerateService<Cart>{
       Cart updateCart(Long userId, Long variantId);

       Cart getCartByUserId(Long userId);
}
