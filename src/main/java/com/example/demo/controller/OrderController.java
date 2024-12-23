package com.example.demo.controller;

import com.example.demo.model.OrderRequest;
import com.example.demo.service.dto.OrderDTO;
import com.example.demo.model.Orders;
import com.example.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/orders")
@CrossOrigin("*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private KafkaTemplate<String, OrderRequest> kafkaTemplate;

    private static final String TOPIC = "order-requests";

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostMapping("")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest orderRequest) {
        kafkaTemplate.send(TOPIC, orderRequest);
        return ResponseEntity.ok("Order request received and queued for processing");
    }

    @MessageMapping("/sendOrder")
    public void sendOrder(OrderDTO order) {
        messagingTemplate.convertAndSend("/topic/orders/" + order.getUserId(), order);
    }

    @GetMapping("/merchant")
    public ResponseEntity<List<OrderDTO>> getAllOrdersByMerchant(
            @RequestParam("merchantNumber") Long merchantNumber,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<OrderDTO> orders = orderService.findOrdersByMerchantPaged(merchantNumber, page, size);
        return ResponseEntity.ok().body(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
          OrderDTO order = orderService.findOrderById(id);
          return ResponseEntity.ok().body(order);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<Page<OrderDTO>> GetOrderByUserId(@PathVariable Long id, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(name="status")String status) {
        Page<OrderDTO> order = orderService.findOrdersByUser(id, page, size,status);
        return ResponseEntity.ok().body(order);
    }

    @PutMapping("/status")
    public ResponseEntity<List<OrderDTO>> updateOrder(@RequestBody List<Long> ids) {
        List<OrderDTO> orders = new LinkedList<>();
        for (Long orderId : ids) {
            orders.add(orderService.toDto(orderService.updateOrderStatus(orderId)));
        }
        return ResponseEntity.ok().body(orders);
    }
}
