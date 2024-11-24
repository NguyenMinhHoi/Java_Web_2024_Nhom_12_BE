package com.example.demo.model;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.Date;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {
     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;

     private String name;

     private String description;

     private String productCode;

     private Long sold;

     @Column(columnDefinition = "boolean default true")
     private Boolean isDiscount;

     @ManyToOne
     private Merchant merchant;

     @OneToMany
     private Set<Image> image;

     private Double rating;

     @OneToMany
     private Set<GroupOption> groupOptions;

     @ManyToOne
     private Category category;

     private Boolean status;

     private Date createdDate;

     private Date updatedDate;

     private Double maxPrice;

     private Double minPrice;

}
