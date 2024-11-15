package com.example.demo.controller;


import com.example.demo.model.Merchant;
import com.example.demo.service.MerchantService;
import com.example.demo.service.dto.MerchantDTO;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/merchants")
@CrossOrigin("*")
public class MerchantController {
    private final MerchantService merchantService;

    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
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

}
