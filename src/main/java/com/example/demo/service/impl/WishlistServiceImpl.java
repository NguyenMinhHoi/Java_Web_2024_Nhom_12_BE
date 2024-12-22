package com.example.demo.service.impl;

import com.example.demo.model.Wishlist;
import com.example.demo.model.Product;
import com.example.demo.model.Merchant;
import com.example.demo.repository.WishlistRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.MerchantRepository;
import com.example.demo.service.UserService;
import com.example.demo.service.WishlistService;
import com.example.demo.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
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
    @Qualifier("userService")
    @Autowired
    private UserService userService;

    @Override
    public List<Wishlist> findAll() {
        return List.of();
    }

    @Override
    public Wishlist findById(Long id) {
        return wishlistRepository.findByUserId(id);
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
    public Wishlist addProductToWishlist(Long userId, Long productId) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId);
        if(CommonUtils.isEmpty(wishlist)){
            wishlist = new Wishlist();
            wishlist.setUser(userService.findById(userId).get());
            wishlist = wishlistRepository.save(wishlist);
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Set<Product> products = wishlist.getProducts() == null ? new HashSet<>() : new HashSet<>(wishlist.getProducts());
        products.add(product);
        wishlist.setProducts(products);
        return wishlistRepository.save(wishlist);
    }

    @Transactional
    @Override
    public Wishlist removeProductFromWishlist(Long userId, Long productId) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId);
        if(CommonUtils.isEmpty(wishlist)){
            wishlist = new Wishlist();
            wishlist.setUser(userService.findById(userId).get());
            wishlist = wishlistRepository.save(wishlist);
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Set<Product> products = wishlist.getProducts() == null ? new HashSet<>() : new HashSet<>(wishlist.getProducts());
        products.remove(product);
        wishlist.setProducts(products);

        return wishlistRepository.save(wishlist);
    }

    @Transactional
    @Override
    public Wishlist addShopToWishlist(Long userId, Long shopId) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId);
        if(CommonUtils.isEmpty(wishlist)){
            wishlist = new Wishlist();
            wishlist.setUser(userService.findById(userId).get());
            wishlist = wishlistRepository.save(wishlist);
        }
        Merchant merchant = merchantRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Shop not found"));

        Set<Merchant> shops = wishlist.getShops() == null ? new HashSet<>() : new HashSet<>(wishlist.getShops());
        shops.add(merchant);
        wishlist.setShops(shops);

        return wishlistRepository.save(wishlist);
    }

    @Transactional
    @Override
    public Wishlist removeShopFromWishlist(Long userId, Long shopId) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId);
        if(CommonUtils.isEmpty(wishlist)){
            wishlist = new Wishlist();
            wishlist.setUser(userService.findById(userId).get());
            wishlist = wishlistRepository.save(wishlist);
        }
        Merchant merchant = merchantRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Shop not found"));

        Set<Merchant> shops = wishlist.getShops() == null ? new HashSet<>() : new HashSet<>(wishlist.getShops());
        shops.remove(merchant);
        wishlist.setShops(shops);

        return wishlistRepository.save(wishlist);
    }

    @Override
    public Long getMerchantFollowers(Long merchantId){
            return wishlistRepository.countByShopsContaining(merchantId);
    }
}