package com.example.demo.controller;


import com.example.demo.model.GroupOption;
import com.example.demo.model.Product;
import com.example.demo.model.Review;
import com.example.demo.model.Variant;
import com.example.demo.service.MerchantService;
import com.example.demo.service.ProductService;
import com.example.demo.service.dto.FilterDTO;
import com.example.demo.service.dto.ProductDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@CrossOrigin("*")
@Controller
@RequestMapping("/products")
public class ProductController {

      @Autowired
      private ProductService productService;

      @Autowired
      private MerchantService merchantService;

      @GetMapping("/product/{id}")
      public ResponseEntity<Product> getProductById(@PathVariable Long id) {
            Product product = productService.findById(id);
            return ResponseEntity.ok().body(product);
      }

      @PostMapping("")
      public ResponseEntity<Product> createProduct(@RequestBody Product product) {
            Product back =  productService.createProduct(product);
             return ResponseEntity.ok().body(back);
      }

      @PutMapping("")
      public ResponseEntity<?> updateProduct(@RequestBody Product product) {
            Product product1 = productService.save(product);
            return ResponseEntity.ok().body(product1);
      }

      @DeleteMapping("/{id}")
      public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
             productService.deleteById(id);
             return ResponseEntity.ok().build();
      }

      @GetMapping("/merchant/{id}")
      public ResponseEntity<List<Product>> getProducts(@PathVariable Long id){
            List<Product> products = productService.getAllProductByShopId(id);
            return new ResponseEntity<>(products, HttpStatus.OK);
      }

      @GetMapping("/variant/{id}")
      public ResponseEntity<List<Variant>> getVariantsByProductId(@PathVariable Long id){
            List<Variant> products = productService.getVariantsByProductId(id);
            return new ResponseEntity<>(products, HttpStatus.OK);
      }

      @PutMapping("/variants")
      public ResponseEntity<?> updateVariants(@RequestBody List<Variant> variants) {
            productService.updateVariant(variants);
            return ResponseEntity.ok().build();
      }

      @GetMapping("/search")
      public ResponseEntity<List<Product>> searchProducts(
              @RequestParam String keyword,
              @RequestParam(required = false) Long categoryId,
              @RequestParam Double minPrice,
              @RequestParam Double maxPrice) {
            List<Product> products = productService.searchProducts(keyword, categoryId, minPrice, maxPrice);
            return ResponseEntity.ok(products);
      }

      @PostMapping("/variant/save/{productId}")
      public ResponseEntity<?> saveVariant(@RequestBody List<GroupOption> groupOptions,@PathVariable Long productId) {
            List<Variant> variants =  productService.saveVariants(groupOptions,productId);
            variants.forEach(variant -> {
                  variant.getProduct().setMerchant(null);
                  variant.getProduct().setImage(null);
            });
            return ResponseEntity.ok().body(variants);
      }

      @GetMapping("/featured")
      public ResponseEntity<List<Product>> getFeaturedProducts(@RequestParam int limit) {
            List<Product> products = productService.getFeaturedProducts(limit);
            return ResponseEntity.ok(products);
      }

      @GetMapping("/category/{categoryId}")
      public ResponseEntity<List<Product>> getProductsByCategory(
              @PathVariable Long categoryId,
              @RequestParam int page,
              @RequestParam int size) {
            List<Product> products = productService.getProductsByCategory(categoryId, page, size);
            return ResponseEntity.ok(products);
      }

      @GetMapping("/discounted")
      public ResponseEntity<List<Product>> getDiscountedProducts(@RequestParam int limit) {
            List<Product> products = productService.getDiscountedProducts(limit);
            return ResponseEntity.ok(products);
      }

      @PutMapping("/{productId}/stock")
      public ResponseEntity<?> updateProductStock(
              @PathVariable Long productId,
              @RequestParam int quantityChange,
              @RequestParam Long variantId) {
            productService.updateProductStock(productId, quantityChange, variantId);
            return ResponseEntity.ok().build();
      }

      @GetMapping("/{productId}/reviews")
      public ResponseEntity<List<Review>> getProductReviews(
              @PathVariable Long productId,
              @RequestParam int page,
              @RequestParam int size) {
            List<Review> reviews = productService.getProductReviews(productId, page, size);
            return ResponseEntity.ok(reviews);
      }

      @PostMapping("/cart")
      public ResponseEntity<?> addToCart(
              @RequestParam Long userId,
              @RequestParam Long productId,
              @RequestParam int quantity) {
            productService.addToCart(userId, productId, quantity);
            return ResponseEntity.ok().build();
      }

      @GetMapping("")
      public ResponseEntity<List<ProductDTO>> getAllProduct(@RequestParam int page, @RequestParam int size){
            List<ProductDTO> products = productService.findAllPage(page,size);
            return ResponseEntity.ok(products);
      }

      @GetMapping("/{productId}/details")
      public ResponseEntity<HashMap<String, Object>> getProductDetails(@PathVariable Long productId) {
            HashMap<String, Object> productDetails = productService.getDetailsProducts(productId);
            if (productDetails.isEmpty()) {
                  return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(productDetails);
      }


      @GetMapping("/variant/one/{productId}")
      public ResponseEntity<Object> getOneVariant(@PathVariable Long productId) {
            return ResponseEntity.ok(productService.getOneVariant(productId));
      }
      @PostMapping("filter")
      public ResponseEntity<Object> filterProduct(@RequestBody FilterDTO filterDTO, @RequestParam int size, @RequestParam int page) {
          Pageable pageable = PageRequest.of(page-1, size);
          return ResponseEntity.ok(productService.findProductsByFilter(filterDTO, pageable));
      }
      @GetMapping("/merchant")
      public ResponseEntity<Page<ProductDTO>> getProducts(@RequestParam(name="merchantId") Long merchantId,
                                                          @RequestParam(name="page", defaultValue = "0") int page,
                                                          @RequestParam(name="size", defaultValue = "10") int size) {
            return new ResponseEntity<>(productService.getAllProductByShopId(merchantId,page,size), HttpStatus.OK);
      }

}
