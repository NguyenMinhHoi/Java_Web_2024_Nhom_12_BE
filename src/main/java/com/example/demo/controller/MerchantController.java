package com.example.demo.controller;


import com.example.demo.model.Merchant;
import com.example.demo.service.MerchantService;
import com.example.demo.service.OrderService;
import com.example.demo.service.dto.MerchantDTO;
import com.example.demo.service.impl.OrderServiceImpl;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("/merchants")
@CrossOrigin("*")
public class MerchantController {
    private final MerchantService merchantService;
    private final OrderService orderService;

    public MerchantController(MerchantService merchantService, OrderService orderService) {
        this.merchantService = merchantService;

        this.orderService = orderService;
    }

    @PostMapping("")
    public ResponseEntity<?> createMerchant(@RequestBody Merchant merchant) {
        try {
            Merchant createdMerchant = merchantService.createMerchant(merchant);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMerchant);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error creating merchant: " + e.getMessage());
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getMerchantByUserId(@PathVariable Long userId) {
        try {
            Merchant createdMerchant = merchantService.getMerchantById(userId);
            return ResponseEntity.status(HttpStatus.OK).body(createdMerchant);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error creating merchant: " + e.getMessage());
        }
    }

    @PostMapping("/test-approval")
    public ResponseEntity<?> testApproval() {
        Merchant testMerchant = new Merchant();
        testMerchant.setEmail("tecaca64@gmail.com");  // Replace with your email
        testMerchant.setName("Test Merchant");
        merchantService.approveMerchantRegistration(testMerchant);
        return ResponseEntity.ok("Approval email sent successfully");
    }

    @GetMapping("/details/{merchantId}")
    public ResponseEntity<?> getDetailsMerchantShop(@PathVariable Long merchantId){
        MerchantDTO merchantDTO = merchantService.getMerchantByMerchantID(merchantId);
        if (merchantDTO == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok().body(merchantDTO);
    }

    @GetMapping("/compare-order/{merchantId}")
    public ResponseEntity<Map> compareCountOrderWithPreviousMonth(@PathVariable Long merchantId) throws MessagingException {
        Map<String, Object> result = orderService.compareOrderCountWithPreviousMonth(merchantId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/revenue-chart/{merchantId}")
    public ResponseEntity<Map> getRevenueChart(@PathVariable Long merchantId, @RequestParam(name="time")String time) throws MessagingException {
        Map<Integer, Double> result = orderService.getRevenueByShopId(merchantId,time);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/compare-revenue/{merchantId}")
    public ResponseEntity<Map> compareRevenueWithPreviousMonth(@PathVariable Long merchantId) throws MessagingException {
        Map<String, Object> result = orderService.compareRevenueWithPreviousMonth(merchantId);
        return ResponseEntity.ok(result);
    }

}
