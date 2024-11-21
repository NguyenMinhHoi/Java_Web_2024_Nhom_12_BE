package com.example.demo.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @KafkaListener(topics = "your_topic_name", groupId = "group-id")
    public void consume(String message) {
        System.out.println("Received message: " + message);
    }
}