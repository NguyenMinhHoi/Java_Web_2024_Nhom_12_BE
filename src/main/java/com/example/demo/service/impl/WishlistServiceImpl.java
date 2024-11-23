package com.example.demo.service.impl;

import com.example.demo.model.Wishlist;
import com.example.demo.model.Product;
import com.example.demo.model.Merchant;
import com.example.demo.repository.WishlistRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.MerchantRepository;
import com.example.demo.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class WishlistServiceImpl implements WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    @Override
    public List<Wishlist> findAll() {
        return List.of();
    }

    @Override
    public Wishlist findById(Long id) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public Wishlist save(Wishlist entity) {
        return null;
    }

    @Transactional
    @Override
    public Wishlist addProductToWishlist(Long wishlistId, Long productId) {
        Wishlist wishlist = wishlistRepository.findById(wishlistId)
                .orElseThrow(() -> new RuntimeException("Wishlist not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Set<Product> products = wishlist.getProducts();
        products.add(product);
        wishlist.setProducts(products);

        return wishlistRepository.save(wishlist);
    }

    @Transactional
    @Override
    public Wishlist removeProductFromWishlist(Long wishlistId, Long productId) {
        Wishlist wishlist = wishlistRepository.findById(wishlistId)
                .orElseThrow(() -> new RuntimeException("Wishlist not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Set<Product> products = wishlist.getProducts();
        products.remove(product);
        wishlist.setProducts(products);

        return wishlistRepository.save(wishlist);
    }

    @Transactional
    @Override
    public Wishlist addShopToWishlist(Long wishlistId, Long shopId) {
        Wishlist wishlist = wishlistRepository.findById(wishlistId)
                .orElseThrow(() -> new RuntimeException("Wishlist not found"));
        Merchant merchant = merchantRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Shop not found"));

        Set<Merchant> shops = wishlist.getShops();
        shops.add(merchant);
        wishlist.setShops(shops);

        return wishlistRepository.save(wishlist);
    }

    @Transactional
    @Override
    public Wishlist removeShopFromWishlist(Long wishlistId, Long shopId) {
        Wishlist wishlist = wishlistRepository.findById(wishlistId)
                .orElseThrow(() -> new RuntimeException("Wishlist not found"));
        Merchant merchant = merchantRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Shop not found"));

        Set<Merchant> shops = wishlist.getShops();
        shops.remove(merchant);
        wishlist.setShops(shops);

        return wishlistRepository.save(wishlist);
    }
}