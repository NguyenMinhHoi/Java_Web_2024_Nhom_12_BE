package com.example.demo.model;

import com.example.demo.utils.enumeration.ConditionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class VoucherCondition {

       @Id
       @GeneratedValue(strategy = GenerationType.IDENTITY)
       private Long id;

       private ConditionType conditionType;

       private String description;

       @ManyToOne
       @JoinColumn(name = "category_id")
       private Category category;

       @ManyToOne
       private Product product;

       private Double minPrice;
}
