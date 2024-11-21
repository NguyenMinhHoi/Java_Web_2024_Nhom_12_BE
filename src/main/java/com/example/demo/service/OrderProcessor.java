package com.example.demo.service;

import com.example.demo.model.OrderRequest;
import com.example.demo.model.Orders;
import com.example.demo.service.dto.OrderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderProcessor {

    @Autowired
    private OrderService orderService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "order-requests", groupId = "order-group", containerFactory = "orderKafkaListenerContainerFactory")
    public void processOrder(OrderRequest orderRequest) {
        try {
            List<Orders> orders = orderService.createOrder(
                    orderRequest.getVariants(),
                    orderRequest.getUserId(),
                    orderRequest.getMerchantNumber(),
                    orderRequest.getDetail()
            );

            for (Orders order : orders) {
                OrderDTO orderDTO = orderService.toDto(order);
                messagingTemplate.convertAndSend("/topic/user/orders/" + orderRequest.getUserId(), orderDTO);
                messagingTemplate.convertAndSend("/topic/merchant/orders/" + orderRequest.getMerchantNumber(), orderDTO);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}