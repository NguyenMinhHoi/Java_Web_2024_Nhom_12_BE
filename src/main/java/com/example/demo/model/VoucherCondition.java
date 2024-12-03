package com.example.demo.model;

import com.example.demo.utils.enumeration.ConditionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

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

       @ManyToMany
       private Set<Product> product;

       private Double minPrice;
}
