package com.example.demo.service;

import com.example.demo.model.Merchant;
import com.example.demo.model.OrderRequest;
import com.example.demo.model.Orders;
import com.example.demo.repository.MerchantRepository;
import com.example.demo.service.dto.OrderDTO;
import jakarta.transaction.Transactional;
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

    @Autowired
    private MerchantRepository merchantRepository;

    @Transactional
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
                Merchant merchant = merchantRepository.findById(orderRequest.getMerchantNumber()).orElse(null);
                OrderDTO orderDTO = orderService.findOrderById(order.getId());
                messagingTemplate.convertAndSend("/topic/orders/" + orderRequest.getUserId(), orderDTO);
                messagingTemplate.convertAndSend("/topic/orders/" + merchant.getUser().getId(), orderDTO);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}