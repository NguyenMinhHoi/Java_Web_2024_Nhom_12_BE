package com.example.demo.controller;

import com.example.demo.service.dto.OrderDTO;
import com.example.demo.model.Orders;
import com.example.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@CrossOrigin("*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("")
    public ResponseEntity<List<Orders>> createOrder(@RequestBody OrderDTO orderDTO) {
          List<Orders> orders = orderService.createOrder(
              orderDTO.getVariants(),
              orderDTO.getUserId(),
              orderDTO.getMerchantNumber(),
              orderDTO.getDetail()
          );
          return ResponseEntity.ok().body(orders);
    }
}
