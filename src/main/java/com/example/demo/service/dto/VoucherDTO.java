package com.example.demo.service.dto;
import com.example.demo.utils.enumeration.ConditionType;
import com.example.demo.utils.enumeration.VoucherType;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

import java.time.Instant;

@Data
@NoArgsConstructor
public class VoucherDTO {

    private Long id;
    private String name;
    private String code;
    private VoucherType voucherType;
    private ConditionType conditionType;
    private Long voucherConditionId; // We'll use the ID instead of the whole object
    private Double valueCondition;
    private Double discount;
    private Boolean active;
    private Instant expirationDate;
    private Instant startDate;
    private Boolean status;
    private Long merchantId; // We'll use the ID instead of the whole object
    private Integer quantity;
    private List<Long> products; // List of product IDs
    private String description; // This is a simple string for now, you can expand it as needed
    private Long categoryId;
    private Double minPrice;
}