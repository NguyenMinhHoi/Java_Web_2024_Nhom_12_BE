package com.example.demo.controller;


import com.example.demo.model.Address;
import com.example.demo.service.AddressService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin("*")
@Controller
@RequestMapping("/addresses")
public class AddressController {

       @Autowired
       private AddressService addressService;

       @PostMapping("/user/{userId}")
       public ResponseEntity<List<Address>> saveAddress(@RequestBody Address address,@PathVariable Long userId) {;
              return ResponseEntity.ok().body(addressService.saveUserAddresses(address,userId));
       }


}
