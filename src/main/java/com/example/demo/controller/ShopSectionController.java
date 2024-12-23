package com.example.demo.controller;


import com.example.demo.model.Merchant;
import com.example.demo.model.ShopSection;
import com.example.demo.service.MerchantService;
import com.example.demo.service.ShopSectionService;
import com.example.demo.service.dto.ProductDTO;
import com.example.demo.service.impl.MerchantServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shopsections")
@CrossOrigin("*")
public class ShopSectionController {
    @Autowired
    private ShopSectionService shopSectionService;

    @Autowired
    private MerchantService merchantService;

    @GetMapping("/all/{id}")
    public ResponseEntity<Object> getAllShopSections(@PathVariable Long id) {
        shopSectionService.findByName(id);
        return ResponseEntity.ok().body(shopSectionService.findByName(id));
    }

    @PostMapping("")
    public ResponseEntity<Object> saveShopSection(@RequestBody ShopSection shopSection) {
        ShopSection rs = shopSectionService.save(shopSection);
        return ResponseEntity.ok().body(rs);
    }

    @PutMapping("")
    public ResponseEntity<Object> updateShopSection(@RequestBody ShopSection shopSection) {
        return ResponseEntity.ok().body(shopSectionService.update(shopSection));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deleteShopSection(@PathVariable Long id) {
        try {
            shopSectionService.deleteById(id);
        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<ProductDTO>> getShopSectionById(@PathVariable Long id) {
        return new ResponseEntity<>(shopSectionService.getProductsInShopSection(id), HttpStatus.OK);
    }
}