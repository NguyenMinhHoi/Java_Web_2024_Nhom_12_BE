package com.example.demo.service.dto;

import com.example.demo.model.Variant;
import lombok.Data;

import java.util.List;
import java.util.HashMap;

@Data
public class OrderDTO {
    private List<Variant> variants;
    private Long userId;
    private Long merchantNumber;
    private HashMap<String, String> detail;
    private String name;

    private String phone;

    private String email;
}