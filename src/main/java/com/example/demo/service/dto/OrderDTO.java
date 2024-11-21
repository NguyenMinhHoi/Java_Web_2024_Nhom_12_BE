package com.example.demo.service.dto;

import com.example.demo.model.Address;
import com.example.demo.model.Variant;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.HashMap;

@Data
public class OrderDTO {

    private String id;
    private List<Variant> variants;
    private Long userId;
    private Long merchantNumber;
    private HashMap<String, String> detail;
    private String name;

    private String customerName;

    private String orderCode;

    private String phone;

    private String email;

    private String status;

    private String total;

    private Date orderDate;

    private String address;
}