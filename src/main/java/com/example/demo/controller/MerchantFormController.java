package com.example.demo.controller;

import com.example.demo.model.MerchantForm;
import com.example.demo.service.MerchantFormService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/form")
@CrossOrigin("*")
public class MerchantFormController {
    @Autowired
    private MerchantFormService merchantFormService;

    @GetMapping("/all")
    public ResponseEntity<List<MerchantForm>> getAll(){
        return ResponseEntity.ok().body(merchantFormService.findAll());
    }

    @PutMapping("/approval/register")
    public ResponseEntity<?> approvalFormRegister(@RequestBody MerchantForm merchantForm) throws MessagingException {
          merchantFormService.approvalFormRegister(merchantForm.getId());
          return ResponseEntity.ok().body(true);
    }

    @PutMapping("/approval/update")
    public ResponseEntity<?> approvalFormUpdate(@RequestBody MerchantForm merchantForm) throws MessagingException {
        merchantFormService.approvalFormUpgrade(merchantForm.getId());
        return ResponseEntity.ok().body(true);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MerchantForm> getById(@PathVariable Long id){
        return ResponseEntity.ok().body(merchantFormService.findById(id));
    }
}
