package com.example.demo.service.impl;

import com.example.demo.model.Cart;
import com.example.demo.model.Product;
import com.example.demo.model.Variant;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.VariantRepository;
import com.example.demo.service.CartService;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private VariantRepository variantRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductService productService;


    @Override
    public List<Cart> findAll() {
        return List.of();
    }

    @Override
    public Cart findById(Long id) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public Cart save(Cart entity) {

        return null;
    }

    @Override
    public Cart updateCart(Long userId, Long variantId) {
        Cart cart = cartRepository.findCartByUserId(userId);
        if (cart == null) {
            cart = new Cart();
            cart.setUser(userRepository.findById(userId).get());
            cart.setVariants(new HashSet<>());
        }

        Variant product = variantRepository.findVariantById(variantId);
        if (product != null) {
            cart.getVariants().add(product);
        }

        return cartRepository.save(cart);
    }

    @Override
    public Cart getCartByUserId(Long userId) {
        return cartRepository.findCartByUserId(userId);
    }

    @Override
    public Cart UpdateVariantCart(Long userId,Long variantCurrent, Long variantNew) {
        Cart cart = cartRepository.findCartByUserId(userId);
        cart.getVariants().remove(variantRepository.findVariantById(variantCurrent));
        cart.getVariants().add(variantRepository.findVariantById(variantNew));
        cart = cartRepository.save(cart);
        return cart;
    }
}
