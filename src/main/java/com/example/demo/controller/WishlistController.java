package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.model.Wishlist;
import com.example.demo.service.UserService;
import com.example.demo.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wishlists")
@CrossOrigin("*")
public class WishlistController {
       @Autowired
       private WishlistService wishlistService;

       @Autowired
       private UserService userService;

       @PostMapping("/product/{productId}")
       public void addProductToWishlist(@PathVariable long productId) {
           User user = userService.getCurrentUser();
           wishlistService.addProductToWishlist(productId, user.getId());
       }

       @PostMapping("/shop/{shopId}")
       public void addShopToWishlist(@PathVariable long shopId) {
           User user = userService.getCurrentUser();
           wishlistService.addShopToWishlist(shopId, user.getId());
       }

       @DeleteMapping("/{productId}")
       public void removeProductFromWishlist(@PathVariable long productId,@RequestParam(name = "userId") long userId) {
           wishlistService.removeProductFromWishlist(productId, userId);
       }

        @DeleteMapping("/{shopId}")
        public void removeShopFromWishlist(@PathVariable long shopId,@RequestParam(name = "userId") long userId) {
            wishlistService.removeShopFromWishlist(shopId, userId);
        }

        @GetMapping("/{userId}")
       public ResponseEntity<Wishlist> getWishlistByUserId(@PathVariable long userId) {
            Wishlist wishlist = wishlistService.findById(userId);
            return ResponseEntity.ok().body(wishlist);
       }

       @GetMapping("/merchant")
       public ResponseEntity<Long> getMerchantFollow(@RequestParam(name = "merchantId") long merchantId) {
            long followers = wishlistService.getMerchantFollowers(merchantId);
            return ResponseEntity.ok().body(followers);
       }


}