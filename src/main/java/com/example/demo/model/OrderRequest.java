package com.example.demo.model;

import lombok.Data;

import java.util.HashMap;
import java.util.List;

@Data
public class OrderRequest {
    private List<Variant> variants;
    private Long userId;
    private Long merchantNumber;
    private HashMap<String, String> detail;
}