package com.example.demo.controller;

import com.example.demo.model.Cart;
import com.example.demo.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/cart")
@CrossOrigin("*")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<List<Cart>> findAll() {
        List<Cart> carts = cartService.findAll();
        return ResponseEntity.ok(carts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cart> findById(@PathVariable Long id) {
        Cart cart = cartService.findById(id);
        return ResponseEntity.ok(cart);
    }

    @PutMapping("")
    public ResponseEntity<Cart> updateCart(@RequestBody HashMap<String,Object> body) {

        Long userId = Long.parseLong(body.get("userId").toString());
        Long variantId = Long.parseLong(body.get("variantId").toString());
        return ResponseEntity.ok(cartService.updateCart(userId, variantId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Cart> findByUserId(@PathVariable Long userId) {
        Cart carts = cartService.getCartByUserId(userId);
        carts.setUser(null);
        carts.getVariants().forEach(variant -> variant.getProduct().getMerchant().setUser(null));
        return ResponseEntity.ok(carts);
    }

    @PostMapping
    public ResponseEntity<Cart> save(@RequestBody Cart cart) {
        Cart savedCart = cartService.save(cart);
        return ResponseEntity.ok(savedCart);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        cartService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/re-variant/{userId}/{variantCurrent}/{variantNew}")
    public ResponseEntity<Cart> updateVariantCart(@PathVariable Long userId, @PathVariable Long variantCurrent, @PathVariable Long variantNew) {
         return ResponseEntity.ok(cartService.UpdateVariantCart(userId, variantCurrent, variantNew));
    };
}
