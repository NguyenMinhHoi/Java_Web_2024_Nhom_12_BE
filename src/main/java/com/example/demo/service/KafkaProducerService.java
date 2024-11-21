package com.example.demo.service;

import com.example.demo.model.OrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private static final String TOPIC = "your_topic_name";

    @Autowired
    private KafkaTemplate<String, OrderRequest> kafkaTemplate;

    public void sendMessage(OrderRequest message) {
        this.kafkaTemplate.send(TOPIC, message);
    }
}