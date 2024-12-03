package com.example.demo.controller;

import com.example.demo.model.MerchantForm;
import com.example.demo.service.MerchantFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/form")
@CrossOrigin("*")
public class MerchantFormController {
//    @Autowired
//    private MerchantFormService merchantFormService;
//
//    @GetMapping("/all")
//    public ResponseEntity<List<MerchantForm>> getAll(){
//        return ResponseEntity.ok().body(merchantFormService.findAll());
//    }
//
//    @PutMapping("/approval")
//    public ResponseEntity<MerchantForm> approvalForm(@RequestBody MerchantForm merchantForm){
//           merchantFormService.
//    }
}
